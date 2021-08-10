package com.bytepace.dimusco.ui.settings.general

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bytepace.dimusco.R
import com.bytepace.dimusco.data.repository.SettingsRepository
import com.bytepace.dimusco.data.repository.SyncRepository
import com.bytepace.dimusco.databinding.ActivitySettingsGeneralBinding
import com.bytepace.dimusco.service.LocalizationService
import com.bytepace.dimusco.utils.*
import kotlinx.android.synthetic.main.activity_settings_general.*
import kotlinx.android.synthetic.main.layout_checkbox_settings.view.*
import kotlinx.android.synthetic.main.layout_header.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SettingsGeneralActivity : AppCompatActivity(){
    private lateinit var binding: ActivitySettingsGeneralBinding
    private lateinit var viewModel: SettingsGeneralViewModel

    private val settingsRepository = SettingsRepository.get()
    private val syncRepository = SyncRepository.get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(
                this, SettingsGeneralViewModelFactory(intent.extras?.getString(NAV_ARG_SCORE_AID) ?: "")
        ).get(SettingsGeneralViewModel::class.java)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings_general)
        setContentView(binding.root)

        binding.apply {
            lifecycleOwner = this@SettingsGeneralActivity
            viewModel = this@SettingsGeneralActivity.viewModel
            pageSequence.addTextChangedListener(
                    TextChangedListener(pageSequenceButton, viewModel?.haveSequence)
            )
            pageSequenceButton.setOnClickListener { onSequenceButtonClick() }
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

        text_title.text = resources.getString(R.string.global_general)
    }


    private fun onSequenceButtonClick() {
        viewModel.onPageSequenceButtonClick().also {
            if (it.isEmpty()) {
                val deactivateText = getTranslatedString(this, STR_GLOBAL_DEACTIVATE)
                binding.pageSequenceButton.text = getBtnText(deactivateText, this)
            } else {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getBtnText(deactivateText: String, context: Context) =
            when (binding.pageSequenceButton.text) {
                deactivateText -> getTranslatedString(context, STR_GLOBAL_ACTIVATE)
                else -> deactivateText
            }

    inner class TextChangedListener(
            private val button: Button,
            private val haveSequence: Boolean?
    ) : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {}

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            button.text = if (haveSequence == true) {
                LocalizationService.instance.getString(STR_GLOBAL_STORE)
            } else {
                button.text
            }
        }
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