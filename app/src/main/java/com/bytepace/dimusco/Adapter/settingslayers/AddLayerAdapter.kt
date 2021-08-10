package com.bytepace.dimusco.Adapter.settingslayers

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bytepace.dimusco.ui.settings.layers.SettingsLayersActivity
import com.bytepace.dimusco.Model.LayerModel
import com.bytepace.dimusco.R
import com.bytepace.dimusco.helper.GlobalVariables
import com.gmail.samehadar.iosdialog.IOSDialog
import kotlinx.android.synthetic.main.alert_dialog.view.btn_negative
import kotlinx.android.synthetic.main.alert_dialog.view.btn_positive
import kotlinx.android.synthetic.main.alert_dialog.view.text_message
import kotlinx.android.synthetic.main.alert_edit_layer_name.view.*
import kotlinx.android.synthetic.main.list_item_layer.view.*

@Suppress("DEPRECATION")
class AddLayerAdapter(context: Context, scoreList : ArrayList<LayerModel>): RecyclerView.Adapter<AddLayerAdapter.MyViewHolder>() {
    val mContext = context
    val mScoreList = scoreList

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        companion object{
            const val TAG = "NotificationAdapter"
        }

        private var iosDialog: IOSDialog? = null

        init {
            iosDialog = IOSDialog.Builder(itemView.context)
                    .setCancelable(false)
                    .setSpinnerClockwise(false)
                    .setMessageContentGravity(Gravity.END)
                    .build()
        }

        @SuppressLint("DefaultLocale", "UseCompatLoadingForDrawables")
        fun bind(item: LayerModel, position: Int) {
            itemView.text_name.text = item.name
            if(item.bSelected == true) itemView.btn_active.setImageDrawable(itemView.resources.getDrawable(R.drawable.ic_checked))
            else itemView.btn_active.setImageDrawable(itemView.resources.getDrawable(R.drawable.ic_checked_disabled))

            if(item.bShow == true) itemView.btn_visibility.setImageDrawable(itemView.resources.getDrawable(R.drawable.ic_visibility))
            else itemView.btn_visibility.setImageDrawable(itemView.resources.getDrawable(R.drawable.ic_visibility_disabled))

            itemView.btn_drag.visibility = if(GlobalVariables.layerList.count() == 1) View.GONE else View.VISIBLE

            itemView.btn_edit.setOnClickListener { onClickEdit(item, position) }
            itemView.btn_active.setOnClickListener { onClickActive(item, position) }
            itemView.btn_visibility.setOnClickListener { onClickShow(item, position) }
            itemView.btn_delete.setOnClickListener { onClickDel(item, position) }

        }

        private fun onClickEdit(item: LayerModel, position: Int){
            val mDialogView = LayoutInflater.from(itemView.context).inflate(R.layout.alert_edit_layer_name, null)
            //AlertDialogBuilder
            val mBuilder = AlertDialog.Builder(itemView.context)
                    .setView(mDialogView)
            mBuilder.setCancelable(false)
            val  mAlertDialog = mBuilder.show()
            mDialogView.etEditLayerName.setText(item.name)
            mDialogView.swEditDrawing.text = if(item.bDrawing!!) "Drawing: Image" else "Drawing: Object"
            mDialogView.swEditSymbols.text = if(item.bSymbols!!) "Symbols: Image" else "Symbols: Object"
            mDialogView.swEditText.text = if(item.bText!!) "Text: Image" else "Text: Object"

            mDialogView.btn_positive.setOnClickListener {
                GlobalVariables.layerList[position] = LayerModel(
                        mDialogView.etEditLayerName.text.toString(),
                        item.bDrawing,
                        item.bSymbols,
                        item.bText,
                        item.bSelected,
                        item.bShow
                )
                SettingsLayersActivity.layerAdapter.notifyDataSetChanged()
                mAlertDialog?.dismiss()
            }

            mDialogView.btn_negative.setOnClickListener {
                mAlertDialog?.dismiss()
            }
        }

        private fun onClickActive(item: LayerModel, position: Int){
            if(GlobalVariables.itemSelectedLayer != position){
                val prevLayer = GlobalVariables.layerList[GlobalVariables.itemSelectedLayer]
                GlobalVariables.layerList[GlobalVariables.itemSelectedLayer] = LayerModel(
                        prevLayer.name,
                        prevLayer.bDrawing,
                        prevLayer.bSymbols,
                        prevLayer.bText,
                        !prevLayer.bSelected!!,
                        prevLayer.bShow
                )
                GlobalVariables.layerList[position] = LayerModel(
                        item.name,
                        item.bDrawing,
                        item.bSymbols,
                        item.bText,
                        !item.bSelected!!,
                        item.bShow
                )
                GlobalVariables.itemSelectedLayer = position
                SettingsLayersActivity.layerAdapter.notifyDataSetChanged()
            }
        }

        private fun onClickShow(item: LayerModel, position: Int){
            if(!item.bSelected!!){
                GlobalVariables.layerList[position] = LayerModel(
                        item.name,
                        item.bDrawing,
                        item.bSymbols,
                        item.bText,
                        item.bSelected,
                        !item.bShow!!
                )
                SettingsLayersActivity.layerAdapter.notifyDataSetChanged()
            }
        }

        @SuppressLint("SetTextI18n")
        private fun onClickDel(item: LayerModel, position: Int){
            val mDialogView = LayoutInflater.from(itemView.context).inflate(R.layout.alert_dialog, null)
            //AlertDialogBuilder
            val mBuilder = AlertDialog.Builder(itemView.context)
                    .setView(mDialogView)

            val  mAlertDialog = mBuilder.show()
            mDialogView.text_message.text = itemView.resources.getString(R.string.layersSettings_doYouWantToDelete) + " " + item.name + "?"
            mDialogView.btn_positive.text = itemView.resources.getString(R.string.global_yes)
            mDialogView.btn_negative.text = itemView.resources.getString(R.string.global_no)


            mDialogView.btn_positive.setOnClickListener {
                GlobalVariables.layerList.removeAt(position)
                if(GlobalVariables.layerList.count() > 0 && GlobalVariables.itemSelectedLayer == position){
                    val prevLayer = GlobalVariables.layerList[0]
                    GlobalVariables.layerList[0] = LayerModel(
                            prevLayer.name,
                            prevLayer.bDrawing,
                            prevLayer.bSymbols,
                            prevLayer.bText,
                            !prevLayer.bSelected!!,
                            prevLayer.bShow
                    )
                    GlobalVariables.itemSelectedLayer = 0
                }
                SettingsLayersActivity.layerAdapter.notifyDataSetChanged()
                mAlertDialog?.dismiss()
            }

            mDialogView.btn_negative.setOnClickListener {
                mAlertDialog?.dismiss()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddLayerAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(mContext).inflate(R.layout.list_item_layer, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return mScoreList.size
    }

    override fun onBindViewHolder(holder: AddLayerAdapter.MyViewHolder, position: Int) {
        holder.bind(mScoreList[position], position)
    }
}