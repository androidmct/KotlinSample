package com.bytepace.dimusco.ui.editor

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.databinding.ObservableInt
import androidx.lifecycle.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bytepace.dimusco.Adapter.colors.PersonalColorsAdapter
import com.bytepace.dimusco.Adapter.scores.ScorePagesAdapter
import com.bytepace.dimusco.Adapter.symbols.PersonalSymbolsAdapter
import com.bytepace.dimusco.DimuscoApp
import com.bytepace.dimusco.Model.ColorModel
import com.bytepace.dimusco.Model.PageMiniModel
import com.bytepace.dimusco.Model.SymbolModel
import com.bytepace.dimusco.R
import com.bytepace.dimusco.data.manager.PageFileManager
import com.bytepace.dimusco.data.model.Marker
import com.bytepace.dimusco.data.model.PageWithLayers
import com.bytepace.dimusco.data.model.Score
import com.bytepace.dimusco.data.repository.MarkerRepository
import com.bytepace.dimusco.data.repository.SettingsRepository
import com.bytepace.dimusco.data.repository.score.ScoreRepository
import com.bytepace.dimusco.data.source.local.database.DimuscoDatabase
import com.bytepace.dimusco.databinding.ActivityScoreBinding
import com.bytepace.dimusco.helper.GlobalFunctions
import com.bytepace.dimusco.helper.GlobalVariables
import com.bytepace.dimusco.ui.editor.adapter.ColorsAdapter
import com.bytepace.dimusco.ui.editor.adapter.SymbolsAdapter
import com.bytepace.dimusco.ui.score.PageAdapter
import com.bytepace.dimusco.ui.score.selector.adapter.PageItem
import com.bytepace.dimusco.ui.settings.main.SettingsActivity
import com.bytepace.dimusco.utils.NAV_ARG_PAGE
import com.bytepace.dimusco.utils.NAV_ARG_SCORE_AID
import com.bytepace.dimusco.utils.NAV_ARG_SCORE_ID
import com.bytepace.dimusco.utils.list.ColumnLinearLayoutManager
import com.bytepace.dimusco.utils.list.GridPagerSnapHelper
import com.bytepace.dimusco.views.ZoomView
import com.gmail.samehadar.iosdialog.IOSDialog
import kotlinx.android.synthetic.main.activity_score.*
import kotlinx.android.synthetic.main.activity_score.view.*
import kotlinx.android.synthetic.main.alert_marker.view.*
import kotlinx.android.synthetic.main.layout_header.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.sample
import timber.log.Timber
import kotlin.coroutines.CoroutineContext
import kotlin.math.pow

@Suppress("DEPRECATION")
class ScoreActivity :AppCompatActivity(),
    CoroutineScope{

    private var selectedHIndex = 0

    private var job: Job = SupervisorJob()

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Timber.e(exception)
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main.immediate + job + exceptionHandler

    enum class HIndex{
        SEDIT, SERASE, SADDMAKER, SFONT, SLASSO, SUNSET
    }

    companion object{
        const val KEY_SCORE_TITLE = "scoreTitle"
        const val KEY_SCORE_PAGES_COUNT = "scorePageCount"
        lateinit var scoreSymbolsAdapter: PersonalSymbolsAdapter
        lateinit var scoreColorsAdapter: PersonalColorsAdapter
        lateinit var scorePagesAdapter: ScorePagesAdapter
        lateinit var headerScore: View
        var currentSymbol: SymbolModel? = null
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            setCurrentPage((recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition())
        }
    }

    private val screenOrientation: Int?
        get() = resources?.configuration?.orientation

    var score: LiveData<Score>? = null

    private val scoreRepository by lazy { ScoreRepository.get() }
    private val settingsRepository by lazy { SettingsRepository.get() }
    private val markerRepository by lazy { MarkerRepository.get() }

//    var markers: LiveData<List<Marker>>
    val leftMarkers: LiveData<List<Marker>>
        get() = mutableLeftMarkers
    val rightMarkers: LiveData<List<Marker>>
        get() = mutableRightMarkers

    private val mutableLeftMarkers = MutableLiveData<List<Marker>>()
    private val mutableRightMarkers = MutableLiveData<List<Marker>>()

    private val thumbnails = mutableListOf<PageWithLayers>()

    private val markerObserver = Observer<List<Marker>> { filterMarkers(it) }
    private val thumbnailsObserver = Observer<List<PageWithLayers>> { setThumbnails(it) }

    private lateinit var sid: String
    private lateinit var aid: String
    private lateinit var scoreTitle: String
    private var nop: Int = 0
    private val pageFileManager = PageFileManager(DimuscoApp.getContext())
    private val pagesDao by lazy { DimuscoDatabase.getInstance().pageDao() }
    private var pageItemsLiveData: LiveData<List<PageItem>>? = null
    private var pageItemsMiniData: LiveData<List<PageMiniModel>>? = null
    private val pagesAdapter = PageAdapter()
    private lateinit var snapHelper: GridPagerSnapHelper

    private lateinit var pageImage: ImageView
    private var iosDialog: IOSDialog? = null

    val currentPage = object : ObservableInt() {
        override fun set(value: Int) {
            super.set(value)
//            if (!markers.value.isNullOrEmpty()) {
//                filterMarkers(markers.value)
//            }
        }
    }

    val chosenPages: List<Int>?
        get() {
            return getChosenPagesList()
        }

    /** CustomView */
    private lateinit var mCustomView: ZoomView

    private lateinit var binding: ActivityScoreBinding
    private lateinit var viewModel: ScoreViewModel
    private lateinit var symbolsAdapter: SymbolsAdapter
    private lateinit var colorsAdapter: ColorsAdapter

    private val sizeSeekerListener = object : SeekBar.OnSeekBarChangeListener {
        var isChanging = false
        var scale = 1f

        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            if (fromUser) {
                if (!isChanging) {
//                    binding.drawingView.getSelectedPictureAsEditorAction()?.let {
//                        viewModel.onEditorAction(it)
//                    }
                }
                val newScale = 2f.pow(progress / 50f - 1)
//                binding.drawingView.scaleSelectedPicture(newScale / scale)
                scale = newScale
                isChanging = true
            }
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            scale = 1f
            seekBar?.progress = 50
            isChanging = false
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {}
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this, ScoreViewModelFactory(
            intent.extras?.getString(NAV_ARG_SCORE_ID) ?: "",
            intent.extras?.getString(NAV_ARG_SCORE_AID) ?: ""
        )).get(ScoreViewModel::class.java)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_score)
        setContentView(binding.root)

        setViewModelObservers()
        initAdapters()

        binding.apply {
            lifecycleOwner = this@ScoreActivity
            viewModel = this@ScoreActivity.viewModel
            listSymbols.adapter = symbolsAdapter
            listColors.adapter = colorsAdapter
//            drawingView.callback = drawingViewCallback
            seekerSize.setOnSeekBarChangeListener(sizeSeekerListener)
//            seekerLine.setOnSeekBarChangeListener(getSeekBarListener {
//                this@ScoreActivity.viewModel.lineThicknessProgress.set(it)
//            })
//            seekerTransparency.setOnSeekBarChangeListener(getSeekBarListener {
//                this@ScoreActivity.viewModel.transparencyProgress.set(it)
//            })
        }

        GlobalVariables.cvScreenOrientation = screenOrientation!!

        iosDialog = IOSDialog.Builder(this)
                .setCancelable(false)
                .setSpinnerClockwise(false)
                .setMessageContentGravity(Gravity.END)
                .build()
        init()

        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // In landscape
            btn_1.visibility = View.GONE
            btn_2.visibility = View.VISIBLE
            btn_3.visibility = View.VISIBLE
            svButtons.visibility = View.VISIBLE

            binding.listSymbols.visibility = View.VISIBLE
            binding.listColors.visibility = View.VISIBLE
//            seekbar_rtop.visibility = View.VISIBLE
//            cl_score_colors.visibility = View.VISIBLE
//            seekbar_rbottom.visibility = View.VISIBLE

        } else {
            // In portrait
            btn_1.visibility = View.GONE
            btn_2.visibility = View.GONE
            btn_3.visibility = View.GONE
            svButtons.visibility = View.GONE

            binding.listSymbols.visibility = View.GONE
            binding.listColors.visibility = View.GONE
//            seekbar_rtop.visibility = View.GONE
//            cl_score_colors.visibility = View.GONE
//            seekbar_rbottom.visibility = View.GONE
        }

        GlobalFunctions.initSymbols(this)
        GlobalFunctions.initColors()
        GlobalVariables.symbolActivity = 0

        /**
         * header
         */

        btn_back.setOnClickListener {
            onBackPressed()
        }
        btn_0.visibility = View.VISIBLE
        btn_0.setOnClickListener { onClickSettings() }

        text_title.text = scoreTitle
        setTitlePage()

        btnSEdit.setOnClickListener { onSEdit() }
        btnSErase.setOnClickListener { onSErase() }
        btnSAddMarker.setOnClickListener { onSAddMarker() }
        btnSFont.setOnClickListener { onSFont() }
        btnSLasso.setOnClickListener { onSLasso() }

//        /*** left symbols */
//        scoreSymbolsAdapter = PersonalSymbolsAdapter(this, GlobalVariables.symbols){ item: SymbolModel ->
//            setCurrentSymbol(item)
//        }
//        left_symbols.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
//        left_symbols.adapter =
//            scoreSymbolsAdapter
//
//        /*** right score colors */
//        scoreColorsAdapter = PersonalColorsAdapter(this, GlobalVariables.colors){ item: ColorModel ->
//            setCurrentColor(item)
//        }
//        right_score_colors.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
//        right_score_colors.adapter =
//            scoreColorsAdapter
    }

    private fun setViewModelObservers() {
        viewModel.apply {
            onGetSettings.observe(this@ScoreActivity, Observer { onGetSettings() })
//            onSaveChanges.observe(this@ScoreActivity, Observer {
//                viewModel.saveChanges(binding.drawingView.bitmap)
//            })
//            showSaveDialog.observe(this@ScoreActivity, Observer { showSaveDialog() })
//            deletePicture.observe(this@ScoreActivity, Observer {
//                binding.drawingView.deleteSelectedPicture()
//            })
//            onPicturesChanged.observe(this@ScoreActivity, Observer {
//                binding.drawingView.invalidate()
//            })
//            selectedMode.observe(this@ScoreActivity, Observer { binding.drawingView.mode = it })
            selectedSymbolIndex.observe(this@ScoreActivity, Observer {
                symbolsAdapter.setSelectedItem(it)
            })
//            selectedSymbol.observe(this@ScoreActivity, Observer { binding.drawingView.symbol = it })
            selectedColor.observe(this@ScoreActivity, Observer {
                colorsAdapter.setSelectedItem(it)
            })
//            selectedSymbol.observe { binding.drawingView.symbol = it }
            selectedColor.observe(this@ScoreActivity, Observer {
                colorsAdapter.setSelectedItem(it)
            })
//            selectedColorValue.observe { binding.drawingView.color = it }
//            transparency.observe { binding.drawingView.opacity = it }
//            lineThickness.observe { binding.drawingView.brushScale = it }
//            eraserThickness.observe { binding.drawingView.eraserScale = it }
//            fontSize.observe { binding.drawingView.fontSize = it }
//            bottomLayers.observe { binding.pageBottom.setImageBitmap(it) }
//            topLayers.observe { binding.pageTop.setImageBitmap(it) }
//            activeLayer.observe { layer -> layer?.layer.let { binding.drawingView.layer = it } }
//            pictures.observe { setDrawingViewPictures(it) }
//            onClickBack.observe(this@ScoreActivity, Observer { hideKeyboard() })
        }
    }

    private fun initAdapters() {
        symbolsAdapter = SymbolsAdapter(viewModel).apply {
            setItems(viewModel.symbols)
            setSelectedItem(viewModel.selectedSymbolIndex.value)
        }
        colorsAdapter = ColorsAdapter(viewModel).apply {
            setItems(viewModel.colors)
            setSelectedItem(viewModel.selectedColor.value)
        }
    }

    private fun onGetSettings() {
        symbolsAdapter.setItems(viewModel.symbols)
        colorsAdapter.setItems(viewModel.colors)
    }

    fun setCurrentSymbol(item: SymbolModel){
        currentSymbol = item
        GlobalVariables.symbol = item.name!!
        GlobalVariables.mStatus = false
    }

    fun setCurrentColor(item: ColorModel){
        mCustomView.swapColor(item.value)
    }

    fun setTitlePage(){
        tv_page_count.visibility = View.VISIBLE
        tv_page_count.text = (this.currentPage.get()+1).toString() + "/${nop}"
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onRestoreInstanceState(outState)
        outState.putInt(NAV_ARG_PAGE, currentPage.get())
//        if (::binding.isInitialized) {
//            outState.putBoolean(NAV_ARG_NEED_SEEK_BAR, seek_bar.visibility == View.VISIBLE)
//        }
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState.containsKey(NAV_ARG_PAGE)) {
            val pageNumber = savedInstanceState.getInt(NAV_ARG_PAGE)
            setCurrentPage(pageNumber)
            list_pages.scrollToPosition(pageNumber)
        }
//        if (savedInstanceState?.getBoolean(NAV_ARG_NEED_SEEK_BAR) == true) {
//            onCenterAreaClick()
//        }
    }

    fun init(){

//        score = scoreRepository.getScore(aid)
//        markers = markerRepository.getMarkers(sid)
//        markers.observeForever(markerObserver)

        sid = intent.extras?.getString(NAV_ARG_SCORE_ID) ?: ""
        aid = intent.extras?.getString(NAV_ARG_SCORE_AID) ?: ""
        scoreTitle = intent.extras?.getString(KEY_SCORE_TITLE) ?: ""
        nop = intent.extras?.getInt(KEY_SCORE_PAGES_COUNT) ?: 0
        if (sid == "" || aid == "") onBackPressed()
        pageItemsLiveData = receivePages(sid, aid)
        pageItemsMiniData = receiveMiniPages(sid, aid)

        headerScore = findViewById<View>(R.id.header)

        pageItemsLiveData?.observe(this, Observer(::onPagesReady))
        pageItemsMiniData?.observe(this, Observer(::onMiniPagesReady))

        /** Canvas CustomView */
//        mCustomView = findViewById(R.id.cv_custom_view)

        val isScrollHorizontal = Transformations.map(settingsRepository.settings) {
            it.pageScrollingHorizontal
        }

        snapHelper = getSnapHelper(isScrollHorizontal.value ?: false)
        snapHelper.attachToRecyclerView(list_pages)

        scorePagesAdapter = ScorePagesAdapter(this, GlobalVariables.miniPages)
        list_pages.addOnScrollListener(scrollListener)
        list_pages.layoutManager = LinearLayoutManager(this, getListOrientation(true), false)
        list_pages.adapter =
            scorePagesAdapter

        seek_bar.setOnSeekBarChangeListener(SeekBarChangeListener())
        currentPage.addOnPropertyChangedCallback( OnCurrentPageChangedCallback() )

        pageImage = findViewById(R.id.page_image)
    }

    private fun onMiniPagesReady(list: List<PageMiniModel>) {
        iosDialog?.dismiss()
        GlobalVariables.miniPages.clear()
        GlobalVariables.miniPages.addAll(list)
        seek_bar.max = list.size - 1
        scorePagesAdapter.notifyDataSetChanged()
    }

    private fun onPagesReady(list: List<PageItem>) {
        iosDialog?.dismiss()
        GlobalVariables.scorepages.clear()
        GlobalVariables.scorepages.addAll(list)
        pagesAdapter.items = list
        seek_bar.max = list.size - 1
    }

    private fun getSnapHelper(isHorizontal: Boolean): GridPagerSnapHelper {
        return GridPagerSnapHelper(getSpanCount(isHorizontal))
    }

    private fun getSpanCount(isHorizontal: Boolean): Int {
//        return if (screenOrientation == Configuration.ORIENTATION_LANDSCAPE && !isHorizontal) 2 else 1
        return 1
    }

    fun setCurrentPage(currentPage: Int) {
        this.currentPage.set(chosenPages?.getOrNull(currentPage)?.minus(1) ?: currentPage)
        setTitlePage()
    }

    fun receiveMiniPages(sid: String, aid: String): LiveData<List<PageMiniModel>> {
        iosDialog?.show()
        return pagesDao.retrieveScorePages(aid)
                .sample(50)
                .catch {
                    Timber.e(it)
                }
                .map { list ->
                    list.map {
                        PageMiniModel( sid, it)
                    }
                }
                .flowOn(Dispatchers.IO)
                .asLiveData(coroutineContext)
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
                        val fileUrl = pageFileManager.cacheOriginalImage(it, true)
                        PageItem(
                                it.page.id,
                                it.page.aid,
                                sid,
                                fileUrl,
                                it.hashCode()
                        )
                    }
                }
                .flowOn(Dispatchers.IO)
                .asLiveData(coroutineContext)
    }

    private fun onSEdit(){
        settingHIndex(HIndex.SEDIT)
    }

    private fun onSErase(){
        settingHIndex(HIndex.SERASE)
    }

    private fun onSAddMarker(){
        settingHIndex(HIndex.SADDMAKER)


        val mDialogView = LayoutInflater.from(this).inflate(R.layout.alert_marker, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
        val  mAlertDialog = mBuilder.show()

        mDialogView.btn_middle.visibility = View.GONE

        mDialogView.btn_top.setOnClickListener {
            mAlertDialog?.dismiss()
        }
        mDialogView.btn_bottom.setOnClickListener {
            mAlertDialog?.dismiss()
        }
    }

    private fun onSFont(){
        settingHIndex(HIndex.SFONT)
    }

    private fun onSLasso(){
        settingHIndex(HIndex.SLASSO)
    }

    private fun settingHIndex(type: HIndex){
        val border = resources.getDrawable(R.drawable.ic_header_border)
        val noBorder = resources.getDrawable(R.drawable.ic_header_no_border)
        btnSEdit.background = if(type == HIndex.SEDIT) border else noBorder
        btnSErase.background = if(type == HIndex.SERASE) border else noBorder
        btnSAddMarker.background = if(type == HIndex.SADDMAKER) border else noBorder
        btnSFont.background = if(type == HIndex.SFONT) border else noBorder
        btnSLasso.background = if(type == HIndex.SLASSO) border else noBorder
        if(type == HIndex.SUNSET){
            btnSEdit.background = noBorder
            btnSErase.background = noBorder
            btnSAddMarker.background = noBorder
            btnSFont.background = noBorder
            btnSLasso.background = noBorder
        }
    }

    private fun getChosenPagesList(): List<Int>? {
        val pageCount = score?.value?.pages?.size ?: return emptyList()
        return scoreRepository.currentScorePageSequence?.map { page -> if (page > pageCount) pageCount else page }
                ?.map { page -> if (page <= 0) 1 else page }
    }

    private fun getLayoutManager(isHorizontal: Boolean = false): RecyclerView.LayoutManager {
        return if (screenOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (isHorizontal) {
                ColumnLinearLayoutManager(this, getListOrientation(isHorizontal), false)
            } else {
                GridLayoutManager(this, 2, getListOrientation(isHorizontal), false)
            }
        } else {
            LinearLayoutManager(this, getListOrientation(isHorizontal), false)
        }
    }

    inner class SeekBarChangeListener :
            SeekBar.OnSeekBarChangeListener {   //On controlling the seekbar
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            if (fromUser) {
                showPage(progress)
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
            progress_loading.visibility = View.VISIBLE
            pageImage.visibility = View.VISIBLE
            page_number.visibility = View.VISIBLE
            showPage(seekBar?.progress ?: 0)
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            val currentPage = seekBar?.progress ?: 0
            pageImage.visibility = View.GONE
            progress_loading.visibility = View.GONE
            page_number.visibility = View.GONE
            list_pages.scrollToPosition(currentPage)
            setCurrentPage(currentPage)
        }

        private fun showPage(number: Int) {
            page_number.text = (number + 1).toString()
//            val item = pagesAdapter.items[number]
            var item: PageItem
            var pageUrl: String
            if(GlobalVariables.scorepages.size > 0){
                item = GlobalVariables.scorepages[number]
                pageUrl = item.pageUrl
            } else {
                pageUrl = pageFileManager.cacheOriginalImage(GlobalVariables.miniPages[number].complexPage!!, true)
            }
            progress_loading.visibility = View.GONE
            pageImage.visibility = View.VISIBLE
            Glide.with(pageImage)
                    .load(pageUrl)
                    .into(pageImage)
        }
    }

    inner class OnCurrentPageChangedCallback : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            seek_bar.progress = currentPage.get()
        }
    }

    private fun getListOrientation(isHorizontal: Boolean): Int {
        return when (isHorizontal) {
            true -> RecyclerView.HORIZONTAL
            else -> RecyclerView.VERTICAL
        }
    }

    private fun filterMarkers(markers: List<Marker>?) {
        mutableLeftMarkers.value = markers?.filter { it.pageTo <= currentPage.get() + 1 }
        mutableRightMarkers.value = markers?.filter { it.pageTo > currentPage.get() + 1 }
    }

    private fun setThumbnails(items: List<PageWithLayers>) {
        val sortedItems = items.sortedBy { it.page?.pageNumber }

        thumbnails.clear()
        thumbnails.addAll(sortedItems)
    }

    private fun onClickSettings(){
        val settingsIntent = Intent(this, SettingsActivity::class.java)
        settingsIntent.putExtra(NAV_ARG_SCORE_AID, aid)
        startActivity(settingsIntent)
    }

    private fun nfStartActivity(mActivity: Activity){
        startActivity(Intent(this, mActivity::class.java))
    }

    private fun getSeekBarListener(onProgressChanged: (Int) -> Unit): SeekBar.OnSeekBarChangeListener {
        return object : SeekBar.OnSeekBarChangeListener {
            var isChanging = false

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    if (!isChanging) {
//                        binding.drawingView.getSelectedPictureAsEditorAction()?.let {
//                            viewModel.onEditorAction(it)
//                        }
                    }
                    isChanging = true
                }
                onProgressChanged(progress)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                isChanging = false
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
        }
    }

    fun <T> LiveData<T>.observe(callback: (result: T) -> Unit) {
        observe(this@ScoreActivity, Observer {
            it?.let { callback.invoke(it) }
        })
    }

    override fun onBackPressed() {
        finish()
    }
}