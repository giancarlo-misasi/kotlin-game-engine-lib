package dev.misasi.giancarlo.events

import dev.misasi.giancarlo.events.input.gestures.GestureEvent
import dev.misasi.giancarlo.events.input.gestures.detector.GestureDetector
import dev.misasi.giancarlo.events.input.touch.TouchEvent
import dev.misasi.giancarlo.events.lifecycle.LifecycleEvent
import dev.misasi.giancarlo.opengl.Viewport
import java.util.Queue
import java.util.concurrent.LinkedBlockingQueue

class EventQueue (
    private val detectors: Set<GestureDetector>
) {
    private val events: Queue<Event> = LinkedBlockingQueue()

    fun pushLifecycleEvent(stage: LifecycleEvent.Stage, data: Any? = null) {
        events.add(LifecycleEvent(stage, data))
    }

    fun pushTouchEvent(viewport: Viewport, touchEvent: TouchEvent) {
         events.addAll(detectors
             .asSequence()
             .mapNotNull { it.detect(touchEvent) }
             .map { adjustToViewport(viewport, it) }
             .filter { viewport.withinBounds(it.position) }
         )
    }

    fun getNextEvent() : Event? {
        return if (events.isEmpty()) {
            null;
        } else {
            events.remove()
        }
    }

    private fun adjustToViewport(viewport: Viewport, gestureEvent: GestureEvent) : GestureEvent {
        return gestureEvent.copy(
            position = viewport.adjustToBounds(gestureEvent.position)
        )
    }
}
