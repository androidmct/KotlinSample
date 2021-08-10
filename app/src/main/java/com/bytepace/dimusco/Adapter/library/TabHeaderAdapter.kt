package com.bytepace.dimusco.Adapter.library

import android.app.Activity
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bytepace.dimusco.Activity.LibraryActivity
import com.bytepace.dimusco.R
import com.bytepace.dimusco.helper.GlobalVariables
import kotlinx.android.synthetic.main.cell_add_tab.view.*
import kotlinx.android.synthetic.main.list_item_custom_tab.view.*

@Suppress("DEPRECATION")
class TabHeaderAdapter(activity: Activity, itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bindView(){
        itemView.tvTabTitle.text = "Default"
        itemView.btnTabDel.visibility = View.INVISIBLE

        if(GlobalVariables.tabListSelectedItem == 0){
            itemView.tvTabTitle.setTextColor(itemView.resources.getColor(R.color.white))
            itemView.textView7.setBackgroundDrawable(itemView.resources.getDrawable(R.drawable.bg_selected_tab))
        } else {
            itemView.tvTabTitle.setTextColor(itemView.resources.getColor(R.color.color_tab_text))
            itemView.textView7.setBackgroundDrawable(itemView.resources.getDrawable(R.drawable.bg_default_tab))
        }


        itemView.tvTabTitle.setOnClickListener {
            GlobalVariables.tabListSelectedItem = 0
            itemView.tvTabTitle.setTextColor(itemView.resources.getColor(R.color.white))
            itemView.textView7.setBackgroundDrawable(itemView.resources.getDrawable(R.drawable.bg_selected_tab))
            LibraryActivity.tabListAdapter.notifyDataSetChanged()
        }
    }
}
