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

import java.nio.ByteBuffer
import java.nio.ByteOrder

class DirectByteBuffer(
    val capacityInBytes: Int
) {
    var sizeInBytes: Int = 0
        private set

    val byteBuffer: ByteBuffer = ByteBuffer
        .allocateDirect(capacityInBytes)
        .order(ByteOrder.nativeOrder())

    fun putFloat(value: Float): DirectByteBuffer {
        byteBuffer.putFloat(sizeInBytes, value)
        sizeInBytes += FLOAT_SIZE
        return this
    }

    fun putIntArray(value: IntArray): DirectByteBuffer {
        value.forEach { putInt(it) }
        return this
    }

    fun putInt(value: Int): DirectByteBuffer {
        byteBuffer.putInt(sizeInBytes, value)
        sizeInBytes += INT_SIZE
        return this
    }

    fun putByte(value: Byte): DirectByteBuffer {
        byteBuffer.put(value);
        sizeInBytes++
        return this
    }

    fun clear(): DirectByteBuffer {
        sizeInBytes = 0
        return this
    }

    companion object {
        private const val FLOAT_SIZE: Int = 4
        private const val INT_SIZE: Int = 4
    }
}
