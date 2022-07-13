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

package dev.misasi.giancarlo.ux

import dev.misasi.giancarlo.system.TimeAccumulator

class ScreenTransition(
    private val screen: Screen,
    private val transitionDurationsMs: Map<ScreenState, Int> = mapOf()
) {
    private val accumulator = TimeAccumulator()

    fun elapsedPercentage(): Float? {
        val durationMs = transitionDurationsMs[screen.state]
        return if (durationMs != null) {
            accumulator.elapsedPercentage(durationMs)
        } else {
            null
        }
    }

    fun update(elapsedMs: Int) {
        if (screen.state.hasNext()) {
            accumulator.update(elapsedMs)
            val durationMs = transitionDurationsMs[screen.state] ?: 0
            if (accumulator.hasElapsed(durationMs)) {
                screen.goToNextState()
                reset()
            }
        }
    }

    fun reset() {
        accumulator.reset()
    }
}