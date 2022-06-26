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