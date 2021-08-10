package com.bytepace.dimusco.ui.settings.language

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bytepace.dimusco.R
import com.bytepace.dimusco.databinding.ListItemLanguageBinding

class LanguageAdapter(
    private val viewModel: SettingsLanguageViewModel
) : RecyclerView.Adapter<LanguageAdapter.ViewHolder>() {

    private var languageKeys = listOf<String>()

    init {
        viewModel.languages?.keys?.let {
            languageKeys = it.toList()
        }
    }

    private fun setSelected(language: String) {
        val oldPosition = languageKeys.indexOf(viewModel.language)
        val newPosition = languageKeys.indexOf(language)
        viewModel.setLanguage(language)
        notifyItemChanged(oldPosition)
        notifyItemChanged(newPosition)
    }

    override fun getItemCount(): Int {
        return languageKeys.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.list_item_language, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val language = viewModel.languages?.get(languageKeys[position])
        holder.binding.apply {
            this.language = language
            this.isSelected = languageKeys[position] == viewModel.language
            root.setOnClickListener { this@LanguageAdapter.setSelected(languageKeys[position]) }
            executePendingBindings()
        }
    }

    class ViewHolder(val binding: ListItemLanguageBinding) : RecyclerView.ViewHolder(binding.root)
}