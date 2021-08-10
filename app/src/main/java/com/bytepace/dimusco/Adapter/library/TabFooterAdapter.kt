package com.bytepace.dimusco.Adapter.library

import android.app.Activity
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bytepace.dimusco.Activity.LibraryActivity
import com.bytepace.dimusco.helper.GlobalVariables
import com.bytepace.dimusco.viewModel.TabListViewModel

class TabFooterAdapter (activity: Activity, itemView: View) : RecyclerView.ViewHolder(itemView){
    fun bindView(){
        itemView.setOnClickListener {
            GlobalVariables.tabList.add("New Tab")
            LibraryActivity.tabListAdapter.setTabList(GlobalVariables.tabList)
            LibraryActivity.tabListAdapter.notifyDataSetChanged()
        }
    }
}