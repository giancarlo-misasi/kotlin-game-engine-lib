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

package dev.misasi.giancarlo.drawing

import dev.misasi.giancarlo.math.Vector2f
import kotlin.math.max
import kotlin.math.min

class Animator {
    companion object {
        fun fade(percentage: Float, fadeIn: Boolean): Float {
            return if (fadeIn) {
                fadeIn(percentage)
            } else {
                fadeOut(percentage)
            }
        }

        fun fadeIn(percentage: Float) : Float {
            return max(0f, min(percentage, 1f))
        }

        fun fadeOut(percentage: Float) : Float {
            return 1f - fadeIn(percentage)
        }

        fun translate(start: Vector2f, end: Vector2f, percentage: Float) : Vector2f {
            return start.plus(end.minus(start).multiply(percentage))
        }
    }
}