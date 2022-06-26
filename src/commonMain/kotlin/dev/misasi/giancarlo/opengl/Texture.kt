package dev.misasi.giancarlo.opengl

import dev.misasi.giancarlo.drawing.Bitmap
import dev.misasi.giancarlo.math.Vector2f

class Texture(
    gl: OpenGl,
    private val bitmap: Bitmap
) {
    val textureHandle: Int = gl.createTexture2d(bitmap.width, bitmap.height, bitmap.data)

    init {
        bitmap.clear() // free up the memory
    }

    val width by lazy {
        bitmap.width
    }

    val height by lazy {
        bitmap.height
    }

    val size by lazy {
        Vector2f(width.toFloat(), height.toFloat())
    }
}
