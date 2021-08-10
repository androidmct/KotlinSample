package com.bytepace.dimusco.Adapter.scores

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bytepace.dimusco.ui.editor.ScoreActivity
import com.bytepace.dimusco.DimuscoApp
import com.bytepace.dimusco.Model.PageMiniModel
import com.bytepace.dimusco.R
import com.bytepace.dimusco.data.manager.PageFileManager
import kotlinx.android.synthetic.main.grid_item_page.view.page_image
import kotlinx.android.synthetic.main.list_item_page_v2.view.*


@Suppress("DEPRECATION")
class ScorePagesAdapter(context: Context, pageList : ArrayList<PageMiniModel>): RecyclerView.Adapter<ScorePagesAdapter.MyViewHolder>() {
    val mContext = context
    val mPageList = pageList

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val pageFileManager = PageFileManager(DimuscoApp.getContext())

        @SuppressLint("DefaultLocale", "UseCompatLoadingForDrawables")
        fun bind(item: PageMiniModel, position: Int) {
            val imageView = itemView.page_image
            val zoomView = itemView.cv_page_image

            val pageUrl = pageFileManager.cacheOriginalImage(item.complexPage!!, true)

            zoomView.loadPageImageOnCanvas(pageUrl)

//            Glide.with(imageView)
//                    .load(pageUrl)
//                    .signature(ObjectKey(PageItem(item.complexPage.page.id, item.complexPage.page.aid, item.scoreId!!, pageUrl, item.complexPage.hashCode())))
//                    .listener(object : RequestListener<Drawable> {
//                        override fun onLoadFailed(
//                                e: GlideException?,
//                                model: Any?,
//                                target: Target<Drawable>?,
//                                isFirstResource: Boolean
//                        ): Boolean {
//                            Timber.e(e)
////                            progressBar.isVisible = false
//                            return false
//                        }
//
//                        override fun onResourceReady(
//                                resource: Drawable?,
//                                model: Any?,
//                                target: Target<Drawable>?,
//                                dataSource: DataSource?,
//                                isFirstResource: Boolean
//                        ): Boolean {
////                            progressBar.isVisible = false
//                            return false
//                        }
//
//                    })
//                    .dontTransform()
//                    .dontAnimate()
//                    .into(imageView)

            imageView.setOnClickListener { onClickPage() }
        }

        fun onClickPage(){
            val orientation = itemView.resources.configuration.orientation
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                if (ScoreActivity.headerScore.visibility == View.VISIBLE) {
                    ScoreActivity.headerScore.visibility = View.GONE
                } else {
                    ScoreActivity.headerScore.visibility = View.VISIBLE
                }
            }
        }

//        fun openPageCrop(item: PageItem) {
//            val pageCropIntent = Intent(itemView.context, CropPageActivity::class.java)
//            pageCropIntent.putExtra(CropPageActivity.KEY_PAGE_ID, item.pageId)
//            pageCropIntent.putExtra(CropPageActivity.KEY_SCORE_ID, item.scoreId)
//            itemView.context.startActivity(pageCropIntent)
//        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScorePagesAdapter.MyViewHolder {
        var itemView = LayoutInflater.from(mContext).inflate(R.layout.list_item_page_v2, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return mPageList.size
    }

    override fun onBindViewHolder(holder: ScorePagesAdapter.MyViewHolder, position: Int) {
        holder.bind(mPageList[position], position)
    }
}