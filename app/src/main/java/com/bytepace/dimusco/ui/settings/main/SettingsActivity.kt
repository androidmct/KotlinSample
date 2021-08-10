package com.bytepace.dimusco.ui.settings.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.bytepace.dimusco.R
import com.bytepace.dimusco.databinding.ActivitySettingsBinding
import com.bytepace.dimusco.service.LocalizationService
import com.bytepace.dimusco.ui.settings.edit.SettingsEditActivity
import com.bytepace.dimusco.ui.settings.general.SettingsGeneralActivity
import com.bytepace.dimusco.ui.settings.language.SettingsLanguageActivity
import com.bytepace.dimusco.ui.settings.layers.SettingsLayersActivity
import com.bytepace.dimusco.utils.NAV_ARG_SCORE_AID
import com.bytepace.dimusco.utils.NAV_ARG_SCORE_ID
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.layout_header.*

@Suppress("DEPRECATION")
class SettingsActivity : AppCompatActivity(){

    private var bLayers: String = ""

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var viewModel: SettingsViewModel

    override fun onResume() {
        super.onResume()

        setViewModelObservers()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(
                this, SettingsViewModelFactory(
                intent.extras?.getString(NAV_ARG_SCORE_ID) ?: "",
                intent.extras?.getString(NAV_ARG_SCORE_AID) ?: ""
            )
        ).get(SettingsViewModel::class.java)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings)
        setContentView(binding.root)

        binding.apply {
            lifecycleOwner = this@SettingsActivity
            viewModel = this@SettingsActivity.viewModel
        }

        if (savedInstanceState == null) {
            val extras = intent.extras
            if (extras != null) {
                bLayers = extras.getString(NAV_ARG_SCORE_AID)!!
            }
        } else {
            bLayers = savedInstanceState.getSerializable(NAV_ARG_SCORE_AID) as String
        }

        /**
         * header
         */

        btn_back.setOnClickListener {
            onBackPressed()
        }
        btn_0.visibility = View.GONE
        btn_1.visibility = View.GONE
        btn_2.visibility = View.GONE
        btn_3.visibility = View.GONE

        text_title.text = resources.getString(R.string.global_settings)

        /**
         * Layers
         */
        txt_setting_layers.visibility = if(!bLayers.isBlank()) View.VISIBLE else View.GONE

    }

    private fun setViewModelObservers() {
        viewModel.apply {
            LocalizationService.instance.onLanguageChanged.observe(this@SettingsActivity) { refreshActivity() }
        }
    }

    private fun refreshActivity(){
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    fun onClickGeneral(v: View){
        val generalIntent = Intent(this, SettingsGeneralActivity::class.java)
        generalIntent.putExtra(NAV_ARG_SCORE_AID, bLayers)
        startActivity(generalIntent)
    }

    fun onClickEdit(v: View){
        nfStartActivity(SettingsEditActivity())
    }

    fun onClickLayers(v: View){
        nfStartActivity(SettingsLayersActivity())
    }

    fun onClickLanguage(v: View){
        nfStartActivity(SettingsLanguageActivity())
    }

    private fun nfStartActivity(mActivity: Activity){
        startActivity(Intent(this, mActivity::class.java))
    }

    override fun onBackPressed() {
        finish()
    }
}