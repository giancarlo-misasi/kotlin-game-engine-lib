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

import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

data class Vector2f (val x: Float = 0f, val y: Float = 0f) {
    val lengthSquared by lazy {
        x * x + y * y
    }

    val length by lazy {
        sqrt(lengthSquared)
    }

    val inverseLength by lazy {
        1f / length
    }

    val aspectRatio by lazy {
        x / y
    }

    val normal by lazy {
        Vector2f(x * inverseLength, y * inverseLength)
    }

    val negated by lazy {
        Vector2f(-x, -y)
    }

    fun dot(other: Vector2f) : Float {
        return x * other.x + y * other.y
    }

    fun plus(other: Vector2f) : Vector2f {
        return Vector2f(x + other.x, y + other.y)
    }

    fun minus(other: Vector2f) : Vector2f {
        return Vector2f(x - other.x, y - other.y)
    }

    fun multiply(other: Vector2f) : Vector2f {
        return Vector2f(x * other.x, y * other.y);
    }

    fun divide(other: Vector2f) : Vector2f {
        return Vector2f(x / other.x, y / other.y);
    }

    fun scale(scale: Float) : Vector2f {
        return Vector2f(scale * x, scale * y)
    }

    fun lowerBound(bound: Vector2f) : Vector2f {
        return Vector2f(
            max(x, bound.x),
            max(y, bound.y)
        )
    }

    fun upperBound(bound: Vector2f) : Vector2f {
        return Vector2f(
            min(x, bound.x),
            min(y, bound.y)
        )
    }

    fun withinBounds(upperBound: Vector2f, lowerBound: Vector2f = Vector2f()) : Boolean {
        return x >= lowerBound.x
                && x <= upperBound.x
                && y >= lowerBound.y
                && y <= upperBound.y
    }
}