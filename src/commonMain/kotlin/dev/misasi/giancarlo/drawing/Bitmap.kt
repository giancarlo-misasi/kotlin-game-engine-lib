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
