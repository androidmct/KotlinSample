package com.bytepace.dimusco.Adapter.colors

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
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
import com.bytepace.dimusco.Activity.CropPageActivity
import com.bytepace.dimusco.R
import com.bytepace.dimusco.ui.score.selector.adapter.PageItem
import kotlinx.android.synthetic.main.grid_item_page.view.*
import timber.log.Timber

@Suppress("DEPRECATION")
class PagesListAdapter(context: Context, pageList : ArrayList<PageItem>): RecyclerView.Adapter<PagesListAdapter.MyViewHolder>() {
    val mContext = context
    val mPageList = pageList

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("DefaultLocale", "UseCompatLoadingForDrawables")
        fun bind(item: PageItem, position: Int) {
            val imageView = itemView.page_image
            Glide.with(imageView)
                    .load(item.pageUrl)
                    .signature(ObjectKey(item))
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

            imageView.setOnClickListener { openPageCrop(item) }
        }

        fun openPageCrop(item: PageItem){
            val pageCropIntent = Intent(itemView.context, CropPageActivity::class.java)
            pageCropIntent.putExtra( CropPageActivity.KEY_PAGE_ID, item.pageId)
            pageCropIntent.putExtra( CropPageActivity.KEY_SCORE_ID, item.scoreId)
            itemView.context.startActivity( pageCropIntent )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagesListAdapter.MyViewHolder {
        var itemView = LayoutInflater.from(mContext).inflate(R.layout.grid_item_page, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return mPageList.size
    }

    override fun onBindViewHolder(holder: PagesListAdapter.MyViewHolder, position: Int) {
        holder.bind(mPageList[position], position)
    }
}