package dev.misasi.giancarlo.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext

class SingleThreadMainCoroutineDispatcher {
    companion object {
        private const val name = "SingleThreadMainCoroutineDispatcher"

        @Volatile
        private var INSTANCE: CoroutineDispatcher? = null

        @OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
        fun getInstance(): CoroutineDispatcher =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: newSingleThreadContext(name).also { INSTANCE = it }
            }
    }
}