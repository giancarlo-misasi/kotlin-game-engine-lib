package dev.misasi.giancarlo.events

import dev.misasi.giancarlo.getTimeMillis
import kotlin.math.min

class SystemClock {

    @Volatile
    var elapsedMillis = 0

    @Volatile
    private var lastTimeMillis = getTimeMillis()

    @Synchronized
    fun update() {
        elapsedMillis = getElapsedMillis(lastTimeMillis)
        lastTimeMillis += elapsedMillis
    }

    private fun getElapsedMillis(lastTimeMillis: Long) : Int {
        val currentTimeMillis = getTimeMillis()
        val delta = min(currentTimeMillis - lastTimeMillis, MAX_STEP_MILLIS)
        return if (delta == -1L) {
            0
        } else {
            delta.toInt()
        }
    }

    companion object {
        private const val MAX_STEP_MILLIS: Long = 1000
    }
}