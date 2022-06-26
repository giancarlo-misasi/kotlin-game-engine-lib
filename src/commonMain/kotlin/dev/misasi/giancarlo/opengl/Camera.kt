package dev.misasi.giancarlo.opengl

import dev.misasi.giancarlo.math.Vector2f

data class Camera (
    val position: Vector2f = Vector2f(),
    val zoom: Float = 1f // bigger numbers zoom in, smaller zoom out
)
