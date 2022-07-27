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
import dev.misasi.giancarlo.assets.Assets.Companion.assets
import dev.misasi.giancarlo.drawing.Material
import dev.misasi.giancarlo.drawing.programs.Sprite2d
import dev.misasi.giancarlo.events.Event
import dev.misasi.giancarlo.events.EventQueue
import dev.misasi.giancarlo.events.input.keyboard.Key
import dev.misasi.giancarlo.events.input.keyboard.KeyAction
import dev.misasi.giancarlo.events.input.keyboard.KeyEvent
import dev.misasi.giancarlo.events.input.mouse.CursorMode
import dev.misasi.giancarlo.events.input.scroll.ScrollEvent
import dev.misasi.giancarlo.math.*
import dev.misasi.giancarlo.noise.NoiseOctave
import dev.misasi.giancarlo.noise.NoiseOctave.Companion.noise2d
import dev.misasi.giancarlo.noise.SimplexNoise
import dev.misasi.giancarlo.openal.SoundSource
import dev.misasi.giancarlo.opengl.Camera
import dev.misasi.giancarlo.opengl.DisplayContext
import dev.misasi.giancarlo.opengl.LwjglGlfwDisplayContext
import dev.misasi.giancarlo.ux.*
import dev.misasi.giancarlo.ux.views.ScreenState
import dev.misasi.giancarlo.ux.views.View
import kotlin.math.pow

val windowWidth = 800
val windowHeight = 608
val worldWidth = 4800
val worldHeight = 4800

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
            //listOf(floorGrass, tree)
            in 0 until 30 -> listOf(water)
            in 30 until 40 -> listOf(floorSand)
            in 40 until 65 -> listOf(floorGrass)
            in 65 until 80 -> listOf(floorOrange)
            in 80 until 90 -> listOf(floorPurple)
            in 90 .. 100 -> listOf(floorBlue)
            else -> listOf(floorBlue)
        }
    }
}

class Test(
    private val app: App,
    private val assets: Assets,
    waitMs: Long = 0
) : View() {
    private var camera = Camera()//.copy(zoom = 0.25f)
    private var time: Long = 86400000 / 2
    private var alpha = -1f
//    private val screenTransition = ScreenTransition(mapOf(
//        ScreenState.WAITING to waitMs,
//        ScreenState.IN to 1500,
//        ScreenState.OUT to 1500
//    ))
    private var walkSource: SoundSource
    val overworld = Overworld(assets)
    private lateinit var noise: Grid<Float>

    init {
        // So screens have additional state that let us draw everything with variation
        // i.e. position, alpha
        // We may want to share instances of these resources, or simply create new for every screen
        // Possibly have a pool so that we can do loading in case init is slow

        val seed = getTimeMillis()
        val w = worldWidth / 16
        val h = worldHeight / 16
        noise = NoiseOctave
            .octaves(seed, 4, ::SimplexNoise, 2f, 0.65f)
            .noise2d(w, h)

        // Adjusting the normalized point adjusts how functions change the data
        // if we shift origin to the center, we can apply a circular gradient
        // if we shift to top middle we make a smile etc

        // first lets redistribute by increasing peaks and flattening valleys
        noise.forEach {
            var e = it.value
            e = e.times(1.2f).pow(1.16F)
            e = constrainValue(0f, 1f, e)
            noise.replace(it.index, e)
        }
//
//        // next, let's convert the data to an island using a square bump
        noise.forEach {
            val e = it.value
            if (e >= 0.3f) { // let's keep lakes everywhere
                val nxy = Vector2f(it.x, it.y).divide(Vector2f(w, h)).multiply(2f).minus(Vector2f(1f, 1f))
                val d = Distance.squareBump(nxy)
                noise.replace(it.index, (e + (1f - d)) / 2f)
            }
        }

        // Blockify the noise
        // Visit each vertex at most once
        // For each vertex, get the neighbors
        // OPTIONAL check if any neighbors processed, if so, skip, otherwise proceed
        // Take the average of the elevation across all the vertexes and apply it (to all involved)
        val visited = MutableList(noise.size) { false }
        for (cell in noise) {
            val i = cell.index
            if (visited[i]) {
                continue
            }

            // only process neighbors right and down from me, to avoid being too blocky
//            val neighbors = grid.neighbors(i)
            val neighbors = noise.neighbors(i, Direction.RIGHT, Direction.DOWN_RIGHT, Direction.DOWN)
            if (neighbors.any { visited[it.index] }) {
                continue
            }

//            val avg = neighbors.sumOf { it.value } / neighbors.size
            val max = neighbors.maxOf { it.value }
            visited[i] = true
            noise.replace(i, max)
            neighbors.forEach {
                visited[it.index] = true
                noise.replace(it.index, max)
            }
        }

        walkSource = SoundSource(app.context.al).attach(assets.sound("heart container 1"))
        app.context.al.setListenerPosition(Vector3f())
    }

    override fun onUpdate(context: DisplayContext, elapsedMs: Long) {
//        screenTransition.update(elapsedMs)

        val transitionElapsedPercentage = null//screenTransition.elapsedPercentage()
        if (transitionElapsedPercentage != null) {
            // TODO: Figure out screen to view relationship so I can animate screen state in a view
//            when (screen.state) {
//                ScreenState.IN -> camera = camera.copy(position = Animator.translate(Vector2f(-windowWidth.toFloat()), Vector2f(), transitionElapsedPercentage))
////                ScreenState.IN -> alpha = Animator.fadeIn(transitionElapsedPercentage)
//                else -> {}
//            }
//            when (screen.state) {
//                ScreenState.OUT -> camera = camera.copy(position = Animator.translate(Vector2f(), Vector2f(windowWidth.toFloat()), transitionElapsedPercentage))
////                ScreenState.OUT -> alpha = Animator.fadeOut(transitionElapsedPercentage)
//                else -> {}
//            }
        } else {
            camera = camera.copy(position = Vector2f())
            alpha = -1f
        }

        time += elapsedMs.toFloat().times(60 * 60f).toInt() // 24-minute day
        if (time > 86400000) {
            time = 0
        }
    }

    private fun onDraw(context: DisplayContext, gfx: Sprite2d) {
        gfx.bindProgram()
        gfx.setMvp(camera.mvp(context.view.targetResolution.toVector2f()))
//        spriteGfx.setTimeSinceStartMs(time)
        gfx.setAlphaOverride(alpha)
//        spriteGfx.setShake(Shake.calculate(time))
//        spriteGfx.setDayNight(DayNight.calculate(time))

//        gfx.clear() // should like have 2 methods, one to fill buffers, one to draw them
        noise.forEach {
            overworld.fromElevation(it.value).forEach { material ->
                gfx.putSprite(Vector2f(it.x.toFloat(), it.y.toFloat()).multiply(16f), Vector2f(16f, 16f), material)
            }
        }
        gfx.updateVertexBuffer()

        gfx.draw()
    }

    override fun onEvent(context: DisplayContext, event: Event): Boolean {


//        if (event is MouseButtonEvent) {
//            val screen = Screen()
//            app.transitionToScreen(Test(app, assets, 0))
//            walkSource.play()
//        }

        if (event is KeyEvent) {
            if (event.key == Key.KEY_ESCAPE && event.action == KeyAction.RELEASE) {
                context.close()
                return true
            }
        }

        if (event is KeyEvent) {
            if (event.key == Key.KEY_F && event.action == KeyAction.RELEASE) {
                context.setCursorMode(CursorMode.FPS)
                return true
            } else if (event.key == Key.KEY_N && event.action == KeyAction.RELEASE) {
                context.setCursorMode(CursorMode.NORMAL)
                return true
            }
        }

        if (event is ScrollEvent) {
            if (event.offset.y > 0) {
                camera = camera.copy(zoom = camera.zoom.times(event.offset.y * 1.1f))
                return true
            } else if (event.offset.y < 0) {
                camera = camera.copy(zoom = camera.zoom.div(-event.offset.y * 1.1f))
                return true
            }
        }

        return false
    }
}

fun main() {
    val context = LwjglGlfwDisplayContext(
        "title",
        Vector2i(windowWidth, windowHeight),
        Vector2i(windowWidth, windowHeight),
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

    val assets = context.assets()
    val overworld = Overworld(assets)

    // todo improve this variable
    val app = App(context)
    val test = Test(app, assets)
//    val group1 = BoxLayout(overworld.floorOrange, LayoutInset(16, 16), LayoutInset(32, 32))
//    val group2 = SimpleBoxLayoutViewGroup(overworld.floorBlue, margin = LayoutMargin(50, 50))
//    group2.add(test)
//    group1.add(test)

//    val screen = Screen(assets, Sprite2d(app.context.gl, Buffer.Usage.DYNAMIC, 100000), group1)
//    app.transitionToScreen(screen)
//    screen.goToNextState()

//    app.run()
}