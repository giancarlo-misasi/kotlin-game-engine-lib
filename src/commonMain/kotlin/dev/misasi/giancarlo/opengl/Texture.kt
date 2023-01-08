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

import dev.misasi.giancarlo.drawing.Bitmap
import dev.misasi.giancarlo.drawing.Rgb8
import dev.misasi.giancarlo.drawing.Rgba8
import dev.misasi.giancarlo.math.Vector2i

class Texture private constructor(
    private val gl: OpenGl,
    private val handle: Int,
    val name: String,
    val size: Vector2i
) {
    constructor(gl: OpenGl, bmp: Bitmap)
            : this(gl, gl.createTexture2d(bmp.size, bmp.format, buffer(bmp)), bmp.name, bmp.size)

    constructor(
        gl: OpenGl,
        name: String,
        size: Vector2i,
        frameBuffer: FrameBuffer,
        format: Rgba8.Format = Rgba8.Format.RGBA
    ) : this(gl, gl.createTexture2d(size, format), name, size) {
        frameBuffer.attach(this)
    }

    init {
        gl.setTextureMinFilter(Filter.NEAREST)
        gl.setTextureMagFilter(Filter.NEAREST)
//        gl.setTextureBorderColor(Rgba8.BLACK)
        gl.setTextureBorderColor(Rgb8.YELLOW.toRgba()) // for debug
        gl.setTextureWrapS(Wrap.CLAMP_TO_BORDER)
        gl.setTextureWrapT(Wrap.CLAMP_TO_BORDER)
    }

    enum class Filter {
        NEAREST,
        LINEAR
    }

    enum class Wrap {
        REPEAT,
        CLAMP_TO_BORDER
    }

    fun bind(): Texture {
        if (boundHandle != handle) {
            gl.bindTexture2d(handle)
            boundHandle = handle
        }
        return this
    }

    fun attach() {
        bind()
        gl.attachTexture(handle)
        unbind(gl)
    }

    fun delete() {
        gl.deleteTexture2d(handle)
    }

    companion object {
        private var boundHandle = 0

        fun unbind(gl: OpenGl) {
            if (boundHandle != 0) {
                gl.bindTexture2d(0)
                boundHandle = 0
            }
        }

        private fun buffer(bitmap: Bitmap): NioBuffer {
            return NioBuffer(bitmap.sizeInBytes).putIntArray(bitmap.pixels)
        }
    }
}