package dev.misasi.giancarlo.events.input.window

import dev.misasi.giancarlo.events.Event
import dev.misasi.giancarlo.system.System.Companion.getCurrentTimeMs
import dev.misasi.giancarlo.math.Vector2f

data class ResizeEvent(
    override val window: Long,
    override val time: Long,
    override val absolutePosition: Vector2f,
    val size: Vector2f
) : Event {
    companion object {
        fun valueOf(window: Long, position: Vector2f, x: Int, y: Int): ResizeEvent {
            return ResizeEvent(window, getCurrentTimeMs(), position, Vector2f(x.toFloat(), y.toFloat()))
        }
    }
}