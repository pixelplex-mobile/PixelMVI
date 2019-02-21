package com.pixelplex.pixelmvi.tools

import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.pixelplex.pixelmvi.viewmodel.ReducerSupportStreamViewModel

/**
 * Wrapper for observing ViewModel [ReducerSupportStreamViewModel] by any Android [View]
 *
 * Using:
 *       val viewWrapper = AnyViewWrapper(anyView, lifecycleOwner) {
 *          //user action callback
 *       }
 *
 * LifecycleOwner will be get from your activity or fragment, which is parent for wrapped view
 *
 * @author Kirill Volkov
 */
abstract class LifecycleViewWrapper<V : View, WM : ReducerSupportStreamViewModel<*, State, *>, State>(
    view: V, private val lifecycleOwner: LifecycleOwner
) : StateObserver<State> {

    /**
     * ViewModel [WM]
     */
    abstract val viewModel: WM

    /**
     * Need for call [observe] after call child [init] block
     */
    protected fun startObserver() {
        viewModel.uiState.observe(lifecycleOwner, this)
    }
}