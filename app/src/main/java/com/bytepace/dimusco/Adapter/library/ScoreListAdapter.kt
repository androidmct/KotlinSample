package com.bytepace.dimusco.Adapter.library

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bytepace.dimusco.Activity.LibraryActivity
import com.bytepace.dimusco.Activity.PagesGridActivity
import com.bytepace.dimusco.ui.editor.ScoreActivity
import com.bytepace.dimusco.R
import com.bytepace.dimusco.data.model.Page
import com.bytepace.dimusco.data.model.Score
import com.bytepace.dimusco.helper.GlobalVariables
import com.bytepace.dimusco.utils.NAV_ARG_SCORE_AID
import com.bytepace.dimusco.utils.NAV_ARG_SCORE_ID
import com.gmail.samehadar.iosdialog.IOSDialog
import kotlinx.android.synthetic.main.alert_dialog.view.*
import kotlinx.android.synthetic.main.list_item_score.view.*
import kotlinx.coroutines.*
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

class ScoreListAdapter(context: Context, scoreList : ArrayList<Score>): RecyclerView.Adapter<ScoreListAdapter.MyViewHolder>() {
    val mContext = context
    val mScoreList = scoreList

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), CoroutineScope {

        companion object{
            const val TAG = "NotificationAdapter"
        }

        private var iosDialog: IOSDialog? = null

        init {
            iosDialog = IOSDialog.Builder(itemView.context)
                    .setCancelable(false)
                    .setSpinnerClockwise(false)
                    .setMessageContentGravity(Gravity.END)
                    .build()
        }


        @SuppressLint("DefaultLocale", "UseCompatLoadingForDrawables")
        fun bind(item: Score, position: Int) {
            itemView.nameTextView.text = item.name
            itemView.composerTextView.text = item.composer
            itemView.instrumentTextView.text = item.instrument
            itemView.editionTextView.text = item.edition
            itemView.percentageTextView.text = (item.downloadedPages * 100 / item.pageCount).toString() + "%"

            if(item.isDownloaded){
                itemView.progressFlow.visibility = View.INVISIBLE
                itemView.buttonsFlow.visibility = View.VISIBLE
                itemView.btnDelLoad.setImageDrawable(itemView.resources.getDrawable(R.drawable.ic_delete))
            }
            if(item.isDownloadingScoreInProgress){
                itemView.progressFlow.visibility = View.VISIBLE
                itemView.buttonsFlow.visibility = View.INVISIBLE
            }

            if(!item.isDownloaded && !item.isDownloadingScoreInProgress){
                itemView.progressFlow.visibility = View.INVISIBLE
                itemView.buttonsFlow.visibility = View.VISIBLE
                itemView.btnOpen.visibility = View.GONE
                itemView.btnSetting.visibility = View.GONE
                itemView.btnDelLoad.setImageDrawable(itemView.resources.getDrawable(R.drawable.ic_download))
            }

            itemView.btnDelLoad.setOnClickListener {
                if(item.isDownloaded){ onClickDel(item, position) }
                else { onClickDownload(item, position) }
            }

            itemView.btnSetting.setOnClickListener { setupScore(item) }
            itemView.btnOpen.setOnClickListener { openScorePage(item) }
        }

        fun setupScore(score: Score){
            val pageGridIntent = Intent(itemView.context, PagesGridActivity::class.java)
            pageGridIntent.putExtra(PagesGridActivity.KEY_SCORE_ID, score.sid)
            pageGridIntent.putExtra(PagesGridActivity.KEY_SCORE_AID, score.aid)
            GlobalVariables.pages.clear()
            itemView.context.startActivity(pageGridIntent)
        }

        fun openScorePage(score: Score){
            val scorePageIntent = Intent(itemView.context, ScoreActivity::class.java)
            scorePageIntent.putExtra(NAV_ARG_SCORE_ID, score.sid)
            scorePageIntent.putExtra(NAV_ARG_SCORE_AID, score.aid)
            scorePageIntent.putExtra(ScoreActivity.KEY_SCORE_TITLE, score.name)
            scorePageIntent.putExtra(ScoreActivity.KEY_SCORE_PAGES_COUNT, score.pageCount)
            GlobalVariables.scorepages.clear()
            itemView.context.startActivity(scorePageIntent)
        }

        private fun onClickDownload(item: Score, position: Int){
            GlobalScope.launch {
                saveScorePages(item)
            }

            LibraryActivity.scoreListAdapter.notifyDataSetChanged()
        }

        suspend fun saveScorePages(score: Score) {
            try {
                LibraryActivity.scoreRepository.setScoreIsDownloadInProgress(score.aid, true)
                val email = LibraryActivity.user?.email ?: throw IllegalStateException()
                val token = LibraryActivity.userRepository.getLocalUser(email)?.token ?: throw IllegalStateException()
                var scorePagesDownload: ArrayList<Page> = arrayListOf()
                for(page in score.pages){
                    if(!page.isDownloaded)
                        scorePagesDownload.add(page)
                }
                scorePagesDownload
                        .map { downloadPageAsync(it, token) }
                        .forEach { it.await() }
                LibraryActivity.scoreRepository.setScoreIsDownloadInProgress(score.aid, false)
            } catch (ex: Exception) {
                LibraryActivity._handledError.value = ex
            }
        }



        suspend fun downloadPageAsync(page: Page, token: String) = async(Dispatchers.IO) {
            val filename = page.id
            LibraryActivity.downloadPageManager.saveFile(filename, token)
            LibraryActivity.scoreRepository.setPageIsDownloaded(filename.replace(".png", ""))
        }



        private fun onClickDel(item: Score, position: Int){
            val mDialogView = LayoutInflater.from(itemView.context).inflate(R.layout.alert_dialog, null)
            //AlertDialogBuilder
            val mBuilder = AlertDialog.Builder(itemView.context)
                    .setView(mDialogView)
            val  mAlertDialog = mBuilder.show()
            mDialogView.text_message.text = itemView.resources.getString(R.string.remove_score)
            mDialogView.btn_positive.text = itemView.resources.getString(R.string.global_yes)
            mDialogView.btn_negative.text = itemView.resources.getString(R.string.global_no)


            mDialogView.btn_positive.setOnClickListener {
                GlobalScope.launch(Dispatchers.IO) {
                    LibraryActivity.scoreRepository.deletePages(
                            item.aid,
                            item.pages.map { it.id }
                    )
                }

                mAlertDialog?.dismiss()
            }

            mDialogView.btn_negative.setOnClickListener {
                mAlertDialog?.dismiss()
            }
        }

        private var job: Job = SupervisorJob()

        private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            Timber.e(exception)
        }

        override val coroutineContext: CoroutineContext
            get() = Dispatchers.Main.immediate + job + exceptionHandler
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreListAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(mContext).inflate(R.layout.list_item_score, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return mScoreList.size
    }

    override fun onBindViewHolder(holder: ScoreListAdapter.MyViewHolder, position: Int) {
        holder.bind(mScoreList[position], position)
    }


}