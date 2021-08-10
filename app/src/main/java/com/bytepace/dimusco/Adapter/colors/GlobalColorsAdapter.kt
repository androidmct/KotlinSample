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
import kotlinx.android.synthetic.main.list_item_color.view.*
import kotlinx.android.synthetic.main.list_item_score_color.view.*
import kotlinx.android.synthetic.main.list_item_symbol.view.*

@Suppress("DEPRECATION")
class GlobalColorsAdapter(context: Context, colorList : ArrayList<ColorModel>, val clickListener: (ColorModel) -> Unit): RecyclerView.Adapter<GlobalColorsAdapter.MyViewHolder>() {
    val mContext = context
    val mColorList = colorList

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GlobalColorsAdapter.MyViewHolder {
        var itemView = LayoutInflater.from(mContext).inflate(R.layout.list_item_score_color, parent, false)
        if(GlobalVariables.symbolActivity == 2){
            itemView = LayoutInflater.from(mContext).inflate(R.layout.list_item_color, parent, false)
        }
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return mColorList.size
    }

    override fun onBindViewHolder(holder: GlobalColorsAdapter.MyViewHolder, position: Int) {
        holder.bind(mColorList[position], position, clickListener)
    }
}