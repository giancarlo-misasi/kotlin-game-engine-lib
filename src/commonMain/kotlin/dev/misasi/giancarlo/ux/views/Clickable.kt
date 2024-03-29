/*
 * MIT License
 *
 * Copyright (c) 2023 Giancarlo Misasi
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

package dev.misasi.giancarlo.ux.views

import dev.misasi.giancarlo.events.Event
import dev.misasi.giancarlo.events.input.mouse.MouseButton
import dev.misasi.giancarlo.events.input.mouse.MouseButtonAction
import dev.misasi.giancarlo.events.input.mouse.MouseButtonEvent
import dev.misasi.giancarlo.math.Vector2i
import dev.misasi.giancarlo.ux.AppContext
import dev.misasi.giancarlo.ux.View

abstract class Clickable(
    size: Vector2i?,
    button: MouseButton?,
    onClick: (() -> Unit)?,
) : View(size) {
    var pressed = false
        private set

    var onClick = onClick ?: {}
    var button = button ?: MouseButton.LEFT

    final override fun onEvent(context: AppContext, event: Event): Boolean {
        if (event !is MouseButtonEvent || event.button != button) return false

        val contains = absoluteBounds?.contains(event.absolutePosition) ?: false
        if (contains && pressed && event.action == MouseButtonAction.RELEASE) {
            onClick()
            pressed = false
            return true
        }

        pressed = contains && event.action == MouseButtonAction.PRESS
        return false
    }
}