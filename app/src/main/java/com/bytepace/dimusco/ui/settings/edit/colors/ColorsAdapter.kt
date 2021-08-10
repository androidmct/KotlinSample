package com.bytepace.dimusco.ui.settings.edit.colors

import android.content.ClipData
import android.os.Build
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.recyclerview.widget.RecyclerView
import com.bytepace.dimusco.R
import com.bytepace.dimusco.data.model.Color
import com.bytepace.dimusco.databinding.ListItemColorNewBinding
import com.bytepace.dimusco.utils.DEFAULT_COLORS
import com.bytepace.dimusco.utils.getOpaqueColor
import com.bytepace.dimusco.utils.list.SelectableDiffCallback
import com.bytepace.dimusco.utils.list.SelectableItem

class ColorsAdapter(
    private val selectable: Boolean,
    lifecycleOwner: LifecycleOwner? = null,
    private val viewModel: SettingsColorsViewModel? = null
) : RecyclerView.Adapter<ColorsAdapter.ViewHolder>() {

    private val diffCallback = SelectableDiffCallback()
    private val longClickListener = View.OnLongClickListener { startDrag(it) }
    private var selectionCallback: ((Int) -> Unit)? = null
    private var listUpdateCallback = MyListUpdateCallback()
    private var items = mutableListOf<SelectableItem>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    init {
        lifecycleOwner?.let {
            viewModel?.settings?.observe(lifecycleOwner, Observer { updateItems(it.colors) })
        }
        if (viewModel == null) {
            items.addAll(DEFAULT_COLORS.map { SelectableItem(it) })
        }
    }

    fun setSelectionCallback(callback: (Int) -> Unit) {
        this.selectionCallback = callback
    }

    fun setSelectedItemColor(value: Int) {
        val index = items.indexOfFirst { it.isSelected }
        if (index >= 0) {
            val color = Color(value)
            items[index].value = color
            viewModel?.updateSelectedColor(color)
            viewModel?.updateColor(index, color)
            notifyItemChanged(index)
        }
    }

    private fun updateItems(items: List<Color>) {
        val newItems = items.map { SelectableItem(it) }
        newItems.find { (it.value as Color).value == viewModel?.settings?.value?.selectedColor }
            ?.let {
                it.isSelected = true
            }

        diffCallback.setItems(this.items, newItems)
        val result = DiffUtil.calculateDiff(diffCallback)

        this.items.clear()
        this.items.addAll(newItems)

        result.dispatchUpdatesTo(listUpdateCallback)
    }

    private fun startDrag(view: View): Boolean {
        val data = ClipData.newPlainText("", "")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            view.startDragAndDrop(data, View.DragShadowBuilder(view), view, 0)
        } else {
            @Suppress("DEPRECATION")
            view.startDrag(data, View.DragShadowBuilder(view), view, 0)
        }
        return true
    }

    private fun selectItem(position: Int) {
        val selectedIndex = items.indexOfFirst { it.isSelected }
        if (selectedIndex == position) return

        val color = (items[position].value as Color)

        viewModel?.updateSelectedColor(color)
        selectionCallback?.invoke(color.value)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(binding = DataBindingUtil.inflate< ListItemColorNewBinding>(
                LayoutInflater.from(parent.context), R.layout.list_item_color_new, parent, false
        ))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.apply {
            itemColor.setBackgroundColor(getOpaqueColor((item.value as Color).value))
            root.setOnLongClickListener(longClickListener)
            if (selectable) {
                root.setOnClickListener { selectItem(holder.absoluteAdapterPosition) }
            }
            isSelected = item.isSelected
            executePendingBindings()
        }
    }

    class ViewHolder(val binding: ListItemColorNewBinding) : RecyclerView.ViewHolder(binding.root)

    class DragListener : View.OnDragListener {

        private var isDropped = false

        override fun onDrag(v: View?, event: DragEvent?): Boolean {
            v as RecyclerView

            val draggedView = event?.localState as View
            val sourceList = draggedView.parent as RecyclerView
            val sourceAdapter = sourceList.adapter as ColorsAdapter
            val sourcePosition = sourceList.getChildAdapterPosition(draggedView)
            val source = sourceAdapter.items[sourcePosition]
            val targetAdapter = v.adapter as ColorsAdapter

            when (event.action) {
                DragEvent.ACTION_DROP -> {
                    isDropped = true

                    val targetView = v.findChildViewUnder(event.x, event.y)
                    var targetPosition = targetView?.let {
                        v.getChildAdapterPosition(it)
                    }
                    if (targetAdapter == sourceAdapter) {
                        targetPosition = targetPosition ?: targetAdapter.itemCount - 1
                        targetAdapter.items.removeAt(sourcePosition)
                        targetAdapter.items.add(targetPosition, source)
                        targetAdapter.notifyItemMoved(sourcePosition, targetPosition)
                    } else {
                        targetPosition = targetPosition ?: targetAdapter.itemCount
                        targetAdapter.items.add(targetPosition, source.copy())
                        targetAdapter.notifyItemInserted(targetPosition)
                    }
                    targetAdapter.apply {
                        viewModel?.updateColors(items.map {
                            it.value as Color
                        })
                    }
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    if (targetAdapter == sourceAdapter && !isDropped) {
                        targetAdapter.apply {
                            items.removeAt(sourcePosition)
                            notifyItemRemoved(sourcePosition)
                            viewModel?.updateColors(items.map {
                                it.value as Color
                            })
                        }
                    }
                    isDropped = false
                }
            }
            return true
        }
    }

    inner class MyListUpdateCallback : ListUpdateCallback {
        override fun onChanged(position: Int, count: Int, payload: Any?) {
            notifyItemRangeChanged(position, count, Unit)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            notifyItemMoved(fromPosition, toPosition)
        }

        override fun onInserted(position: Int, count: Int) {
            notifyItemRangeChanged(position, count, Unit)
        }

        override fun onRemoved(position: Int, count: Int) {
            notifyItemRangeChanged(position, count, Unit)
        }
    }
}