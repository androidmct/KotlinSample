package com.bytepace.dimusco.ui.settings.layers

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bytepace.dimusco.Adapter.settingslayers.AddLayerAdapter
import com.bytepace.dimusco.Model.LayerModel
import com.bytepace.dimusco.R
import com.bytepace.dimusco.data.model.Layer
import com.bytepace.dimusco.databinding.ActivitySettingsLayersBinding
import com.bytepace.dimusco.helper.GlobalVariables
import com.bytepace.dimusco.utils.*
import com.bytepace.dimusco.utils.list.ItemTouchHelperCallback
import kotlinx.android.synthetic.main.alert_add_layer.view.*
import kotlinx.android.synthetic.main.alert_dialog.view.btn_negative
import kotlinx.android.synthetic.main.alert_dialog.view.btn_positive
import kotlinx.android.synthetic.main.layout_header.*

@Suppress("DEPRECATION")
class SettingsLayersActivity :AppCompatActivity(){

    private lateinit var binding: ActivitySettingsLayersBinding
    private lateinit var viewModel: SettingsLayersViewModel
    private lateinit var adapter: LayersAdapter

    companion object{
        lateinit var layerAdapter: AddLayerAdapter
    }

    private val touchHelper = ItemTouchHelper(object : ItemTouchHelperCallback() {
        override fun onItemMove(from: Int, to: Int) {
            adapter.swapItems(from, to)
        }

        override fun onMovementComplete() {
            adapter.onOrderChanged()
        }
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(
                this, SettingsLayersViewModelFactory(intent.extras?.getString(NAV_ARG_SCORE_ID) ?: "")
        ).get(SettingsLayersViewModel::class.java)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings_layers)
        setContentView(binding.root)

        setViewModelObservers()
        adapter = LayersAdapter(this, viewModel, touchHelper)

        binding.apply {
            lifecycleOwner = this@SettingsLayersActivity
            viewModel = this@SettingsLayersActivity.viewModel
            touchHelper.attachToRecyclerView(listLayers)
        }

        /*** Header */
        btn_back.setOnClickListener { onBackPressed() }
        btn_0.visibility = View.VISIBLE
        btn_0.setImageDrawable(resources.getDrawable(R.drawable.ic_plus))
        btn_1.visibility = View.GONE
        btn_2.visibility = View.GONE
        btn_3.visibility = View.GONE

        btn_0.setOnClickListener { addLayer() }

        text_title.text = resources.getString(R.string.global_layers)

//        /*** list */
//        val rvLayerList = findViewById<RecyclerView>(R.id.list_layers)
//        layerAdapter = AddLayerAdapter(this, GlobalVariables.layerList)
//        rvLayerList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
//        rvLayerList.adapter = layerAdapter
    }

    private fun setViewModelObservers() {
        viewModel.onClickAdd.observe(this, Observer { showAddLayerDialog() })
        viewModel.onClickEdit.observe(this, Observer { showEditNameDialog(it) })
        viewModel.onClickDelete.observe(this, Observer { showDeleteDialog(it) })
        viewModel.onLayerDeleted.observe(
                this,
                Observer { showDeleteSuccessDialog(it) })
    }

    private fun showAddLayerDialog() {
//        AddLayerDialog().also {
//            it.arguments = bundleOf(NAV_ARG_SCORE_ID to viewModel.scoreId)
//        }.show(childFragmentManager, null)
    }

    private fun showEditNameDialog(layer: Layer) {
//        EditLayerNameDialog().also {
//            it.arguments = bundleOf(NAV_ARG_LAYER to layer)
//        }.show(childFragmentManager, null)
    }

    private fun showDeleteDialog(layer: Layer) {
        val delete = getTranslatedString(this, STR_LAYER_DELETE)
        val message = "$delete ${layer.name}?"
        val yes = getTranslatedString(this, STR_GLOBAL_YES)
        val no = getTranslatedString(this, STR_GLOBAL_NO)

//        val dialog = DeleteLayerDialog().also {
//            it.arguments = bundleOf(
//                    NAV_ARG_MESSAGE to message,
//                    NAV_ARG_YES_WORD to yes,
//                    NAV_ARG_NO_WORD to no,
//                    NAV_ARG_LAYER to layer
//            )
//        }
//
//        dialog.show(childFragmentManager, null)
    }

    private fun showDeleteSuccessDialog(layerName: String) {
        val layer = getTranslatedString(this, STR_GLOBAL_LAYER)
        val deleted = getTranslatedString(this, STR_LAYER_DELETE_SUCCESS)
        val message = "$layer $layerName $deleted"
        val yes = getTranslatedString(this, STR_GLOBAL_OK)

//        SettingsDialog().also {
//            it.arguments = bundleOf(
//                    NAV_ARG_MESSAGE to message,
//                    NAV_ARG_YES_WORD to yes
//            )
//        }.show(childFragmentManager, null)
    }

    private fun addLayer(){
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.alert_add_layer, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
        mBuilder.setCancelable(false)
        val  mAlertDialog = mBuilder.show()

        mDialogView.swDrawing.isChecked = true
        mDialogView.swSymbols.isChecked = true
        mDialogView.swText.isChecked = true

        mDialogView.btn_positive.setOnClickListener {
            if(!mDialogView.etAddLayerName.text.isNullOrBlank()){
                if(GlobalVariables.layerList.count() > 0){
                    val prevLayer = GlobalVariables.layerList[GlobalVariables.itemSelectedLayer]
                    GlobalVariables.layerList[GlobalVariables.itemSelectedLayer] = LayerModel(
                            prevLayer.name,
                            prevLayer.bDrawing,
                            prevLayer.bSymbols,
                            prevLayer.bText,
                            !prevLayer.bSelected!!,
                            prevLayer.bShow
                    )
                }

                GlobalVariables.layerList.add(0, LayerModel(
                        mDialogView.etAddLayerName.text.toString(),
                        mDialogView.swDrawing.isChecked,
                        mDialogView.swSymbols.isChecked,
                        mDialogView.swText.isChecked,
                        true, true
                ))

                GlobalVariables.itemSelectedLayer = 0

                layerAdapter.notifyDataSetChanged()
                mAlertDialog?.dismiss()
            }
        }
        mDialogView.btn_negative.setOnClickListener {
            mAlertDialog?.dismiss()
        }
    }

    override fun onBackPressed() {
        viewModel.syncModifiedLayers()
        finish()
    }
}