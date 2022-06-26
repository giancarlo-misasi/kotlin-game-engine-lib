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
import dev.misasi.giancarlo.events.input.touch.TouchEvent
import dev.misasi.giancarlo.math.Vector2f
import kotlin.math.sqrt

class PinchDetector : GestureDetector {

    private var initialEvent: TouchEvent? = null
    private var initialPoint1: Vector2f? = null
    private var initialPoint2: Vector2f? = null
    private var currentPoint1: Vector2f? = null
    private var currentPoint2: Vector2f? = null

    override fun reset() {
        initialEvent = null
        initialPoint1 = null
        initialPoint2 = null
        currentPoint1 = null
        currentPoint2 = null
    }

    override fun detect(event: TouchEvent) : GestureEvent? {
        when {
            TouchEvent.Type.Cancel == event.type
                    || event.numberOfPoints != 2
                    || initialEvent?.hasSamePoints(event) == false -> {
                reset()
            }
            TouchEvent.Type.Begin == event.type -> {
                reset()
                initialEvent = event
                initialPoint1 = event.getPointByIndex(0)!!.position
                initialPoint2 = event.getPointByIndex(1)!!.position
            }
            TouchEvent.Type.Move == event.type -> {
                return pinch(event)
            }
            TouchEvent.Type.End == event.type -> {
                val gestureEvent: GestureEvent? = pinch(event)
                reset()
                return gestureEvent
            }
        }

        return null
    }

    private fun pinch(event: TouchEvent) : GestureEvent? {
        if (initialEvent?.valid == false) {
            return null
        }

        currentPoint1 = event.getPointByIndex(0)!!.position
        currentPoint2 = event.getPointByIndex(1)!!.position

        return GestureEvent(GestureEvent.Type.Pinch, event.firstPoint.position, pinchDelta = calculatePinchDelta())
    }

    private fun calculatePinchDelta() : Float {
        if (initialPoint1 == null
            || initialPoint2 == null
            || currentPoint1 == null
            || currentPoint2 == null) {
            return 0.0f
        }

        val startDelta = initialPoint1!!.minus(initialPoint2!!).lengthSquared
        val endDelta = currentPoint1!!.minus(currentPoint2!!).lengthSquared

        return if (startDelta > endDelta) {
            sqrt(startDelta - endDelta) // zoom out / contract
        } else {
            -sqrt(endDelta - startDelta) // zoom in / expand
        }
    }
}
