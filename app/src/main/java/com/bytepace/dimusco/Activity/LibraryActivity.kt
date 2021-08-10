package com.bytepace.dimusco.Activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bytepace.dimusco.Adapter.library.ScoreListAdapter
import com.bytepace.dimusco.Adapter.library.TabComposeAdapter
import com.bytepace.dimusco.DimuscoApp
import com.bytepace.dimusco.R
import com.bytepace.dimusco.data.manager.DownloadPageManager
import com.bytepace.dimusco.data.mapper.ScoreMapper
import com.bytepace.dimusco.data.model.Page
import com.bytepace.dimusco.data.model.Score
import com.bytepace.dimusco.data.model.Tab
import com.bytepace.dimusco.data.model.User
import com.bytepace.dimusco.data.repository.*
import com.bytepace.dimusco.data.repository.score.ScoreRepository
import com.bytepace.dimusco.data.repository.score.Sorting
import com.bytepace.dimusco.data.source.local.model.TabDB
import com.bytepace.dimusco.data.source.remote.socket.SocketScoreListener
import com.bytepace.dimusco.data.source.remote.socket.SocketService
import com.bytepace.dimusco.data.source.remote.socket.SocketTabsListener
import com.bytepace.dimusco.helper.GlobalVariables
import com.bytepace.dimusco.service.ConnectivityService
import com.bytepace.dimusco.ui.components.SortMenu
import com.bytepace.dimusco.ui.library.TabVM
import com.bytepace.dimusco.ui.login.MainActivity
import com.bytepace.dimusco.ui.settings.main.SettingsActivity
import com.bytepace.dimusco.utils.scrollTo
import kotlinx.android.synthetic.main.activity_library.*
import kotlinx.android.synthetic.main.alert_dialog.view.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber
import kotlin.coroutines.CoroutineContext


@Suppress("DEPRECATION")
class LibraryActivity :AppCompatActivity(), Toolbar.OnMenuItemClickListener, PopupMenu.OnMenuItemClickListener,
        SocketTabsListener, SocketScoreListener, CoroutineScope{

    private var job: Job = SupervisorJob()

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Timber.e(exception)
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main.immediate + job + exceptionHandler

    enum class SortItems{
        NameAsc, NameDesc, ComposerAsc, ComposerDesc
    }

    private val _sortType = MutableStateFlow(Sorting.NAME_ASC)
    private val currentTabId: MutableStateFlow<String> = MutableStateFlow(TabDB.DEFAULT_ID)
    private var tabRepository: TabRepository = TabRepository
    val sortType: Sorting get() = _sortType.value

    val userName: LiveData<String>
        get() = liveData(Dispatchers.IO) {
            val name = userRepository.currentUser?.name ?: ""
            toolbar.title = name
        }

    val tabsList: LiveData<List<TabVM>> = tabRepository
            .selectAll()
            .distinctUntilChanged()
            .onEach { tabs ->
                if (!tabs.map { it.tid }.contains(currentTabId.value)) {
                    currentTabId.value = TabDB.DEFAULT_ID
                }
            }
            .combine(currentTabId) { items, current ->
                items.map { TabVM(it.tid, it.name, it.order, it.tid == current, it.scoreIds) }
            }
            .flowOn(Dispatchers.IO)
            .asLiveData(coroutineContext)

    val scoresList: LiveData<List<Score>>
        get() = currentTabId.combine(_sortType) { tab, sortType ->
            tab to sortType
        }.flatMapLatest { (tabId, sortType) ->
            val tabScores = mutableListOf<String>()
            tabsList.value?.map { it.scores?.let {
                    scores -> tabScores.addAll(scores)
            } }
            val userId = userRepository.currentUser?.uid ?: throw IllegalStateException("$userRepository")
            scoreRepository.subscribeForLocalScores(userId, tabId, sortType)
        }.flowOn(Dispatchers.IO).asLiveData(coroutineContext)

    companion object{
        var tabListAdapter: TabComposeAdapter = TabComposeAdapter(LibraryActivity())
        lateinit var scoreListAdapter: ScoreListAdapter
        val userRepository = UserRepository.get()

        lateinit var scoreRepository: ScoreRepository
        lateinit var syncRepository: SyncRepository

        val user: User?
            get() = userRepository.currentUser

        val downloadPageManager: DownloadPageManager by lazy { DownloadPageManager(DimuscoApp.getContext()) }
        val _handledError = MutableStateFlow<Throwable?>(null)
    }

    override fun onResume() {
        super.onResume()

        if(ConnectivityService.isConnected){
            connectivity_indicator.setBackgroundColor(resources.getColor(R.color.cl3))
        } else {
            connectivity_indicator.setBackgroundColor(resources.getColor(R.color.cl2))
        }

        this.tabsList.observe(this, Observer {

        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)

        toolbar.setOnMenuItemClickListener(this)

        val rvTabList = findViewById<RecyclerView>(R.id.tabs_recycler_view)
        rvTabList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvTabList.adapter = tabListAdapter

        val rvScoreList = findViewById<RecyclerView>(R.id.scoresRecyclerView)
        scoreListAdapter = ScoreListAdapter(this, GlobalVariables.scoreList)
        rvScoreList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvScoreList.adapter = scoreListAdapter

        init()

    }

    private fun init(){

        this.userName.observe(this, Observer {  })
        scoresList.observe(this, Observer(::onScoresListChanged))

        val userId = userRepository.currentUser?.uid
                ?: throw IllegalArgumentException("UserID can't be null")
        LayerRepository.init(userId)
        MarkerRepository.init(userId)
        SettingsRepository.init(userId)
        ScoreRepository.init(userId)
        SocketService.setScoreCallback(this)
        SocketService.setTabsCallback(this)
        SyncRepository.init(userId)
        syncRepository = SyncRepository.get()
        tabRepository = TabRepository
        scoreRepository = ScoreRepository.get()
        userRepository.getSession()?.let {
            SocketService.connect(it)
        }
        syncScoresState()
    }

    override fun onMenuItemClick(mItem: MenuItem?): Boolean {
        when (mItem?.itemId) {
            R.id.menu_item_logout -> showLogoutDialog()
            R.id.menu_item_settings -> nfStartActivity(SettingsActivity())
            R.id.menu_item_sort -> showSortDialog(sortType)
            R.id.menu_name_asc -> onSetMenuColor(mItem)
            R.id.menu_name_desc-> onSetMenuColor(mItem)
            R.id.menu_composer_asc-> onSetMenuColor(mItem)
            R.id.menu_composer_desc-> onSetMenuColor(mItem)
            else -> return false
        }
        return true
    }

    private fun showSortDialog(sortType: Sorting) {
        val button: View = toolbar.findViewById(R.id.menu_item_sort) ?: return
        SortMenu(this, button, sortType) {
            changeSortType(it)
            scrollTo(scoresRecyclerView, 0)
        }.show()
    }

    fun changeSortType(sorting: Sorting) {
        _sortType.value = sorting
    }

    private fun onSetMenuColor(mItem: MenuItem?){
        mItem!!.isChecked = true
    }

    private fun showLogoutDialog(){
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.alert_dialog, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
        val  mAlertDialog = mBuilder.show()

        mDialogView.text_message.text = resources.getString(R.string.logout_areYouSure)
        mDialogView.btn_positive.text = resources.getString(R.string.logout_yes)
        mDialogView.btn_negative.text = resources.getString(R.string.global_no)

        mDialogView.btn_positive.setOnClickListener {
            SocketService.disconnect()
            userRepository.logout()
            onBackPressed()
            mAlertDialog?.dismiss()
        }
        mDialogView.btn_negative.setOnClickListener {
            mAlertDialog?.dismiss()
        }
    }

    private fun showSettingsMenu(){
        val popup = PopupMenu(this, findViewById(R.id.menu_item_settings))
        popup.setOnMenuItemClickListener(this)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.menu_sort, popup.menu)

        popup.show()
    }

    override fun onBackPressed() {
        mStartActivity(MainActivity())
    }

    private fun mStartActivity(mActivity: Activity){
        startActivity(Intent(this@LibraryActivity, mActivity::class.java))
        finish()
    }

    private fun nfStartActivity(mActivity: Activity){
        startActivity(Intent(this@LibraryActivity, mActivity::class.java))
    }

    override fun onGetScores(scores: List<Score>) {
        GlobalScope.launch(Dispatchers.IO) {
            scoreRepository.savePages(scores)
        }
    }
    override fun saveTabs(tabs: List<Tab>) {
        GlobalScope.launch(Dispatchers.IO) {
            tabRepository.upsertTabs(tabs)
        }
    }

    private fun onScoresListChanged(scoList: List<Score>) {
        GlobalVariables.scoreList.clear()
        GlobalVariables.scoreList.addAll(scoList)
        scoreListAdapter.notifyDataSetChanged()

        if(GlobalVariables.scoreList.size > 0){
            emptyScoreListTexView.visibility = View.GONE
        } else {
            emptyScoreListTexView.visibility = View.VISIBLE
        }
    }

    private fun syncScoresState() = GlobalScope.launch(Dispatchers.Default) {
        scoreRepository.selectAllScores()
                .take(1).map { list ->
                    list
                            .filter { it.score.isDownloadingScoreInProgress }
                            .map { ScoreMapper.fromLocalScore(it) }
                }.catch {
                    _handledError.value = it
                }.collect { list ->
                    list.forEach { score -> saveScorePages(score) }
                }
    }

    suspend fun saveScorePages(score: Score) {
        try {
            scoreRepository.setScoreIsDownloadInProgress(score.aid, true)
            val email = user?.email ?: throw IllegalStateException()
            val token = userRepository.getLocalUser(email)?.token ?: throw IllegalStateException()
            var scorePagesDownload: ArrayList<Page> = arrayListOf()
            for(page in score.pages){
                if(!page.isDownloaded)
                    scorePagesDownload.add(page)
            }
            scorePagesDownload
                    .map { downloadPageAsync(it, token) }
                    .forEach { it.await() }
            scoreRepository.setScoreIsDownloadInProgress(score.aid, false)
        } catch (ex: Exception) {
            _handledError.value = ex
        }
    }



    suspend fun downloadPageAsync(page: Page, token: String) = async(Dispatchers.IO) {
        val filename = page.id
        downloadPageManager.saveFile(filename, token)
        scoreRepository.setPageIsDownloaded(filename.replace(".png", ""))
    }

}