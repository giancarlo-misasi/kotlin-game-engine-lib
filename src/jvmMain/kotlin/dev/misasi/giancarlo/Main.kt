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

import dev.misasi.giancarlo.*
import dev.misasi.giancarlo.drawing.*
import dev.misasi.giancarlo.events.EventQueue
import dev.misasi.giancarlo.events.SystemClock
import dev.misasi.giancarlo.events.input.gestures.detector.PanDetector
import dev.misasi.giancarlo.events.input.gestures.detector.PinchDetector
import dev.misasi.giancarlo.events.input.gestures.detector.SingleTapDetector
import dev.misasi.giancarlo.events.input.keyboard.Key
import dev.misasi.giancarlo.events.input.keyboard.KeyAction
import dev.misasi.giancarlo.events.input.keyboard.KeyEvent
import dev.misasi.giancarlo.events.input.mouse.*
import dev.misasi.giancarlo.events.input.scroll.ScrollEvent
import dev.misasi.giancarlo.math.*
import dev.misasi.giancarlo.opengl.*

fun main() {
    var r1 = Rectangle(Vector2f(200f, 200f))
    var c1 = Circle(Vector2f(), 50f)

    val result = Physics.dynamicElasticCollision(Vector2f(10f, -1f), 1f, Vector2f(-1f, 0f), 1f, Vector2f(-1f, 0f))


    val clock = SystemClock()

    val gestureDetectors = setOf(
        PanDetector(),
        SingleTapDetector(),
        PinchDetector()
    )
    val eventQueue = EventQueue(gestureDetectors)

    val context = LwjglGlfwDisplayContext(
        "title",
        Vector2f(1920f, 1080f),
        Vector2f(1920f, 1080f),
        false,
        false,
        60,
        eventQueue
    )

    context.enableKeyboardEvents(true)
//    context.enableTextEvents(true)
    context.enableMouseEvents(true)
    context.enableMouseButtonEvents(true)
    context.enableScrollEvents(true)

    val gl = context.getOpenGl()
    val viewport = context.getViewport()
    val program = createProgram(gl, "/programs/Material.program")
    val vboDynamic = createVertexBuffer(gl, "/buffers/Dynamic.buffer")
    val vboDynamic2 = createVertexBuffer(gl, "/buffers/Dynamic.buffer")
    val atlasGeneral = createAtlas(gl, "/atlases/General.atlas")
    val atlasOverworld = createAtlas(gl, "/atlases/Overworld.atlas")
    val gfx = GraphicsBuffer(vboDynamic.maxBytes)
    val gfx2 = GraphicsBuffer(vboDynamic2.maxBytes)
    var camera = Camera()//.copy(zoom = 4f)
    var mvp: Matrix4f

    // Draw static things
//    gfx.write(
//        Vector2f(), viewport.targetResolution,
//        Rgba8.LAVENDER_BLUSH
//    )
//    gfx.write(
//        Vector2f(), Vector2f(100f, 100f),
//        Rgba8.RED
//    )
//    gfx.write(
//        Vector2f(100f, 0f), Vector2f(100f, 100f),
//        Rgba8.GREEN
//    )
//    gfx.write(
//        Vector2f(200f, 0f), Vector2f(100f, 100f),
//        Rgba8.BLUE
//    )
//
//    // Update the buffer
//    gl.bindVbo(vboStatic.vertexBufferHandle)
//    gl.updateVboData(vboStatic.vertexBufferHandle, gfx.directBuffer)

    // Draw dynamic things
    val animation = Animation(atlasGeneral.getMaterialSet("PlayerWalkDown"))
    val grass = atlasOverworld.getMaterial("FloorGrass")
    val orange = atlasOverworld.getMaterial("FloorOrange")
    val purple = atlasOverworld.getMaterial("FloorPurple")
    val tree = atlasOverworld.getMaterial("Tree")

    var mousePressing = false
    var lastMousePos = Vector2f()

    // Draw loop
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
        gfx.updateVertexBuffer(vboDynamic)
        program.enableAttributes()
        program.draw(gfx.drawOrders)

        // draw the rect and circle we test intersection with
        c1 = c1.moveTo(lastMousePos)
        val inter5 = Intersection.intersection(c1, r1)

        gfx2.clear()
        gfx2.writeCircle(c1.position, c1.radius, Rgba8.YELLOW_GREEN)
        gfx2.writeRectangle(r1.tl, r1.size, if (inter5 != null) Rgba8.RED else Rgba8.GREEN)
        gfx2.writeCircle(c1.position.plus(inter5 ?: Vector2f()), c1.radius, Rgba8.PURPLE)
        gfx2.writeLine(Vector2f(), Vector2f(1920f, 1080f), Rgba8.RED) // Draw a big diagonal line to test
        gfx2.writeLine(Vector2f(0f, 100f), Vector2f(1920f, 0f), Rgba8.RED) // Draw a big diagonal line to test
        gfx2.writeLine(Vector2f(0f, 200f), Vector2f(1920f, 0f), Rgba8.RED) // Draw a big diagonal line to test
        gfx2.writeLine(Vector2f(0f, 300f), Vector2f(1920f, 0f), Rgba8.RED) // Draw a big diagonal line to test
//        gfx2.writeCircle(lastMousePos, 150f, Rgba8.ORANGE)

        gfx2.updateVertexBuffer(vboDynamic2)
        program.enableAttributes()
        program.draw(gfx2.drawOrders)

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
                    camera = camera.copy(position = camera.position.plus(delta.scale(0.5f)))
                }

                lastMousePos = event.position
            }

//            println(event)
        } while (true)

        clock.update()
    }
}

private const val DELIMITER = " "

private fun createProgram(gl: OpenGl, path: String): Program {
    var lines = getResourceAsLines(path)
    if (lines.size < 3) {
        crash("Program does not have enough lines.")
    }

    val name = lines[0]
    lines = lines.drop(1)

    val shaders = lines.take(2).map { it.split(DELIMITER) }
        .filter { it.size == 2 }
        .map { Shader(Shader.Type.valueOf(it[0]), getResourceAsString(it[1])!!) }
    lines = lines.drop(2)

    val lineData = lines.map { it.split(DELIMITER) }
    val uniforms = lineData
        .filter { it.size == 1 }
        .map { it[0] }
        .toList()
    val attributes = lineData
        .filter { it.size == 5 }
        .map { createAttribute(it) }
        .toList()

    return Program(gl, shaders, uniforms, attributes)
}

private fun createAtlas(gl: OpenGl, path: String): Atlas {
    var lines = getResourceAsLines(path)
    if (lines.size < 3) {
        crash("Atlas does not have enough lines.")
    }

    val name = lines.first()
    lines = lines.drop(1)

    // TODO: Why does material/material set just keep a handle...
    val texture = createTexture(gl, lines.first())
    lines = lines.drop(1)

    val lineData = lines.map { it.split(DELIMITER) }
    val materials = lineData
        .filter { it.size == 5 }
        .map { createMaterial(texture, it) }
        .map { it.name to it }
        .toMap()
    val materialSets = lineData
        .filter { it.size == 2 }
        .map { createMaterialSet(materials, it) }
        .map { it.name to it }
        .toMap()

    return Atlas(materials, materialSets)
}

private fun createTexture(gl: OpenGl, path: String): Texture {
    val bitmap = getResourceAsBitmap(path)!!
    return Texture(gl, bitmap)
}

private fun createMaterial(texture: Texture, data: List<String>): Material {
    val name = data[0]
    val position = Vector2f(data[1].toFloat(), data[2].toFloat())
    val size = Vector2f(data[3].toFloat(), data[4].toFloat())
    val uvPosition = position.divide(texture.size)
    val uvSize = size.divide(texture.size)
    return Material(name, texture.textureHandle, Point4.create(uvPosition, uvSize), size)
}

private fun createMaterialSet(materials: Map<String, Material>, data: List<String>): MaterialSet {
    val prefix = data[0]
    val frames = materials.values
        .filter { it.name.startsWith(prefix) }
        .toList()
        .sortedBy { it.name }
    val frameDurationMillis = data[1].toInt()
    return MaterialSet(prefix, frames, frameDurationMillis)
}

private fun createAttribute(data: List<String>): Attribute {
    val name = data[0]
    val type = Attribute.Type.valueOf(data[1])
    val count = data[2].toInt()
    val strideOffset = data[3].toInt()
    val totalStride = data[4].toInt()
    return Attribute(name, type, count, strideOffset, totalStride)
}

private fun createVertexBuffer(gl: OpenGl, path: String): VertexBuffer {
    var lines = getResourceAsLines(path)
    if (lines.size < 3) {
        crash("Buffer does not have enough lines.")
    }

    val name = lines[0]
    val maxBytes = lines[1].toInt()
    val usage = VertexBuffer.Usage.valueOf(lines[2])
    return VertexBuffer(gl, usage, maxBytes)
}