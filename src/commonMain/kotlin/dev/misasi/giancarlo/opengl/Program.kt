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

import dev.misasi.giancarlo.math.Matrix2f
import dev.misasi.giancarlo.math.Matrix4f
import dev.misasi.giancarlo.math.Vector2f

class Program(private val gl: OpenGl, shaderSpecs: List<Shader.Spec>, uniformNames: List<String>) {
    private val handle: Int = gl.createProgram()
    private val uniforms: Map<String, Int>

    init {
        // Create, compile and attach shaders
        val shaders = shaderSpecs.map { Shader(gl, handle, it) }

        // Link the program and cleanup the shaders
        try {
            gl.linkProgram(handle)
        } finally {
            shaders.forEach { it.close() }
        }

        // Bind the program
        bind()

        // Find the uniforms
        uniforms = uniformNames.associateWith { gl.getUniformLocation(handle, it) }
    }

    fun bind(): Program {
        if (boundHandle != handle) {
            gl.bindProgram(handle)
            boundHandle = handle
        }
        return this
    }

    fun setUniformMatrix2f(name: String, value: Matrix2f) = gl.setUniformMatrix2f(handle, uniforms[name]!!, value)
    fun setUniformMatrix4f(name: String, value: Matrix4f) = gl.setUniformMatrix4f(handle, uniforms[name]!!, value)
    fun setUniformVector2f(name: String, value: Vector2f) = gl.setUniformVector2f(handle, uniforms[name]!!, value)
    fun setUniformFloat(name: String, value: Float) = gl.setUniform1f(handle, uniforms[name]!!, value)
    fun setUniformInt(name: String, value: Int) = gl.setUniform1i(handle, uniforms[name]!!, value)
    fun setUniformBoolean(name: String, value: Boolean) = gl.setUniform1b(handle, uniforms[name]!!, value)

    fun getAttributeHandle(name: String): Int {
        return gl.getAttributeLocation(handle, name)
    }

    fun delete() {
        gl.deleteProgram(handle)
    }

    companion object {
        private var boundHandle = 0

        fun unbind(gl: OpenGl) {
            if (boundHandle != 0) {
                gl.bindProgram(0)
                boundHandle = 0
            }
        }
    }
}