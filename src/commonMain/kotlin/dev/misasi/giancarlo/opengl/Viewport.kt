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

class Viewport(val targetResolution: Vector2f, actualScreenSize: Vector2f) {
    var actualScreenSize: Vector2f = actualScreenSize
        private set
    var adjustedScreenSize: Vector2f = actualScreenSize
        private set
    var view: Rectangle = Rectangle(adjustedScreenSize)
        private set
    var offset: Vector2f = Vector2f()
        private set
    var scale: Vector2f = Vector2f(1f, 1f)
        private set

    init {
        update(actualScreenSize)
    }

    fun contains(point: Vector2f): Boolean {
        return view.contains(point) // todo verify if this change is okay
    }

    fun adjustToBounds(point: Vector2f): Vector2f {
        return point.minus(offset).divide(scale)
    }

    fun update(actualScreenSize: Vector2f) {
        this.actualScreenSize = actualScreenSize
        this.adjustedScreenSize = calculateAdjustedScreenSize(targetResolution.aspectRatio, actualScreenSize)
        this.offset = actualScreenSize.minus(adjustedScreenSize).multiply(0.5f)
        this.view = Rectangle(offset, adjustedScreenSize.plus(offset))
        this.scale = adjustedScreenSize.divide(targetResolution)
    }

    companion object {
        private fun calculateAdjustedScreenSize(aspectRatio: Float, actualScreenSize: Vector2f): Vector2f {
            val requiredScreenHeight = actualScreenSize.x / aspectRatio;
            return if (requiredScreenHeight > actualScreenSize.y) {
                Vector2f(actualScreenSize.y * aspectRatio, actualScreenSize.y);
            } else {
                Vector2f(actualScreenSize.x, requiredScreenHeight);
            }
        }
    }
}