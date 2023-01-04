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

import kotlin.math.max
import dev.misasi.giancarlo.math.Vector2i

class Bitmap(
    val name: String,
    val pixels: IntArray,
    val format: Rgba8.Format,
    val size: Vector2i
) {
    constructor(name: String, format: Rgba8.Format, size: Vector2i)
            : this(name, IntArray(size.xy) { 0 }, format, size)

    val sizeInBytes by lazy {
        4 * size.x * size.y
    }

    fun copy(bitmap: Bitmap, x: Int, y: Int): Boolean {
        if (!fits(bitmap, x, y)) {
            return false
        }

        for (xi in 0 until bitmap.size.x) {
            for (yj in 0 until bitmap.size.y) {
                setPixel(x + xi, y + yj, bitmap.getPixel(xi, yj))
            }
        }
        return true
    }

    fun fits(bitmap: Bitmap, x: Int, y: Int): Boolean =
        bitmap.size.x + x <= size.x && bitmap.size.y + y <= size.y

    private fun getPixel(x: Int, y: Int): Int = pixels[index(x, y)]

    private fun setPixel(x: Int, y: Int, value: Int) {
        pixels[index(x, y)] = value
    }

    private fun index(x: Int, y: Int): Int = y * size.x + x

    companion object {
        fun pack(bitmaps: List<Bitmap>, size: Vector2i): Map<Bitmap, List<String>> {
            if (bitmaps.isEmpty()) return mapOf()

            val result = mutableMapOf<Bitmap, List<String>>()
            val format = bitmaps.first().format
            val sorted = bitmaps.sortedByDescending { it.size.xy }

            var h = 0
            var x = 0
            var y = 0
            var mergedBitmaps: MutableList<String>? = null
            var newBitmap: Bitmap? = null
            var createNewBitmap = true
            for (bitmap in sorted) {
                if (createNewBitmap) {
                    mergedBitmaps = mutableListOf()
                    newBitmap = Bitmap("packed_${result.size}", format, size)
                    result[newBitmap] = mergedBitmaps
                    createNewBitmap = false
                }

                h = max(h, bitmap.size.y)

                if (newBitmap!!.copy(bitmap, x, y)) {
                    mergedBitmaps!!.add(bitmap.name)
                    x += bitmap.size.x + 1
                    continue
                }

                y += h + 1
                x = 0
                h = bitmap.size.y

                if (newBitmap.copy(bitmap, x, y)) {
                    mergedBitmaps!!.add(bitmap.name)
                    x += bitmap.size.x + 1
                    continue
                }

                createNewBitmap = true
            }

            return result
        }
    }
}