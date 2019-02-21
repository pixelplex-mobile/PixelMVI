package com.pixelplex.pixelmvi.viewmodel


import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.launch

/**
 * Base implementation of event streaming MVI-style view model
 *
 *     Usage:
 *     1) Define initial state by overriding abstract [initialState]
 *     2) Override [reducer] function for [Result] to [State] transformation
 *     3) Register all required ui event types with their transformers from [Event] to [Result]
 *
 *     Example:
 *          query(AuthorizationEvent.LoginEvent::class.java) { event -> logIn(event.username) }
 *
 * There are two event types that can be registered - command and query.
 * First only sends\requests some data and doesn't generate return result,
 * another one - generate result after request succeed\fails.
 *
 * Command can be used for sending some data without return value, subscription on different events.
 *
 *      Subscription example:
 *      init {
 *           command(MainEvent.StepsEvent::class.java) { launch { requestSteps() } }
 *          }
 *
 *           private suspend fun requestSteps() {
 *                      stepsUpdates().fold({ channel ->
 *                      channel.consumeEach { sendResult(MainResult.StepsResult(it)) }
 *                          }, { error ->
 *                      sendResult(MainResult.Failure(error))
 *                      })
 *                      }
 *
 * @author Dmitriy Bushuev
 */
abstract class EventStreamViewModel<Event : UiEvent, State, Result>(initialState: State) :
    BaseViewModel() {

    val uiState = MutableLiveData<State>()

    // Channel.UNLIMITED was chose because not all events\results come into reduce state
    private val uiEvents =
        GlobalScope.actor<Event>(
            context = Dispatchers.Default + parentJob,
            capacity = Channel.UNLIMITED
        ) {
            consumeEach { event ->
                GlobalScope.launch(kotlin.coroutines.coroutineContext + parentJob + exceptionHandler) {
                    eventTransformerRegistry[event::class.java]?.invoke(event)?.let {
                        sendResult(it)
                    }
                    commandTransformerRegistry[event::class.java]?.invoke(event)
                }
            }
        }

    private val results =
        GlobalScope.actor<Result>(Dispatchers.Default + parentJob, Channel.UNLIMITED) {
            consumeEach { state.send(reduce(lastState(), it)) }
        }

    private val state = ConflatedBroadcastChannel(initialState)

    private var stateSub: ReceiveChannel<State>? = null

    private val eventTransformerRegistry: MutableMap<Class<out Event>, suspend (Event) -> Result> =
        hashMapOf()

    private val commandTransformerRegistry: MutableMap<Class<out Event>, suspend (Event) -> Unit> =
        hashMapOf()

    init {
        start()
    }

    protected fun start() {
        stop()
        stateSub = state.openSubscription().also { subscription ->
            GlobalScope.launch(Dispatchers.Main + parentJob + exceptionHandler) {
                subscription.consumeEach { state ->
                    uiState.value = state
                }
            }
        }
    }

    protected fun stop() {
        stateSub?.cancel()
    }

    /**
     * Sends result to state reducer
     */
    suspend fun sendResult(result: Result) {
        results.send(result)
    }

    /**
     * Returns last state of viewModel
     */
    fun lastState() = state.value

    /**
     * Register transformer for ui event of query type
     */
    fun <T : Event> query(event: Class<T>, transformer: suspend (T) -> Result) {
        eventTransformerRegistry[event] = transformer as suspend (Event) -> Result
    }

    /**
     * Register transformer for ui event of command type
     */
    fun <T : Event> command(event: Class<out T>, transformer: suspend (T) -> Unit) {
        commandTransformerRegistry[event] = transformer as suspend (Event) -> Unit
    }

    /**
     * Sends UI event on processing
     */
    fun nextEvent(event: Event) {
        uiEvents.offer(event)
    }

    override fun onCleared() {
        super.onCleared()
        stop()
    }

    /**
     * Transforms previous screen state using view intent result
     */
    protected abstract suspend fun reduce(previousState: State, result: Result): State
}
