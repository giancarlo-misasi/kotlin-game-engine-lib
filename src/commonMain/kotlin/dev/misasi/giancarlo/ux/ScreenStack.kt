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

import dev.misasi.giancarlo.drawing.Rgba8
import dev.misasi.giancarlo.opengl.DisplayContext
import dev.misasi.giancarlo.system.Clock

class ScreenStack (private val context: DisplayContext) {
    private val screens = mutableListOf<Screen>()
    private val clock = Clock()

    init {
//        context.gl.setClearColor(Rgba8.BLACK)
        context.gl.setClearColor(Rgba8.RED)

    }

    fun push(screen: Screen) {
        screen.onInit(context)
        screens.add(screen)
    }

    fun update(elapsedMs: Int) {
        val workList = ArrayDeque(screens)
        var transitionOut = false
        while (!workList.isEmpty()) {
            val screen = workList.removeLast()

            // Check if we need to transition
            if (transitionOut) {
                screen.close()
            }

            // Update the screen
            screen.onUpdate(elapsedMs)

            // Remove the screen once it is hidden
            if (screen.state == ScreenState.HIDDEN) {
                screens.remove(screen)
                screen.onDestroy(context)
            } else {
                transitionOut = true
            }
        }
    }

    fun draw() {
        // Clean the display
        context.gl.clear()

        // Draw the screens
        for (screen in screens) {
            if (screen.state == ScreenState.HIDDEN || screen.state == ScreenState.WAITING) {
                continue
            }

            // Draw visible screens
            screen.onDraw(context)
        }

        // Swap the buffers so we see the image
        context.swapBuffers()
    }

    fun handleEvents() {
        context.pollEvents()
        while (true) {
            val event = context.getNextEvent() ?: break
            val workList = ArrayDeque(screens)
            while (!workList.isEmpty()) {
                val screen = workList.removeLast()
                if (screen.state == ScreenState.ACTIVE) {
                    screen.onEvent(context, event)
                    break // first active screen handles events
                } else if (screen.state != ScreenState.HIDDEN) {
                    break // nobody handles events during transition
                }
            }
        }
    }

    fun runOnce() {
        val elapsedMs = clock.elapsedSinceUpdateMs(1000)
        clock.update()

        update(elapsedMs)
        draw()
        handleEvents()
    }

    fun run() {
        clock.update() // reset at start
        while (!context.shouldClose()) {
            runOnce()
        }
    }
}