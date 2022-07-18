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
import dev.misasi.giancarlo.math.Vector2f
import dev.misasi.giancarlo.memory.DirectNativeByteBuffer
import java.nio.ByteBuffer
import java.nio.charset.Charset

class LwjglOpenGl : OpenGl {

    companion object {
        private val dataTypeMapping = mapOf(
            DataType.FLOAT to GL_FLOAT,
            DataType.INT to GL_INT,
            DataType.UNSIGNED_INT to GL_UNSIGNED_INT,
            DataType.SHORT to GL_SHORT,
            DataType.UNSIGNED_SHORT to GL_UNSIGNED_SHORT,
            DataType.BYTE to GL_BYTE,
            DataType.UNSIGNED_BYTE to GL_UNSIGNED_BYTE
        )

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

        private val textureFilter = mapOf(
            Texture.Filter.NEAREST to GL_NEAREST,
            Texture.Filter.LINEAR to GL_LINEAR
        )

        private val textureWrap = mapOf(
            Texture.Wrap.REPEAT to GL_REPEAT,
            Texture.Wrap.CLAMP_TO_BORDER to GL_CLAMP_TO_BORDER
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
        glVerifyStatus(this, ::glGetProgramiv, program, GL_LINK_STATUS, ::glGetProgramInfoLog)
    }

    override fun bindProgram(program: Int): Int {
        glVerify(this) { glUseProgram(program) }
        return program
    }

    override fun deleteProgram(program: Int) {
        glVerify(this) { glDeleteProgram(program) }
    }

    override fun createShader(type: Shader.Type): Int {
        val shaderType = shaderTypeMapping[type]!!
        return glVerifyCreate(this, { glCreateShader(shaderType) }) { "SHADER(${type.name})" }
    }

    override fun compileShader(shader: Int, source: String) {
        glVerify(this) { glShaderSource(shader, source) }
        glVerify(this) { glCompileShader(shader) }
        glVerifyStatus(this, ::glGetShaderiv, shader, GL_COMPILE_STATUS, ::glGetShaderInfoLog)
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
            verifyProgramState(program, GL_ACTIVE_UNIFORMS, ::glGetActiveUniform) { "Missing uniform: $name" }
        }
        return location
    }

    override fun setUniformMatrix4f(program: Int, uniform: Int, value: Matrix4f) {
        glVerify(this) { glUniformMatrix4fv(uniform, false, value.data) }
    }
    override fun setUniformVector2f(program: Int, uniform: Int, value: Vector2f) {
        glVerify(this) { glUniform2f(uniform, value.x, value.y) }
    }
    override fun setUniform1f(program: Int, uniform: Int, value: Float) {
        glVerify(this) { glUniform1f(uniform, value) }
    }
    override fun setUniform1i(program: Int, uniform: Int, value: Int) {
        glVerify(this) { glUniform1i(uniform, value) }
    }
    override fun setUniform1b(program: Int, uniform: Int, value: Boolean) {
        glVerify(this) { glUniform1i(uniform, if (value) 1 else 0) }
    }

    override fun getAttributeLocation(program: Int, name: String): Int {
        glVerifyBound(this, GL_CURRENT_PROGRAM, program) { "PROGRAM<$program>" }
        val location = glVerify<Int>(this) { glGetAttribLocation(program, name) }
        if (location < 0) {
            verifyProgramState(program, GL_ACTIVE_ATTRIBUTES, ::glGetActiveAttrib) { "Missing attribute: $name" }
        }
        return location
    }

    override fun enableVertexAttributeArray(attribute: Int) {
        glVerify(this) { glEnableVertexAttribArray(attribute) }
    }

    override fun setVertexAttributePointer(handle: Int, attribute: Attribute, totalStride: Int) {
        val t = dataTypeMapping[attribute.spec.type]!!
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
    }

    override fun setVertexAttributeIPointer(handle: Int, attribute: Attribute, totalStride: Int) {
        val t = dataTypeMapping[attribute.spec.type]!!
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

    override fun deleteBuffer(handle: Int) {
        glVerify(this) { glDeleteBuffers(handle) }
    }

    override fun createAttributeArray(): Int {
        return bindAttributeArray(glVerifyGenerate(this, ::glGenVertexArrays) { "VAO" })
    }

    override fun bindAttributeArray(vao: Int): Int {
        glVerify(this) { glBindVertexArray(vao) }
        return vao
    }

    override fun deleteAttributeArray(vao: Int) {
        glVerify(this) { glDeleteVertexArrays(vao) }
    }

    override fun createTexture2d(width: Int, height: Int, format: Rgba8.Format, data: DirectNativeByteBuffer?): Int {
        val texture = bindTexture2d(glVerifyGenerate(this, ::glGenTextures) { "TEX2D" })
        val formatParameter = textureFormat[format]!!
        glVerify(this) {
            glTexImage2D(
                GL_TEXTURE_2D,
                0,
                GL_RGBA,
                width,
                height,
                0,
                formatParameter,
                GL_UNSIGNED_BYTE,
                data?.byteBuffer
            )
        }
        return texture
    }

    override fun setTextureMinFilter(filter: Texture.Filter) {
        val filterParameter = textureFilter[filter]!!
        glVerify(this) { glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filterParameter) }
    }

    override fun setTextureMagFilter(filter: Texture.Filter) {
        val filterParameter = textureFilter[filter]!!
        glVerify(this) { glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filterParameter) }
    }

    override fun setTextureBorderColor(color: Rgba8) {
        glVerify(this) {
            glTexParameterfv(
                GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, floatArrayOf(
                    color.floatR,
                    color.floatG,
                    color.floatB,
                    color.floatA
                )
            )
        }
    }

    override fun setTextureWrapS(wrap: Texture.Wrap) {
        val wrapParameter = textureWrap[wrap]!!
        glVerify(this) { glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, wrapParameter) }
    }

    override fun setTextureWrapT(wrap: Texture.Wrap) {
        val wrapParameter = textureWrap[wrap]!!
        glVerify(this) { glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, wrapParameter) }
    }

    override fun bindTexture2d(texture: Int): Int {
        glVerify(this) { glBindTexture(GL_TEXTURE_2D, texture) }
        return texture
    }

    override fun setActiveTextureUnit(target: Int) {
        glVerify(this) { glActiveTexture(GL_TEXTURE0 + target) }
    }

    override fun deleteTexture2d(handle: Int) {
        glVerify(this) { glDeleteTextures(handle) }
    }

    override fun createFrameBuffer(): Int {
        return glVerifyGenerate(this, ::glGenFramebuffers) { "FRAME BUFFER" }
    }

    override fun bindFrameBuffer(handle: Int): Int {
        glVerify(this) { glBindFramebuffer(GL_FRAMEBUFFER, handle) }
        return handle
    }

    override fun attachTexture(texture: Int) {
        glVerify(this) { glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture, 0) }
        glVerifyEquals(
            this,
            GL_FRAMEBUFFER_COMPLETE,
            { glCheckFramebufferStatus(GL_FRAMEBUFFER) }
        ) { "Failed to attach texture $texture to frame buffer." }
    }

    override fun deleteFrameBuffer(handle: Int) {
        glVerify(this) { glDeleteFramebuffers(handle) }
    }

    override fun setViewport(x: Int, y: Int, width: Int, height: Int) {
        glVerify(this) { glViewport(0, 0, width, height) }
    }

    override fun enableScissor(enabled: Boolean) {
        glVerify(this) { glEnable(GL_SCISSOR_TEST) }
    }

    override fun setScissor(x: Int, y: Int, width: Int, height: Int) {
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
        val t = dataTypeMapping[indexType]!!
        glVerify(this) { glDrawElements(GL_LINES, vertexCount, t, offset.toLong()) }
    }

    override fun drawTriangles(offset: Int, vertexCount: Int) {
        glVerify(this) { glDrawArrays(GL_TRIANGLES, offset, vertexCount) }
    }

    override fun drawTrianglesIndexed(offset: Int, vertexCount: Int, indexType: DataType) {
        val t = dataTypeMapping[indexType]!!
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
        glVerify(this) { glClear(GL_COLOR_BUFFER_BIT) }
    }

    override fun enable(target: Int) {
        glVerify(this) { glEnable(target) }
    }

    override fun getErrorMessage(): String? {
        return when (val errorCode = glGetError()) {
            GL_NO_ERROR -> null
            GL_INVALID_ENUM -> "OGL Error: Invalid enum parameter."
            GL_INVALID_VALUE -> "OGL Error: Invalid value parameter."
            GL_INVALID_OPERATION -> "OGL Error: Invalid operation."
            GL_OUT_OF_MEMORY -> "OGL Error: Out of memory."
            GL_INVALID_FRAMEBUFFER_OPERATION -> "OGL Error: Invalid framebuffer operation."
            else -> "OGL Error: $errorCode"
        }
    }

    override fun getCurrentHandle(attribute: Int): Int {
        val handle = IntArray(1)
        glVerify(this) { glGetIntegerv(attribute, handle) }
        return handle[0]
    }

    private fun verifyProgramState(
        program: Int,
        pname: Int,
        glGetOperation: (Int, Int, IntArray, IntArray, IntArray, ByteBuffer) -> Unit,
        error: () -> String
    ) {
        val count = IntArray(1)
        glGetProgramiv(program, pname, count)
        for (i in 0 until count[0]) {
            val length = IntArray(1)
            val size = IntArray(1)
            val type = IntArray(1)
            val byteBuffer = ByteBuffer.allocateDirect(256)
            glGetOperation(program, i, length, size, type, byteBuffer)
            println(getString(byteBuffer, length[0]))
        }
        crash(error())
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