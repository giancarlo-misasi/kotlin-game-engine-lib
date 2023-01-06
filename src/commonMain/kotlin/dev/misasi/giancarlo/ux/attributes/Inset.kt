package dev.misasi.giancarlo.ux.attributes

import dev.misasi.giancarlo.math.Rectangle
import dev.misasi.giancarlo.math.Vector2f
import dev.misasi.giancarlo.math.Vector2i

data class Inset (val top: Int = 0, val right: Int = 0, val bottom: Int = 0, val left: Int = 0) {
    val horizontal by lazy { left.plus(right) }
    val vertical by lazy { top.plus(bottom) }
    val size by lazy { Vector2i(horizontal, vertical) }

    fun concatenate(other: Inset) = Inset(
        top + other.top,
        right + other.right,
        bottom + other.bottom,
        left + other.left
    )
}