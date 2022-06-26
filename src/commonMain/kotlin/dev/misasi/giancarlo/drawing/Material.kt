package dev.misasi.giancarlo.drawing

import dev.misasi.giancarlo.math.Point4
import dev.misasi.giancarlo.math.Vector2f
import kotlin.math.max

data class Material (
    val name: String,
    val textureHandle: Int,
    val coordinates: Point4,
    val size: Vector2f
) {
    val diameter by lazy {
        max(size.x, size.y)
    }
}
