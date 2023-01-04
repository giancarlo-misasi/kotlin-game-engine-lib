package dev.misasi.giancarlo.ux.attributes

import dev.misasi.giancarlo.math.Vector2i

data class Inset (val top: Int = 0, val right: Int = 0, val bottom: Int = 0, val left: Int = 0) {
    val tl by lazy { Vector2i(left, top) }
    val horizontal by lazy { left.plus(right) }
    val vertical by lazy { top.plus(bottom) }
    val size by lazy { Vector2i(horizontal, vertical) }
}