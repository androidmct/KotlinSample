package com.bytepace.dimusco.ui.settings.layers

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bytepace.dimusco.R
import com.bytepace.dimusco.data.model.Layer
import com.bytepace.dimusco.data.repository.LayerRepository
import com.bytepace.dimusco.data.repository.SyncRepository
//import com.bytepace.dimusco.navigation.SettingsNavigator
import com.bytepace.dimusco.utils.LAYER_MIN_POS
import com.bytepace.dimusco.utils.SingleLiveEvent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SettingsLayersViewModel(val scoreId: String) : ViewModel() {

    val layers: LiveData<List<Layer>>

    val onClickAdd: LiveData<Any>
        get() = onClickAddEvent
    val onClickEdit: LiveData<Layer>
        get() = onClickEditEvent
    val onClickDelete: LiveData<Layer>
        get() = onClickDeleteEvent
    val onLayerDeleted: LiveData<String>
        get() = onLayerDeletedEvent

    private val onClickAddEvent = SingleLiveEvent<Any>()
    private val onClickEditEvent = SingleLiveEvent<Layer>()
    private val onClickDeleteEvent = SingleLiveEvent<Layer>()
    private val onLayerDeletedEvent = SingleLiveEvent<String>()

    private val layersRepository = LayerRepository.get()
    private val syncRepository = SyncRepository.get()

    private val modifiedLayersIds = HashSet<String>()

    init {
        layers = layersRepository.getLayers(scoreId)
        LayerRepository.setDeleteLayerCallback { layerName -> onLayerDeleted(layerName) }
    }

    fun onClickBack() {
//        SettingsNavigator.get().back()
    }

    fun onClickAdd() {
        onClickAddEvent.call()
    }

    fun updateLayersOrder(layers: List<Layer>) {
        val dbLayers = this.layers.value
        layers.forEachIndexed { index, layer ->
            if (dbLayers?.get(index)?.lid != layer.lid) {
                modifiedLayersIds.add(layer.lid)
            }
            layer.position = layers.size - 1 - index + LAYER_MIN_POS
        }
        GlobalScope.launch { layersRepository.updateLayers(layers) }
    }

    fun onClickEdit(layer: Layer) {
        onClickEditEvent.value = layer
    }

    fun toggleVisibility(layer: Layer) {
        modifiedLayersIds.add(layer.lid)
        viewModelScope.launch { layersRepository.updateVisibility(layer.lid, !layer.isVisible) }
    }

    fun setActive(layer: Layer) {
        viewModelScope.launch { layersRepository.setActive(layer.lid, layer.sid) }
    }

    fun onClickImport() {
//        SettingsNavigator.get().navigate(R.id.action_layers_to_import)
    }

    fun onClickDelete(layer: Layer) {
        onClickDeleteEvent.value = layer
    }

    fun syncModifiedLayers() {
        if (modifiedLayersIds.isEmpty()) return
        GlobalScope.launch {
            val layers = layersRepository.getEditableLayers(modifiedLayersIds.toList())
            layers.forEach { layer ->
                when (layer.laid.isEmpty()) {
                    true -> syncRepository.createLayer(layer)
                    else -> syncRepository.updateLayer(layer)
                }
            }
        }
    }

    private fun onLayerDeleted(layerName: String) {
        onLayerDeletedEvent.value = layerName
    }

    override fun onCleared() {
        super.onCleared()
        LayerRepository.removeDeleteLayerCallback()
    }
}
