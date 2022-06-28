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
import dev.misasi.giancarlo.memory.DirectByteBuffer

interface OpenGl {

    // General
    fun enable(target: Int)

    // Program
    fun createProgram(): Int
    fun linkProgram(program: Int)
    fun bindProgram(program: Int): Int

    // Shader
    fun createShader(type: Shader.Type): Int
    fun compileShader(shader: Int, source: String)
    fun attachShader(program: Int, shader: Int)
    fun detachShader(program: Int, shader: Int)
    fun deleteShader(shader: Int)

    // Uniforms
    fun getUniformLocation(program: Int, name: String): Int
    fun setUniform(program: Int, uniform: Int, value: Any)

    // Attributes
    fun getAttributeLocation(program: Int, name: String): Int
    fun enableVertexAttributeArray(attribute: Int)
    fun setVertexAttributePointer(attribute: Int, opts: Attribute)

    // Vertex buffers
    fun createVbo(usage: VertexBuffer.Usage, maxBytes: Int): Int
    fun bindVbo(vbo: Int): Int
    fun updateVboData(vbo: Int, data: DirectByteBuffer, byteOffset: Int = 0)

    // Textures
    fun createTexture2d(width: Int, height: Int, format: Rgba8.Format, data: DirectByteBuffer): Int
    fun bindTexture2d(texture: Int): Int
    fun setActiveTextureUnit(target: Int)

    // Viewport
    fun setViewport(x: Int, y: Int, width: Int, height: Int)
    fun setScissor(x: Int, y: Int, width: Int, height: Int)

    // Draw
    fun drawLines(offset: Int, vertexCount: Int)
    fun drawTriangles(offset: Int, vertexCount: Int)
    fun setClearColor(color: Rgba8)
    fun clear()

    // Debugging
    fun getCurrentHandle(attribute: Int): Int
    fun getErrorMessage(): String?
}