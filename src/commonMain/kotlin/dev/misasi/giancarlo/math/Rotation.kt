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

enum class Rotation(val cosTheta: Float, val sinTheta: Float) {
    DEGREES_90(0f, 1f),
    DEGREES_180(-1f, 0f),
    DEGREES_270(0f, -1f);

    fun concatenate(other: Rotation?) = other?.let { concatMap[this]!![it]!! } ?: this

    fun toVector2f(): Vector2f = Vector2f(cosTheta, sinTheta)

    companion object {
        private val concatMap = mapOf(
            DEGREES_90 to mapOf(
                DEGREES_90 to DEGREES_180,
                DEGREES_180 to DEGREES_270,
                DEGREES_270 to null,
            ),
            DEGREES_180 to mapOf(
                DEGREES_90 to DEGREES_270,
                DEGREES_180 to null,
                DEGREES_270 to DEGREES_90,
            ),
            DEGREES_270 to mapOf(
                DEGREES_90 to null,
                DEGREES_180 to DEGREES_90,
                DEGREES_270 to DEGREES_180,
            ),
        )

        fun Rotation?.toVector2f(): Vector2f =
            this?.toVector2f() ?: Vector2f(1f, 0f)

        fun fromDirection(facingDirection: Direction): Rotation? {
            return when (facingDirection) {
                Direction.LEFT,
                Direction.DOWN_LEFT -> DEGREES_90
                Direction.UP,
                Direction.UP_LEFT -> DEGREES_180
                Direction.RIGHT,
                Direction.UP_RIGHT -> DEGREES_270
                Direction.DOWN,
                Direction.DOWN_RIGHT -> null
            }
        }
    }
}