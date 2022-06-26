package dev.misasi.giancarlo.math

enum class Rotation {
    None,
    Rotate90,
    Rotate180,
    Rotate270;

    companion object {
        fun fromDirection(facingDirection: Direction) : Rotation {
            return when (facingDirection) {
                Direction.Left,
                Direction.DownLeft -> {
                    Rotate90
                }
                Direction.Up,
                Direction.UpLeft -> {
                    Rotate180
                }
                Direction.Right,
                Direction.UpRight -> {
                    Rotate270
                }
                Direction.Down,
                Direction.DownRight -> {
                    None
                }
            }
        }
    }
}