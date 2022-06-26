package dev.misasi.giancarlo.math

enum class Direction {
    Up,
    Right,
    Down,
    Left,
    UpRight,
    DownRight,
    DownLeft,
    UpLeft;

    companion object {

        fun fromDegree4(degrees: Float): Direction {
            return when (degrees) {
                in 45.0f..134.0f -> {
                    Up
                }
                in 135.0f..224.0f -> {
                    Right
                }
                in 225.0f..314.0f -> {
                    Down
                }
                else -> {
                    Left
                }
            }
        }

        fun fromDegree8(degrees: Float): Direction {
            return when (degrees) {
                in 70.0..109.0 -> {
                    Up
                }
                in 110.0..159.0 -> {
                    UpRight
                }
                in 160.0..199.0 -> {
                    Right
                }
                in 200.0..249.0 -> {
                    DownRight
                }
                in 250.0..289.0 -> {
                    Down
                }
                in 290.0..339.0 -> {
                    DownLeft
                }
                in 340.0..360.0, in 0.0..19.0 -> {
                    Left
                }
                else -> {
                    UpLeft
                }
            }
        }
    }
}