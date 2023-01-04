/*
 * MIT License
 *
 * Copyright (c) 2022 Giancarlo Misasi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package dev.misasi.giancarlo.math

import dev.misasi.giancarlo.math.Reflection.Companion.toVector2f
import dev.misasi.giancarlo.math.Rotation.Companion.toVector2f

data class LinearTransform(
    val scale: Vector2f = Vector2f(1f, 1f),
    val reflection: Reflection? = null,
    val rotation: Rotation? = null
) {
    fun applyLinearTransform(point: Vector2f): Vector2f =
        applyLinearTransform(point, scale, reflection, rotation)

    fun applyLinearTransformEach(vararg points: Vector2f): List<Vector2f> =
        applyLinearTransformEach(scale, reflection, rotation, *points)

    companion object {
        fun applyLinearTransform(
            point: Vector2f,
            scale: Vector2f = Vector2f(1f, 1f),
            reflection: Reflection? = null,
            rotation: Rotation? = null
        ): Vector2f {
            val scaleReflectPoint = scale.multiply(reflection.toVector2f()).multiply(point)
            val (cosTheta, sinTheta) = rotation.toVector2f()
            return applyLinearTransform(scaleReflectPoint, cosTheta, sinTheta)
        }

        fun applyLinearTransformEach(
            scale: Vector2f,
            reflection: Reflection?,
            rotation: Rotation?,
            vararg points: Vector2f
        ): List<Vector2f> {
            val scaleReflect = scale.multiply(reflection.toVector2f())
            val (cosTheta, sinTheta) = rotation.toVector2f()
            return points.map { applyLinearTransform(scaleReflect.multiply(it), cosTheta, sinTheta) }
        }

        private fun applyLinearTransform(scaleReflectPoint: Vector2f, cosTheta: Float, sinTheta: Float): Vector2f =
            Vector2f(
                scaleReflectPoint.x * cosTheta - scaleReflectPoint.y * sinTheta,
                scaleReflectPoint.y * cosTheta + scaleReflectPoint.x * sinTheta
            )
    }
}