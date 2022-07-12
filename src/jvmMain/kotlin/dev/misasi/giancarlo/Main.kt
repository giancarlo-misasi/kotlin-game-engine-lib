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

package dev.misasi.giancarlo

import dev.misasi.giancarlo.assets.Assets
import dev.misasi.giancarlo.drawing.Animator
import dev.misasi.giancarlo.drawing.Atlas
import dev.misasi.giancarlo.drawing.graphics.Sprite2dGraphics
import dev.misasi.giancarlo.events.Event
import dev.misasi.giancarlo.events.EventQueue
import dev.misasi.giancarlo.events.input.keyboard.Key
import dev.misasi.giancarlo.events.input.keyboard.KeyAction
import dev.misasi.giancarlo.events.input.keyboard.KeyEvent
import dev.misasi.giancarlo.events.input.mouse.CursorMode
import dev.misasi.giancarlo.events.input.scroll.ScrollEvent
import dev.misasi.giancarlo.events.input.window.ResizeEvent
import dev.misasi.giancarlo.math.Vector2f
import dev.misasi.giancarlo.opengl.Buffer
import dev.misasi.giancarlo.opengl.Camera
import dev.misasi.giancarlo.opengl.DisplayContext
import dev.misasi.giancarlo.opengl.LwjglGlfwDisplayContext
import dev.misasi.giancarlo.ux.*

class TestScreen : Screen {
    override val model: Boolean = false
    override var state: ScreenState = ScreenState.WAITING

    private lateinit var assets: Assets
    private lateinit var atlasGeneral: Atlas
    private lateinit var atlasOverworld: Atlas
    private lateinit var spriteGfx: Sprite2dGraphics
    private var camera = Camera()
    private var screenTransition = ScreenTransition(Transition(0, 1000, 1000))

    override fun onInit(context: DisplayContext) {
        assets = Assets(context.gl)
        atlasGeneral = assets.atlas("General")
        atlasOverworld = assets.atlas("Overworld")
        spriteGfx = Sprite2dGraphics(context.gl, Buffer.Usage.DYNAMIC, 10000)

        val orange = atlasOverworld.getMaterial("FloorOrange")
        val purple = atlasOverworld.getMaterial("FloorPurple")

        spriteGfx.clear()
        for (x in 0..800 step 16) {
            for (y in 0..600 step 16) {
                if (x < 400) {
                    spriteGfx.putSprite(Vector2f(x.toFloat(), y.toFloat()), orange)
                } else {
                    spriteGfx.putSprite(Vector2f(x.toFloat(), y.toFloat()), purple)
                }
            }
        }
        spriteGfx.updateVertexBuffer()
    }

    override fun onUpdate(elapsedMs: Int) {
        screenTransition.update(this, elapsedMs)
        val completion = screenTransition.getCompletionPercentage(this)
        if (completion != null) {
            if (state == ScreenState.IN) {
                camera = camera.copy(position = Animator.getTransitionPosition(Vector2f(-800f), Vector2f(), completion))
            }
        } else {
            camera = camera.copy(position = Vector2f())
        }
    }

    override fun onDraw(context: DisplayContext) {
        spriteGfx.bindProgram()
        spriteGfx.setModelViewProjection(context.viewport.getModelViewProjection(camera))
        spriteGfx.draw()
    }

    override fun onEvent(context: DisplayContext, event: Event) {
        if (event is KeyEvent) {
            if (event.key == Key.KEY_ESCAPE && event.action == KeyAction.RELEASE) {
                context.close()
            }
        }

        if (event is KeyEvent) {
            if (event.key == Key.KEY_F && event.action == KeyAction.RELEASE) {
                context.setCursorMode(CursorMode.FPS)
            } else if (event.key == Key.KEY_N && event.action == KeyAction.RELEASE) {
                context.setCursorMode(CursorMode.NORMAL)
            }
        }

        if (event is ScrollEvent) {
            if (event.offset.y > 0) {
                camera = camera.copy(zoom = camera.zoom.times(event.offset.y * 1.1f))
            } else if (event.offset.y < 0) {
                camera = camera.copy(zoom = camera.zoom.div(-event.offset.y * 1.1f))
            }
        }

        if (event is ResizeEvent) {
            context.viewport.setActualScreenSize(context.gl, event.size)
        }
    }
}

fun main() {
    val context = LwjglGlfwDisplayContext(
        "title",
        Vector2f(800f, 600f),
        Vector2f(800f, 600f),
        fullScreen = false,
        vsync = false,
        refreshRate = 60,
        events = EventQueue(setOf())
    )

    context.enableKeyboardEvents(true)
    context.enableMouseEvents(true)
    context.enableMouseButtonEvents(true)
    context.enableScrollEvents(true)
    context.enableResizeEvents(true)

    val stack = ScreenStack(context)
    stack.push(TestScreen())
    stack.run()
}