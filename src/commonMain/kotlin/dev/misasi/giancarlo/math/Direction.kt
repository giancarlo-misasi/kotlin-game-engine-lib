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

enum class Direction {
    UP,
    RIGHT,
    DOWN,
    LEFT,
    UP_RIGHT,
    DOWN_RIGHT,
    DOWN_LEFT,
    UP_LEFT;

    companion object {

        fun fromDegree4(degrees: Float): Direction {
            return when (degrees) {
                in 45.0f..134.0f -> UP
                in 135.0f..224.0f -> RIGHT
                in 225.0f..314.0f -> DOWN
                else -> LEFT
            }
        }

        fun fromDegree8(degrees: Float): Direction {
            return when (degrees) {
                in 70.0..109.0 -> UP
                in 110.0..159.0 -> UP_RIGHT
                in 160.0..199.0 -> RIGHT
                in 200.0..249.0 -> DOWN_RIGHT
                in 250.0..289.0 -> DOWN
                in 290.0..339.0 -> DOWN_LEFT
                in 340.0..360.0, in 0.0..19.0 -> LEFT
                else -> UP_LEFT
            }
        }
    }
}