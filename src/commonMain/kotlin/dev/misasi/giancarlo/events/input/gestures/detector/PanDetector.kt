/*
 * MIT License
 *
 * Copyright (c) 2022 Giancarlo Misasi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package dev.misasi.giancarlo.events.input.gestures.detector

import dev.misasi.giancarlo.events.input.gestures.GestureEvent
import dev.misasi.giancarlo.events.input.gestures.touch.TouchEvent
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
            GestureEvent(event.window, event.time, event.firstPoint.position, GestureEvent.Type.Pan, distanceMoved)
        } else {
            null
        }
    }

    private fun continuePanning(event: TouchEvent) : GestureEvent {
        return GestureEvent(event.window, event.time, event.firstPoint.position, GestureEvent.Type.Pan, calculatePanDelta(previousPosition))
    }

    private fun calculatePanDelta(startPosition: Vector2f?) : Vector2f {
        if (startPosition == null || currentPosition == null) {
            return Vector2f()
        }

        return startPosition.minus(currentPosition!!)
    }
}
