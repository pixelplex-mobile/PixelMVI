package com.pixelplex.pixelmvi.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.SupervisorJob

/**
 * Base implementation of ViewModel
 *
 * Contains logic of cancelling [parentJob] in [onCleared] method
 *
 * @author Dmitriy Bushuev
 */
open class BaseViewModel : ViewModel() {

    protected val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        println("Caught $exception")
    }

    protected val parentJob = SupervisorJob()

    override fun onCleared() {
        parentJob.cancel()
    }
}