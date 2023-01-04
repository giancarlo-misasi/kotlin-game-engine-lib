package dev.misasi.giancarlo.coroutines

import kotlinx.coroutines.CoroutineDispatcher

interface CoroutineDispatchers {
    val UI: CoroutineDispatcher
    val IO: CoroutineDispatcher
    val Unconfined: CoroutineDispatcher
}

expect val Dispatchers: CoroutineDispatchers