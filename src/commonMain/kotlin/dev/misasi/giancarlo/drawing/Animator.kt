package dev.misasi.giancarlo.drawing

import dev.misasi.giancarlo.math.Vector2f
import kotlin.math.max

class Animator {
    companion object {
        fun getFadeInAlpha(percentage: Float) : Int {
            return (255f * (1f - percentage)).toInt()
        }

        fun getFadeOutAlpha(percentage: Float) : Int {
            return (255f * percentage).toInt()
        }

        fun getTransitionPosition(start: Vector2f, end: Vector2f, percentage: Float) : Vector2f {
            return start.plus(end.minus(start).scale(percentage))
        }

        fun getPercentage(elapsedMillis: Int, animationDurationMillis: Int) : Float {
            return elapsedMillis.toFloat() / max(animationDurationMillis, 1).toFloat()
        }
    }
}
