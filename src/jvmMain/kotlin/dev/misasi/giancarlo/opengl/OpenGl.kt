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
import dev.misasi.giancarlo.drawing.Rgba8
import dev.misasi.giancarlo.drawing.ShapeType
import dev.misasi.giancarlo.events.input.mouse.CursorMode
import dev.misasi.giancarlo.math.Matrix2f
import dev.misasi.giancarlo.math.Matrix4f
import dev.misasi.giancarlo.math.Vector2f
import dev.misasi.giancarlo.math.Vector2i
import dev.misasi.giancarlo.system.DataType
import dev.misasi.giancarlo.system.System.Companion.crash
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.system.MemoryStack
import java.nio.ByteBuffer
import java.nio.charset.Charset

actual class OpenGl private constructor() {

    actual fun createWindow(title: String, size: Vector2i, fullScreen: Boolean, refreshRate: Int, vsync: Boolean): Long {
        // Setup an error callback
        GLFWErrorCallback.createPrint(System.err).set()

        // Configure GLFW
        check(GLFW.glfwInit()) { "Unable to initialize GLFW" }
        GLFW.glfwDefaultWindowHints() // optional, the current window hints are already the default
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE) // the window will stay hidden after creation
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE) // the window will not be manually resizable

        // Create the window
        val window = GLFW.glfwCreateWindow(size.x, size.y, title, 0, 0)
        check(window != 0L) { "Unable to create window" }

        // Make the OpenGL context current
        GLFW.glfwMakeContextCurrent(window)

        // Configure the window
        // TODO: Does this need to move down?
        if (fullScreen) {
            gl.setFullScreen(window, size, refreshRate)
        } else {
            gl.setWindowed(window, size)
        }
        gl.setVsync(window, vsync)

        // Make the window visible
        GLFW.glfwShowWindow(window)

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities()

        // Return the window
        return window
    }

    actual fun setCurrentWindow(window: Long) {
        GLFW.glfwMakeContextCurrent(window)
    }

    actual fun setWindowTitle(window: Long, title: String) {
        GLFW.glfwSetWindowTitle(window, title)
    }

    actual fun setFullScreen(window: Long, resolution: Vector2i, refreshRate: Int) {
        val monitor = GLFW.glfwGetPrimaryMonitor()
        GLFW.glfwSetWindowSize(window, resolution.x, resolution.y)
        GLFW.glfwSetWindowMonitor(window, monitor, 0, 0, resolution.x, resolution.y, refreshRate)
    }

    actual fun setWindowed(window: Long, windowSize: Vector2i) {
        val primaryMonitor = GLFW.glfwGetPrimaryMonitor()
        val mode = GLFW.glfwGetVideoMode(primaryMonitor)!!
        val x = ((mode.width().toFloat() - windowSize.x) / 2f).toInt()
        val y = ((mode.height().toFloat() - windowSize.y) / 2f).toInt()
        GLFW.glfwSetWindowSize(window, windowSize.x, windowSize.y)
        GLFW.glfwSetWindowMonitor(window, 0, x, y, windowSize.x, windowSize.y, GLFW.GLFW_DONT_CARE)
    }

    actual fun setVsync(window: Long, vsync: Boolean) {
        GLFW.glfwSwapInterval(if (vsync) 1 else 0)
    }

    actual fun getPrimaryMonitorResolution(): Vector2i {
        val mode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor())!!
        return Vector2i(mode.width(), mode.height())
    }

    actual fun getActualWindowSize(window: Long): Vector2i {
        val stack: MemoryStack
        try {
            stack = MemoryStack.stackPush()
            val pWidth = stack.mallocInt(1)
            val pHeight = stack.mallocInt(1)
            GLFW.glfwGetWindowSize(window, pWidth, pHeight)
            return Vector2i(pWidth.get(0), pHeight.get(0))
        } finally {
            MemoryStack.stackPop()
        }
    }

    actual fun setCursorMode(window: Long, mode: CursorMode) {
        if (mode == CursorMode.FPS) {
            GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED)
            if (GLFW.glfwRawMouseMotionSupported()) {
                GLFW.glfwSetInputMode(window, GLFW.GLFW_RAW_MOUSE_MOTION, GLFW.GLFW_TRUE);
            }
        } else {
            GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL)
        }
    }

    actual fun swapBuffers(window: Long) {
        GLFW.glfwSwapBuffers(window)
    }

    actual fun closeWindow(window: Long) {
        GLFW.glfwSetWindowShouldClose(window, true)
    }

    actual fun shouldCloseWindow(window: Long): Boolean {
        return GLFW.glfwWindowShouldClose(window)
    }

    actual fun getMousePosition(window: Long): Vector2f {
        val stack: MemoryStack
        try {
            stack = MemoryStack.stackPush()
            val pWidth = stack.mallocDouble(1)
            val pHeight = stack.mallocDouble(1)
            GLFW.glfwGetCursorPos(window, pWidth, pHeight)
            return Vector2f(pWidth.get(0), pHeight.get(0))
        } finally {
            MemoryStack.stackPop()
        }
    }

    actual fun onKeyboardEvents(window: Long, handler: ((window: Long, keyCode: Int, scanCode: Int, actionCode: Int, modifierCode: Int) -> Unit)?) {
        GLFW.glfwSetKeyCallback(window, handler)
    }

    actual fun onTextEvents(window: Long, handler: ((window: Long, codePoint: Int) -> Unit)?) {
        GLFW.glfwSetCharCallback(window, handler)
    }

    actual fun onMouseEvents(window: Long, handler: ((window: Long, x: Double, y: Double) -> Unit)?) {
        GLFW.glfwSetCursorPosCallback(window, handler)
    }

    actual fun onMouseButtonEvents(window: Long, handler: ((window: Long, buttonCode: Int, actionCode: Int, modifierCode: Int) -> Unit)?) {
        GLFW.glfwSetMouseButtonCallback(window, handler)
    }

    actual fun onScrollEvents(window: Long, handler: ((window: Long, x: Double, y: Double) -> Unit)?) {
        GLFW.glfwSetScrollCallback(window, handler)
    }

    actual fun onResizeEvents(window: Long, handler: ((window: Long, x: Int, y: Int) -> Unit)?) {
        GLFW.glfwSetFramebufferSizeCallback(window, handler)
    }

    actual fun pollEvents() {
        GLFW.glfwPollEvents()
    }

    actual fun createProgram(): Int {
        return glVerifyCreate(this, ::glCreateProgram) { "PROGRAM" }
    }

    actual fun linkProgram(program: Int) {
        glVerify(this) { glLinkProgram(program) }
        glVerifyStatus(this, ::glGetProgramiv, program, GL_LINK_STATUS, ::glGetProgramInfoLog)
    }

    actual fun bindProgram(program: Int): Int {
        glVerify(this) { glUseProgram(program) }
        return program
    }

    actual fun deleteProgram(program: Int) {
        glVerify(this) { glDeleteProgram(program) }
    }

    actual fun createShader(type: Shader.Type): Int {
        val shaderType = shaderTypeMapping[type]!!
        return glVerifyCreate(this, { glCreateShader(shaderType) }) { "SHADER(${type.name})" }
    }

    actual fun compileShader(shader: Int, source: String) {
        glVerify(this) { glShaderSource(shader, source) }
        glVerify(this) { glCompileShader(shader) }
        glVerifyStatus(this, ::glGetShaderiv, shader, GL_COMPILE_STATUS, ::glGetShaderInfoLog)
    }

    actual fun attachShader(program: Int, shader: Int) {
        glVerify(this) { glAttachShader(program, shader) }
    }

    actual fun detachShader(program: Int, shader: Int) {
        glVerify(this) { glDetachShader(program, shader) }
    }

    actual fun deleteShader(shader: Int) {
        glVerify(this) { glDeleteShader(shader) }
    }

    actual fun getUniformLocation(program: Int, name: String): Int {
        glVerifyBound(this, GL_CURRENT_PROGRAM, program) { "PROGRAM<$program>" }
        val location = glVerify<Int>(this) { glGetUniformLocation(program, name) }
        if (location < 0) {
            verifyProgramState(program, GL_ACTIVE_UNIFORMS, ::glGetActiveUniform) { "Missing uniform: $name" }
        }
        return location
    }

    actual fun setUniformMatrix2f(program: Int, uniform: Int, value: Matrix2f) {
        // opengl does not use row order, so we apply the transpose
        glVerify(this) { glUniformMatrix2fv(uniform, false, value.transpose.data) }
    }

    actual fun setUniformMatrix4f(program: Int, uniform: Int, value: Matrix4f) {
        // opengl does not use row order, so we apply the transpose
        glVerify(this) { glUniformMatrix4fv(uniform, false, value.transpose.data) }
    }
    actual fun setUniformVector2f(program: Int, uniform: Int, value: Vector2f) {
        glVerify(this) { glUniform2f(uniform, value.x, value.y) }
    }
    actual fun setUniform1f(program: Int, uniform: Int, value: Float) {
        glVerify(this) { glUniform1f(uniform, value) }
    }
    actual fun setUniform1i(program: Int, uniform: Int, value: Int) {
        glVerify(this) { glUniform1i(uniform, value) }
    }
    actual fun setUniform1b(program: Int, uniform: Int, value: Boolean) {
        glVerify(this) { glUniform1i(uniform, if (value) 1 else 0) }
    }

    actual fun getAttributeLocation(program: Int, name: String): Int {
        glVerifyBound(this, GL_CURRENT_PROGRAM, program) { "PROGRAM<$program>" }
        val location = glVerify<Int>(this) { glGetAttribLocation(program, name) }
        if (location < 0) {
            verifyProgramState(program, GL_ACTIVE_ATTRIBUTES, ::glGetActiveAttrib) { "Missing attribute: $name" }
        }
        return location
    }

    actual fun enableVertexAttributeArray(attribute: Int) {
        glVerify(this) { glEnableVertexAttribArray(attribute) }
    }

    actual fun setVertexAttributePointer(handle: Int, attribute: Attribute, totalStride: Int) {
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

    actual fun setVertexAttributeIPointer(handle: Int, attribute: Attribute, totalStride: Int) {
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

    actual fun createBuffer(type: Buffer.Type, usage: Buffer.Usage, maxBytes: Int): Int {
        val t = bufferTypeMapping[type]!!
        val u = bufferUsageMapping[usage]!!
        val vbo = bindBuffer(glVerifyGenerate(this, ::glGenBuffers) { "BUFFER(${type.name})[${usage.name}]" }, type)
        glVerify(this) { glBufferData(t, maxBytes.toLong(), u) }
        return vbo
    }

    actual fun createBuffer(type: Buffer.Type, usage: Buffer.Usage, data: NioBuffer): Int {
        val t = bufferTypeMapping[type]!!
        val u = bufferUsageMapping[usage]!!
        val vbo = bindBuffer(glVerifyGenerate(this, ::glGenBuffers) { "BUFFER(${type.name})[${usage.name}]" }, type)
        glVerify(this) { glBufferData(t, data.buffer as ByteBuffer, u) }
        return vbo
    }

    actual fun bindBuffer(handle: Int, type: Buffer.Type): Int {
        val t = bufferTypeMapping[type]!!
        glVerify(this) { glBindBuffer(t, handle) }
        return handle
    }

    actual fun updateBufferData(handle: Int, type: Buffer.Type, data: NioBuffer, byteOffset: Int) {
        val t = bufferTypeMapping[type]!!
        val b = bufferBindingMapping[type]!!
        glVerifyBound(this, b, handle) { "BUFFER<$handle>" }
        glVerify(this) { glBufferSubData(t, byteOffset.toLong(), data.buffer as ByteBuffer) }
    }

    actual fun deleteBuffer(handle: Int) {
        glVerify(this) { glDeleteBuffers(handle) }
    }

    actual fun createAttributeArray(): Int {
        return bindAttributeArray(glVerifyGenerate(this, ::glGenVertexArrays) { "VAO" })
    }

    actual fun bindAttributeArray(vao: Int): Int {
        glVerify(this) { glBindVertexArray(vao) }
        return vao
    }

    actual fun deleteAttributeArray(vao: Int) {
        glVerify(this) { glDeleteVertexArrays(vao) }
    }

    actual fun createTexture2d(size: Vector2i, format: Rgba8.Format, data: NioBuffer?): Int {
        val texture = bindTexture2d(glVerifyGenerate(this, ::glGenTextures) { "TEX2D" })
        val formatParameter = textureFormat[format]!!
        glVerify(this) {
            glTexImage2D(
                GL_TEXTURE_2D,
                0,
                GL_RGBA,
                size.x,
                size.y,
                0,
                formatParameter,
                GL_UNSIGNED_BYTE,
                data?.buffer as ByteBuffer?
            )
        }
        return texture
    }

    actual fun setTextureMinFilter(filter: Texture.Filter) {
        val filterParameter = textureFilter[filter]!!
        glVerify(this) { glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filterParameter) }
    }

    actual fun setTextureMagFilter(filter: Texture.Filter) {
        val filterParameter = textureFilter[filter]!!
        glVerify(this) { glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filterParameter) }
    }

    actual fun setTextureBorderColor(color: Rgba8) {
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

    actual fun setTextureWrapS(wrap: Texture.Wrap) {
        val wrapParameter = textureWrap[wrap]!!
        glVerify(this) { glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, wrapParameter) }
    }

    actual fun setTextureWrapT(wrap: Texture.Wrap) {
        val wrapParameter = textureWrap[wrap]!!
        glVerify(this) { glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, wrapParameter) }
    }

    actual fun bindTexture2d(texture: Int): Int {
        glVerify(this) { glBindTexture(GL_TEXTURE_2D, texture) }
        return texture
    }

    actual fun setActiveTextureUnit(target: Int) {
        glVerify(this) { glActiveTexture(GL_TEXTURE0 + target) }
    }

    actual fun deleteTexture2d(handle: Int) {
        glVerify(this) { glDeleteTextures(handle) }
    }

    actual fun createFrameBuffer(): Int {
        return glVerifyGenerate(this, ::glGenFramebuffers) { "FRAME BUFFER" }
    }

    actual fun bindFrameBuffer(handle: Int): Int {
        glVerify(this) { glBindFramebuffer(GL_FRAMEBUFFER, handle) }
        return handle
    }

    actual fun attachTexture(texture: Int) {
        glVerify(this) { glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture, 0) }
        glVerifyEquals(
            this,
            GL_FRAMEBUFFER_COMPLETE,
            { glCheckFramebufferStatus(GL_FRAMEBUFFER) }
        ) { "Failed to attach texture $texture to frame buffer." }
    }

    actual fun deleteFrameBuffer(handle: Int) {
        glVerify(this) { glDeleteFramebuffers(handle) }
    }

    actual fun setViewport(size: Vector2i, position: Vector2i) {
        glVerify(this) { glViewport(position.x, position.y, size.x, size.y) }
    }

    actual fun enableScissor(enabled: Boolean) {
        glVerify(this) { glEnable(GL_SCISSOR_TEST) }
    }

    actual fun setScissor(size: Vector2i, position: Vector2i) {
        glVerify(this) { glScissor(position.x, position.y, size.x, size.y) }
    }

    actual fun draw(type: ShapeType, offset: Int, vertexCount: Int) {
        when (type) {
            ShapeType.LINE -> drawLines(offset, vertexCount)
            ShapeType.TRIANGLE -> drawTriangles(offset, vertexCount)
            ShapeType.SQUARE -> drawTriangles(offset, vertexCount)
//            else -> throw IllegalArgumentException("Draw type ${type.name} not supported.")
        }
    }

    actual fun drawIndexed(type: ShapeType, offset: Int, vertexCount: Int, indexType: DataType) {
        when (type) {
            ShapeType.LINE -> drawLinesIndexed(offset, vertexCount, indexType)
            ShapeType.TRIANGLE, ShapeType.SQUARE -> drawTrianglesIndexed(offset, vertexCount, indexType)
//            else -> throw IllegalArgumentException("Draw type ${type.name} not supported.")
        }
    }

    actual fun drawLines(offset: Int, vertexCount: Int) {
        glVerify(this) { glDrawArrays(GL_LINES, offset, vertexCount) }
    }

    actual fun drawLinesIndexed(offset: Int, vertexCount: Int, indexType: DataType) {
        val t = dataTypeMapping[indexType]!!
        glVerify(this) { glDrawElements(GL_LINES, vertexCount, t, offset.toLong()) }
    }

    actual fun drawTriangles(offset: Int, vertexCount: Int) {
        glVerify(this) { glDrawArrays(GL_TRIANGLES, offset, vertexCount) }
    }

    actual fun drawTrianglesIndexed(offset: Int, vertexCount: Int, indexType: DataType) {
        val t = dataTypeMapping[indexType]!!
        glVerify(this) { glDrawElements(GL_TRIANGLES, vertexCount, t, offset.toLong()) }
    }

    actual fun setClearColor(color: Rgba8) {
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

    actual fun clear() {
        glVerify(this) { glClear(GL_COLOR_BUFFER_BIT) }
    }

    actual fun enable(target: Int) {
        glVerify(this) { glEnable(target) }
    }

    actual fun getErrorMessage(): String? {
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

    actual fun getCurrentHandle(attribute: Int): Int {
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

    actual companion object {
        actual val gl: OpenGl get() = getInstance()

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

        @Volatile
        private var INSTANCE: OpenGl? = null

        private fun getInstance(): OpenGl =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: OpenGl().also { INSTANCE = it }
            }
    }
}