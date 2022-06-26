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

import dev.misasi.giancarlo.crash
import dev.misasi.giancarlo.drawing.Rgba8
import dev.misasi.giancarlo.math.Matrix4f
import dev.misasi.giancarlo.memory.DirectByteBuffer
import org.lwjgl.opengl.ARBFramebufferObject.GL_INVALID_FRAMEBUFFER_OPERATION
import org.lwjgl.opengl.GL20.*

class LwjglOpenGl : OpenGl {

    private val shaderTypeMapping = mapOf(
        Shader.Type.VERTEX to GL_VERTEX_SHADER,
        Shader.Type.FRAGMENT to GL_FRAGMENT_SHADER
    )

    private val vertexBufferUsageMapping = mapOf(
        VertexBuffer.Usage.STATIC to GL_STATIC_DRAW,
        VertexBuffer.Usage.DYNAMIC to GL_DYNAMIC_DRAW,
        VertexBuffer.Usage.STREAM to GL_STREAM_DRAW
    )

    override fun createProgram(): Int {
        return glCreateHandle(::glCreateProgram) { "PROGRAM" }
    }

    override fun linkProgram(program: Int): String? {
        glVerify { glLinkProgram(program) }

        val status = IntArray(1)
        glVerify { glGetProgramiv(program, GL_LINK_STATUS, status) }

        return if (status[0] == GL_FALSE) {
            glVerify<String> { glGetProgramInfoLog(program) }
        } else {
            null
        }
    }

    override fun bindProgram(program: Int): Int {
        glVerify { glUseProgram(program) }
        return program
    }

    override fun createShader(type: Shader.Type): Int {
        return glCreateHandle({ glCreateShader(shaderTypeMapping[type]!!) }) { "VERTEX" }
    }

    override fun compileShader(shader: Int, source: String): String? {
        glVerify { glShaderSource(shader, source) }
        glVerify { glCompileShader(shader) }

        val status = IntArray(1)
        glVerify { glGetShaderiv(shader, GL_COMPILE_STATUS, status) }

        return if (status[0] == GL_FALSE) {
            glVerify<String> { glGetShaderInfoLog(shader) }
        } else {
            null
        }
    }

    override fun attachShader(program: Int, shader: Int) {
        glVerify { glAttachShader(program, shader) }
    }

    override fun detachShader(program: Int, shader: Int) {
        glVerify { glDetachShader(program, shader) }
    }

    override fun deleteShader(shader: Int) {
        glVerify { glDeleteShader(shader) }
    }

    override fun getUniformLocation(program: Int, name: String): Int {
        glVerifyProgramBound(program)
        return glVerify<Int> { glGetUniformLocation(program, name) }
    }

    override fun setUniform(program: Int, uniform: Int, value: Any) {
        glVerifyProgramBound(program)

        when (value) {
            is Matrix4f ->  glVerify { glUniformMatrix4fv(uniform, false, value.data) }
            is Int -> glVerify { glUniform1i(uniform, value) }
            else -> crash("Cannot set uniform, type '${value.javaClass}' not supported.")
        }
    }

    override fun getAttributeLocation(program: Int, name: String): Int {
        glVerifyProgramBound(program)
        return glVerify<Int> { glGetAttribLocation(program, name) }
    }

    override fun enableVertexAttributeArray(attribute: Int) {
        glVerify { glEnableVertexAttribArray(attribute) }
    }

    override fun setVertexAttributePointerFV(attribute: Int, opts: Attribute) {
        glVerify { glVertexAttribPointer(attribute, opts.count, GL_FLOAT, false, opts.totalStride, opts.strideOffset.toLong()) }
    }

    override fun createVbo(usage: VertexBuffer.Usage, maxBytes: Int): Int {
        val vbo = bindVbo(glGenHandle(::glGenBuffers) { "VBO" })
        glVerify { glBufferData(GL_ARRAY_BUFFER, maxBytes.toLong(), vertexBufferUsageMapping[usage]!!) }
        return vbo
    }

    override fun bindVbo(vbo: Int): Int {
        glVerify { glBindBuffer(GL_ARRAY_BUFFER, vbo) }
        return vbo
    }

    override fun updateVboData(vbo: Int, data: DirectByteBuffer, byteOffset: Int) {
        glVerifyVboBound(vbo)
        glVerify { glBufferSubData(GL_ARRAY_BUFFER, byteOffset.toLong(), data.byteBuffer) }
    }

    override fun createTexture2d(width: Int, height: Int, data: DirectByteBuffer): Int {
        val texture = bindTexture2d(glGenHandle(::glGenTextures) { "TEX2D" })
        glVerify { glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data.byteBuffer) }
        glVerify { glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST) }
        glVerify { glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST) }
        glVerify { glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE) }
        glVerify { glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE) }
        return texture
    }

    override fun bindTexture2d(texture: Int): Int {
        glVerify { glBindTexture(GL_TEXTURE_2D, texture) }
        return texture
    }

    override fun setActiveTextureUnit(target: Int) {
        glVerify { glActiveTexture(GL_TEXTURE0 + target) }
    }

    override fun setViewport(x: Int, y: Int, width: Int, height: Int) {
        glVerify { glViewport(0, 0, width, height) }
    }

    override fun setScissor(x: Int, y: Int, width: Int, height: Int) {
        glVerify { glEnable(GL_SCISSOR_TEST) }
        glVerify { glScissor(x, y, width, height) }
    }

    override fun drawTriangles(triangleOffset: Int, triangleCount: Int) {
        val offset = 3 * triangleOffset
        val vertexCount = 3 * triangleCount
        glVerify { glDrawArrays(GL_TRIANGLES, offset, vertexCount) }
    }

    override fun setClearColor(color: Rgba8) {
        glVerify { glEnable(GL_BLEND) }
        glVerify { glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA) }
        glVerify {
            glClearColor(
                color.floatR,
                color.floatG,
                color.floatB,
                color.floatA
            )
        }
    }

    override fun clear() {
        glVerify { glClear(GL_COLOR_BUFFER_BIT) }
    }

    override fun enable(target: Int) {
        glVerify { glEnable(target) }
    }


    private inline fun <T> glVerify(glOperation: () -> T): T {
        val result = glOperation()
        glCheckError()
        return result
    }

    private inline fun glVerify(glOperation: () -> Unit) {
        glOperation()
        glCheckError()
    }


    private fun glCreateHandle(glCreateOperation: () -> Int, tag: () -> String): Int {
        val handle = glVerify<Int> { glCreateOperation() }
        if (handle == 0) {
            crash("Failed to create ${tag()}.")
        }
        return handle
    }

    private fun glGenHandle(glGenOperation: (IntArray) -> Unit, tag: () -> String): Int {
        val handleArray = IntArray(1)
        glVerify { glGenOperation(handleArray) }
        if (handleArray[0] == 0) {
            crash("Failed to create ${tag()}.")
        }
        return handleArray[0]
    }

    private fun glVerifyProgramBound(handle: Int) {
        glVerifyBound(GL_CURRENT_PROGRAM, handle) { "PROGRAM" }
    }

    private fun glVerifyVboBound(handle: Int) {
        glVerifyBound(GL_ARRAY_BUFFER_BINDING, handle) { "VBO" }
    }

    private fun glVerifyVertexAttributeEnabled(handle: Int) {
        val enabled = IntArray(1)
        glVerify { glGetVertexAttribiv(handle, GL_VERTEX_ATTRIB_ARRAY_ENABLED, enabled) }
        if (enabled[0] != 1) {
            crash("[VA] $handle is not enabled.")
        }
    }

    private fun glVerifyBound(name: Int, handle: Int, tag: () -> String) {
        val currentHandle = IntArray(1)
        glVerify { glGetIntegerv(name, currentHandle) }
        if (currentHandle[0] != handle) {
            crash("[${tag()}] ${currentHandle[0]} was bound instead of $handle.")
        }
    }

    private fun glCheckError() {
        val errorCode = glGetError();
        if (errorCode != GL_NO_ERROR) {
            crash(glGetErrorMessage(errorCode))
        }
    }

    private fun glGetErrorMessage(errorCode: Int): String {
        when (errorCode) {
            GL_INVALID_ENUM -> {
                return "OGL Error: Invalid enum parameter."
            }
            GL_INVALID_VALUE -> {
                return "OGL Error: Invalid value parameter."
            }
            GL_INVALID_OPERATION -> {
                return "OGL Error: Invalid operation."
            }
            GL_OUT_OF_MEMORY -> {
                return "OGL Error: Out of memory."
            }
            GL_INVALID_FRAMEBUFFER_OPERATION -> {
                return "OGL Error: Invalid framebuffer operation."
            }
            else -> {
                return "OGL Error: $errorCode"
            }
        }
    }
}