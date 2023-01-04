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

import dev.misasi.giancarlo.drawing.Rgba8
import dev.misasi.giancarlo.math.Vector2f
import dev.misasi.giancarlo.math.Vector2i
import dev.misasi.giancarlo.system.DataType
import java.nio.ByteBuffer

expect class NioBuffer(capacityInBytes: Int) {
    constructor(dataType: DataType, count: Int)

    var index: Int
    val sizeInBytes: Int
    val byteBuffer: ByteBuffer

    fun cleanup()
    fun setIndex(newIndex: Int): Int
    fun putFloat(index: Int, value: Float): NioBuffer
    fun putFloat(value: Float): NioBuffer
    fun putInt(index: Int, value: Int): NioBuffer
    fun putInt(value: Int): NioBuffer
    fun putShort(index: Int, value: Short): NioBuffer
    fun putShort(value: Short): NioBuffer
    fun putByte(index: Int, value: Byte): NioBuffer
    fun putByte(value: Byte): NioBuffer
    fun putUInt(index: Int, value: UInt): NioBuffer
    fun putUInt(value: UInt): NioBuffer
    fun putUShort(index: Int, value: UShort): NioBuffer
    fun putUShort(value: UShort): NioBuffer
    fun putUByte(index: Int, value: UByte): NioBuffer
    fun putUByte(value: UByte): NioBuffer
    fun putVector2f(index: Int, xy: Vector2f): NioBuffer
    fun putVector2f(xy: Vector2f): NioBuffer
    fun putVector2i(index: Int, xy: Vector2i): NioBuffer
    fun putVector2i(xy: Vector2i): NioBuffer
    fun putRgba8(index: Int, color: Rgba8): NioBuffer
    fun putRgba8(color: Rgba8): NioBuffer
    fun putFloats(value: Iterable<Float>): NioBuffer
    fun putInts(value: Iterable<Int>): NioBuffer
    fun putFloatArray(value: FloatArray): NioBuffer
    fun putIntArray(value: IntArray): NioBuffer
}