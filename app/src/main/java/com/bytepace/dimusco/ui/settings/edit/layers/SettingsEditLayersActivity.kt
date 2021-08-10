package com.bytepace.dimusco.ui.settings.edit.layers

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bytepace.dimusco.R
import com.bytepace.dimusco.data.repository.SettingsRepository
import com.bytepace.dimusco.data.repository.SyncRepository
import com.bytepace.dimusco.databinding.ActivitySettingsEditLayersBinding
import kotlinx.android.synthetic.main.layout_header.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SettingsEditLayersActivity : AppCompatActivity(){

    private lateinit var binding: ActivitySettingsEditLayersBinding
    private lateinit var viewModel: SettingsEditLayersViewModel

    private val settingsRepository = SettingsRepository.get()
    private val syncRepository = SyncRepository.get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(SettingsEditLayersViewModel::class.java)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings_edit_layers)
        val view = binding.root
        setContentView(view)

        binding.apply {
            lifecycleOwner = this@SettingsEditLayersActivity
            viewModel = this@SettingsEditLayersActivity.viewModel
        }

        /*** header */
        btn_back.setOnClickListener {
            onBackPressed()
        }
        btn_0.visibility = View.GONE
        btn_1.visibility = View.GONE
        btn_2.visibility = View.GONE
        btn_3.visibility = View.GONE

        text_title.text = resources.getString(R.string.global_layers)
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