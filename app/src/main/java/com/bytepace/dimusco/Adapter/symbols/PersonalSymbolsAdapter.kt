package com.bytepace.dimusco.Adapter.symbols

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bytepace.dimusco.Model.SymbolModel
import com.bytepace.dimusco.R
import com.bytepace.dimusco.helper.GlobalVariables
import kotlinx.android.synthetic.main.list_item_symbol.view.*
import java.io.IOException
import java.io.InputStream

@Suppress("DEPRECATION")
class PersonalSymbolsAdapter(context: Context, symbolList : ArrayList<SymbolModel>, val clickListener: (SymbolModel) -> Unit): RecyclerView.Adapter<PersonalSymbolsAdapter.MyViewHolder>() {
    val mContext = context
    val mSymbolList = symbolList

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("DefaultLocale", "UseCompatLoadingForDrawables")
        fun bind(item: SymbolModel, position: Int, clickListener: (SymbolModel) -> Unit) {
            itemView.item_symbol.setImageBitmap(getBitmapFromAsset(itemView.context, "symbols/${item.name}.png"))

            if(GlobalVariables.symbolActivity == 1){
                itemView.item_symbol.background = itemView.resources.getDrawable(R.drawable.bg_symbol)
            } else {
                itemView.item_symbol.background = itemView.resources.getDrawable(R.drawable.bg_transparent)
            }

            itemView.setOnClickListener { clickListener(item) }
        }

        fun getBitmapFromAsset(context: Context, filePath: String?): Bitmap? {
            val assetManager = context.assets
            val istr: InputStream
            var bitmap: Bitmap? = null
            try {
                istr = assetManager.open(filePath!!)
                bitmap = BitmapFactory.decodeStream(istr)
            } catch (e: IOException) {
                // handle exception
            }
            return bitmap
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonalSymbolsAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(mContext).inflate(R.layout.list_item_symbol, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return mSymbolList.size
    }

    override fun onBindViewHolder(holder: PersonalSymbolsAdapter.MyViewHolder, position: Int) {
        holder.bind(mSymbolList[position], position, clickListener)
    }
}