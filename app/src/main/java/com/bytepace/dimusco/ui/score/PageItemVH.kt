package com.bytepace.dimusco.ui.score

import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.signature.ObjectKey
import com.bytepace.dimusco.ui.editor.ScoreActivity
import com.bytepace.dimusco.databinding.ListItemPageV2Binding
import com.bytepace.dimusco.ui.components.list.BaseViewHolder
import com.bytepace.dimusco.ui.components.list.OnItemClickListener
import com.bytepace.dimusco.ui.score.selector.adapter.PageItem
import timber.log.Timber

class PageItemVH(view: View) : BaseViewHolder<PageItem>(view) {

    private var binding: ListItemPageV2Binding? = ListItemPageV2Binding.bind(view)

    override fun bindView(item: PageItem, itemClickListener: OnItemClickListener<PageItem>?) {
        super.bindView(item, itemClickListener)
        val progressBar = binding?.progressLoading ?: return
        progressBar.isVisible = true
        val imageView = binding?.pageImage ?: return
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
                    progressBar.isVisible = false
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    progressBar.isVisible = false
                    return false
                }

            })
            .dontTransform()
            .dontAnimate()
            .into(imageView)

        imageView.setOnClickListener {
           if(ScoreActivity.headerScore.visibility == View.VISIBLE) ScoreActivity.headerScore.visibility = View.GONE
            else ScoreActivity.headerScore.visibility = View.VISIBLE
        }
    }

    override fun unbindView() {
        super.unbindView()
        val image = binding?.pageImage ?: return
        Glide.with(image).clear(image)
    }
}