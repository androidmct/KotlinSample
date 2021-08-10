package com.bytepace.dimusco.Adapter.colors

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bytepace.dimusco.Model.ColorModel
import com.bytepace.dimusco.Model.SymbolModel
import com.bytepace.dimusco.R
import com.bytepace.dimusco.helper.GlobalVariables
import com.bytepace.dimusco.ui.settings.edit.colors.SettingsColorsViewModel
import com.bytepace.dimusco.utils.list.SelectableItem
import kotlinx.android.synthetic.main.list_item_color.view.*
import kotlinx.android.synthetic.main.list_item_score_color.view.*
import kotlinx.android.synthetic.main.list_item_symbol.view.*

@Suppress("DEPRECATION")
class PersonalColorsAdapter(context: Context, colorList : ArrayList<ColorModel>, private val viewModel: SettingsColorsViewModel? = null, val clickListener: (ColorModel) -> Unit): RecyclerView.Adapter<PersonalColorsAdapter.MyViewHolder>() {
    val mContext = context
    val mColorList = colorList

    private var items = mutableListOf<SelectableItem>()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("DefaultLocale", "UseCompatLoadingForDrawables")
        fun bind(item: ColorModel, position: Int, clickListener: (ColorModel) -> Unit) {
            if(GlobalVariables.symbolActivity == 2){
                itemView.item_color.setBackgroundColor(Color.parseColor(item.value!!))
            } else {
                itemView.item_score_color.setBackgroundColor(Color.parseColor(item.value!!))
            }

            itemView.setOnClickListener { clickListener(item) }
        }
    }

    fun setSelectedItemColor(value: Int) {
        val index = items.indexOfFirst { it.isSelected }
        if (index >= 0) {
            val color = com.bytepace.dimusco.data.model.Color(value)
            items[index].value = color
            viewModel?.updateSelectedColor(color)
            viewModel?.updateColor(index, color)
            notifyItemChanged(index)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonalColorsAdapter.MyViewHolder {
        var itemView = LayoutInflater.from(mContext).inflate(R.layout.list_item_score_color, parent, false)
        if(GlobalVariables.symbolActivity == 2){
            itemView = LayoutInflater.from(mContext).inflate(R.layout.list_item_color, parent, false)
        }
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return mColorList.size
    }

    override fun onBindViewHolder(holder: PersonalColorsAdapter.MyViewHolder, position: Int) {
        holder.bind(mColorList[position], position, clickListener)
    }
}