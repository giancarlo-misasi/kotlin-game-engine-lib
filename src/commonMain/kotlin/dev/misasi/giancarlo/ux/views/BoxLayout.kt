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

package dev.misasi.giancarlo.ux.views

import dev.misasi.giancarlo.math.Vector2i
import dev.misasi.giancarlo.opengl.DisplayContext
import dev.misasi.giancarlo.ux.attributes.LayoutAlignment
import dev.misasi.giancarlo.ux.attributes.LayoutDirection

class BoxLayout(
    var layoutDirection: LayoutDirection = LayoutDirection.HORIZONTAL,
    var layoutAlignment: LayoutAlignment = LayoutAlignment()
) : ViewGroup() {

    override fun onMeasure(context: DisplayContext): Vector2i {
        val measurements = children.map { it.onMeasure(context) }
        return when (layoutDirection) {
            LayoutDirection.HORIZONTAL -> Vector2i(measurements.sumOf { it.x }, measurements.maxOf { it.y })
            LayoutDirection.VERTICAL -> Vector2i(measurements.maxOf { it.x }, measurements.sumOf { it.y })
        }
    }
}