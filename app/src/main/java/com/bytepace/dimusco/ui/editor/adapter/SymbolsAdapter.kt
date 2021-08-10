package com.bytepace.dimusco.ui.editor.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.bytepace.dimusco.R
import com.bytepace.dimusco.data.model.Symbol
import com.bytepace.dimusco.databinding.ListItemSymbolEditorBinding
import com.bytepace.dimusco.ui.base.BaseAdapter
import com.bytepace.dimusco.ui.editor.ScoreViewModel
import com.bytepace.dimusco.ui.editor.adapter.viewholder.SymbolViewHolder
import com.bytepace.dimusco.utils.getSymbolImage

class SymbolsAdapter(private val viewModel: ScoreViewModel) :
    BaseAdapter<Symbol, SymbolViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SymbolViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_item_symbol_editor, parent, false)
        return SymbolViewHolder(ListItemSymbolEditorBinding.bind(view))
    }

    override fun onBindViewHolder(holder: SymbolViewHolder, position: Int) {
        val item = items[position]
        holder.binding.apply {
            itemSymbol.setImageBitmap(getSymbolImage(item.value))
            itemSymbol.setOnClickListener { viewModel.selectSymbol(item) }
            isSelected = position == selectedItem
            executePendingBindings()
        }
    }
}