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
import dev.misasi.giancarlo.drawing.StaticMaterial
import dev.misasi.giancarlo.drawing.graphics.Sprite2dGraphics
import dev.misasi.giancarlo.events.Event
import dev.misasi.giancarlo.events.EventQueue
import dev.misasi.giancarlo.events.input.keyboard.Key
import dev.misasi.giancarlo.events.input.keyboard.KeyAction
import dev.misasi.giancarlo.events.input.keyboard.KeyEvent
import dev.misasi.giancarlo.events.input.mouse.CursorMode
import dev.misasi.giancarlo.events.input.mouse.MouseButtonEvent
import dev.misasi.giancarlo.events.input.scroll.ScrollEvent
import dev.misasi.giancarlo.events.input.window.ResizeEvent
import dev.misasi.giancarlo.math.Point4f
import dev.misasi.giancarlo.math.Vector2f
import dev.misasi.giancarlo.opengl.*
import dev.misasi.giancarlo.ux.Screen
import dev.misasi.giancarlo.ux.ScreenStack
import dev.misasi.giancarlo.ux.ScreenState
import dev.misasi.giancarlo.ux.ScreenTransition

lateinit var screenStack: ScreenStack

class TestScreen (private val assets: Assets, waitMs: Int = 0) : Screen() {
    private lateinit var spriteGfx: Sprite2dGraphics
    private lateinit var frameBuffer: FrameBuffer
    private lateinit var postProcessingGfx: Sprite2dGraphics
    private var camera = Camera()
    private var alpha = -1f
    private val screenTransition = ScreenTransition(this, mapOf(
        ScreenState.WAITING to waitMs,
        ScreenState.IN to 1500,
        ScreenState.OUT to 1500
    ))

    override fun onInit(context: DisplayContext) {
        // So screens have additional state that let us draw everything with variation
        // i.e. position, alpha
        // We may want to share instances of these resources, or simply create new for every screen
        // Possibly have a pool so that we can do loading in case init is slow

        spriteGfx = Sprite2dGraphics(context.gl, Buffer.Usage.DYNAMIC, 10000)

        val atlasGeneral = assets.atlas("General")
        val atlasOverworld = assets.atlas("Overworld")
        val orange = atlasOverworld.material("FloorOrange")
        val purple = atlasOverworld.material("FloorPurple")
        val player = atlasGeneral.material("PlayerWalkDown1")

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
        spriteGfx.putSprite(Vector2f(100f, 100f), player)
        spriteGfx.updateVertexBuffer()

        val screenSize = context.viewport.actualScreenSize.toVector2i()
        val postProcessingMaterial = StaticMaterial("postProcessing",
            Texture(context.gl, screenSize.x, screenSize.y),
            Point4f.create(Vector2f(), Vector2f(1f, 1f)).toPoint4us(),
            context.viewport.actualScreenSize
        )

        frameBuffer = FrameBuffer(context.gl)
        frameBuffer.attach(postProcessingMaterial.texture())
        postProcessingGfx = Sprite2dGraphics(context.gl, Buffer.Usage.STATIC, 1)
        postProcessingGfx.clear()
        postProcessingGfx.putSprite(Vector2f(), postProcessingMaterial)
        postProcessingGfx.updateVertexBuffer()
    }

    override fun onUpdate(elapsedMs: Int) {
        screenTransition.update(elapsedMs)

        val transitionElapsedPercentage = screenTransition.elapsedPercentage()
        if (transitionElapsedPercentage != null) {
            when (state) {
                ScreenState.IN -> alpha = Animator.fadeIn(transitionElapsedPercentage)
//                ScreenState.IN -> camera = camera.copy(position = Animator.translate(Vector2f(-800f), Vector2f(), transitionElapsedPercentage))
//                ScreenState.OUT -> camera = camera.copy(position = Animator.translate(Vector2f(), Vector2f(800f), transitionElapsedPercentage))
                else -> {}
            }
            when (state) {
                ScreenState.OUT -> alpha = Animator.fadeOut(transitionElapsedPercentage)
                else -> {}
            }
        } else {
            camera = camera.copy(position = Vector2f())
            alpha = -1f
        }
    }

    override fun onDraw(context: DisplayContext) {
        frameBuffer.bind()
        spriteGfx.bindProgram()
        spriteGfx.setModelViewProjection(context.viewport.getModelViewProjection(camera))
        spriteGfx.setAlpha(alpha)
        spriteGfx.draw()
        FrameBuffer.unbind(context.gl)

        postProcessingGfx.bindProgram()
        postProcessingGfx.setResolution(context.viewport.actualScreenSize)
        postProcessingGfx.setModelViewProjection(context.viewport.getModelViewProjection(camera))
//        postProcessingGfx.setFxaa(true)
        postProcessingGfx.setEffect(2)
        postProcessingGfx.draw()
    }

    override fun onEvent(context: DisplayContext, event: Event) {
        if (event is MouseButtonEvent) {
            screenStack.push(TestScreen(assets, 1500))
        }

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
            context.viewport.actualScreenSize = event.size
        }
    }

    override fun onDestroy(context: DisplayContext) {
        spriteGfx.delete()
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

    val assets = Assets(context.gl)

    // todo improve this variable
    screenStack = ScreenStack(context)
    screenStack.push(TestScreen(assets))
    screenStack.run()
}