package com.bytepace.dimusco.ui.settings.edit.defaults

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bytepace.dimusco.R
import com.bytepace.dimusco.data.repository.SettingsRepository
import com.bytepace.dimusco.data.repository.SyncRepository
import com.bytepace.dimusco.databinding.ActivitySettingsDefaultsBinding
import com.bytepace.dimusco.databinding.LayoutSeekerBinding
import com.bytepace.dimusco.utils.ERASER_THICKNESS_MIN
import com.bytepace.dimusco.utils.FLOAT_FORMAT
import com.bytepace.dimusco.utils.LINE_THICKNESS_MIN
import kotlinx.android.synthetic.main.activity_settings_defaults.*
import kotlinx.android.synthetic.main.layout_header.*
import kotlinx.android.synthetic.main.layout_seeker.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import kotlin.math.roundToInt


class SettingsEditDefaultsActivity : AppCompatActivity(){

    private lateinit var binding: ActivitySettingsDefaultsBinding
    private lateinit var viewModel: SettingsDefaultsViewModel

    private val settingsRepository = SettingsRepository.get()
    private val syncRepository = SyncRepository.get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(SettingsDefaultsViewModel::class.java)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings_defaults)
        setContentView(binding.root)

        binding.apply {
            lifecycleOwner = this@SettingsEditDefaultsActivity
            viewModel = this@SettingsEditDefaultsActivity.viewModel
            setSeekBarCallback(seekerTransparency){value ->
                this@SettingsEditDefaultsActivity.viewModel.onTransparencyChanged(value)
            }
            setSeekBarCallback(seekerLine, LINE_THICKNESS_MIN){ value ->
                this@SettingsEditDefaultsActivity.viewModel.onLineThicknessChanged(value)
            }
            setSeekBarCallback(seekerEraser, ERASER_THICKNESS_MIN){ value ->
                this@SettingsEditDefaultsActivity.viewModel.onEraserThicknessChanged(value)
            }
            setSeekBarCallback(seekerSize){ value ->
                this@SettingsEditDefaultsActivity.viewModel.onTextSizeChanged(value)
            }
            formatter = DecimalFormat(FLOAT_FORMAT)
        }

        /*** header */
        btn_back.setOnClickListener {
            onBackPressed()
        }
        btn_0.visibility = View.GONE
        btn_1.visibility = View.GONE
        btn_2.visibility = View.GONE
        btn_3.visibility = View.GONE

        text_title.text = resources.getString(R.string.global_defaults)

    }

    private fun setSeekBarCallback(
            binding: LayoutSeekerBinding,
            minValue: Float = 0f,
            onProgressChanged: (Float) -> Unit
    ) {
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.value = (minValue * 100 + (seekBar?.progress ?: 0)) / 100f
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                onProgressChanged((minValue * 100 + (seekBar?.progress ?: 0)) / 100f)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
        })
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

