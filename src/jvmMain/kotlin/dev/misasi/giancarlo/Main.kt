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

import dev.misasi.giancarlo.drawing.DrawState
import dev.misasi.giancarlo.drawing.Material
import dev.misasi.giancarlo.drawing.StaticMaterial
import dev.misasi.giancarlo.events.Event
import dev.misasi.giancarlo.events.input.keyboard.Key
import dev.misasi.giancarlo.events.input.keyboard.KeyAction
import dev.misasi.giancarlo.events.input.keyboard.KeyEvent
import dev.misasi.giancarlo.events.input.mouse.MouseButton
import dev.misasi.giancarlo.events.input.mouse.MouseButtonAction
import dev.misasi.giancarlo.events.input.mouse.MouseButtonEvent
import dev.misasi.giancarlo.events.input.mouse.MouseEvent
import dev.misasi.giancarlo.events.input.scroll.ScrollEvent
import dev.misasi.giancarlo.math.*
import dev.misasi.giancarlo.noise.NoiseOctave
import dev.misasi.giancarlo.noise.NoiseOctave.Companion.noise2d
import dev.misasi.giancarlo.noise.SimplexNoise
import dev.misasi.giancarlo.system.System.Companion.getCurrentTimeMs
import dev.misasi.giancarlo.ux.App
import dev.misasi.giancarlo.ux.AppContext
import dev.misasi.giancarlo.ux.Renderer
import dev.misasi.giancarlo.ux.View
import dev.misasi.giancarlo.ux.transitions.Slide
import dev.misasi.giancarlo.ux.transitions.Transition
import dev.misasi.giancarlo.ux.views.HorizontalLayout
import dev.misasi.giancarlo.ux.views.VerticalLayout
import kotlin.math.pow

val windowWidth = 800
val windowHeight = 800

val worldWidth = 4800
val worldHeight = 4800

class Overworld(overworld: Map<String, StaticMaterial>) {

    val bigRock = overworld["BigRock"]!!
    val bigTree = overworld["BigTree"]!!
    val bigTreeStump = overworld["BigTreeStump"]!!
    val cliffInnerCorner = overworld["CliffInnerCorner"]!!
    val cliffOuterCorner = overworld["CliffOuterCorner"]!!
    val cliffWall = overworld["CliffWall"]!!
    val floorBlue = overworld["FloorBlue"]!!
    val floorCliff1 = overworld["FloorCliff1"]!!
    val floorCliff2 = overworld["FloorCliff2"]!!
    val floorCliff3 = overworld["FloorCliff3"]!!
    val floorGrass = overworld["FloorGrass"]!!
    val floorOrange = overworld["FloorOrange"]!!
    val floorPurple = overworld["FloorPurple"]!!
    val floorRed = overworld["FloorRed"]!!
    val floorSand = overworld["FloorSand"]!!
    val floorTeal = overworld["FloorTeal"]!!
    val floorYellow = overworld["FloorYellow"]!!
    val rock = overworld["Rock"]!!
    val stairs = overworld["Stairs"]!!
    val tree = overworld["Tree"]!!
    val treeStump = overworld["TreeStump"]!!
    val water = overworld["Water"]!!

    fun fromElevation(elevation: Float): List<Material> {
        // elevation falls into 0 to 1
        return when (elevation.times(100).toInt()) {
            //listOf(floorGrass, tree)
            in 0 until 30 -> listOf(water)
            in 30 until 40 -> listOf(floorSand)
            in 40 until 65 -> listOf(floorGrass)
            in 65 until 80 -> listOf(floorOrange)
            in 80 until 90 -> listOf(floorPurple)
            in 90..100 -> listOf(floorBlue)
            else -> listOf(floorBlue)
        }
    }
}

fun generateTerrain(): Grid<Float> {
    val seed = getCurrentTimeMs()
    val w = worldWidth / 16
    val h = worldHeight / 16
    val noise = NoiseOctave
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

    // next, let's convert the data to an island using a square bump
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
    return noise
}

data class TestData(
    var time: Int = 86400000 / 2,
    var alpha: Float = -1f,
    var noise: Grid<Float> = generateTerrain(),
    var position: Vector2f = Vector2f(),
    var zoom: Float = 1f,
    var rotation: Rotation? = null,
    var reflection: Reflection? = null,
)

class TestView : View() {
    val data = TestData()
    var mousePressing = false
    var lastMousePos = Vector2f()

    override fun onEvent(context: AppContext, event: Event): Boolean {
        if (event is KeyEvent) {
            if (event.key == Key.KEY_ESCAPE && event.action == KeyAction.RELEASE) {
//                context.window.close()
                return true
            }

            if (event.key == Key.KEY_R && event.action == KeyAction.RELEASE) {
                data.rotation = when (data.rotation) {
                    Rotation.DEGREES_90 -> Rotation.DEGREES_180
                    Rotation.DEGREES_180 -> Rotation.DEGREES_270
                    Rotation.DEGREES_270 -> null
                    else -> Rotation.DEGREES_90
                }
                return true
            }

            if (event.key == Key.KEY_F && event.action == KeyAction.RELEASE) {
                data.reflection = when (data.reflection) {
                    Reflection.VERTICAL -> Reflection.HORIZONTAL
                    Reflection.HORIZONTAL -> Reflection.BOTH
                    Reflection.BOTH -> null
                    else -> Reflection.VERTICAL
                }
                return true
            }
        }

        if (event is ScrollEvent) {
            if (event.offset.y > 0) {
                data.zoom *= event.offset.y * 1.1f
                return true
            } else if (event.offset.y < 0) {
                data.zoom /= -event.offset.y * 1.1f
                return true
            }
        }

        if (event is MouseButtonEvent) {
            if (event.button == MouseButton.LEFT) {
                if (event.action == MouseButtonAction.PRESS) {
                    mousePressing = true
                } else if (event.action == MouseButtonAction.RELEASE) {
                    mousePressing = false
                }
            }
        }

        if (event is MouseEvent) {
            val delta = lastMousePos.minus(event.position)
            if (mousePressing) {
                data.position = data.position.plus(delta)
            }

            lastMousePos = event.position
        }

        return false
    }

    override fun onElapsed(context: AppContext, elapsedMs: Long) {
    }
}

class MyView : View() {
    override fun onEvent(context: AppContext, event: Event): Boolean {
        if (event is MouseButtonEvent) {
            if (event.button == MouseButton.LEFT) {
                context.go(MyView(), inTransition = Slide.slideUp(
                    context.viewport.designedResolution.y.toFloat(),
                    Vector2f(0, context.viewport.designedResolution.y))
                )
            }
        }
        return false
    }
}

class MyViewRenderer : Renderer {
    override fun render(target: Any, context: AppContext, state: DrawState) {
        if (target !is MyView) return

        val contentSize = target.onMeasure(context)
        state.putSprite("White", AffineTransform(
            scale = Vector2f(400, 400),
            translation = Vector2f(400f, 400f),
        ))
        state.putSprite("Black", AffineTransform(
            scale = Vector2f(400, 400),
        ))
        state.putSprite("PlayerWalkDown1", AffineTransform(
            scale = Vector2f(100, 100),
            translation = Vector2f(100, 100),
        ))
    }
}

fun main() {
    val app = App(
        "title",
        Vector2i(windowWidth + 100, windowHeight),
        fullScreen = false,
        refreshRate = 60,
        vsync = false,
        Vector2i(windowWidth, windowHeight),
    )
    app.enableResizeEvents(true)
    app.enableMouseEvents(true)
    app.enableMouseButtonEvents(true)
    app.register(MyView::class, MyViewRenderer())

    val rootView = HorizontalLayout()
    val child1 = VerticalLayout()
    val child2 = MyView()
    child1.add(child2)
    rootView.children.add(child1)
    app.run(rootView);
}