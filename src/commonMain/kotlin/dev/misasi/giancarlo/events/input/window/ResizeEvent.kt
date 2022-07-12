package dev.misasi.giancarlo.events.input.window

import dev.misasi.giancarlo.events.Event
import dev.misasi.giancarlo.getTimeMillis
import dev.misasi.giancarlo.math.Vector2f

data class ResizeEvent(
    val size: Vector2f,
    val time: Long
) : Event {

    companion object {
        fun valueOf(x: Int, y: Int): ResizeEvent {
            return ResizeEvent(Vector2f(x.toFloat(), y.toFloat()), getTimeMillis())
        }
    }
}