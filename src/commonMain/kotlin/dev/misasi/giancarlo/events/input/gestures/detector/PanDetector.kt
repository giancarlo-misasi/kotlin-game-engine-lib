package dev.misasi.giancarlo.events.input.gestures.detector

import dev.misasi.giancarlo.events.input.gestures.GestureEvent
import dev.misasi.giancarlo.events.input.touch.TouchEvent
import dev.misasi.giancarlo.math.Vector2f

class PanDetector : GestureDetector {

    companion object {
        const val minimumMovedDistanceSquared = 12.0f * 12.0f
    }

    private var panning = false
    private var initialEvent: TouchEvent? = null
    private var currentPosition: Vector2f? = null
    private var previousPosition: Vector2f? = null

    override fun reset() {
        panning = false
        initialEvent = null
        currentPosition = null
        previousPosition = null
    }

    override fun detect(event: TouchEvent) : GestureEvent? {
        when {
            TouchEvent.Type.Cancel == event.type
                    || event.numberOfPoints != 1
                    || initialEvent?.hasSamePoints(event) == false -> {
                reset()
            }
            TouchEvent.Type.Begin == event.type -> {
                reset()
                initialEvent = event
                currentPosition = event.firstPoint.position
            }
            TouchEvent.Type.Move == event.type -> {
                return pan(event)
            }
            TouchEvent.Type.End == event.type -> {
                val gestureEvent: GestureEvent? = pan(event)
                reset()
                return gestureEvent
            }
        }

        return null
    }

    private fun pan(event: TouchEvent) : GestureEvent? {
        if (initialEvent?.valid == false) {
            return null
        }

        previousPosition = currentPosition
        currentPosition = event.firstPoint.position

        return if (!panning) {
            startPanning(event)
        } else {
            continuePanning(event)
        }
    }

    private fun startPanning(event: TouchEvent) : GestureEvent? {
        val distanceMoved = calculatePanDelta(initialEvent?.firstPoint?.position)
        return if (distanceMoved.lengthSquared >= minimumMovedDistanceSquared) {
            panning = true
            GestureEvent(GestureEvent.Type.Pan, event.firstPoint.position, distanceMoved)
        } else {
            null
        }
    }

    private fun continuePanning(event: TouchEvent) : GestureEvent {
        return GestureEvent(GestureEvent.Type.Pan, event.firstPoint.position, calculatePanDelta(previousPosition))
    }

    private fun calculatePanDelta(startPosition: Vector2f?) : Vector2f {
        if (startPosition == null || currentPosition == null) {
            return Vector2f()
        }

        return startPosition.minus(currentPosition!!)
    }
}
