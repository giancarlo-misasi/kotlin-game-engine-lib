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

import dev.misasi.giancarlo.drawing.Animator

class Fade private constructor(
    val startAlpha: Float,
    val endAlpha: Float,
    waitMs: Long,
    durationMs: Long
) : Transition(waitMs, durationMs) {
    override val currentAlpha: Float get() = Animator.fade(percentage, endAlpha > startAlpha)

    companion object {
        fun fadeIn(waitMs: Long = 0, durationMs: Long = 1000) =
            Fade(0f, 1f, waitMs, durationMs)

        fun fadeOut(waitMs: Long = 0, durationMs: Long = 1000) =
            Fade(1f, 0f, waitMs, durationMs)
    }
}