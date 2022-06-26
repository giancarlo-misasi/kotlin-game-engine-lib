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
import dev.misasi.giancarlo.events.SystemClock
import dev.misasi.giancarlo.events.input.gestures.detector.PanDetector
import dev.misasi.giancarlo.events.input.gestures.detector.PinchDetector
import dev.misasi.giancarlo.events.input.gestures.detector.SingleTapDetector
import dev.misasi.giancarlo.math.Point4
import dev.misasi.giancarlo.math.Vector2f
import dev.misasi.giancarlo.opengl.*

fun main() {
    println("Hello")

    val clock = SystemClock()
    val gestureDetectors = setOf(
        PanDetector(),
        SingleTapDetector(),
        PinchDetector()
    )

    val context = LwjglGlfwDisplayContext(
        gestureDetectors,
        "title",
        Vector2f(1920f, 1080f),
        Vector2f(1920f, 1080f),
        false,
        false,
        60
    )

    val gl = LwjglOpenGl()

    val targetResolution = Vector2f(1920f, 1080f)
    val program = createProgram(gl, "/programs/Material.program")
//    val vboStatic = createVertexBuffer(gl, "/buffers/Static.buffer")
    val vboDynamic = createVertexBuffer(gl, "/buffers/Dynamic.buffer")
    val atlasGeneral = createAtlas(gl, "/atlases/General.atlas")
    val atlasOverworld = createAtlas(gl, "/atlases/Overworld.atlas")
    val gfx = GraphicsBuffer(10000)
    val viewport = Viewport(gl, targetResolution, targetResolution)
    val camera = Camera()//.copy(zoom = 4f)
    val mvp = viewport.getModelViewProjection(camera)

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
    val tree = atlasOverworld.getMaterial("Tree")

    // Draw loop
    gl.setClearColor(Rgba8.BLACK)
    while (!context.shouldClose()) {
        gl.clear()

        gfx.clear()
        for (x in 0..1920 step 16) {
            for (y in 0..1080 step 16) {
                gfx.write(Vector2f(x.toFloat(), y.toFloat()), grass)
            }
        }

        gfx.write(Vector2f(128f, 296f), tree)
        gfx.write(Vector2f(355f, 312f), tree)
        gfx.write(Vector2f(512f, 800f), tree)
        gfx.write(Vector2f(725f, 256f), tree)
        gfx.write(Vector2f(64f, 64f), animation.currentFrame())

        gl.bindVbo(vboDynamic.vertexBufferHandle)
        gl.updateVboData(vboDynamic.vertexBufferHandle, gfx.directBuffer)

        // TODO: GraphicsBuffer is currently per VBO because we need DrawOrders...
        // Is that the desired behavior?
        program.draw(
            gl, mapOf(
                "uMvp" to mvp,
                "uEffect" to 0
            ), gfx.drawOrders, vboDynamic
        )

        context.swapBuffers()
        context.pollEvents()
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
        .filter { it.size == 4 }
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
    val count = data[1].toInt()
    val strideOffset = data[2].toInt()
    val totalStride = data[3].toInt()
    return Attribute(name, count, strideOffset, totalStride)
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