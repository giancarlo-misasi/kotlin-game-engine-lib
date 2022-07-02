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
import dev.misasi.giancarlo.drawing.AnimatedMaterial
import dev.misasi.giancarlo.drawing.Animator
import dev.misasi.giancarlo.drawing.GraphicsBuffer2d
import dev.misasi.giancarlo.drawing.Rgba8
import dev.misasi.giancarlo.events.EventQueue
import dev.misasi.giancarlo.system.Clock
import dev.misasi.giancarlo.events.input.keyboard.Key
import dev.misasi.giancarlo.events.input.keyboard.KeyAction
import dev.misasi.giancarlo.events.input.keyboard.KeyEvent
import dev.misasi.giancarlo.events.input.mouse.*
import dev.misasi.giancarlo.events.input.scroll.ScrollEvent
import dev.misasi.giancarlo.math.*
import dev.misasi.giancarlo.opengl.Buffer
import dev.misasi.giancarlo.opengl.Camera
import dev.misasi.giancarlo.opengl.LwjglGlfwDisplayContext

fun main() {
    val context = LwjglGlfwDisplayContext(
        "title",
        Vector2f(1920f, 1080f),
        Vector2f(1920f, 1080f),
        fullScreen = false,
        vsync = false,
        refreshRate = 60,
        events = EventQueue(setOf())
    )

    context.enableKeyboardEvents(true)
    context.enableMouseEvents(true)
    context.enableMouseButtonEvents(true)
    context.enableScrollEvents(true)

    val clock = Clock()
    val assets = Assets(context.getOpenGl())
    val gfx = GraphicsBuffer2d(context.getOpenGl(), Buffer.Usage.DYNAMIC, 100000)
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

        val cp = Animator.getTransitionPosition(Vector2f(), Vector2f(1920f, 0f), 0f)

        animation.update(clock.elapsedMillis)
        mvp = viewport.getModelViewProjection(camera.copy(position = cp))

        gfx.bind()
        gfx.updateUniform("uMvp", mvp)
        gfx.updateUniform("uEffect", mvp)

        gfx.clear()
        for (x in 0..1920 step 16) {
            for (y in 0..1080 step 16) {
                if (x < 800) {
                    gfx.writeSprite(Vector2f(x.toFloat(), y.toFloat()), grass)
                } else if (x < 1600) {
                    gfx.writeSprite(Vector2f(x.toFloat(), y.toFloat()), orange)
                } else {
                    gfx.writeSprite(Vector2f(x.toFloat(), y.toFloat()), purple)
                }
            }
        }
        gfx.writeSprite(Vector2f(128f, 296f), tree)
        gfx.writeSprite(Vector2f(355f, 312f), tree)
        gfx.writeSprite(Vector2f(512f, 800f), tree)
        gfx.writeSprite(Vector2f(725f, 256f), tree)
        gfx.writeSprite(Vector2f(200f, 300f), animation)

        gfx.updateVertexBuffer()
        gfx.draw()

        mvp = viewport.getModelViewProjection(camera.copy(position = cp))
        gfx.updateUniform("uMvp", mvp)

        // draw the rect and circle we test intersection with
        c1 = c1.moveTo(lastMousePos)
        val inter5 = Intersection.intersection(c1, r1)

        gfx.clear()
        gfx.writeCircle(c1.position, c1.radius, Rgba8.YELLOW_GREEN)
        gfx.writeRectangle(r1.tl, r1.size, if (inter5 != null) Rgba8.RED else Rgba8.GREEN)
        gfx.writeCircle(c1.position.plus(inter5 ?: Vector2f()), c1.radius, Rgba8.PURPLE)
        gfx.writeLine(Vector2f(), Vector2f(1920f, 1080f), Rgba8.RED) // Draw a big diagonal line to test
        gfx.writeLine(Vector2f(0f, 100f), Vector2f(1920f, 0f), Rgba8.RED) // Draw a big diagonal line to test
        gfx.writeLine(Vector2f(0f, 200f), Vector2f(1920f, 0f), Rgba8.RED) // Draw a big diagonal line to test
        gfx.writeLine(Vector2f(0f, 300f), Vector2f(1920f, 0f), Rgba8.RED) // Draw a big diagonal line to test

        gfx.updateVertexBuffer()
        gfx.draw()

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
            }

//            println(event)
        } while (true)

        clock.update()
    }
}