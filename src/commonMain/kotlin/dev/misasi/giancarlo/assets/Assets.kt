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

package dev.misasi.giancarlo.assets

import dev.misasi.giancarlo.assets.loaders.*
import dev.misasi.giancarlo.crash
import dev.misasi.giancarlo.opengl.OpenGl

class Assets (gl: OpenGl) {
    private val programs = ProgramLoader(gl, ShaderLoader()).load()
    private val atlases = AtlasLoader(TextureLoader(gl)).load()
    private val buffers = VertexBufferLoader(gl).load()

    fun program(name: String) = programs[name] ?: crash("Program not found")
    fun atlas(name: String) = atlases[name] ?: crash("Atlas not found")
    fun buffer(name: String) = buffers[name] ?: crash("Buffer not found")
}