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

data class AffineTransform(
    val translation: Vector2f = Vector2f(),
    val scale: Vector2f = Vector2f(1f, 1f),
    val reflection: Reflection? = null,
    val rotation: Rotation? = null
) {
    fun translate(translation: Vector2f): AffineTransform =
        copy(translation = this.translation.plus(translation))

    fun scale(scale: Vector2f): AffineTransform =
        copy(scale = this.scale.plus(scale))

    fun concatenate(affine: AffineTransform?): AffineTransform {
        return affine?.let {
            AffineTransform(
                translation.plus(it.translation),
                scale.multiply(it.scale),
                reflection?.concatenate(it.reflection),
                rotation?.concatenate(it.rotation),
            )
        } ?: this
    }

    fun toLinearTransform(): LinearTransform = LinearTransform(scale, reflection, rotation)

    fun applyLinearTransform(point: Vector2f): Vector2f =
        LinearTransform.applyLinearTransform(point, scale, reflection, rotation)

    fun applyLinearTransformEach(vararg points: Vector2f): List<Vector2f> =
        LinearTransform.applyLinearTransformEach(scale, reflection, rotation, *points)

    fun applyAffineTransformEach(vararg points: Vector2f): List<Vector2f> =
        applyLinearTransformEach(*points).map { it.plus(translation) }

    fun applyLinearTransform(aabb: Aabb): Aabb =
        toAabb(applyLinearTransformEach(aabb.tl, aabb.tr, aabb.br, aabb.bl))

    fun applyAffineTransform(aabb: Aabb): Aabb =
        toAabb(applyAffineTransformEach(aabb.tl, aabb.tr, aabb.br, aabb.bl))

    private fun toAabb(points: List<Vector2f>): Aabb =
        Aabb(points[0], points[1], points[2], points[3])

    companion object {
        fun translate(translation: Vector2f): AffineTransform = AffineTransform(translation = translation)
        fun scale(scale: Vector2f): AffineTransform = AffineTransform(scale = scale)
    }
}