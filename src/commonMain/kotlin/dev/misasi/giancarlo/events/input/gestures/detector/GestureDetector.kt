package dev.misasi.giancarlo.events.input.gestures.detector

import dev.misasi.giancarlo.events.input.gestures.GestureEvent
import dev.misasi.giancarlo.events.input.touch.TouchEvent

interface GestureDetector {
    fun reset()
    fun detect(event: TouchEvent) : GestureEvent?
}
