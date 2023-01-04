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

package dev.misasi.giancarlo.ux.transitions

import dev.misasi.giancarlo.math.Vector2f

class Slide private constructor(
    val startPosition: Vector2f,
    val endPosition: Vector2f,
    waitMs: Long,
    durationMs: Long
) : Transition(waitMs, durationMs) {

    companion object {
        fun slideLeft(distance: Float, start: Vector2f = Vector2f(), waitMs: Long = 0, durationMs: Long = 1000) =
            Slide(start, Vector2f(start.x - distance, start.y), waitMs, durationMs)

        fun slideRight(distance: Float, start: Vector2f = Vector2f(), waitMs: Long = 0, durationMs: Long = 1000) =
            Slide(start, Vector2f(start.x + distance, start.y), waitMs, durationMs)

        fun slideUp(distance: Float, start: Vector2f = Vector2f(), waitMs: Long = 0, durationMs: Long = 1000) =
            Slide(start, Vector2f(start.x, start.y - distance), waitMs, durationMs)

        fun slideDown(distance: Float, start: Vector2f = Vector2f(), waitMs: Long = 0, durationMs: Long = 1000) =
            Slide(start, Vector2f(start.x, start.y + distance), waitMs, durationMs)
    }
}
