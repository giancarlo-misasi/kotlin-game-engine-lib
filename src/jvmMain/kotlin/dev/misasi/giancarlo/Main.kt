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
import dev.misasi.giancarlo.drawing.programs.Sprite2d
import dev.misasi.giancarlo.events.Event
import dev.misasi.giancarlo.events.EventQueue
import dev.misasi.giancarlo.events.input.keyboard.Key
import dev.misasi.giancarlo.events.input.keyboard.KeyAction
import dev.misasi.giancarlo.events.input.keyboard.KeyEvent
import dev.misasi.giancarlo.events.input.mouse.CursorMode
import dev.misasi.giancarlo.events.input.mouse.MouseButtonEvent
import dev.misasi.giancarlo.events.input.scroll.ScrollEvent
import dev.misasi.giancarlo.events.input.window.ResizeEvent
import dev.misasi.giancarlo.math.*
import dev.misasi.giancarlo.openal.SoundSource
import dev.misasi.giancarlo.opengl.*
import dev.misasi.giancarlo.ux.*
import dev.misasi.giancarlo.ux.effects.DayNight
import kotlin.math.*
import kotlin.random.Random

val width = 1600
val height = 1200

class TestScreen(
    private val context: DisplayContext,
    private val screenStack: ScreenStack,
    private val assets: Assets,
    waitMs: Int = 0
) : Screen() {
    private lateinit var spriteGfx: Sprite2d
    private var camera = Camera()
    private var time = 86400000 / 2
    private var alpha = -1f
    private val screenTransition = ScreenTransition(this, mapOf(
        ScreenState.WAITING to waitMs,
        ScreenState.IN to 1500,
        ScreenState.OUT to 1500
    ))

    private lateinit var walkSource: SoundSource

    override fun onInit(context: DisplayContext) {
        // So screens have additional state that let us draw everything with variation
        // i.e. position, alpha
        // We may want to share instances of these resources, or simply create new for every screen
        // Possibly have a pool so that we can do loading in case init is slow

        spriteGfx = Sprite2d(context.gl, Buffer.Usage.DYNAMIC, 10000)

        val atlasOverworld = assets.atlas("Overworld")
        val water = atlasOverworld.material("Water")
        val sand = atlasOverworld.material("FloorSand")
        val grass = atlasOverworld.material("FloorGrass")
        val tree = atlasOverworld.material("Tree")
        val purple = atlasOverworld.material("FloorPurple")

        // https://www.redblobgames.com/maps/terrain-from-noise/
        var min = Float.MAX_VALUE
        var max = Float.MIN_VALUE
        spriteGfx.clear()
        val seed = getTimeMillis()
        val random = Random(seed)
        val octaves = List(6) { SimplexNoise().shuffle(random.nextLong()) }
        for (x in 0..width step 16) {
            for (y in 0..height step 16) {
                val nx = x / width.toFloat() - 0.5f // center around origin (i.e. -0.5 to 0.5 instead of 0 to 1)
                val ny = y / width.toFloat() - 0.5f
                // todo put in aspect ratio?
                var e = SimplexNoise.fractal(octaves, nx, ny)

                // one way to re-distribute heights
                e = e.times(1.2f).pow(2.4f)
                e = constrainValue(0f, 1f, e)

                min = min(e, min)
                max = max(e, max)
                val materials = if (e <= 0.3f) {
                    listOf(water)
                } else if (e > 0.3f && e <= 0.45f) {
                    listOf(sand)
                }  else if (e > 0.45f && e <= 0.7f) {
                    listOf(grass)
                }  else {
                    listOf(grass, tree)
                }

                materials.forEach { spriteGfx.putSprite(Vector2f(x.toFloat(), y.toFloat()), Vector2f(16f, 16f), it) }
            }
        }
        println("min: $min, max: $max")
        spriteGfx.updateVertexBuffer()

        walkSource = SoundSource(context.al).attach(assets.sound("heart container 1"))
        context.al.setListenerPosition(Vector3f())
    }

    override fun onUpdate(elapsedMs: Int) {
        screenTransition.update(elapsedMs)

        val transitionElapsedPercentage = screenTransition.elapsedPercentage()
        if (transitionElapsedPercentage != null) {
            when (state) {
                ScreenState.IN -> camera = camera.copy(position = Animator.translate(Vector2f(-width.toFloat()), Vector2f(), transitionElapsedPercentage))
//                ScreenState.IN -> alpha = Animator.fadeIn(transitionElapsedPercentage)
                else -> {}
            }
            when (state) {
                ScreenState.OUT -> camera = camera.copy(position = Animator.translate(Vector2f(), Vector2f(width.toFloat()), transitionElapsedPercentage))
//                ScreenState.OUT -> alpha = Animator.fadeOut(transitionElapsedPercentage)
                else -> {}
            }
        } else {
            camera = camera.copy(position = Vector2f())
            alpha = -1f
        }

        time += elapsedMs.toFloat().times(60f).toInt() // 24-minute day
        if (time > 86400000) {
            time = 0
        }
    }

    override fun onDraw(context: DisplayContext) {
        spriteGfx.bindProgram()
        spriteGfx.setMvp(camera.mvp(context.view.targetResolution))
//        spriteGfx.setTimeSinceStartMs(time)
        spriteGfx.setAlphaOverride(alpha)
        spriteGfx.setDayNight(DayNight.calculate(time))
        spriteGfx.draw()
    }

    override fun onEvent(context: DisplayContext, event: Event) {
        if (event is ResizeEvent) {
            context.view.actualScreenSize = event.size
        }

        if (event is MouseButtonEvent) {
            screenStack.transitionToScreen(TestScreen(context, screenStack, assets, 0))
            walkSource.play()
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
    }

    override fun onDestroy(context: DisplayContext) {
        spriteGfx.delete()
    }
}

fun main() {
    val context = LwjglGlfwDisplayContext(
        "title",
        Vector2f(width.toFloat(), height.toFloat()),
        Vector2f(width.toFloat(), height.toFloat()),
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

    val assets = Assets(context)

    // todo improve this variable
    val screenStack = ScreenStack(context)
    screenStack.transitionToScreen(TestScreen(context, screenStack, assets))
    screenStack.run()
}