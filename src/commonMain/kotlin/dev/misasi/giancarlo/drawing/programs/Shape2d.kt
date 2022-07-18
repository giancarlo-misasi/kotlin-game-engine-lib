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

package dev.misasi.giancarlo.drawing.programs

import dev.misasi.giancarlo.assets.constants.Shaders
import dev.misasi.giancarlo.drawing.DrawOrder
import dev.misasi.giancarlo.drawing.Rgba8
import dev.misasi.giancarlo.drawing.ShapeType
import dev.misasi.giancarlo.math.Matrix4f
import dev.misasi.giancarlo.math.Point4f
import dev.misasi.giancarlo.math.Vector2f
import dev.misasi.giancarlo.memory.DirectNativeByteBuffer
import dev.misasi.giancarlo.opengl.*
import kotlin.math.cos
import kotlin.math.sin

class Shape2d(private val gl: OpenGl, bufferUsage: Buffer.Usage, maxEntities: Int) {
    private val program = Program(gl, Shaders.shape2d(), listOf("uMvp"))
    private val attributeArray = AttributeArray(
        gl, program, listOf(
            Attribute.Spec("inXy", DataType.FLOAT, 2),
            Attribute.Spec("inColor", DataType.UNSIGNED_BYTE, 4, normalize = true)
        )
    )
    private val totalSizeInBytes = 6 * attributeArray.totalSizeInBytes * maxEntities // 6 points, TODO optimize to 4
    private val vertexBuffer = VertexBuffer(gl, bufferUsage, totalSizeInBytes)
    private val directBuffer = DirectNativeByteBuffer(totalSizeInBytes)
    private var drawOrders: MutableList<DrawOrder> = mutableListOf()

    init {
        attributeArray.attach(vertexBuffer)
    }

    fun bindProgram() = program.bind()
    fun setMvp(mvp: Matrix4f) = program.setUniformMatrix4f(Sprite2d.Uniform.MVP.id, mvp)
    fun updateVertexBuffer() = vertexBuffer.bind().update(directBuffer)
    fun draw() = DrawOrder.draw(gl, drawOrders)
    fun clear() = directBuffer.reset().also { drawOrders.clear() }

    fun putLine(point1: Vector2f, point2: Vector2f, color: Rgba8) {
        DrawOrder.updateDrawOrder(drawOrders, program, attributeArray, ShapeType.LINE)
        directBuffer.putVector2f(point1)
        directBuffer.putRgba8(color)
        directBuffer.putVector2f(point2)
        directBuffer.putRgba8(color)
    }

    fun putTriangle(point1: Vector2f, point2: Vector2f, point3: Vector2f, color: Rgba8) {
        DrawOrder.updateDrawOrder(drawOrders, program, attributeArray, ShapeType.TRIANGLE)
        directBuffer.putVector2f(point1)
        directBuffer.putRgba8(color)
        directBuffer.putVector2f(point2)
        directBuffer.putRgba8(color)
        directBuffer.putVector2f(point3)
        directBuffer.putRgba8(color)
    }

    fun putRectangle(position: Vector2f, size: Vector2f, color: Rgba8) {
        DrawOrder.updateDrawOrder(drawOrders, program, attributeArray, ShapeType.SQUARE)
        putSquare(Point4f.create(position, size), color)
    }

    fun putCircle(position: Vector2f, radius: Float, color: Rgba8) {
        getTrianglePointsForCircle(position, radius).forEach {
            putTriangle(position, it.first, it.second, color)
        }
    }

    private fun putSquare(xy: Point4f, color: Rgba8) {
        directBuffer.putVector2f(xy.tl)
        directBuffer.putRgba8(color)
        directBuffer.putVector2f(xy.bl)
        directBuffer.putRgba8(color)
        directBuffer.putVector2f(xy.br)
        directBuffer.putRgba8(color)
        directBuffer.putVector2f(xy.br)
        directBuffer.putRgba8(color)
        directBuffer.putVector2f(xy.tr)
        directBuffer.putRgba8(color)
        directBuffer.putVector2f(xy.tl)
        directBuffer.putRgba8(color)
    }

    private fun getTrianglePointsForCircle(position: Vector2f, radius: Float): List<Pair<Vector2f, Vector2f>> {
        val numberOfPointsToUse = 16
        val points = (0 until numberOfPointsToUse).map {
            Vector2f(
                (position.x + (radius * cos(it * TWO_PI / numberOfPointsToUse))).toFloat(),
                (position.y + (radius * sin(it * TWO_PI / numberOfPointsToUse))).toFloat()
            )
        }
        return points.zipWithNext().plus(Pair(points.last(), points.first()))
    }

    companion object {
        const val TWO_PI = 2 * kotlin.math.PI
    }
}