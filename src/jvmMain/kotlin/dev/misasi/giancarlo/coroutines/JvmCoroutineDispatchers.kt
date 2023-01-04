package dev.misasi.giancarlo.coroutines

class JvmCoroutineDispatchers : CoroutineDispatchers {
    override val UI = SingleThreadMainCoroutineDispatcher.getInstance()
    override val IO = kotlinx.coroutines.Dispatchers.IO
    override val Unconfined = kotlinx.coroutines.Dispatchers.Unconfined

    companion object {
        @Volatile
        private var INSTANCE: CoroutineDispatchers? = null

        fun getInstance(): CoroutineDispatchers =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: JvmCoroutineDispatchers().also { INSTANCE = it }
            }
    }
}

actual val Dispatchers = JvmCoroutineDispatchers.getInstance()