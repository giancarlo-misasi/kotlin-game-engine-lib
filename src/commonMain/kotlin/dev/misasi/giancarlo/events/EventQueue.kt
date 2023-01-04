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

package dev.misasi.giancarlo.events

import dev.misasi.giancarlo.events.input.PositionEvent
import dev.misasi.giancarlo.events.input.gestures.GestureEvent
import dev.misasi.giancarlo.events.input.gestures.detector.GestureDetector
import dev.misasi.giancarlo.events.input.mouse.MouseEvent
import dev.misasi.giancarlo.events.input.touch.TouchEvent
import dev.misasi.giancarlo.opengl.Viewport

class EventQueue (
    private val detectors: Set<GestureDetector>
) {
    private val events = ArrayDeque<Event>()

    fun pushEvent(event: Event) {
        events.addLast(event)
        println(events.last())
    }

    fun pushPositionEvent(viewport: Viewport, event: PositionEvent) {
        val position = viewport.adjustToBounds(event.position)
        if (viewport.contains(position)) {
            events.addLast(event.withPosition(position))
            println(events.last())
        }
    }

    fun pushGestureEvent(viewport: Viewport, touchEvent: TouchEvent) {
         events.addAll(detectors
             .asSequence()
             .mapNotNull { it.detect(touchEvent) }
             .map { adjustToViewport(viewport, it) }
             .filter { viewport.contains(it.position) }
         )
    }

    fun getNextEvent() : Event? {
        return if (events.isEmpty()) {
            null;
        } else {
            events.removeFirst()
        }
    }

    private fun adjustToViewport(viewport: Viewport, event: GestureEvent) = event.copy(position = viewport.adjustToBounds(event.position))
}