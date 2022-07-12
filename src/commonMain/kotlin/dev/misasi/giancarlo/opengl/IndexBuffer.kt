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

import dev.misasi.giancarlo.memory.DirectNativeByteBuffer

class IndexBuffer : Buffer {
    constructor(gl: OpenGl, usage: Usage, maxBytes: Int) : super(gl, Type.INDEX, usage, maxBytes)
    constructor(gl: OpenGl, usage: Usage, data: DirectNativeByteBuffer) : super(gl, Type.INDEX, usage, data)

    override fun bind(): IndexBuffer {
        if (boundHandle != handle) {
            gl.bindBuffer(handle, type)
            boundHandle = handle
        }
        return this
    }

    override fun update(data: DirectNativeByteBuffer, byteOffset: Int): IndexBuffer {
        gl.updateBufferData(handle, type, data, byteOffset)
        return this
    }

    companion object {
        private var boundHandle = 0

        fun unbind(gl: OpenGl) {
            if (boundHandle != 0) {
                gl.bindBuffer(0, Type.INDEX)
                boundHandle = 0
            }
        }
    }
}