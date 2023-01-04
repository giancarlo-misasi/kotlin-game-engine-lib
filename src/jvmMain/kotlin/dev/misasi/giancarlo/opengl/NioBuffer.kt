/*
 * MIT License
 *
 * Copyright (c) 2023 Giancarlo Misasi
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
import org.lwjgl.system.MemoryUtil

actual class NioBuffer actual constructor(capacityInBytes: Int) {
    actual constructor(dataType: DataType, count: Int) : this(dataType.size * count)

    private var index: Int = 0
    private val byteBuffer = MemoryUtil.memAlloc(capacityInBytes)
    // ByteBuffer.allocateDirect(capacityInBytes).order(ByteOrder.nativeOrder())
    actual val buffer: Any = byteBuffer
    actual val sizeInBytes = capacityInBytes

    actual fun cleanup() = MemoryUtil.memFree(byteBuffer)

    actual fun setIndex(newIndex: Int): Int {
        val previous = index
        index = newIndex
        return previous
    }

    actual fun putFloat(index: Int, value: Float): NioBuffer = apply { byteBuffer.putFloat(index, value) }
    actual fun putFloat(value: Float): NioBuffer = putFloat(index, value).updateIndex(DataType.FLOAT)
    actual fun putInt(index: Int, value: Int): NioBuffer = apply { byteBuffer.putInt(index, value) }
    actual fun putInt(value: Int): NioBuffer = putInt(index, value).updateIndex(DataType.INT)
    actual fun putShort(index: Int, value: Short): NioBuffer = apply { byteBuffer.putShort(index, value) }
    actual fun putShort(value: Short): NioBuffer = putShort(index, value).updateIndex(DataType.SHORT)
    actual fun putByte(index: Int, value: Byte): NioBuffer = apply { byteBuffer.put(index, value) }
    actual fun putByte(value: Byte): NioBuffer = putByte(index, value).updateIndex(DataType.BYTE)

    actual fun putUInt(index: Int, value: UInt): NioBuffer = putInt(index, value.toInt())
    actual fun putUInt(value: UInt): NioBuffer = putInt(value.toInt())
    actual fun putUShort(index: Int, value: UShort): NioBuffer = putShort(index, value.toShort())
    actual fun putUShort(value: UShort): NioBuffer = putShort(value.toShort())
    actual fun putUByte(index: Int, value: UByte): NioBuffer = putByte(index, value.toByte())
    actual fun putUByte(value: UByte): NioBuffer = putByte(value.toByte())

    actual fun putVector2f(index: Int, xy: Vector2f): NioBuffer = putFloat(index, xy.x).putFloat(index + DataType.FLOAT.size, xy.y)
    actual fun putVector2f(xy: Vector2f): NioBuffer = putFloat(xy.x).putFloat(xy.y)
    actual fun putVector2i(index: Int, xy: Vector2i): NioBuffer = putInt(index, xy.x).putInt(index + DataType.INT.size, xy.y)
    actual fun putVector2i(xy: Vector2i): NioBuffer = putInt(xy.x).putInt(xy.y)
    actual fun putRgba8(index: Int, color: Rgba8): NioBuffer = putInt(index, color.packedRgba)
    actual fun putRgba8(color: Rgba8): NioBuffer = putInt(color.packedRgba)

    actual fun putFloats(value: Iterable<Float>): NioBuffer = putIterable(value, this::putFloat)
    actual fun putInts(value: Iterable<Int>): NioBuffer = putIterable(value, this::putInt)
    actual fun putFloatArray(value: FloatArray): NioBuffer = putIterator(value.iterator(), this::putFloat)
    actual fun putIntArray(value: IntArray): NioBuffer = putIterator(value.iterator(), this::putInt)

    private inline fun <T> putIterable(value: Iterable<T>, action: (T) -> Unit): NioBuffer {
        value.forEach { action(it) }
        return this
    }

    private inline fun <T> putIterator(value: Iterator<T>, action: (T) -> Unit): NioBuffer {
        value.forEach { action(it) }
        return this
    }

    private fun updateIndex(dataType: DataType): NioBuffer {
        index += dataType.size
        return this
    }
}
