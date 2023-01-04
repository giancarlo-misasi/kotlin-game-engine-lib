package dev.misasi.giancarlo.math

enum class Reflection(val x: Float, val y: Float) {
    VERTICAL(1f, -1f),
    HORIZONTAL(-1f, 1f),
    BOTH(-1f, -1f);

    fun toVector2f(): Vector2f =
        Vector2f(x, y)

    companion object {
        fun Reflection?.toVector2f(): Vector2f =
            this?.toVector2f() ?: Vector2f(1f, 1f)
    }
}