package com.bytepace.dimusco.Adapter.library

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bytepace.dimusco.R

class TabComposeAdapter(activity: Activity): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    val mActivity: Activity = activity

    private var listOfTabs = listOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            CellType.HEADER.ordinal -> TabHeaderAdapter(mActivity,
                    LayoutInflater.from(parent.context).inflate(
                            R.layout.list_item_custom_tab, parent, false
                    )
            )
            CellType.FOOTER.ordinal -> TabFooterAdapter(mActivity,
                    LayoutInflater.from(parent.context).inflate(
                            R.layout.cell_add_tab, parent, false
                    )
            )
            else -> TabListAdapter(LayoutInflater.from(parent.context).inflate(R.layout.list_item_custom_tab, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return listOfTabs.size + 2
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(getItemViewType(position)){
            CellType.HEADER.ordinal -> {
                val headerAdapter = holder as TabHeaderAdapter
                headerAdapter.bindView()
            }
            CellType.FOOTER.ordinal -> {
                val footerAdapter = holder as TabFooterAdapter
                footerAdapter.bindView()
            }
            else -> {
                val tabListAdapter = holder as TabListAdapter
                tabListAdapter.bindView(listOfTabs[position-1], position)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return  when (position){
            0 -> CellType.HEADER.ordinal
            listOfTabs.size + 1 -> CellType.FOOTER.ordinal
            else -> CellType.CONTENT.ordinal
        }
    }

    enum class CellType(viewType: Int){
        HEADER(0),
        FOOTER(1),
        CONTENT(2)
    }

    fun setTabList(listOfTabs: List<String>){
        this.listOfTabs = listOfTabs
        notifyDataSetChanged()
    }

}