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

import dev.misasi.giancarlo.ux.View

class ViewOrchestrator {
    private val pendingTransitions = mutableListOf<PendingTransition>()
    private val currentTransitions = mutableListOf<ActiveTransition>()

    var rootView: View? = null
        private set

    val isEmpty: Boolean
        get() = currentTransitions.isEmpty()

    val isNotEmpty: Boolean
        get() = currentTransitions.isNotEmpty()

    val activeTransitions: Map<View, Transition>
        get() = currentTransitions.associate { it.view to it.transition }

    fun go(nextRootView: View, outTransition: Transition, inTransition: Transition) {
        if (rootView != nextRootView
            && pendingTransitions.none { it.view == nextRootView }
            && currentTransitions.none { it.view == nextRootView }
        ) {
            pendingTransitions.add(PendingTransition(nextRootView, outTransition, inTransition))
        }
    }

    fun onElapsed(elapsedMs: Long) {
        if (currentTransitions.isEmpty()) {
            promotePendingTransitions()
        } else {
            updateActiveTransitions(elapsedMs)
        }
    }

    private fun promotePendingTransitions() {
        val currentRootView = rootView
        if (pendingTransitions.isNotEmpty()) {
            val pending = pendingTransitions.removeFirst()
            if (currentRootView != null) {
                currentTransitions.add(ActiveTransition(currentRootView, pending.outTransition, false))
            }
            currentTransitions.add(ActiveTransition(pending.view, pending.inTransition, true))
        }
    }

    private fun updateActiveTransitions(elapsedMs: Long) {
        val it = currentTransitions.iterator()
        while (it.hasNext()) {
            val (nextRootView, transition, replaceCurrent) = it.next()
            transition.onUpdate(elapsedMs)

            if (transition.complete) {
                it.remove()

                if (replaceCurrent) {
                    rootView = nextRootView
                }
            }
        }
    }

    private data class ActiveTransition(
        val view: View,
        val transition: Transition,
        val replaceCurrentView: Boolean
    )

    private data class PendingTransition(
        val view: View,
        val outTransition: Transition,
        val inTransition: Transition
    )
}