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

data class Point4f (val tl: Vector2f, val tr: Vector2f, val br: Vector2f, val bl: Vector2f) {

    companion object {
        fun create(position: Vector2f, size: Vector2f) : Point4f {
            val br = position.plus(size)
            return Point4f(position, Vector2f(br.x, position.y), br, Vector2f(position.x, br.y))
        }

        fun create(positionTl: Vector2f, size: Vector2f, rotation: Rotation) : Point4f {
            val br = positionTl.plus(size)
            return when (rotation) {
                Rotation.None -> {
                    Point4f(positionTl, Vector2f(br.x, positionTl.y), br, Vector2f(positionTl.x, br.y))
                }
                Rotation.Rotate90 -> {
                    Point4f(Vector2f(br.x, positionTl.y), br, Vector2f(positionTl.x, br.y), positionTl)
                }
                Rotation.Rotate180 -> {
                    Point4f(br, Vector2f(positionTl.x, br.y), positionTl, Vector2f(br.x, positionTl.y))
                }
                Rotation.Rotate270 -> {
                    Point4f(Vector2f(positionTl.x, br.y), positionTl, Vector2f(br.x, positionTl.y), br)
                }
            }
        }
    }

    fun rotate(rotation: Rotation) : Point4f {
        return when (rotation) {
            Rotation.None -> {
                this.copy()
            }
            Rotation.Rotate90 -> {
                Point4f(Vector2f(br.x, tl.y), br, Vector2f(tl.x, br.y), tl)
            }
            Rotation.Rotate180 -> {
                Point4f(br, Vector2f(tl.x, br.y), tl, Vector2f(br.x, tl.y))
            }
            Rotation.Rotate270 -> {
                Point4f(Vector2f(tl.x, br.y), tl, Vector2f(br.x, tl.y), br)
            }
        }
    }

    fun toPoint4us(): Point4us {
        return Point4us(tl.toNormalizedVector2us(), tr.toNormalizedVector2us(), br.toNormalizedVector2us(), bl.toNormalizedVector2us())
    }
}

data class Point4us (val tl: Vector2us, val tr: Vector2us, val br: Vector2us, val bl: Vector2us)