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
import dev.misasi.giancarlo.drawing.StaticMaterial
import dev.misasi.giancarlo.drawing.graphics.Sprite2dGraphics
import dev.misasi.giancarlo.events.input.window.ResizeEvent
import dev.misasi.giancarlo.math.Flip
import dev.misasi.giancarlo.math.Point4f
import dev.misasi.giancarlo.math.Vector2f
import dev.misasi.giancarlo.opengl.*
import dev.misasi.giancarlo.system.Clock

class ScreenStack (private val context: DisplayContext) {
    private val clock = Clock()
    private val frameBuffer: FrameBuffer
    private val postProcessingGfx: Sprite2dGraphics
    private val screens = mutableListOf<Screen>()

    init {
        context.gl.setClearColor(Rgba8.BLACK)
        frameBuffer = FrameBuffer(context.gl)
        frameBuffer.attach(Texture(context.gl, context.view.targetResolution.x.toInt(), context.view.targetResolution.y.toInt()))
        postProcessingGfx = Sprite2dGraphics(context.gl, Buffer.Usage.STATIC, 1)
        updateScreenSize()
    }

    fun setEffect(effect: Effect, enabled: Boolean) {
        postProcessingGfx.setEffect(effect, enabled)
    }

    fun transitionToScreen(screen: Screen) {
        screen.onInit(context)
        screens.add(screen)
    }

    fun run() {
        clock.update() // reset at start
        while (!context.shouldClose()) {
            runOnce()
        }
    }

    private fun update(elapsedMs: Int) {
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

    private fun draw() {
        // Attach the frame buffer to handle scaling and post-processing
        frameBuffer.bind()

        // Clean the frame buffer to avoid artifacts
        context.gl.clear()

        // Set the viewport to the target resolution
        // everything draws assuming target resolution and then this will scale to fit nicely
        context.gl.setViewport(0, 0, context.view.targetResolution.x.toInt(), context.view.targetResolution.y.toInt())

        // Draw the screens
        for (screen in screens) {
            if (screen.state == ScreenState.HIDDEN || screen.state == ScreenState.WAITING) {
                continue
            }

            // Draw visible screens
            screen.onDraw(context)
        }

        // Detach the frame buffer and clear the screen to avoid artifacts
        FrameBuffer.unbind(context.gl)
        context.gl.clear()

        // Apply post-processing
        postProcessingGfx.bindProgram()
        context.gl.setViewport(0, 0, context.view.actualScreenSize.x.toInt(), context.view.actualScreenSize.y.toInt())
        postProcessingGfx.draw()

        // Swap the buffers so we see the image
        context.swapBuffers()
    }

    private fun handleEvents() {
        context.pollEvents()
        while (true) {
            val event = context.getNextEvent() ?: break
            if (event is ResizeEvent) {
                updateScreenSize()
            }

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

    private fun runOnce() {
        val elapsedMs = clock.elapsedSinceUpdateMs(1000)
        clock.update()

        update(elapsedMs)
        draw()
        handleEvents()
    }

    private fun updateScreenSize() {
        postProcessingGfx.bindProgram()
        postProcessingGfx.setMvp(Camera.mvp(context.view.actualScreenSize))
        postProcessingGfx.clear()
        postProcessingGfx.putSprite(
            context.view.offset,
            context.view.adjustedScreenSize,
            StaticMaterial("", frameBuffer.attachedTexture!!, postProcessUv),
            flip = Flip.VERTICAL
        )
        postProcessingGfx.updateVertexBuffer()
    }

    companion object {
        val postProcessUv = Point4f.create(Vector2f(), Vector2f(1f, 1f))
    }
}