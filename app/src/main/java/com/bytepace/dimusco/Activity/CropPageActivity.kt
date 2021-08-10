package com.bytepace.dimusco.Activity

import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.bytepace.dimusco.DimuscoApp
import com.bytepace.dimusco.R
import com.bytepace.dimusco.data.manager.PageFileManager
import com.bytepace.dimusco.data.mapper.PageWindowMapper
import com.bytepace.dimusco.data.repository.SyncRepository
import com.bytepace.dimusco.data.source.local.database.DimuscoDatabase
import com.bytepace.dimusco.data.source.local.model.ScorePageWindowDB
import com.canhub.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_crop_image.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

@Suppress("DEPRECATION")
class CropPageActivity : AppCompatActivity(),
        CropImageView.OnSetCropWindowChangeListener,
        CropImageView.OnSetImageUriCompleteListener,
        CoroutineScope {

    private var job: Job = SupervisorJob()

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Timber.e(exception)
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main.immediate + job + exceptionHandler

    companion object{
        const val KEY_SCORE_ID = "score.id"
        const val KEY_PAGE_ID = "page.id"

        fun arguments(scoreId: String, pageId: String): Bundle {
            return bundleOf(
                    KEY_SCORE_ID to scoreId,
                    KEY_PAGE_ID to pageId
            )
        }
    }

    private val _pageFile: MutableStateFlow<Pair<String, String>?> = MutableStateFlow(null)

    private val pageFileManager = PageFileManager(DimuscoApp.getContext())
    private val offsetsDao by lazy { DimuscoDatabase.getInstance().pageWindowsDao() }
    private val pageDao by lazy { DimuscoDatabase.getInstance().pageDao() }
    private val syncRepository: SyncRepository by lazy { SyncRepository.get() }

    val page
        get() = _pageFile.map { it?.second }
                .filterNotNull()
                .map {
                    val page = pageDao.subscribeForPage(it).first() ?: return@map null
                    pageFileManager.cacheOriginalImage(page, false)
                }
                .flowOn(Dispatchers.IO)
                .filterNotNull()
                .asLiveData(coroutineContext)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop_image)

        val scoreId = intent.extras?.getString(KEY_SCORE_ID) ?: ""
        val pageId = intent.extras?.getString(KEY_PAGE_ID) ?: ""
        if(scoreId == "" || pageId == "") onBackPressed()
        setupPageId(scoreId, pageId)

        page.observe(this, Observer(::onPageReady))
        cropImageView.setOnCropWindowChangedListener(this)
        cropImageView.setOnSetImageUriCompleteListener(this)

//        val orientation = resources.configuration.orientation
//        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            // In landscape
//            btn_crop_cancel.text = ""
//            btn_crop_cancel.background = resources.getDrawable(R.drawable.ic_close_solid)
//        } else {
//            // In portrait
//            btn_crop_cancel.text = resources.getString(R.string.global_cancel)
//            btn_crop_done.text = resources.getString(R.string.global_done)
//        }

        btn_crop_restore.setOnClickListener { restoreImage() }
        btn_crop_cancel.setOnClickListener { onBackPressed() }
        btn_crop_done.setOnClickListener { onDoneClick() }
    }

    fun setupPageId(sid: String, paid: String) {
        _pageFile.value = sid to paid
    }

    private fun onPageReady(url: String) {
        cropImageView.setImageUriAsync(Uri.parse(url))
    }

    private fun onDoneClick() {
        val rect = cropImageView.cropRect ?: return
        val bitmapRect = cropImageView.wholeImageRect ?: return
        val widthCoefficient = rect.width() / bitmapRect.width().toFloat()
        val heightCoefficient = rect.height() / bitmapRect.height().toFloat()
        val xCoefficient = rect.left / bitmapRect.width().toFloat()
        val yCoefficient = rect.top / bitmapRect.height().toFloat()
        save(xCoefficient, yCoefficient, widthCoefficient, heightCoefficient)
    }

    fun save(x: Float, y: Float, width: Float, height: Float) {
        _pageFile.filterNotNull().map { (sid, paid) ->
            val entity = offsetsDao.selectByPageAndScoreId(paid, sid).firstOrNull()
            when (entity) {
                null -> ScorePageWindowDB(
                        width,
                        height,
                        x,
                        y,
                        paid,
                        sid
                )
                else -> entity.copy(
                        widthCoefficient = width,
                        heightCoefficient = height,
                        xCoefficient = x,
                        yCoefficient = y
                )
            }
        }.flowOn(Dispatchers.IO).onEach {
            offsetsDao.insert(it)
            synchronizePageWindows()
        }.flowOn(Dispatchers.IO).catch {
            Timber.e(it)
        }.onEach {
            onBackPressed()
        }.launchIn(this)
    }

    suspend fun synchronizePageWindows() {
        val windows = PageWindowMapper.mapLocal(offsetsDao.selectAll())
        syncRepository.updateWindows(windows)
    }

    private fun restoreImage() = with(cropImageView) {
        cropRect = wholeImageRect
        resetCropRect()
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onCropWindowChanged() {

    }

    override fun onSetImageUriComplete(view: CropImageView?, uri: Uri?, error: Exception?) {

    }
}