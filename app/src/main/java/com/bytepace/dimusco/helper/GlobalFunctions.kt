package com.bytepace.dimusco.helper

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.graphics.drawable.toBitmap
import com.bytepace.dimusco.Model.ColorModel
import com.bytepace.dimusco.Model.PageModel
import com.bytepace.dimusco.Model.SymbolModel
import com.bytepace.dimusco.R

@Suppress("DEPRECATION")
class GlobalFunctions {
    companion object{
        fun initSymbols(context: Context){
            val symbolsList = context.resources.getStringArray(R.array.symbols_name)
            var cnt = 0
            GlobalVariables.symbols.clear()
            for (sbname in symbolsList){
                GlobalVariables.symbols.add(SymbolModel(sbname, cnt++, 1, 1.0f))
            }
        }

        fun initColors(){
            val colorsList = arrayListOf<ColorModel>(
                    ColorModel("#000000"),
                    ColorModel("#FFFFFF"),
                    ColorModel("#FF0000"),
                    ColorModel("#00FF00"),
                    ColorModel("#0000FF"),
                    ColorModel("#FFFF00"),
                    ColorModel("#00FFFF"),
                    ColorModel("#FF00FF"),
                    ColorModel("#C0C0C0"),
                    ColorModel("#808080"),
                    ColorModel("#800000"),
                    ColorModel("#808000"),
                    ColorModel("#008000"),
                    ColorModel("#800080"),
                    ColorModel("#008080"),
                    ColorModel("#000080")
            )
            GlobalVariables.colors.clear()
            GlobalVariables.colors.addAll(colorsList)
        }

        fun openBrowser(url: String, context: Context) {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            context.startActivity(i)
        }
    }
}