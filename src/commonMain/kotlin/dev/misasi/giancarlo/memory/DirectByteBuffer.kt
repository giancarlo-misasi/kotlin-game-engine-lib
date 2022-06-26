package dev.misasi.giancarlo.memory

import java.nio.ByteBuffer
import java.nio.ByteOrder

class DirectByteBuffer (
    capacityInBytes: Int
) {
    var sizeInBytes: Int = 0
        private set

    val byteBuffer: ByteBuffer = ByteBuffer
        .allocateDirect(capacityInBytes)
        .order(ByteOrder.nativeOrder())

    fun putFloat(value: Float) : DirectByteBuffer {
        byteBuffer.putFloat(sizeInBytes, value)
        sizeInBytes += FLOAT_SIZE
        return this
    }

    fun putInt(value: Int) : DirectByteBuffer {
        byteBuffer.putInt(sizeInBytes, value)
        sizeInBytes += INT_SIZE
        return this
    }

    fun clear() : DirectByteBuffer {
        sizeInBytes = 0
        return this
    }

    companion object {
        private const val FLOAT_SIZE: Int = 4
        private const val INT_SIZE: Int = 4
    }
}
