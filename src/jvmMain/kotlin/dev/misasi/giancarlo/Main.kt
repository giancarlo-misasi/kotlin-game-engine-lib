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

import dev.misasi.giancarlo.assets.Assets
import dev.misasi.giancarlo.drawing.*
import dev.misasi.giancarlo.events.EventQueue
import dev.misasi.giancarlo.events.SystemClock
import dev.misasi.giancarlo.events.input.keyboard.Key
import dev.misasi.giancarlo.events.input.keyboard.KeyAction
import dev.misasi.giancarlo.events.input.keyboard.KeyEvent
import dev.misasi.giancarlo.events.input.mouse.*
import dev.misasi.giancarlo.events.input.scroll.ScrollEvent
import dev.misasi.giancarlo.math.*
import dev.misasi.giancarlo.opengl.*

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

    val clock = SystemClock()
    val assets = Assets(context.getOpenGl())
    val program = assets.program("Material")
    val vbo = assets.buffer("Default")
    val gfx = GraphicsBuffer(vbo.maxBytes)
    val viewport = context.getViewport()
    var camera = Camera()
    var mvp: Matrix4f

    // Draw dynamic things
    val atlasGeneral = assets.atlas("General")
    val atlasOverworld = assets.atlas("Overworld")
    val animation = Animation(atlasGeneral.getMaterialSet("PlayerWalkDown"))
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

        mvp = viewport.getModelViewProjection(camera)
        animation.update(clock.elapsedMillis)
        program.updateUniforms(mapOf("uMvp" to mvp, "uEffect" to 0))

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
        gfx.writeSprite(Vector2f(64f, 64f), animation.currentFrame())
        gfx.updateVertexBuffer(vbo)
        program.enableAttributes()
        program.draw(gfx.drawOrders)

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
        gfx.updateVertexBuffer(vbo)
        program.enableAttributes()
        program.draw(gfx.drawOrders)

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