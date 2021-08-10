package com.bytepace.dimusco.ui.settings.edit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bytepace.dimusco.R
import com.bytepace.dimusco.databinding.ActivitySettingsEditBinding
import com.bytepace.dimusco.ui.settings.edit.colors.SettingsEditColorActivity
import com.bytepace.dimusco.ui.settings.edit.defaults.SettingsEditDefaultsActivity
import com.bytepace.dimusco.ui.settings.edit.layers.SettingsEditLayersActivity
import com.bytepace.dimusco.ui.settings.edit.symbols.SettingsEditSymbolsActivity
import kotlinx.android.synthetic.main.activity_settings_edit.*
import kotlinx.android.synthetic.main.layout_header.*

class SettingsEditActivity : AppCompatActivity(){

    private lateinit var binding: ActivitySettingsEditBinding
    private lateinit var viewModel: SettingsEditViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(SettingsEditViewModel::class.java)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings_edit)
        setContentView(binding.root)

        binding.apply {
            lifecycleOwner = this@SettingsEditActivity
            viewModel = this@SettingsEditActivity.viewModel
        }

        /*** header */

        btn_back.setOnClickListener {
            onBackPressed()
        }
        btn_0.visibility = View.GONE
        btn_1.visibility = View.GONE
        btn_2.visibility = View.GONE
        btn_3.visibility = View.GONE

        text_title.text = resources.getString(R.string.global_editors)


    }

    fun onClickDefaults(v: View){
        nfStartActivity(SettingsEditDefaultsActivity())
    }

    fun onClickColors(v: View){
        nfStartActivity(SettingsEditColorActivity())
    }

    fun onClickSymbols(v: View){
        nfStartActivity(SettingsEditSymbolsActivity())
    }

    fun onClickLayers(v: View){
        nfStartActivity(SettingsEditLayersActivity())
    }

    fun onClickConfirm(v: View){
        chb_setEdit_confirm.isChecked = !chb_setEdit_confirm.isChecked
    }

    private fun nfStartActivity(mActivity: Activity){
        startActivity(Intent(this, mActivity::class.java))
    }

    override fun onBackPressed() {
        finish()
    }
}