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

import dev.misasi.giancarlo.drawing.DrawState
import dev.misasi.giancarlo.events.input.mouse.MouseButton
import dev.misasi.giancarlo.math.AffineTransform
import dev.misasi.giancarlo.math.Vector2i
import dev.misasi.giancarlo.ux.AppContext

class Button(
    size: Vector2i,
    button: MouseButton = MouseButton.LEFT,
) : Clickable(size, button) {
    var background: String? = null
    var fontName: String = ""
    var text: String? = null

    override fun onUpdateDrawState(context: AppContext, state: DrawState) {
        val s = size?.toVector2f() ?: return

        background?.let { state.putSprite(it, AffineTransform.scale(s),) }
        text?.let {  }

        super.onUpdateDrawState(context, state)
    }
}