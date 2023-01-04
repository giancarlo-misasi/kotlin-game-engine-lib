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

import dev.misasi.giancarlo.system.System.Companion.crash

data class Shader(private val gl: OpenGl, private val programHandle: Int, val spec: Spec) {
    data class Spec(val name: String, val type: Type, val source: String) {
        constructor(name: String, source: String) : this(name, fromName(name), source)
    }

    enum class Type {
        VERTEX,
        FRAGMENT;
    }

    private val shaderHandle = gl.createShader(spec.type)

    init {
        gl.compileShader(shaderHandle, spec.source)
        gl.attachShader(programHandle, shaderHandle)
    }

    fun close() {
        gl.detachShader(programHandle, shaderHandle)
        gl.deleteShader(shaderHandle)
    }

    companion object {
        private const val VERTEX_EXTENSION = "Vertex"
        private const val FRAGMENT_EXTENSION = "Fragment"

        fun fromName(name: String): Shader.Type {
            return if (name.endsWith(VERTEX_EXTENSION)) {
                Shader.Type.VERTEX
            } else if (name.endsWith(FRAGMENT_EXTENSION)) {
                Shader.Type.FRAGMENT
            } else {
                crash("Unknown shader type: $name")
            }
        }
    }
}