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

package dev.misasi.giancarlo.memory

import dev.misasi.giancarlo.drawing.Rgba8
import dev.misasi.giancarlo.math.Vector2f
import dev.misasi.giancarlo.math.Vector2us
import java.nio.ByteBuffer
import java.nio.ByteOrder

class DirectNativeByteBuffer(
    capacityInBytes: Int
) {
    private var index: Int = 0
    val byteBuffer: ByteBuffer = ByteBuffer
        .allocateDirect(capacityInBytes)
        .order(ByteOrder.nativeOrder())
    val sizeInBytes: Int
        get() {
            return index
        }

    fun reset() {
        index = 0
    }

    fun putFloat(value: Float): DirectNativeByteBuffer {
        byteBuffer.putFloat(index, value)
        index += 4
        return this
    }

    fun putInt(value: Int): DirectNativeByteBuffer {
        byteBuffer.putInt(index, value)
        index += 4
        return this
    }

    fun putShort(value: Short): DirectNativeByteBuffer {
        byteBuffer.putShort(index, value)
        index += 2
        return this
    }

    fun putByte(value: Byte): DirectNativeByteBuffer {
        byteBuffer.put(index, value);
        index += 1
        return this
    }

    fun putUInt(value: UInt): DirectNativeByteBuffer = putInt(value.toInt())
    fun putUShort(value: UShort): DirectNativeByteBuffer = putShort(value.toShort())
    fun putUByte(value: UByte): DirectNativeByteBuffer = putByte(value.toByte())
    fun putVector2f(xy: Vector2f): DirectNativeByteBuffer = putFloat(xy.x).putFloat(xy.y)
    fun putVector2us(xy: Vector2us): DirectNativeByteBuffer = putUShort(xy.x).putUShort(xy.y)
    fun putRgba8(color: Rgba8): DirectNativeByteBuffer = putInt(color.packedRgba)

    fun putFloats(value: Iterable<Float>): DirectNativeByteBuffer = putIterable(value, this::putFloat)
    fun putInts(value: Iterable<Int>): DirectNativeByteBuffer = putIterable(value, this::putInt)
    fun putFloatArray(value: FloatArray): DirectNativeByteBuffer = putIterator(value.iterator(), this::putFloat)
    fun putIntArray(value: IntArray): DirectNativeByteBuffer = putIterator(value.iterator(), this::putInt)

    private inline fun <T> putIterable(value: Iterable<T>, action: (T) -> Unit): DirectNativeByteBuffer {
        value.forEach { action(it) }
        return this
    }

    private inline fun <T> putIterator(value: Iterator<T>, action: (T) -> Unit): DirectNativeByteBuffer {
        value.forEach { action(it) }
        return this
    }
}