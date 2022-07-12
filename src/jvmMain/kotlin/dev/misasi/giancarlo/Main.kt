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
import dev.misasi.giancarlo.drawing.*
import dev.misasi.giancarlo.drawing.graphics.Shape2dGraphics
import dev.misasi.giancarlo.drawing.graphics.Sprite2dGraphics
import dev.misasi.giancarlo.events.EventQueue
import dev.misasi.giancarlo.events.input.keyboard.Key
import dev.misasi.giancarlo.events.input.keyboard.KeyAction
import dev.misasi.giancarlo.events.input.keyboard.KeyEvent
import dev.misasi.giancarlo.events.input.mouse.*
import dev.misasi.giancarlo.events.input.scroll.ScrollEvent
import dev.misasi.giancarlo.events.input.window.ResizeEvent
import dev.misasi.giancarlo.math.*
import dev.misasi.giancarlo.opengl.Buffer
import dev.misasi.giancarlo.opengl.Camera
import dev.misasi.giancarlo.opengl.LwjglGlfwDisplayContext
import dev.misasi.giancarlo.system.Clock

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

    val clock = Clock()
    val assets = Assets(context.getOpenGl())
    val spriteGfx = Sprite2dGraphics(context.getOpenGl(), Buffer.Usage.DYNAMIC, 10000)
    val shapeGfx = Shape2dGraphics(context.getOpenGl(), Buffer.Usage.DYNAMIC, 10000)
    val viewport = context.getViewport()
    var camera = Camera()
    var mvp: Matrix4f

    // Draw dynamic things
    val atlasGeneral = assets.atlas("General")
    val atlasOverworld = assets.atlas("Overworld")
    val animation = AnimatedMaterial(atlasGeneral.getMaterialSet("PlayerWalkDown"))
    val grass = atlasOverworld.getMaterial("FloorGrass")
    val orange = atlasOverworld.getMaterial("FloorOrange")
    val purple = atlasOverworld.getMaterial("FloorPurple")
    val tree = atlasOverworld.getMaterial("Tree")

    // Some state for testing
    var mousePressing = false
    var lastMousePos = Vector2f()
    var r1 = Rectangle(Vector2f(), Vector2f(200f, 200f))
    var c1 = Circle(Vector2f(), 50f)

    // Draw loop
    val gl = context.getOpenGl()
    gl.setClearColor(Rgba8.BLACK)
    while (!context.shouldClose()) {
        gl.clear()

        // TODO Viewport needs an update function

//        val cp = Animator.getTransitionPosition(Vector2f(), Vector2f(1920f, 0f), 0f)

        animation.update(clock.elapsedMillis)
        mvp = viewport.getModelViewProjection(camera) //camera.copy(position = cp))

        spriteGfx.bindProgram()
        spriteGfx.setModelViewProjection(mvp)

        spriteGfx.clear()
        spriteGfx.putSprite(Vector2f(0f, 0f), orange)
        spriteGfx.putSprite(Vector2f(16f, 0f), purple)
        for (x in 0..800 step 16) {
            for (y in 0..600 step 16) {
                if (x < 200) {
                    spriteGfx.putSprite(Vector2f(x.toFloat(), y.toFloat()), grass, Rotation.Rotate90)
                } else if (x < 400) {
                    spriteGfx.putSprite(Vector2f(x.toFloat(), y.toFloat()), orange)
                } else {
                    spriteGfx.putSprite(Vector2f(x.toFloat(), y.toFloat()), purple)
                }
            }
        }
        spriteGfx.putSprite(Vector2f(128f, 296f), tree)
        spriteGfx.putSprite(Vector2f(355f, 312f), tree)
        spriteGfx.putSprite(Vector2f(512f, 800f), tree)
        spriteGfx.putSprite(Vector2f(725f, 256f), tree)
        spriteGfx.putSprite(Vector2f(100f, 300f), animation)
        spriteGfx.putSprite(Vector2f(300f, 200f), atlasGeneral.getMaterialSet("PlayerWalkDown").frames[0])
        spriteGfx.putSprite(Vector2f(300f, 200f), animation)
        spriteGfx.putSprite(Vector2f(0f, 100f), tree)
        spriteGfx.putSprite(Vector2f(32f, 100f), tree)
        spriteGfx.putSprite(Vector2f(400f, 100f), animation)
        spriteGfx.updateVertexBuffer()
        spriteGfx.draw()

        context.swapBuffers()
        context.pollEvents()

        do {
            val event = context.getNextEvent() ?: break
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

            // TODO: Mouse events should have gestures to make it easy to determine how far the mouse has moved
            if (event is MouseButtonEvent) {
                if (event.button == MouseButton.LEFT) {
                    if (event.action == MouseButtonAction.PRESS) {
                        mousePressing = true
                    } else if (event.action == MouseButtonAction.RELEASE) {
                        mousePressing = false
                    }
                }

                if (event.button == MouseButton.RIGHT && event.action == MouseButtonAction.RELEASE) {
                    r1 = r1.moveTo(lastMousePos)
                }
            }

            if (event is MouseEvent) {
                val delta = lastMousePos.minus(event.position)
                if (mousePressing) {
                    camera = camera.copy(position = camera.position.plus(delta))
                }

                lastMousePos = event.position
//                println(event.position)
            }

            if (event is ResizeEvent) {
                viewport.setActualScreenSize(gl, event.size)
            }
        } while (true)

        clock.update()
    }
}