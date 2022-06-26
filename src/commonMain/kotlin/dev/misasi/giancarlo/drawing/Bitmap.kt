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

package dev.misasi.giancarlo.drawing

import dev.misasi.giancarlo.memory.DirectByteBuffer

class Bitmap(
    pixels: IntArray,
    val width: Int,
    val height: Int
) {
    val data: DirectByteBuffer = DirectByteBuffer(4 * width * height)

    init {
        // Generate the direct buffer
        for (y in 0 until height) {
            for (x in 0 until width) {
                val pixel = pixels[y * width + x]
                data.putInt(pixel)
            }
        }

        // TODO Check this
//            buffer.put((byte) ((pixel >> 16) & 0xFF));     // Red component
//            buffer.put((byte) ((pixel >> 8) & 0xFF));      // Green component
//            buffer.put((byte) (pixel & 0xFF));               // Blue component
//            buffer.put((byte) ((pixel >> 24) & 0xFF));    // Alpha component. Only for RGBA
    }

    fun clear() {
        data.clear()
    }
}
