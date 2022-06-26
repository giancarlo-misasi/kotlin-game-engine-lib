package dev.misasi.giancarlo.events.input.gestures

import dev.misasi.giancarlo.events.Event
import dev.misasi.giancarlo.math.Vector2f

data class GestureEvent (
    val type: Type,
    val position: Vector2f,
    val panDelta: Vector2f = Vector2f(),
    val pinchDelta: Float = 0f
) : Event {
    enum class Type {
        Tap,
        Pan,
        Pinch
    }
}
