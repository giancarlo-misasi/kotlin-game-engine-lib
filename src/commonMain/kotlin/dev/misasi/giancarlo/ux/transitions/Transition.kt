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

import dev.misasi.giancarlo.math.AffineTransform
import dev.misasi.giancarlo.math.Vector2f
import dev.misasi.giancarlo.system.TimeAccumulator
import dev.misasi.giancarlo.ux.attributes.TransitionState

abstract class Transition(
    private val waitMs: Long,
    private val durationMs: Long
) {
    private val accumulator = TimeAccumulator()
    private var transitionState = TransitionState.WAITING

    val waiting: Boolean get() = TransitionState.WAITING == transitionState
    val active: Boolean get() = TransitionState.ACTIVE == transitionState
    val complete: Boolean get() = TransitionState.COMPLETE == transitionState
    val percentage: Float get() = if (active) accumulator.elapsedPercentage(durationMs) else 0f

    open val currentPosition: Vector2f? = null
    open val currentAffine: AffineTransform? = null
    open val currentAlpha: Float? = null

    fun onUpdate(elapsedMs: Long) {
        if (transitionState.hasNext()) {
            accumulator.update(elapsedMs)

            val durationMs = if (waiting) waitMs else durationMs
            if (accumulator.hasElapsed(durationMs)) {
                transitionState = transitionState.next()!!
                accumulator.reset()
            }
        }
    }
}