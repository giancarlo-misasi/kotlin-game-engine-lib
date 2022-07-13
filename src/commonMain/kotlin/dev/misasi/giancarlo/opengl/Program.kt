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

class Program(private val gl: OpenGl, shaderSpecs: List<Shader.Spec>) {
    private val handle: Int = gl.createProgram()

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
    }

    fun bind(): Program {
        if (boundHandle != handle) {
            gl.bindProgram(handle)
            boundHandle = handle
        }
        return this
    }

    fun getUniformHandle(name: String): Int {
        return gl.getUniformLocation(handle, name)
    }

    fun getAttributeHandle(name: String): Int {
        return gl.getAttributeLocation(handle, name)
    }

    fun setUniform(uniformHandle: Int, value: Any): Program {
        gl.setUniform(handle, uniformHandle, value)
        return this
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