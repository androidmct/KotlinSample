package com.bytepace.dimusco.ui.settings.language

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bytepace.dimusco.R
import com.bytepace.dimusco.data.repository.SettingsRepository
import com.bytepace.dimusco.data.repository.SyncRepository
import com.bytepace.dimusco.databinding.ActivitySettingsLanguageBinding
import com.bytepace.dimusco.utils.STR_GLOBAL_LANGUAGE
import com.bytepace.dimusco.utils.getTranslatedString
import kotlinx.android.synthetic.main.layout_header.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SettingsLanguageActivity : AppCompatActivity(){

    private lateinit var binding: ActivitySettingsLanguageBinding
    private lateinit var viewModel: SettingsLanguageViewModel

    private val settingsRepository = SettingsRepository.get()
    private val syncRepository = SyncRepository.get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(SettingsLanguageViewModel::class.java)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings_language)
        setContentView(binding.root)

        viewModel.onLanguageChanged.observe(this, Observer { updateHeaderTitle() })
        val adapter = LanguageAdapter(viewModel)

        binding.apply {
            viewModel = this@SettingsLanguageActivity.viewModel
            listLanguages.adapter = adapter
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

        text_title.text = resources.getString(R.string.global_language)
    }

    private fun updateHeaderTitle() {
        binding.header.textTitle.text = getTranslatedString(this, STR_GLOBAL_LANGUAGE)
    }

    private fun saveSettings() {
        GlobalScope.launch {
            val settings = settingsRepository.getSettings()
            syncRepository.saveSettings(settings)
        }
    }

    override fun onBackPressed() {
        saveSettings()
        finish()
    }
}