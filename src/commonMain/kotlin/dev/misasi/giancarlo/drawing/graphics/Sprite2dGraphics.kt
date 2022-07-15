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

package dev.misasi.giancarlo.drawing.graphics

import dev.misasi.giancarlo.assets.constants.Shaders
import dev.misasi.giancarlo.drawing.DrawOrder
import dev.misasi.giancarlo.drawing.Material
import dev.misasi.giancarlo.math.*
import dev.misasi.giancarlo.memory.DirectNativeByteBuffer
import dev.misasi.giancarlo.opengl.*

class Sprite2dGraphics(private val gl: OpenGl, bufferUsage: Buffer.Usage, maxEntities: Int) {
    private val program = Program(gl, Shaders.sprite2d())
    private val uniformMap = UniformMap(gl, program, listOf("uResolution", "uMvp", "uFxaa", "uAlpha", "uEffect"))
    private val attributeArray = AttributeArray(
        gl, program, listOf(
            Attribute.Spec("inXy", DataType.FLOAT, 2),
            Attribute.Spec("inUv", DataType.UNSIGNED_SHORT, 2, normalize = true),
            Attribute.Spec("inAlpha", DataType.FLOAT, 1)
        )
    )
    private val indexBuffer: IndexBuffer
    private val totalSizeInBytes = 4 * attributeArray.totalSizeInBytes * maxEntities
    private val vertexBuffer = VertexBuffer(gl, bufferUsage, totalSizeInBytes)
    private val directBuffer = DirectNativeByteBuffer(totalSizeInBytes)
    private var drawOrders: MutableList<DrawOrder> = mutableListOf()

    init {
        val indexes = generateTriangleIndexes(maxEntities)
        val data = DirectNativeByteBuffer(DataType.INT.size * indexes.size).putInts(indexes)
        indexBuffer = IndexBuffer(gl, Buffer.Usage.STATIC, data)
        attributeArray.attach(listOf(indexBuffer, vertexBuffer))
    }

    fun bindProgram() = program.bind()
    fun setResolution(resolution: Vector2f) = uniformMap.update("uResolution", resolution)
    fun setMvp(mvp: Matrix4f) = uniformMap.update("uMvp", mvp)
    fun setFxaa(enabled: Boolean) = uniformMap.update("uFxaa", enabled)
    fun setAlpha(alpha: Float) = uniformMap.update("uAlpha", alpha)
    fun setEffect(effect: Int) = uniformMap.update("uEffect", effect)
    fun updateVertexBuffer() = vertexBuffer.bind().update(directBuffer)
    fun draw() = DrawOrder.drawIndexed(gl, DataType.UNSIGNED_INT, drawOrders)
    fun clear() = directBuffer.reset().also { drawOrders.clear() }

    fun putSprite(position: Vector2f, size: Vector2f, material: Material, rotation: Rotation? = null, flip: Flip? = null, alpha: UByte? = null) {
        DrawOrder.updateDrawOrder(drawOrders, program, attributeArray, material)
        putSpriteIndexed(
            Point4f.create(position, size, rotation),
            material.coordinates().flip(flip).toPoint4us(),
            alpha?.toFloat()?.div(UByte.MAX_VALUE.toFloat()) ?: -1f
        )
    }

    fun delete() {
        attributeArray.delete()
        indexBuffer.delete()
        vertexBuffer.delete()
        program.delete()
        drawOrders.clear()
    }

    private fun putSpriteIndexed(xy: Point4f, uv: Point4us, alpha: Float) {
        directBuffer.putVector2f(xy.tl)
        directBuffer.putVector2us(uv.tl)
        directBuffer.putFloat(alpha)

        directBuffer.putVector2f(xy.bl)
        directBuffer.putVector2us(uv.bl)
        directBuffer.putFloat(alpha)

        directBuffer.putVector2f(xy.br)
        directBuffer.putVector2us(uv.br)
        directBuffer.putFloat(alpha)

        directBuffer.putVector2f(xy.tr)
        directBuffer.putVector2us(uv.tr)
        directBuffer.putFloat(alpha)
    }

    private fun generateTriangleIndexes(count: Int): List<Int> {
        return (0 until count).map { i ->
            val start = 4 * i
            arrayOf(0, 1, 2, 2, 3, 0).map { j -> start + j }
        }.flatten()
    }
}