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
import dev.misasi.giancarlo.events.Event
import dev.misasi.giancarlo.opengl.DisplayContext

abstract class Screen(
    val modal: Boolean
) {
    enum class State {
        WAITING,
        IN,
        ACTIVE,
        OUT,
        HIDDEN
    }

    var state: State = State.WAITING
        private set

    private val timer: Timer = Timer()
    private val waitMs = 100
    private val inMs = 100
    private val outMs = 100

    fun update(elapsedMs: Int, transitionOut: Boolean) {
        timer.update(elapsedMs)

        if (transitionOut && state != State.HIDDEN && state != State.OUT) {
            state = State.OUT
            timer.restart()
        } else if (state == State.WAITING) {
            if (timer.isComplete(waitMs)) {
                state = State.IN
                timer.restart()
            }
        } else if (state == State.IN) {
            if (timer.isComplete(inMs)) {
                state = State.ACTIVE
            }
        } else if (state == State.OUT) {
            if (timer.isComplete(outMs)) {
                state = State.HIDDEN
            }
        } else if (state == State.ACTIVE) {
            onUpdate(elapsedMs)
        }
    }

    abstract fun onUpdate(elapsedMs: Int)
    abstract fun onDraw(context: DisplayContext)
    abstract fun onEvent(event: Event)
}