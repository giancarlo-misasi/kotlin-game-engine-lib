package dev.misasi.giancarlo.math

data class Point4 (val tl: Vector2f, val tr: Vector2f, val br: Vector2f, val bl: Vector2f) {

    companion object {
        fun create(position: Vector2f, size: Vector2f) : Point4 {
            val br = position.plus(size)
            return Point4(position, Vector2f(br.x, position.y), br, Vector2f(position.x, br.y))
        }

        fun create(positionTl: Vector2f, size: Vector2f, rotation: Rotation) : Point4 {
            val br = positionTl.plus(size)
            return when (rotation) {
                Rotation.None -> {
                    Point4(positionTl, Vector2f(br.x, positionTl.y), br, Vector2f(positionTl.x, br.y))
                }
                Rotation.Rotate90 -> {
                    Point4(Vector2f(br.x, positionTl.y), br, Vector2f(positionTl.x, br.y), positionTl)
                }
                Rotation.Rotate180 -> {
                    Point4(br, Vector2f(positionTl.x, br.y), positionTl, Vector2f(br.x, positionTl.y))
                }
                Rotation.Rotate270 -> {
                    Point4(Vector2f(positionTl.x, br.y), positionTl, Vector2f(br.x, positionTl.y), br)
                }
            }
        }
    }
}