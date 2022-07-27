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

import dev.misasi.giancarlo.events.Event
import dev.misasi.giancarlo.ResourceContext
import dev.misasi.giancarlo.system.TimeAccumulator
import dev.misasi.giancarlo.ux.Renderer
import dev.misasi.giancarlo.ux.Connector
import kotlin.reflect.KClass

class Screen<ViewModel>(
    private val connector: Connector<ViewModel>,
    private val transitionDurationsMs: Map<ScreenState, Long> = mapOf()
) : View() {
    private val accumulator = TimeAccumulator()

    var state: ScreenState = ScreenState.WAITING
        private set

    fun getViewModel(): ViewModel =
        connector.getViewModel()

    fun getRenderer(): KClass<Renderer<Screen<ViewModel>>> =
        connector.getRenderer()

    fun getTransitionPercentage(state: ScreenState): Float? {
        val durationMs = transitionDurationsMs[state]
        return if (durationMs != null) {
            accumulator.elapsedPercentage(durationMs)
        } else {
            null
        }
    }

    override fun onUpdate(context: ResourceContext, elapsedMs: Long) {
        if (state.hasNext()) {
            accumulator.update(elapsedMs)

            val durationMs = transitionDurationsMs[state] ?: 0
            if (accumulator.hasElapsed(durationMs)) {
                state = state.next()!!
                accumulator.reset()
            }
        }

        connector.onUpdate(context, elapsedMs)
    }

    override fun onEvent(context: ResourceContext, event: Event): Boolean =
        connector.onEvent(context, event)

    fun close() {
        if (state == ScreenState.ACTIVE) {
            state = ScreenState.OUT
        }
    }
}