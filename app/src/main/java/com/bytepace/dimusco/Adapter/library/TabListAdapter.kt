package com.bytepace.dimusco.Adapter.library

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bytepace.dimusco.Activity.LibraryActivity
import com.bytepace.dimusco.R
import com.bytepace.dimusco.helper.GlobalVariables
import kotlinx.android.synthetic.main.list_item_custom_tab.view.*
import java.text.FieldPosition

@Suppress("DEPRECATION")
class TabListAdapter (itemView: View): RecyclerView.ViewHolder(itemView){
    fun bindView(item: String, position: Int){
        itemView.tvTabTitle.text = item

        if(position != GlobalVariables.tabListSelectedItem){
            itemView.tvTabTitle.setTextColor(itemView.resources.getColor(R.color.color_tab_text))
            itemView.btnTabDel.setColorFilter(itemView.resources.getColor(R.color.color_tab_text))
            itemView.textView7.setBackgroundDrawable(itemView.resources.getDrawable(R.drawable.bg_default_tab))
        } else {
            itemView.tvTabTitle.setTextColor(itemView.resources.getColor(R.color.white))
            itemView.btnTabDel.setColorFilter(itemView.resources.getColor(R.color.white))
            itemView.textView7.setBackgroundDrawable(itemView.resources.getDrawable(R.drawable.bg_selected_tab))
        }


        itemView.tvTabTitle.setOnClickListener {
            GlobalVariables.tabListSelectedItem = position
            itemView.tvTabTitle.setTextColor(itemView.resources.getColor(R.color.white))
            itemView.btnTabDel.setColorFilter(itemView.resources.getColor(R.color.white))
            itemView.textView7.setBackgroundDrawable(itemView.resources.getDrawable(R.drawable.bg_selected_tab))
            LibraryActivity.tabListAdapter.notifyDataSetChanged()
        }


        itemView.btnTabDel.setOnClickListener {
            GlobalVariables.tabList.remove(item)
            if(GlobalVariables.tabListSelectedItem == position){
                GlobalVariables.tabListSelectedItem--
            }
            LibraryActivity.tabListAdapter.setTabList(GlobalVariables.tabList)
            LibraryActivity.tabListAdapter.notifyDataSetChanged()
        }
    }
}