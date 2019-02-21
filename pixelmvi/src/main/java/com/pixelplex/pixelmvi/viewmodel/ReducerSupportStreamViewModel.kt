package com.pixelplex.pixelmvi.viewmodel

/**
 * Extension of [EventStreamViewModel] with ability to define state reducers by event type
 *
 * <p>
 *     Usage:
 *     Register all required state reducers for all result types
 *
 *     Example:
 *          reducer(AuthorizationResult.LoadingResult::class.java,
 *           { state, result -> state.copy(loading = result.loading) })
 *
 *           reducer(AuthorizationResult.SuccessResult::class.java,
 *           { state, result ->
 *                  state.copy(loading = false,
 *                  error = result.error,
 *                  success = result.success)
 *           })
 *
 * </p>
 *
 * @author Dmitriy Bushuev.
 */
abstract class ReducerSupportStreamViewModel<Event : UiEvent, State, Result : Any>(initialState: State) :
    EventStreamViewModel<Event, State, Result>(initialState) {

    private val reducerRegistry: MutableMap<Class<out Result>, suspend (State, Result) -> State> =
        HashMap()

    override suspend fun reduce(previousState: State, result: Result): State =
        reducerRegistry[result::class.java]!!(previousState, result)

    /**
     * Register state reducer by result type
     */
    fun <T : Result> reducer(result: Class<T>, reducer: suspend (State, T) -> State) {
        reducerRegistry[result] = reducer as suspend (State, Result) -> State
    }
}