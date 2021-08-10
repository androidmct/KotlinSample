package com.bytepace.dimusco.Activity

import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bytepace.dimusco.Adapter.colors.PagesListAdapter
import com.bytepace.dimusco.Adapter.colors.PersonalColorsAdapter
import com.bytepace.dimusco.DimuscoApp
import com.bytepace.dimusco.R
import com.bytepace.dimusco.data.manager.PageFileManager
import com.bytepace.dimusco.data.source.local.database.DimuscoDatabase
import com.bytepace.dimusco.helper.GlobalFunctions
import com.bytepace.dimusco.helper.GlobalVariables
import com.bytepace.dimusco.ui.components.list.decorations.GridItemMarginDecoration
import com.bytepace.dimusco.ui.score.selector.adapter.PageItem
import com.gmail.samehadar.iosdialog.IOSDialog
import kotlinx.android.synthetic.main.activity_library.*
import kotlinx.android.synthetic.main.activity_pages_grid.*
import kotlinx.android.synthetic.main.activity_settings_colors.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.sample
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

class PagesGridActivity : AppCompatActivity(),
        Toolbar.OnMenuItemClickListener,
        PopupMenu.OnMenuItemClickListener,
        CoroutineScope {

    private var job: Job = SupervisorJob()

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Timber.e(exception)
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main.immediate + job + exceptionHandler

    companion object{
        lateinit var pagesListAdapter: PagesListAdapter

        const val KEY_SCORE_ID = "score.id"
        const val KEY_SCORE_AID = "score.aid"

        fun arguments(sid: String, aid: String): Bundle {
            return bundleOf(KEY_SCORE_ID to sid, KEY_SCORE_AID to aid)
        }
    }

    private val pageFileManager = PageFileManager(DimuscoApp.getContext())
    private val pagesDao by lazy { DimuscoDatabase.getInstance().pageDao() }
    private var pageItemsLiveData: LiveData<List<PageItem>>? = null

    private var iosDialog: IOSDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pages_grid)

        iosDialog = IOSDialog.Builder(this)
                .setCancelable(false)
                .setSpinnerClockwise(false)
                .setMessageContentGravity(Gravity.END)
                .build()

        val sid = intent.extras?.getString(KEY_SCORE_ID) ?: ""
        val aid = intent.extras?.getString(KEY_SCORE_AID) ?: ""
        if (sid == "" || aid == "") onBackPressed()
        pageItemsLiveData = receivePages(sid, aid)

        toolbar_pages_grid.title = resources.getString(R.string.global_pages)
        toolbar_pages_grid.setOnMenuItemClickListener(this)

        pagesListAdapter = PagesListAdapter(this, GlobalVariables.pages)
        list_thumbs.layoutManager = GridLayoutManager(this, 3)
        list_thumbs.adapter = pagesListAdapter
        val verticalPadding = 16
        val horizontalPadding = 16
        val decoration = GridItemMarginDecoration(horizontalPadding, verticalPadding, 3)
        list_thumbs.addItemDecoration(decoration)
        pageItemsLiveData?.observe(this, Observer(::onItemsReady))

    }

    private fun onItemsReady(list: List<PageItem>) {
        GlobalVariables.pages.clear()
        GlobalVariables.pages.addAll(list)
        pagesListAdapter.notifyDataSetChanged()
        iosDialog?.dismiss()
    }

    fun receivePages(sid: String, aid: String): LiveData<List<PageItem>> {
        iosDialog?.show()
        return pagesDao.retrieveScorePages(aid)
                .sample(50)
                .catch {
                    Timber.e(it)
                }
                .map { list ->
                    list.map {
                        val fileUrl = pageFileManager.cacheThumbnailImage(it, true)
                        PageItem(
                                it.page.id,
                                it.page.aid,
                                sid,
                                fileUrl,
                                it.hashCode()
                        )
                    }
                }.flowOn(Dispatchers.IO).asLiveData(coroutineContext)
    }

    override fun onMenuItemClick(mItem: MenuItem?): Boolean {
        when (mItem?.itemId) {
            R.id.menu_item_back -> onBackPressed()
            else -> return false
        }
        return true
    }

    override fun onBackPressed() {
        finish()
    }
}