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

import dev.misasi.giancarlo.drawing.Rgba8
import dev.misasi.giancarlo.drawing.ShapeType
import dev.misasi.giancarlo.math.Matrix4f
import dev.misasi.giancarlo.math.Vector2f
import dev.misasi.giancarlo.memory.DirectNativeByteBuffer

interface OpenGl {

    // General
    fun enable(target: Int)

    // Program
    fun createProgram(): Int
    fun linkProgram(program: Int)
    fun bindProgram(program: Int): Int
    fun deleteProgram(program: Int)


    // Shader
    fun createShader(type: Shader.Type): Int
    fun compileShader(shader: Int, source: String)
    fun attachShader(program: Int, shader: Int)
    fun detachShader(program: Int, shader: Int)
    fun deleteShader(shader: Int)

    // Uniforms
    fun getUniformLocation(program: Int, name: String): Int
    fun setUniformMatrix4f(program: Int, uniform: Int, value: Matrix4f)
    fun setUniformVector2f(program: Int, uniform: Int, value: Vector2f)
    fun setUniform1f(program: Int, uniform: Int, value: Float)
    fun setUniform1i(program: Int, uniform: Int, value: Int)
    fun setUniform1b(program: Int, uniform: Int, value: Boolean)

    // Attributes
    fun getAttributeLocation(program: Int, name: String): Int
    fun enableVertexAttributeArray(attribute: Int)
    fun setVertexAttributePointer(handle: Int, attribute: Attribute, totalStride: Int)
    fun setVertexAttributeIPointer(handle: Int, attribute: Attribute, totalStride: Int)

    // Vertex buffers
    fun createBuffer(type: Buffer.Type, usage: Buffer.Usage, maxBytes: Int): Int
    fun createBuffer(type: Buffer.Type, usage: Buffer.Usage, data: DirectNativeByteBuffer): Int
    fun bindBuffer(handle: Int, type: Buffer.Type): Int
    fun updateBufferData(handle: Int, type: Buffer.Type, data: DirectNativeByteBuffer, byteOffset: Int = 0)
    fun deleteBuffer(handle: Int)


    fun createAttributeArray(): Int
    fun bindAttributeArray(vao: Int): Int
    fun deleteAttributeArray(vao: Int)

    // Textures
    fun createTexture2d(width: Int, height: Int, format: Rgba8.Format, data: DirectNativeByteBuffer? = null): Int
    fun setTextureMinFilter(filter: Texture.Filter)
    fun setTextureMagFilter(filter: Texture.Filter)
    fun setTextureBorderColor(color: Rgba8)
    fun setTextureWrapS(wrap: Texture.Wrap)
    fun setTextureWrapT(wrap: Texture.Wrap)
    fun bindTexture2d(texture: Int): Int
    fun setActiveTextureUnit(target: Int)
    fun deleteTexture2d(handle: Int)

    // Frame buffers
    fun createFrameBuffer(): Int
    fun bindFrameBuffer(handle: Int): Int
    fun attachTexture(texture: Int)
    fun deleteFrameBuffer(handle: Int)

    // Viewport
    fun setViewport(x: Int, y: Int, width: Int, height: Int)
    fun enableScissor(enabled: Boolean)
    fun setScissor(x: Int, y: Int, width: Int, height: Int)

    // Draw
    fun draw(type: ShapeType, offset: Int, vertexCount: Int)
    fun drawIndexed(type: ShapeType, offset: Int, vertexCount: Int, indexType: DataType)
    fun drawLines(offset: Int, vertexCount: Int)
    fun drawLinesIndexed(offset: Int, vertexCount: Int, indexType: DataType)
    fun drawTriangles(offset: Int, vertexCount: Int)
    fun drawTrianglesIndexed(offset: Int, vertexCount: Int, indexType: DataType)

    fun setClearColor(color: Rgba8)
    fun clear()

    // Debugging
    fun getCurrentHandle(attribute: Int): Int
    fun getErrorMessage(): String?
}