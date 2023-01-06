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
import dev.misasi.giancarlo.events.input.mouse.CursorMode
import dev.misasi.giancarlo.math.Matrix2f
import dev.misasi.giancarlo.math.Matrix4f
import dev.misasi.giancarlo.math.Rectangle
import dev.misasi.giancarlo.math.Vector2f
import dev.misasi.giancarlo.math.Vector2i
import dev.misasi.giancarlo.system.DataType

expect class OpenGl {

    // Instance
    companion object {
        val gl: OpenGl
    }

    // Window
    fun createWindow(title: String, size: Vector2i, fullScreen: Boolean, refreshRate: Int, vsync: Boolean): Long
    fun setCurrentWindow(window: Long)
    fun setWindowTitle(window: Long, title: String)
    fun setFullScreen(window: Long, resolution: Vector2i, refreshRate: Int)
    fun setWindowed(window: Long, windowSize: Vector2i)
    fun setVsync(window: Long, vsync: Boolean)
    fun getPrimaryMonitorResolution(): Vector2i
    fun getActualWindowSize(window: Long): Vector2i
    fun setCursorMode(window: Long, mode: CursorMode)
    fun swapBuffers(window: Long)
    fun closeWindow(window: Long)
    fun shouldCloseWindow(window: Long): Boolean

    // Events
    fun getMousePosition(window: Long): Vector2f
    fun onKeyboardEvents(window: Long, handler: ((window: Long, keyCode: Int, scanCode: Int, actionCode: Int, modifierCode: Int) -> Unit)?)
    fun onTextEvents(window: Long, handler: ((window: Long, codePoint: Int) -> Unit)?)
    fun onMouseEvents(window: Long, handler: ((window: Long, x: Double, y: Double) -> Unit)?)
    fun onMouseButtonEvents(window: Long, handler: ((window: Long, buttonCode: Int, actionCode: Int, modifierCode: Int) -> Unit)?)
    fun onScrollEvents(window: Long, handler: ((window: Long, x: Double, y: Double) -> Unit)?)
    fun onResizeEvents(window: Long, handler: ((window: Long, x: Int, y: Int) -> Unit)?)
    fun pollEvents()


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
    fun setUniformMatrix2f(program: Int, uniform: Int, value: Matrix2f)
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
    fun createBuffer(type: Buffer.Type, usage: Buffer.Usage, data: NioBuffer): Int
    fun bindBuffer(handle: Int, type: Buffer.Type): Int
    fun updateBufferData(handle: Int, type: Buffer.Type, data: NioBuffer, byteOffset: Int = 0)
    fun deleteBuffer(handle: Int)


    fun createAttributeArray(): Int
    fun bindAttributeArray(vao: Int): Int
    fun deleteAttributeArray(vao: Int)

    // Textures
    fun createTexture2d(size: Vector2i, format: Rgba8.Format, data: NioBuffer? = null): Int
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
    fun setViewport(size: Vector2i)
    fun enableScissor(enabled: Boolean)
    fun setScissor(viewportHeight: Int, bounds: Rectangle)

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