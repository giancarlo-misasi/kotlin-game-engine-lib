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

abstract class Buffer private constructor(
    protected val gl: OpenGl,
    protected val handle: Int,
    val type: Type,
    val usage: Usage,
    val capacityInBytes: Int
) {
    constructor(gl: OpenGl, type: Type, usage: Usage, capacityInBytes: Int)
            : this(gl, gl.createBuffer(type, usage, capacityInBytes), type, usage, capacityInBytes)

    constructor(gl: OpenGl, type: Type, usage: Usage, data: NioBuffer)
            : this(gl, gl.createBuffer(type, usage, data), type, usage, data.sizeInBytes)

    enum class Type {
        VERTEX,
        INDEX
    }

    enum class Usage {
        STATIC,
        DYNAMIC,
        STREAM
    }

    abstract fun bind(): Any
    abstract fun update(data: NioBuffer, byteOffset: Int = 0): Any
    abstract fun delete()
}