package com.bytepace.dimusco.ui.settings.layers

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bytepace.dimusco.R
import com.bytepace.dimusco.data.model.Layer
import com.bytepace.dimusco.databinding.ListItemLayerBinding
import com.bytepace.dimusco.utils.LAYER_TYPE_READ_ONLY
import com.bytepace.dimusco.utils.LAYER_TYPE_READ_WRITE

class LayersAdapter(
    lifecycleOwner: LifecycleOwner,
    private val viewModel: SettingsLayersViewModel,
    private val touchHelper: ItemTouchHelper
) : RecyclerView.Adapter<LayersAdapter.ViewHolder>() {

    private val layers = mutableListOf<Layer>()
    private val diffCallback = LayersDiffCallback()

    init {
        viewModel.layers.observe(lifecycleOwner, Observer { setItems(it) })
    }

    private fun setItems(items: List<Layer>) {
        diffCallback.setItems(layers, items)
        val result = DiffUtil.calculateDiff(diffCallback)

        layers.clear()
        layers.addAll(items)

        result.dispatchUpdatesTo(this)
    }

    fun swapItems(from: Int, to: Int) {
        if (layers[to].type == LAYER_TYPE_READ_WRITE) {
            layers[from] = layers[to].also { layers[to] = layers[from] }
            notifyItemMoved(from, to)
        }
    }

    fun onOrderChanged() {
        viewModel.updateLayersOrder(layers)
    }

    override fun getItemCount(): Int {
        return layers.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.list_item_layer, parent, false
            )
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val layer = layers[position]
        holder.binding.apply {
            name = layer.name

            isReadOnly = layer.type == LAYER_TYPE_READ_ONLY
            btnEdit.setOnClickListener { viewModel.onClickEdit(layer) }

            isActive = layer.isActive
            btnActive.setOnClickListener { viewModel.setActive(layer) }

            isVisible = layer.isVisible
            btnVisibility.setOnClickListener { viewModel.toggleVisibility(layer) }

            btnDelete.setOnClickListener { viewModel.onClickDelete(layer) }

            if (layer.type == LAYER_TYPE_READ_WRITE) {
                btnDrag.setOnTouchListener { _, event ->
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        touchHelper.startDrag(holder)
                    }
                    false
                }
            }
            executePendingBindings()
        }
    }

    class ViewHolder(val binding: ListItemLayerBinding) : RecyclerView.ViewHolder(binding.root)

    private class LayersDiffCallback : DiffUtil.Callback() {
        private var oldItems: List<Layer> = listOf()
        private var newItems: List<Layer> = listOf()

        fun setItems(oldItems: List<Layer>, newItems: List<Layer>) {
            this.oldItems = oldItems
            this.newItems = newItems
        }

        override fun getOldListSize(): Int {
            return oldItems.size
        }

        override fun getNewListSize(): Int {
            return newItems.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItems[oldItemPosition].lid == newItems[newItemPosition].lid
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItems[oldItemPosition] == newItems[newItemPosition]
        }
    }
}