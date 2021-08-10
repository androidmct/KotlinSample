package com.bytepace.dimusco.Adapter.scores

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.signature.ObjectKey
import com.bytepace.dimusco.ui.editor.ScoreActivity
import com.bytepace.dimusco.DimuscoApp
import com.bytepace.dimusco.Model.PageMiniModel
import com.bytepace.dimusco.R
import com.bytepace.dimusco.data.manager.PageFileManager
import com.bytepace.dimusco.ui.score.selector.adapter.PageItem
import kotlinx.android.synthetic.main.grid_item_page.view.*
import timber.log.Timber


class ScorePagerAdapter : RecyclerView.Adapter<ScorePagerAdapter.ScorePagerViewHolder>() {
    var list: List<PageMiniModel> = listOf()

    class ScorePagerViewHolder constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        private val pageFileManager = PageFileManager(DimuscoApp.getContext())

        constructor(parent: ViewGroup) :
                this(LayoutInflater.from(parent.context).inflate(R.layout.list_item_page_v2, parent, false))
        fun bind(item: PageMiniModel) {
            val imageView = itemView.page_image
            val pageUrl = pageFileManager.cacheOriginalImage(item.complexPage!!, true)
            Glide.with(imageView)
                .load(pageUrl)
                .signature(ObjectKey(PageItem(item.complexPage.page.id, item.complexPage.page.aid, item.scoreId!!, pageUrl, item.complexPage.hashCode())))
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Timber.e(e)
//                            progressBar.isVisible = false
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
//                            progressBar.isVisible = false
                        return false
                    }

                })
                .dontTransform()
                .dontAnimate()
                .into(imageView)

            imageView.setOnClickListener { onClickPage() }
        }

        fun onClickPage(){
            if(ScoreActivity.headerScore.visibility == View.VISIBLE){
                ScoreActivity.headerScore.visibility = View.GONE
            } else {
                ScoreActivity.headerScore.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScorePagerViewHolder {
        return ScorePagerViewHolder(parent)
    }
    override fun onBindViewHolder(holder: ScorePagerViewHolder, position: Int) {
        holder.bind(list[position])
    }
    fun setItem(list: List<PageMiniModel>) {
        this.list = list
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int = list.size
}