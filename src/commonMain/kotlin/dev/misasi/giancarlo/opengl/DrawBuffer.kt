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

package dev.misasi.giancarlo.opengl

import dev.misasi.giancarlo.drawing.ShapeType
import dev.misasi.giancarlo.math.Rectangle
import dev.misasi.giancarlo.math.Vector2f
import dev.misasi.giancarlo.opengl.Attribute.Companion.sizeInBytes
import dev.misasi.giancarlo.system.DataType

class DrawBuffer(
    private val gl: OpenGl,
    program: Program,
    attributeSpecs: List<Attribute.Spec>,
    bufferUsage: Buffer.Usage,
    spriteCapacity: Int,
) {
    private val indexBuffer: IndexBuffer
    private val vertexBuffer: VertexBuffer
    private val attributeArray: AttributeArray

    init {
        indexBuffer = IndexBuffer(gl, Buffer.Usage.STATIC, spriteCapacity * shapeType.vertexCount * indexType.size)
        vertexBuffer = VertexBuffer(gl, bufferUsage,  spriteCapacity * vertexCount * attributeSpecs.sizeInBytes())
        attributeArray = AttributeArray(gl, program, attributeSpecs, indexBuffer, vertexBuffer)
    }

    fun updateIndexes(data: NioBuffer) = indexBuffer.bind().update(data)

    fun updateVertexes(data: NioBuffer) = vertexBuffer.bind().update(data)

    fun applyScissorScoped(viewport: Viewport, scopedScissor: Rectangle?, block: () -> Unit) {
        if (scopedScissor == null) {
            block()
            return
        }

        gl.setScissor(viewport.actualScreenSize.y, scopedScissor)
        gl.enableScissor(true)
        block()
        gl.enableScissor(false)
    }

    fun draw(texture: Texture, spriteOffset: Int = 0, count: Int): Int {
        texture.bind()
        attributeArray.bind()
        gl.drawIndexed(
            shapeType,
            spriteOffset * shapeType.vertexCount * indexType.size,
            shapeType.vertexCount * count,
            indexType
        )
        AttributeArray.unbind(gl)
        return spriteOffset + count
    }

    companion object {
        private const val vertexCount = 4
        private val shapeType = ShapeType.SQUARE
        private val indexType = DataType.UNSIGNED_INT
    }
}