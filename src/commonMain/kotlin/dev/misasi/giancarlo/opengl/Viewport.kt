package dev.misasi.giancarlo.opengl

import dev.misasi.giancarlo.math.Matrix4f
import dev.misasi.giancarlo.math.Vector2f
import dev.misasi.giancarlo.math.Vector3f

class Viewport (
    gl: OpenGl,
    val targetResolution: Vector2f,
    val actualScreenSize: Vector2f
) {
    val adjustedScreenSize: Vector2f =
        calculateAdjustedScreenSize(
            targetResolution,
            actualScreenSize
        )
    val offset: Vector2f = actualScreenSize.minus(adjustedScreenSize).scale(0.5f)
    val scale: Vector2f = adjustedScreenSize.divide(targetResolution)

    init {
        gl.setViewport(0, 0, actualScreenSize.x.toInt(), actualScreenSize.y.toInt())
        gl.setScissor(offset.x.toInt(), offset.y.toInt(), adjustedScreenSize.x.toInt(), adjustedScreenSize.y.toInt())
    }

    fun withinBounds(point: Vector2f) : Boolean {
        return point.withinBounds(targetResolution)
    }

    fun adjustToBounds(point: Vector2f) : Vector2f {
        return point.minus(offset).divide(scale)
    }

    fun getModelViewProjection(camera: Camera) : Matrix4f {
        val zoom = 1f / camera.zoom
        val model = Matrix4f.scale(scale)
        val adjusted = camera.position.minus(offset.scale(zoom))
        val view = Matrix4f.lookAt(
            Vector3f(adjusted, 0.5f),
            Vector3f(adjusted, 0.0f),
            Vector3f(0.0f, 1.0f, 0.0f)
        )
        val projection = Matrix4f.ortho(actualScreenSize.scale(zoom))
        return projection.multiply(view).multiply(model).transpose
    }

    companion object {
        private fun calculateAdjustedScreenSize(targetResolution: Vector2f, actualScreenSize: Vector2f) : Vector2f {
            val requiredScreenHeight = actualScreenSize.x / targetResolution.aspectRatio;
            return if (requiredScreenHeight > actualScreenSize.y) {
                Vector2f(actualScreenSize.x * targetResolution.aspectRatio, actualScreenSize.y);
            } else {
                Vector2f(actualScreenSize.x, requiredScreenHeight);
            }
        }
    }
}
