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
import dev.misasi.giancarlo.drawing.Material
import dev.misasi.giancarlo.memory.DirectNativeByteBuffer
import dev.misasi.giancarlo.math.*
import dev.misasi.giancarlo.opengl.*
import dev.misasi.giancarlo.ux.effects.DayNight

class Sprite2d(private val gl: OpenGl, bufferUsage: Buffer.Usage, maxEntities: Int) {
    enum class Uniform(val id: String) {
        MVP("uMvp"),
//        TIME("uTimeSinceStartMs"),
        ALPHA("uAlpha"),
        SEPIA("uSepia"),
        RETRO("uRetro"),
        INVERT("uInvert"),
        DAY_NIGHT("uDayNight"),
        DAY_NIGHT_ALPHA("uDayNightAlpha"),
        DAY_NIGHT_COLOR_MIX("uDayNightColorMix"),
        DAY_NIGHT_AM("uDayNightColorAm");

        companion object {
            fun uniforms(): List<String> = Uniform.values().map { it.id }
        }
    }

    private val program = Program(gl, Shaders.sprite2d(), Uniform.uniforms())
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
    fun setMvp(mvp: Matrix4f) = program.setUniformMatrix4f(Uniform.MVP.id, mvp)
//    fun setTimeSinceStartMs(ms: Int) = program.setUniformInt(Uniform.TIME.id, ms)
    fun setAlphaOverride(alpha: Float) = program.setUniformFloat(Uniform.ALPHA.id, alpha)
    fun setSepia(enabled: Boolean) = program.setUniformBoolean(Uniform.SEPIA.id, enabled)
    fun setRetro(enabled: Boolean) = program.setUniformBoolean(Uniform.RETRO.id, enabled)
    fun setInvert(enabled: Boolean) = program.setUniformBoolean(Uniform.INVERT.id, enabled)
    fun setDayNight(dayNight: DayNight?) {
       if (dayNight == null) {
           program.setUniformBoolean(Uniform.DAY_NIGHT.id, false)
       } else {
           program.setUniformBoolean(Uniform.DAY_NIGHT.id, true)
           program.setUniformFloat(Uniform.DAY_NIGHT_ALPHA.id, dayNight.alpha)
           program.setUniformFloat(Uniform.DAY_NIGHT_COLOR_MIX.id, dayNight.colorMix)
           program.setUniformBoolean(Uniform.DAY_NIGHT_AM.id, dayNight.am)
       }
    }

    fun updateVertexBuffer() = vertexBuffer.bind().update(directBuffer)
    fun draw() = DrawOrder.drawIndexed(gl, DataType.UNSIGNED_INT, drawOrders)
    fun clear() = directBuffer.reset().also { drawOrders.clear() }

    fun putSprite(
        position: Vector2f,
        size: Vector2f,
        material: Material,
        rotation: Rotation? = null,
        flip: Flip? = null,
        alpha: UByte? = null
    ) {
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