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

package dev.misasi.giancarlo.opengl

import dev.misasi.giancarlo.math.Rectangle
import dev.misasi.giancarlo.math.Vector2f
import dev.misasi.giancarlo.math.Vector2i

class Viewport private constructor(
    val designedResolution: Vector2i,
    val actualScreenSize: Vector2i,
    val adjustedScreenSize: Vector2i,
    val offset: Vector2f,
    val bounds: Rectangle,
    val scale: Vector2f
) {
    fun contains(point: Vector2f): Boolean {
        return bounds.contains(point) // todo verify if this change is okay (do we need to factor in offset, or am I handling that in events already?)
    }

    fun adjustToBounds(point: Vector2f): Vector2f {
        return point.minus(offset).div(scale)
    }

    companion object {
        private fun calculateAdjustedScreenSize(aspectRatio: Float, actualScreenSize: Vector2i): Vector2i {
            val requiredScreenHeight = actualScreenSize.x / aspectRatio;
            return if (requiredScreenHeight > actualScreenSize.y) {
                Vector2i(actualScreenSize.y * aspectRatio, actualScreenSize.y);
            } else {
                Vector2i(actualScreenSize.x, requiredScreenHeight);
            }
        }

        fun create(actualScreenSize: Vector2i, designedResolution: Vector2i): Viewport {
            val adjustedScreenSize = calculateAdjustedScreenSize(designedResolution.aspectRatio, actualScreenSize)
            val offset = actualScreenSize.minus(adjustedScreenSize).toVector2f().times(0.5f)
            val scale = adjustedScreenSize.toVector2f().div(designedResolution.toVector2f())
            val bounds = Rectangle(offset, adjustedScreenSize.toVector2f().plus(offset))
            return Viewport(designedResolution, actualScreenSize, adjustedScreenSize, offset, bounds, scale)
        }
    }
}