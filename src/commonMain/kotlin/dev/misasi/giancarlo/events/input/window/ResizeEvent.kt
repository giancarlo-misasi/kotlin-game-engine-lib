package dev.misasi.giancarlo.events.input.window

import dev.misasi.giancarlo.events.Event
import dev.misasi.giancarlo.system.System.Companion.getCurrentTimeMs
import dev.misasi.giancarlo.math.Vector2f

data class ResizeEvent(
    val window: Long,
    val size: Vector2f,
    val time: Long
) : Event {

    companion object {
        fun valueOf(window: Long, x: Int, y: Int): ResizeEvent {
            return ResizeEvent(window, Vector2f(x.toFloat(), y.toFloat()), getCurrentTimeMs())
        }
    }
}