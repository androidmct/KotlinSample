package com.bytepace.dimusco.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bytepace.dimusco.helper.GlobalVariables

class TabListViewModel : ViewModel(){
    private val tabLists:MutableLiveData<ArrayList<String>> = MutableLiveData()

    fun getTabList(): LiveData<ArrayList<String>>{
        if(GlobalVariables.tabListCount != GlobalVariables.tabList.size){
            GlobalVariables.tabListCount = GlobalVariables.tabList.size
            tabLists.postValue(GlobalVariables.tabList)
        }

        return tabLists
    }
}