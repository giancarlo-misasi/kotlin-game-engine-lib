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

data class Aabb (val tl: Vector2f, val tr: Vector2f, val br: Vector2f, val bl: Vector2f) {

    fun rotate(rotation: Rotation) : Aabb {
        return when (rotation) {
            Rotation.DEGREES_90 -> Aabb(Vector2f(br.x, tl.y), br, Vector2f(tl.x, br.y), tl)
            Rotation.DEGREES_180 -> Aabb(br, Vector2f(tl.x, br.y), tl, Vector2f(br.x, tl.y))
            Rotation.DEGREES_270 -> Aabb(Vector2f(tl.x, br.y), tl, Vector2f(br.x, tl.y), br)
            else -> this.copy()
        }
    }

    fun flip(flip: Flip?): Aabb {
        return if (flip == null) {
            this
        } else {
            Aabb(tl.flip(flip), tr.flip(flip), br.flip(flip), bl.flip(flip))
        }
    }

    companion object {
        fun create(position: Vector2f, size: Vector2f) : Aabb {
            val br = position.plus(size)
            return Aabb(position, Vector2f(br.x, position.y), br, Vector2f(position.x, br.y))
        }

        fun create(positionTl: Vector2f, size: Vector2f, rotation: Rotation?) : Aabb {
            val br = positionTl.plus(size)
            return when (rotation) {
                Rotation.DEGREES_90 -> Aabb(Vector2f(br.x, positionTl.y), br, Vector2f(positionTl.x, br.y), positionTl)
                Rotation.DEGREES_180 -> Aabb(br, Vector2f(positionTl.x, br.y), positionTl, Vector2f(br.x, positionTl.y))
                Rotation.DEGREES_270 -> Aabb(Vector2f(positionTl.x, br.y), positionTl, Vector2f(br.x, positionTl.y), br)
                else -> Aabb(positionTl, Vector2f(br.x, positionTl.y), br, Vector2f(positionTl.x, br.y))
            }
        }

        private fun Vector2f.flip(flip: Flip?): Vector2f = when(flip) {
            Flip.VERTICAL -> Vector2f(x, 1f - y)
            Flip.HORIZONTAL -> Vector2f(1f - x, y)
            else -> Vector2f(1f - x, 1f - y)
        }
    }
}