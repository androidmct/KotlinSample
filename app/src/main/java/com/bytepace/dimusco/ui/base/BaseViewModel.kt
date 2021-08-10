package com.bytepace.dimusco.ui.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel : ViewModel(), CoroutineScope {

    private var job: Job = SupervisorJob()

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Timber.e(exception)
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main.immediate + job + exceptionHandler

    fun executeOnUI(block: suspend () -> Unit) {
        launch(coroutineContext) { block() }
    }

    suspend fun <T> executePending(block: suspend () -> T): T =
        withContext(Dispatchers.IO) { block() }
}