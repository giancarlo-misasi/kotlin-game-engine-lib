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

import dev.misasi.giancarlo.system.Timer

class ScreenTransition (private val transition: Transition) {
    private val timer: Timer = Timer()

    fun restart() = timer.restart()

    fun getCompletionPercentage(screen: Screen): Float? {
        return when (screen.state) {
            ScreenState.WAITING -> timer.getCompletionPercentage(transition.waitMs)
            ScreenState.IN -> timer.getCompletionPercentage(transition.inMs)
            ScreenState.OUT -> timer.getCompletionPercentage(transition.outMs)
            else -> null
        }
    }

    fun update(screen: Screen, elapsedMs: Int) {
        timer.update(elapsedMs)

        if (screen.state == ScreenState.WAITING) {
            if (timer.isComplete(transition.waitMs)) {
                screen.state = ScreenState.IN
                timer.restart()
            }
        } else if (screen.state == ScreenState.IN) {
            if (timer.isComplete(transition.inMs)) {
                screen.state = ScreenState.ACTIVE
            }
        } else if (screen.state == ScreenState.OUT) {
            if (timer.isComplete(transition.outMs)) {
                screen.state = ScreenState.HIDDEN
            }
        }
    }

    fun hide(screen: Screen) {
        if (screen.state != ScreenState.HIDDEN && screen.state != ScreenState.OUT) {
            screen.state = ScreenState.OUT
            timer.restart()
        }
    }
}