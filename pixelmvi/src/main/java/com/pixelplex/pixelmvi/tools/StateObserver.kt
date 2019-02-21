package com.pixelplex.pixelmvi.tools

import androidx.lifecycle.Observer

/**
 * Describes functionality of state applying for different ui entities
 *
 * Used for taking the logic of view state rendering out to standalone entity
 *
 * @author Dmitriy Bushuev
 */
interface StateObserver<State> : Observer<State>