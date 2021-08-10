package com.bytepace.dimusco.ui.settings.edit.symbols

import android.content.ClipData
import android.os.Build
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bytepace.dimusco.R
import com.bytepace.dimusco.data.model.Symbol
import com.bytepace.dimusco.databinding.ListItemSymbolNewBinding
import com.bytepace.dimusco.utils.DEFAULT_SYMBOLS
import com.bytepace.dimusco.utils.getSymbolImage
import com.bytepace.dimusco.utils.list.SelectableDiffCallback
import com.bytepace.dimusco.utils.list.SelectableItem

@Suppress("DEPRECATION")
class SymbolsAdapter(
    private val selectable: Boolean,
    lifecycleOwner: LifecycleOwner? = null,
    private val viewModel: SettingsSymbolsViewModel? = null
) : RecyclerView.Adapter<SymbolsAdapter.ViewHolder>() {

    private val diffCallback = SelectableDiffCallback()
    private val longClickListener = View.OnLongClickListener { startDrag(it) }
    private var items = mutableListOf<SelectableItem>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    init {
        lifecycleOwner?.let {
            viewModel?.settings?.observe(lifecycleOwner) { updateItems(it.symbols) }
        }
        if (viewModel == null) {
            items.addAll(DEFAULT_SYMBOLS.map { SelectableItem(it) })
        }
    }

    private fun updateItems(items: List<Symbol>) {
        val newItems = items.map { SelectableItem(it) }
        var selectedIndex = this.items.indexOfFirst { it.isSelected }

        if (-1 == selectedIndex) {
            selectedIndex = viewModel?.selectedItem?.value ?: -1
        }

        diffCallback.setItems(this.items, newItems)
        val result = DiffUtil.calculateDiff(diffCallback)

        this.items.clear()
        this.items.addAll(newItems)

        result.dispatchUpdatesTo(this)
        if (selectedIndex >= 0 && selectedIndex < this.items.size) {
            selectItem(selectedIndex)
        } else {
            viewModel?.selectedItem?.value = null
        }
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

        if (selectedIndex >= 0) {
            items[selectedIndex].isSelected = false
            notifyItemChanged(selectedIndex, Unit)
        }
        items[position].isSelected = true
        notifyItemChanged(position, Unit)
        viewModel?.selectedItem?.value = position
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate<ListItemSymbolNewBinding>(
                LayoutInflater.from(parent.context), R.layout.list_item_symbol_new, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.apply {
            itemSymbol.setImageBitmap(getSymbolImage((item.value as Symbol).value))
            itemSymbol.setOnLongClickListener(longClickListener)
            if (selectable) {
                itemSymbol.setOnClickListener { selectItem(holder.absoluteAdapterPosition) }
            }
            isSelected = item.isSelected
            executePendingBindings()
        }
    }

    class ViewHolder(val binding: ListItemSymbolNewBinding) : RecyclerView.ViewHolder(binding.root)

    class DragListener : View.OnDragListener {

        private var isDropped = false

        override fun onDrag(v: View?, event: DragEvent?): Boolean {
            v as RecyclerView

            val draggedView = event?.localState as View
            val sourceList = draggedView.parent as RecyclerView
            val sourceAdapter = sourceList.adapter as SymbolsAdapter
            val sourcePosition = sourceList.getChildAdapterPosition(draggedView)
            if (sourcePosition == -1) {
                return true
            }

            val source = sourceAdapter.items[sourcePosition]
            val targetAdapter = v.adapter as SymbolsAdapter

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
                        viewModel?.updateSymbols(items.mapIndexed { index, item ->
                            (item.value as Symbol).let { Symbol(index, it.value, it.scale) }
                        })
                    }
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    if (targetAdapter == sourceAdapter && !isDropped) {
                        targetAdapter.apply {
                            items.removeAt(sourcePosition)
                            notifyItemRemoved(sourcePosition)
                            viewModel?.updateSymbols(items.mapIndexed { index, item ->
                                (item.value as Symbol).let { Symbol(index, it.value, it.scale) }
                            })
                        }
                    }
                    isDropped = false
                }
            }
            return true
        }
    }
}