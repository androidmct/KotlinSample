package com.bytepace.dimusco.ui.editor.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.bytepace.dimusco.R
import com.bytepace.dimusco.data.model.Color
import com.bytepace.dimusco.databinding.ListItemColorEditorBinding
import com.bytepace.dimusco.ui.base.BaseAdapter
import com.bytepace.dimusco.ui.editor.ScoreViewModel
import com.bytepace.dimusco.ui.editor.adapter.viewholder.ColorViewHolder
import com.bytepace.dimusco.utils.getOpaqueColor

class ColorsAdapter(private val viewModel: ScoreViewModel) :
    BaseAdapter<Color, ColorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.list_item_color_editor, parent, false)
        return ColorViewHolder(ListItemColorEditorBinding.bind(view))
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        val item = items[position]
        holder.binding.apply {
            itemColor.setBackgroundColor(getOpaqueColor(item.value))
            root.setOnClickListener { viewModel.selectColor(position) }
            isSelected = position == selectedItem
            executePendingBindings()
        }
    }
}