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
import dev.misasi.giancarlo.drawing.Font
import dev.misasi.giancarlo.events.input.mouse.MouseButton
import dev.misasi.giancarlo.math.AffineTransform
import dev.misasi.giancarlo.math.Vector2f
import dev.misasi.giancarlo.math.Vector2i
import dev.misasi.giancarlo.ux.AppContext
import java.lang.IllegalStateException

class Button(
    var background: String? = null,
    var font: Font = Font.ROBOTO24,
    var text: String? = null,
    size: Vector2i? = null,
    button: MouseButton? = null,
    onClick: (() -> Unit)? = null,
) : Clickable(size, button, onClick) {
    override fun onUpdateDrawState(context: AppContext, state: DrawState) {
        val s = size?.toVector2f() ?: return
        background?.let {
            if (pressed) {
                state.putSprite(it, AffineTransform(scale = s, translation = Vector2f(2, 2)),)
            } else {
                state.putSprite(it, AffineTransform.scale(s),)
            }
        }
        text?.let {
            val bitmapFont = context.assets.fonts(font.assetName)
            val width = s.x - bitmapFont.width(it)
            val height = s.y - bitmapFont.properties.lineHeight
            if (width < 0 || height < 0) throw IllegalStateException("Button too small!")
            val affine = AffineTransform.translate(Vector2f(width, height).half())
            state.putText(it, affine, font)
        }
    }
}