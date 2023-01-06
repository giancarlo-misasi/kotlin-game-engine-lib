package dev.misasi.giancarlo.math

enum class Reflection(val x: Float, val y: Float) {
    VERTICAL(1f, -1f),
    HORIZONTAL(-1f, 1f),
    BOTH(-1f, -1f);

    fun concatenate(other: Reflection?) = other?.let { concatMap[this]!![it]!! } ?: this

    fun toVector2f(): Vector2f =
        Vector2f(x, y)

    companion object {
        private val concatMap = mapOf(
            VERTICAL to mapOf(
                VERTICAL to null,
                HORIZONTAL to BOTH,
                BOTH to HORIZONTAL,
            ),
            HORIZONTAL to mapOf(
                VERTICAL to BOTH,
                HORIZONTAL to null,
                BOTH to VERTICAL,
            ),
            BOTH to mapOf(
                VERTICAL to HORIZONTAL,
                HORIZONTAL to VERTICAL,
                BOTH to null,
            ),
        )

        fun Reflection?.toVector2f(): Vector2f =
            this?.toVector2f() ?: Vector2f(1f, 1f)
    }
}