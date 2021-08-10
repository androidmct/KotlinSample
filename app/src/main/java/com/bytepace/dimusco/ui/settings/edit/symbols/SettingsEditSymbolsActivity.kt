package com.bytepace.dimusco.ui.settings.edit.symbols

import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bytepace.dimusco.Adapter.symbols.GlobalSymbolsAdapter
import com.bytepace.dimusco.Adapter.symbols.PersonalSymbolsAdapter
import com.bytepace.dimusco.R
import com.bytepace.dimusco.data.repository.SettingsRepository
import com.bytepace.dimusco.data.repository.SyncRepository
import com.bytepace.dimusco.databinding.ActivitySettingsSymbolsBinding
import com.bytepace.dimusco.databinding.LayoutSeekerBinding
import com.bytepace.dimusco.helper.GlobalFunctions
import com.bytepace.dimusco.helper.GlobalVariables
import com.bytepace.dimusco.ui.settings.edit.colors.ColorsAdapter
import com.bytepace.dimusco.ui.settings.edit.colors.SettingsEditColorActivity
import kotlinx.android.synthetic.main.activity_settings_defaults.seeker_size
import kotlinx.android.synthetic.main.layout_header.*
import kotlinx.android.synthetic.main.layout_seeker.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class SettingsEditSymbolsActivity : AppCompatActivity(){

    companion object{
        private lateinit var personalSymbolsAdapter: SymbolsAdapter
    }

    private lateinit var binding: ActivitySettingsSymbolsBinding
    private lateinit var viewModel: SettingsSymbolsViewModel
    private lateinit var seekBarBinding: LayoutSeekerBinding

    private val settingsRepository = SettingsRepository.get()
    private val syncRepository = SyncRepository.get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(SettingsSymbolsViewModel::class.java)
        personalSymbolsAdapter = SymbolsAdapter(true, this, this.viewModel)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings_symbols)
        val view = binding.root
        setContentView(view)

        seekBarBinding = binding.seekerSize

        binding.apply {
            lifecycleOwner = this@SettingsEditSymbolsActivity
            viewModel = this@SettingsEditSymbolsActivity.viewModel
            listPersonalSymbols.apply {
                layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                adapter = personalSymbolsAdapter
                setOnDragListener(SymbolsAdapter.DragListener())
            }
            listGlobalSymbols.apply {
                layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                adapter = SymbolsAdapter(false)
            }
            setSeekBarCallback(
                    seekerSize,
                    {
                        imagePreview.scaleX = it
                        imagePreview.scaleY = it
                    },
                    { this.viewModel?.updateSelectedSymbolScale(it) })
        }

        binding.seekerSize.maxValue = 2.5f
        binding.seekerSize.minValue = 0.25f
//        binding.seekerSize.progress = viewModel.settings.symbols[viewModel.selectedItem].scale

        GlobalFunctions.initSymbols(this)
        GlobalVariables.symbolActivity = 1

        /*** header */
        btn_back.setOnClickListener {
            onBackPressed()
        }
        btn_0.visibility = View.GONE
        btn_1.visibility = View.GONE
        btn_2.visibility = View.GONE
        btn_3.visibility = View.GONE

        text_title.text = resources.getString(R.string.global_symbols)
    }

    private fun setSeekBarCallback(
            binding: LayoutSeekerBinding,
            onProgressChanged: (Float) -> Unit,
            onStopTracking: (Float) -> Unit
    ) {
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.value = (0.25f * 100 + (seekBar?.progress ?: 0)) / 100f
                onProgressChanged((0.25f * 100 + (seekBar?.progress ?: 0)) / 100f)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                onStopTracking((0.25f * 100 + (seekBar?.progress ?: 0)) / 100f)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
        })
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