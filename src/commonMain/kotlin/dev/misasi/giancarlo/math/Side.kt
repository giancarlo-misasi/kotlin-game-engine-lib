package dev.misasi.giancarlo.math

enum class Side {
    Top,
    Right,
    Bottom,
    Left;

    val opposite by lazy {
        when(this) {
            Side.Top -> Side.Bottom
            Side.Right -> Side.Left
            Side.Bottom -> Side.Top
            Side.Left -> Side.Right
        }
    }
}