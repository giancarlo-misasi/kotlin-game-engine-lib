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

package dev.misasi.giancarlo.drawing

import dev.misasi.giancarlo.math.Point4
import dev.misasi.giancarlo.math.Rotation
import dev.misasi.giancarlo.math.Rotation.None
import dev.misasi.giancarlo.math.Vector2f
import dev.misasi.giancarlo.memory.DirectByteBuffer
import dev.misasi.giancarlo.opengl.VertexBuffer
import kotlin.math.cos
import kotlin.math.sin

class GraphicsBuffer(
    maxBytes: Int,
) {
    private val directBuffer: DirectByteBuffer = DirectByteBuffer(maxBytes)
    val drawOrders: MutableList<DrawOrder> = mutableListOf()

    fun writeSprite(position: Vector2f, material: Material, rotation: Rotation = None, alpha: Float = NO_ALPHA) {
        val drawOrder = drawOrders.lastOrNull()
        if (drawOrder == null || drawOrder.isDifferent(DrawOrder.Type.SQUARE, material.textureHandle)) {
            drawOrders.add(DrawOrder(material.textureHandle))
        } else {
            drawOrders[drawOrders.lastIndex] = drawOrder.copy(count = drawOrder.count + 1)
        }

        write(
            Point4.create(position, material.size, rotation),
            material.coordinates,
            alpha
        )
    }

    fun writeLine(point1: Vector2f, point2: Vector2f, color: Rgba8) {
        val drawOrder = drawOrders.lastOrNull()
        if (drawOrder == null || drawOrder.isDifferent(DrawOrder.Type.LINE, color = color)) {
            drawOrders.add(DrawOrder(DrawOrder.Type.LINE, color))
        } else {
            drawOrders[drawOrders.lastIndex] = drawOrder.copy(count = drawOrder.count + 1)
        }

        write(point1)
        write(color)
        write(point2)
        write(color)
    }

    fun writeTriangle(point1: Vector2f, point2: Vector2f, point3: Vector2f, color: Rgba8) {
        val drawOrder = drawOrders.lastOrNull()
        if (drawOrder == null || drawOrder.isDifferent(DrawOrder.Type.TRIANGLE, color = color)) {
            drawOrders.add(DrawOrder(DrawOrder.Type.TRIANGLE, color))
        } else {
            drawOrders[drawOrders.lastIndex] = drawOrder.copy(count = drawOrder.count + 1)
        }

        write(point1)
        write(color)
        write(point2)
        write(color)
        write(point3)
        write(color)
    }

    fun writeRectangle(position: Vector2f, size: Vector2f, color: Rgba8) {
        val drawOrder = drawOrders.lastOrNull()
        if (drawOrder == null || drawOrder.isDifferent(DrawOrder.Type.SQUARE, color = color)) {
            drawOrders.add(DrawOrder(DrawOrder.Type.SQUARE, color))
        } else {
            drawOrders[drawOrders.lastIndex] = drawOrder.copy(count = drawOrder.count + 1)
        }

        write(
            Point4.create(position, size),
            color
        )
    }

    fun writeCircle(position: Vector2f, radius: Float, color: Rgba8) {
        val numberOfPointsToUse = 16
        val points = (0 until numberOfPointsToUse).map {
            Vector2f(
                (position.x + (radius * cos(it * TWO_PI / numberOfPointsToUse))).toFloat(),
                (position.y + (radius * sin(it * TWO_PI / numberOfPointsToUse))).toFloat()
            )
        }

        val triangles = points.zipWithNext().plus(Pair(points.last(), points.first()))
        triangles.forEach {
            writeTriangle(position, it.first, it.second, color)
        }
    }

    fun updateVertexBuffer(vertexBuffer: VertexBuffer) {
        vertexBuffer.update(directBuffer)
    }

    fun clear() {
        directBuffer.clear()
        drawOrders.clear()
    }

    private fun write(xy: Point4, uv: Point4, alpha: Float) {
        write(xy.tl)
        write(uv.tl)
        write(USE_TEXTURE_COORDINATES)
        write(alpha)

        write(xy.bl)
        write(uv.tl.x)
        write(uv.br.y)
        write(USE_TEXTURE_COORDINATES)
        write(alpha)

        write(xy.br)
        write(uv.br)
        write(USE_TEXTURE_COORDINATES)
        write(alpha)

        write(xy.br)
        write(uv.br)
        write(USE_TEXTURE_COORDINATES)
        write(alpha)

        write(xy.tr)
        write(uv.br.x)
        write(uv.tl.y)
        write(USE_TEXTURE_COORDINATES)
        write(alpha)

        write(xy.tl)
        write(uv.tl)
        write(USE_TEXTURE_COORDINATES)
        write(alpha)
    }

    private fun write(xy: Point4, color: Rgba8) {
        write(xy.tl)
        write(color)

        write(xy.bl)
        write(color)

        write(xy.br)
        write(color)

        write(xy.br)
        write(color)

        write(xy.tr)
        write(color)

        write(xy.tl)
        write(color)
    }

    private fun write(xy: Vector2f) {
        write(xy.x)
        write(xy.y)
    }

    private fun write(color: Rgba8) {
        write(color.floatR)
        write(color.floatG)
        write(color.floatB)
        write(color.floatA)
    }

    private fun write(value: Float) {
        directBuffer.putFloat(value)
    }

    companion object {
        // 2 triangles of 3 points each with 6 floats, 144 bytes per rect
        const val ENTITY_SIZE: Int = 144

        // This will inform the shader that texture coordinates should be used
        // If not provided, the color provided will be used
        const val USE_TEXTURE_COORDINATES: Float = -1f

        // When rendering materials, allow user to make them
        // transparent (on top of their existing transparency)
        const val NO_ALPHA: Float = -1f;

        // 2 PI
        const val TWO_PI = 2 * kotlin.math.PI
    }
}
