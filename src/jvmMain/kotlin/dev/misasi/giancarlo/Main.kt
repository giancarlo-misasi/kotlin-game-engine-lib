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
import dev.misasi.giancarlo.drawing.Material
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
import dev.misasi.giancarlo.math.Vector2f
import dev.misasi.giancarlo.math.Vector3f
import dev.misasi.giancarlo.noise.Noise
import dev.misasi.giancarlo.noise.Noise.Companion.noise2d
import dev.misasi.giancarlo.noise.SimplexNoise
import dev.misasi.giancarlo.openal.SoundSource
import dev.misasi.giancarlo.opengl.Buffer
import dev.misasi.giancarlo.opengl.Camera
import dev.misasi.giancarlo.opengl.DisplayContext
import dev.misasi.giancarlo.opengl.LwjglGlfwDisplayContext
import dev.misasi.giancarlo.ux.Screen
import dev.misasi.giancarlo.ux.ScreenStack
import dev.misasi.giancarlo.ux.ScreenState
import dev.misasi.giancarlo.ux.ScreenTransition
import dev.misasi.giancarlo.ux.effects.DayNight

val width = 1600
val height = 1200

class Overworld(assets: Assets) {

    private val atlas = assets.atlas("Overworld")

    val bigRock = atlas.material("BigRock")
    val bigTree = atlas.material("BigTree")
    val bigTreeStump = atlas.material("BigTreeStump")
    val cliffInnerCorner = atlas.material("CliffInnerCorner")
    val cliffOuterCorner = atlas.material("CliffOuterCorner")
    val cliffWall = atlas.material("CliffWall")
    val floorBlue = atlas.material("FloorBlue")
    val floorCliff1 = atlas.material("FloorCliff1")
    val floorCliff2 = atlas.material("FloorCliff2")
    val floorCliff3 = atlas.material("FloorCliff3")
    val floorGrass = atlas.material("FloorGrass")
    val floorOrange = atlas.material("FloorOrange")
    val floorPurple = atlas.material("FloorPurple")
    val floorRed = atlas.material("FloorRed")
    val floorSand = atlas.material("FloorSand")
    val floorTeal = atlas.material("FloorTeal")
    val floorYellow = atlas.material("FloorYellow")
    val rock = atlas.material("Rock")
    val stairs = atlas.material("Stairs")
    val tree = atlas.material("Tree")
    val treeStump = atlas.material("TreeStump")
    val water = atlas.material("Water")

    fun fromElevation(elevation: Float): List<Material> {
        // elevation falls into 0 to 1
        return when (elevation.times(100).toInt()) {
            in 0 until 20 -> listOf(water)
            in 20 until 35 -> listOf(floorSand)
            in 35 until 55 -> listOf(floorGrass)
            in 55 until 65 -> listOf(floorGrass, tree)
            in 65 until 80 -> listOf(floorOrange)
            in 80 .. 100 -> listOf(floorPurple)
            else -> listOf(floorBlue)
        }
    }
}

class TestScreen(
    private val context: DisplayContext,
    private val screenStack: ScreenStack,
    private val assets: Assets,
    waitMs: Long = 0
) : Screen() {
    private lateinit var spriteGfx: Sprite2d
    private var camera = Camera()
    private var time: Long = 86400000 / 2
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


        val seed = getTimeMillis()
        val points = Noise.points(width, height, 16)
        val octaves = Noise.octaves(seed, 3, 1.99f, 0.79f, ::SimplexNoise)
        val noise = octaves.noise2d(points)

        spriteGfx = Sprite2d(context.gl, Buffer.Usage.DYNAMIC, 10000)
        val overworld = Overworld(assets)
        noise.forEach {
            overworld.fromElevation(it.value).forEach { material ->
                spriteGfx.putSprite(it.key, Vector2f(16f, 16f), material)
            }
        }
        spriteGfx.updateVertexBuffer()

        walkSource = SoundSource(context.al).attach(assets.sound("heart container 1"))
        context.al.setListenerPosition(Vector3f())
    }

    override fun onUpdate(elapsedMs: Long) {
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

        time += elapsedMs.toFloat().times(60 * 60f).toInt() // 24-minute day
        if (time > 86400000) {
            time = 0
        }
    }

    override fun onDraw(context: DisplayContext) {
        spriteGfx.bindProgram()
        spriteGfx.setMvp(camera.mvp(context.view.targetResolution))
//        spriteGfx.setTimeSinceStartMs(time)
        spriteGfx.setAlphaOverride(alpha)
//        spriteGfx.setShake(Shake.calculate(time))
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