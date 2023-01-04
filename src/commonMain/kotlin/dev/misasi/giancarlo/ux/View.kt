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

import dev.misasi.giancarlo.events.Event
import dev.misasi.giancarlo.math.Vector2i
import dev.misasi.giancarlo.ux.attributes.Inset

abstract class View {
    var margin: Inset = Inset()
    var padding: Inset = Inset()
    var visible: Boolean = true
    val hidden: Boolean get() = !visible

    open fun onMeasure(context: AppContext): Vector2i = defaultContentSize(context)
    open fun onElapsed(context: AppContext, elapsedMs: Long) = Unit
    open fun onEvent(context: AppContext, event: Event): Boolean = false

    private fun defaultContentSize(context: AppContext): Vector2i =
        context.viewport.designedResolution.minus(margin.size).minus(padding.size)
}