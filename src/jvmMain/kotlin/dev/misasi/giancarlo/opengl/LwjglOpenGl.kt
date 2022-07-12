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

import org.lwjgl.opengl.GL40.*

import dev.misasi.giancarlo.crash
import dev.misasi.giancarlo.drawing.Rgba8
import dev.misasi.giancarlo.drawing.ShapeType
import dev.misasi.giancarlo.math.Matrix4f
import dev.misasi.giancarlo.memory.DirectNativeByteBuffer
import java.nio.ByteBuffer
import java.nio.charset.Charset

class LwjglOpenGl : OpenGl {

    companion object {
        private val shaderTypeMapping = mapOf(
            Shader.Type.VERTEX to GL_VERTEX_SHADER,
            Shader.Type.FRAGMENT to GL_FRAGMENT_SHADER
        )

        private val bufferTypeMapping = mapOf(
            Buffer.Type.VERTEX to GL_ARRAY_BUFFER,
            Buffer.Type.INDEX to GL_ELEMENT_ARRAY_BUFFER
        )

        private val bufferBindingMapping = mapOf(
            Buffer.Type.VERTEX to GL_ARRAY_BUFFER_BINDING,
            Buffer.Type.INDEX to GL_ELEMENT_ARRAY_BUFFER_BINDING
        )

        private val bufferUsageMapping = mapOf(
            Buffer.Usage.STATIC to GL_STATIC_DRAW,
            Buffer.Usage.DYNAMIC to GL_DYNAMIC_DRAW,
            Buffer.Usage.STREAM to GL_STREAM_DRAW
        )

        private val vertexAttributeTypeMapping = mapOf(
            DataType.FLOAT to GL_FLOAT,
            DataType.INT to GL_INT,
            DataType.UNSIGNED_INT to GL_UNSIGNED_INT,
            DataType.SHORT to GL_SHORT,
            DataType.UNSIGNED_SHORT to GL_UNSIGNED_SHORT,
            DataType.BYTE to GL_BYTE,
            DataType.UNSIGNED_BYTE to GL_UNSIGNED_BYTE
        )

        private val textureFormat = mapOf(
            Rgba8.Format.BGRA to GL_BGRA,
            Rgba8.Format.RGBA to GL_RGBA
        )
    }

    override fun createProgram(): Int {
        return glVerifyCreate(this, ::glCreateProgram) { "PROGRAM" }
    }

    override fun linkProgram(program: Int) {
        glVerify(this) { glLinkProgram(program) }
        glVerifyStatus(this, ::glGetProgramiv, program, GL_LINK_STATUS, ::glGetProgramInfoLog);
    }

    override fun bindProgram(program: Int): Int {
        glVerify(this) { glUseProgram(program) }
        return program
    }

    override fun createShader(type: Shader.Type): Int {
        val shaderType = shaderTypeMapping[type]!!;
        return glVerifyCreate(this, { glCreateShader(shaderType) }) { "SHADER(${type.name})" }
    }

    override fun compileShader(shader: Int, source: String) {
        glVerify(this) { glShaderSource(shader, source) }
        glVerify(this) { glCompileShader(shader) }
        glVerifyStatus(this, ::glGetShaderiv, shader, GL_COMPILE_STATUS, ::glGetShaderInfoLog);
    }

    override fun attachShader(program: Int, shader: Int) {
        glVerify(this) { glAttachShader(program, shader) }
    }

    override fun detachShader(program: Int, shader: Int) {
        glVerify(this) { glDetachShader(program, shader) }
    }

    override fun deleteShader(shader: Int) {
        glVerify(this) { glDeleteShader(shader) }
    }

    override fun getUniformLocation(program: Int, name: String): Int {
        glVerifyBound(this, GL_CURRENT_PROGRAM, program) { "PROGRAM<$program>" }
        val location = glVerify<Int>(this) { glGetUniformLocation(program, name) }
        if (location < 0) {
            val count = IntArray(1)
            glGetProgramiv(program, GL_ACTIVE_UNIFORMS, count)
            println("Found ${count[0]} uniforms. Was looking for: $name, but found:")
            for (i in 0 until count[0]) {
                val length = IntArray(1)
                val size = IntArray(1)
                val type = IntArray(1)
                val byteBuffer = ByteBuffer.allocateDirect(256)
                glGetActiveUniform(program, i, length, size, type, byteBuffer)
                println(getString(byteBuffer, length[0]))
            }
            throw IllegalStateException("Missing expected uniform: $name")
        }
        return location
    }

    override fun setUniform(program: Int, uniform: Int, value: Any) {
        glVerifyBound(this, GL_CURRENT_PROGRAM, program) { "PROGRAM<$program>" }
        when (value) {
            is Matrix4f -> glVerify(this) { glUniformMatrix4fv(uniform, false, value.data) }
            is Int -> glVerify(this) { glUniform1i(uniform, value) }
            else -> crash("Cannot set uniform, type '${value.javaClass}' not supported.")
        }
    }

    override fun getAttributeLocation(program: Int, name: String): Int {
        glVerifyBound(this, GL_CURRENT_PROGRAM, program) { "PROGRAM<$program>" }
        val location = glVerify<Int>(this) { glGetAttribLocation(program, name) }
        if (location < 0) {
            val count = IntArray(1)
            glGetProgramiv(program, GL_ACTIVE_ATTRIBUTES, count)
            println("Found ${count[0]} attributes. Was looking for: $name, but found:")
            for (i in 0 until count[0]) {
                val length = IntArray(1)
                val size = IntArray(1)
                val type = IntArray(1)
                val byteBuffer = ByteBuffer.allocateDirect(256)
                glGetActiveAttrib(program, i, length, size, type, byteBuffer)
                println(getString(byteBuffer, length[0]))
            }
            throw IllegalStateException("Missing expected attribute: $name")
        }
        return location
    }

    override fun enableVertexAttributeArray(attribute: Int) {
        glVerify(this) { glEnableVertexAttribArray(attribute) }
    }

    override fun setVertexAttributePointer(handle: Int, attribute: Attribute, totalStride: Int) {
        val t = vertexAttributeTypeMapping[attribute.spec.type]!!
        if (attribute.spec.type == DataType.FLOAT || attribute.spec.normalize) {
            glVerify(this) {
                glVertexAttribPointer(
                    handle,
                    attribute.spec.count,
                    t,
                    attribute.spec.normalize,
                    totalStride,
                    attribute.strideOffset.toLong()
                )
            }
        } else {
            glVerify(this) {
                glVertexAttribIPointer(
                    handle,
                    attribute.spec.count,
                    t,
                    totalStride,
                    attribute.strideOffset.toLong()
                )
            }
        }
    }

    override fun createBuffer(type: Buffer.Type, usage: Buffer.Usage, maxBytes: Int): Int {
        val t = bufferTypeMapping[type]!!
        val u = bufferUsageMapping[usage]!!
        val vbo = bindBuffer(glVerifyGenerate(this, ::glGenBuffers) { "BUFFER(${type.name})[${usage.name}]" }, type)
        glVerify(this) { glBufferData(t, maxBytes.toLong(), u) }
        return vbo
    }

    override fun createBuffer(type: Buffer.Type, usage: Buffer.Usage, data: DirectNativeByteBuffer): Int {
        val t = bufferTypeMapping[type]!!
        val u = bufferUsageMapping[usage]!!
        val vbo = bindBuffer(glVerifyGenerate(this, ::glGenBuffers) { "BUFFER(${type.name})[${usage.name}]" }, type)
        glVerify(this) { glBufferData(t, data.byteBuffer, u) }
        return vbo
    }

    override fun bindBuffer(handle: Int, type: Buffer.Type): Int {
        val t = bufferTypeMapping[type]!!
        glVerify(this) { glBindBuffer(t, handle) }
        return handle
    }

    override fun updateBufferData(handle: Int, type: Buffer.Type, data: DirectNativeByteBuffer, byteOffset: Int) {
        val b = bufferBindingMapping[type]!!
        glVerifyBound(this, b, handle) { "BUFFER<$handle>" }
        glVerify(this) { glBufferSubData(GL_ARRAY_BUFFER, byteOffset.toLong(), data.byteBuffer) }
    }

    override fun createAttributeArray(): Int {
        return bindAttributeArray(glVerifyGenerate(this, ::glGenVertexArrays) { "VAO" })
    }

    override fun bindAttributeArray(vao: Int): Int {
        glVerify(this) { glBindVertexArray(vao) }
        return vao
    }

    override fun createTexture2d(width: Int, height: Int, format: Rgba8.Format, data: DirectNativeByteBuffer): Int {
        val texture = bindTexture2d(glVerifyGenerate(this, ::glGenTextures) { "TEX2D" })
        val format = textureFormat[format]!!
        glVerify(this) {
            glTexImage2D(
                GL_TEXTURE_2D,
                0,
                GL_RGBA,
                width,
                height,
                0,
                format,
                GL_UNSIGNED_BYTE,
                data.byteBuffer
            )
        }
        glVerify(this) { glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST) }
        glVerify(this) { glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST) }
        glVerify(this) { glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE) }
        glVerify(this) { glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE) }
        return texture
    }

    override fun bindTexture2d(texture: Int): Int {
        glVerify(this) { glBindTexture(GL_TEXTURE_2D, texture) }
        return texture
    }

    override fun setActiveTextureUnit(target: Int) {
        glVerify(this) { glActiveTexture(GL_TEXTURE0 + target) }
    }

    override fun setViewport(x: Int, y: Int, width: Int, height: Int) {
        glVerify(this) { glViewport(0, 0, width, height) }
    }

    override fun setScissor(x: Int, y: Int, width: Int, height: Int) {
        glVerify(this) { glEnable(GL_SCISSOR_TEST) }
        glVerify(this) { glScissor(x, y, width, height) }
    }

    override fun draw(type: ShapeType, offset: Int, vertexCount: Int) {
        when (type) {
            ShapeType.LINE -> drawLines(offset, vertexCount)
            ShapeType.TRIANGLE -> drawTriangles(offset, vertexCount)
            ShapeType.SQUARE -> drawTriangles(offset, vertexCount)
            else -> throw IllegalArgumentException("Draw type ${type.name} not supported.")
        }
    }

    override fun drawIndexed(type: ShapeType, offset: Int, vertexCount: Int, indexType: DataType) {
        when (type) {
            ShapeType.LINE -> drawLinesIndexed(offset, vertexCount, indexType)
            ShapeType.TRIANGLE, ShapeType.SQUARE -> drawTrianglesIndexed(offset, vertexCount, indexType)
            else -> throw IllegalArgumentException("Draw type ${type.name} not supported.")
        }
    }

    override fun drawLines(offset: Int, vertexCount: Int) {
        glVerify(this) { glDrawArrays(GL_LINES, offset, vertexCount) }
    }

    override fun drawLinesIndexed(offset: Int, vertexCount: Int, indexType: DataType) {
        val t = vertexAttributeTypeMapping[indexType]!!
        glVerify(this) { glDrawElements(GL_LINES, vertexCount, t, offset.toLong()) }
    }

    override fun drawTriangles(offset: Int, vertexCount: Int) {
        glVerify(this) { glDrawArrays(GL_TRIANGLES, offset, vertexCount) }
    }

    override fun drawTrianglesIndexed(offset: Int, vertexCount: Int, indexType: DataType) {
        val t = vertexAttributeTypeMapping[indexType]!!
        glVerify(this) { glDrawElements(GL_TRIANGLES, vertexCount, t, offset.toLong()) }
    }

    override fun setClearColor(color: Rgba8) {
        glVerify(this) { glEnable(GL_BLEND) }
        glVerify(this) { glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA) }
        glVerify(this) {
            glClearColor(
                color.floatR,
                color.floatG,
                color.floatB,
                color.floatA
            )
        }
    }

    override fun clear() {
        glVerify(this) { glDisable(GL_SCISSOR_TEST) }
        glVerify(this) { glClear(GL_COLOR_BUFFER_BIT) }
        glVerify(this) { glEnable(GL_SCISSOR_TEST) }
    }

    override fun enable(target: Int) {
        glVerify(this) { glEnable(target) }
    }

    override fun getErrorMessage(): String? {
        when (val errorCode = glGetError()) {
            GL_NO_ERROR -> {
                return null
            }
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

    override fun getCurrentHandle(attribute: Int): Int {
        val handle = IntArray(1)
        glVerify(this) { glGetIntegerv(attribute, handle) }
        return handle[0]
    }

    private fun getString(byteBuffer: ByteBuffer, length: Int): String {
        return if (byteBuffer.remaining() > length) {
            val bytes = ByteArray(length)
            byteBuffer.get(bytes)
            String(bytes, Charset.defaultCharset())
        } else {
            throw IllegalStateException("ByteBuffer did not have $length bytes to retrieve string from")
        }
    }
}