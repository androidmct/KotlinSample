package com.bytepace.dimusco.ui.settings.edit.colors

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bytepace.dimusco.Adapter.colors.GlobalColorsAdapter
import com.bytepace.dimusco.R
import com.bytepace.dimusco.data.repository.SettingsRepository
import com.bytepace.dimusco.data.repository.SyncRepository
import com.bytepace.dimusco.databinding.ActivitySettingsColorsBinding
import com.bytepace.dimusco.helper.GlobalFunctions
import com.bytepace.dimusco.helper.GlobalVariables
import com.bytepace.dimusco.utils.getHexString
import kotlinx.android.synthetic.main.layout_header.*
import kotlinx.coroutines.*
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

class SettingsEditColorActivity : AppCompatActivity(){

    companion object{
        private lateinit var personalColorsAdapter: ColorsAdapter
    }

    private val settingsRepository = SettingsRepository.get()
    private val syncRepository = SyncRepository.get()


    private lateinit var binding: ActivitySettingsColorsBinding
    private lateinit var viewModel: SettingsColorsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(SettingsColorsViewModel::class.java)

        personalColorsAdapter = ColorsAdapter(true, this, this.viewModel).apply {
            setSelectionCallback { onColorSelected(it) }
        }

        setViewModelObservers()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings_colors)
        val view = binding.root
        setContentView(view)

        binding.apply {
            lifecycleOwner = this@SettingsEditColorActivity
            viewModel = this.viewModel
            listPersonalColors.layoutManager = LinearLayoutManager(this@SettingsEditColorActivity, LinearLayoutManager.HORIZONTAL, false)
            listPersonalColors.adapter = personalColorsAdapter
            listPersonalColors.setOnDragListener(ColorsAdapter.DragListener())
            listGlobalColors.layoutManager = LinearLayoutManager(this@SettingsEditColorActivity, LinearLayoutManager.HORIZONTAL, false)
            listGlobalColors.adapter = ColorsAdapter(false)
            pickerColor.setOnColorChangedCallback { textColor.text = getHexString(it) }
            pickerHue.setOnHueChangedCallback { onHueChanged(it) }
        }

        binding.btnSet.setOnClickListener { viewModel.onClickSetColor() }

        GlobalFunctions.initColors()
        GlobalVariables.symbolActivity = 2

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

        text_title.text = resources.getString(R.string.global_colors)

    }

    private fun setViewModelObservers() {
        viewModel.onSetColor.observe(this, Observer { onSetColor() })
        viewModel.settings.observe(this, Observer {
            binding.root.apply { post { onColorSelected(it.selectedColor) } }
        })
    }

    private fun onHueChanged(color: Int) {
        binding.pickerColor.hue = color
    }

    private fun onColorSelected(color: Int) {
        binding.apply {
            pickerHue.selectedColor = color
            pickerColor.selectedColor = color
        }
    }

    private fun onSetColor() {
        personalColorsAdapter.setSelectedItemColor(binding.pickerColor.selectedColor)
    }

    override fun onBackPressed() {
        saveSettings()
        finish()
    }

    private fun saveSettings() {
        GlobalScope.launch {
            val settings = settingsRepository.getSettings()
            syncRepository.saveSettings(settings)
        }
    }
}