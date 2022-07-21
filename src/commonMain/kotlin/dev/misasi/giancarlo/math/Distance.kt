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

import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

class Distance {
    companion object {
        fun squareBump(width: Int, height: Int): Map<Vector2f, Float> {
            val points = NormalizedPoint.points(width, height) { v -> 2f * v - 1f }
            return points.associate { it.point to 1f - (1 - it.normal.x.pow(2) * (1 - it.normal.y.pow(2))) }
        }

        fun euclideanSquared(width: Int, height: Int): Map<Vector2f, Float> {
            val points = NormalizedPoint.points(width, height) { v -> 2f * v - 1f }
            return points.associate { it.point to min(1f, (it.normal.x.pow(2) + it.normal.y.pow(2)) / sqrt(2f)) }
        }
    }
}