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

package dev.misasi.giancarlo.ux

import dev.misasi.giancarlo.drawing.DrawState
import dev.misasi.giancarlo.events.Event
import dev.misasi.giancarlo.math.Rectangle
import dev.misasi.giancarlo.math.Vector2f
import dev.misasi.giancarlo.math.Vector2i
import dev.misasi.giancarlo.ux.attributes.Inset

// temp hack
private var nextViewId = 0
private fun getNextViewId() = nextViewId++

abstract class View() {
    val id = getNextViewId()  // debugging
    var parent: View? = null  // debugging

    var visible: Boolean = true

    var size: Vector2i? = null
        set(value) {
            field = value
            bounds = calculateBounds()
        }

    var position: Vector2f? = null
        set(value) {
            field = value
            bounds = calculateBounds()
        }

    var bounds: Rectangle? = null
        private set

    var margin: Inset = Inset()
        set(value) {
            field = value
            inset = calculateInset()
        }

    var padding: Inset = Inset()
        set(value) {
            field = value
            inset = calculateInset()
        }

    var inset: Inset = calculateInset()
        private set

    val hidden: Boolean get() = !visible

    /**
     * Sets the size of a view if not already set.
     */
    open fun onSize(context: AppContext, maxSize: Vector2i) {
        size = maxSize
    }

    /**
     * Populate the draw state with draw commands.
     */
    open fun onUpdateDrawState(context: AppContext, state: DrawState) = Unit

    /**
     * Respond to elapsed time.
     */
    open fun onElapsed(context: AppContext, elapsedMs: Long) = Unit

    /**
     * Handle events.
     */
    open fun onEvent(context: AppContext, event: Event): Boolean = false

    private fun calculateInset(): Inset = margin.concatenate(padding)

    private fun calculateBounds(): Rectangle? = size?.let { s -> position?.let { p -> Rectangle(p, s.toVector2f()) } }
}