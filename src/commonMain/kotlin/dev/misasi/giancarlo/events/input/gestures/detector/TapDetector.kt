package dev.misasi.giancarlo.events.input.gestures.detector

import dev.misasi.giancarlo.events.input.gestures.GestureEvent
import dev.misasi.giancarlo.events.input.touch.TouchEvent

abstract class TapDetector (
    private val tapsToDetect: Int,
    private val gestureType: GestureEvent.Type
) : GestureDetector {

    companion object {
        const val touchTimeout = 225;
        const val tapTimeout = 600;
        const val maxTouchDistanceSquared = 18.0f * 18.0f;
        const val maxTapDistanceSquared = 32.0f * 32.0f;
    }

    private var previousEvent: TouchEvent? = null
    private var taps: Int = 0

    override fun reset() {
        previousEvent = null
        taps = 0
    }

    override fun detect(event: TouchEvent) : GestureEvent? {
        when {
            TouchEvent.Type.Cancel == event.type || event.numberOfPoints > 1 -> {
                reset()
            }
            TouchEvent.Type.Begin == event.type -> {
                if (taps > 0) {
                    // if  we already have taps, check if we are within
                    // max distance and tap timeout of the last tap
                    // or if we need to start a new sequence
                    if (!isWithinTimeout(event, tapTimeout) || !isWithinDistance(event, maxTapDistanceSquared)) {
                        reset()
                    }
                }

                previousEvent = event
            }
            TouchEvent.Type.End == event.type -> {
                if (isWithinTimeout(event, touchTimeout) && isWithinDistance(event, maxTouchDistanceSquared)) {
                    taps++
                    previousEvent = event

                    if (tapsToDetect == taps) {
                        reset()
                        return GestureEvent(gestureType, event.firstPoint.position)
                    }
                }
            }
        }

        return null
    }

    private fun isWithinTimeout(event: TouchEvent, timeout: Int) : Boolean {
        if (previousEvent == null) {
            return false
        }

        return event.time - previousEvent!!.time < timeout
    }

    private fun isWithinDistance(event: TouchEvent, distanceSquared: Float) : Boolean {
        if (previousEvent == null) {
            return false
        }

        return event.firstPoint.position.minus(previousEvent!!.firstPoint.position).lengthSquared < distanceSquared
    }
}
