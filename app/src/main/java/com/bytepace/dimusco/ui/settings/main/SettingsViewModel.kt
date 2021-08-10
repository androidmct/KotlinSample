package com.bytepace.dimusco.ui.settings.main

import androidx.lifecycle.ViewModel
import com.bytepace.dimusco.R
import com.bytepace.dimusco.utils.NAV_ARG_SCORE_AID
import com.bytepace.dimusco.utils.NAV_ARG_SCORE_ID
import com.bytepace.dimusco.utils.SingleLiveEvent

class SettingsViewModel(val sid: String, val aid: String) : ViewModel() {

    fun onClickBack() {
//        MainNavigator.get().back()
    }

    fun onClickGeneral() {
//        SettingsNavigator.get().navigate(R.id.action_settings_to_general, NAV_ARG_SCORE_AID, aid)
    }

    fun onClickEdit() {
//        SettingsNavigator.get().navigate(R.id.action_settings_to_edit)
    }

    fun onClickLayers() {
//        SettingsNavigator.get().navigate(R.id.action_settings_to_layers, NAV_ARG_SCORE_ID, sid)
    }

    fun onClickLanguages() {
//        SettingsNavigator.get().navigate(R.id.action_settings_to_language)
    }
}