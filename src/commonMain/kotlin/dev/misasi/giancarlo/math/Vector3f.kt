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

import kotlin.math.sqrt

data class Vector3f (val x: Float = 0f, val y: Float = 0f, val z: Float = 0f) {

    constructor(v: Vector2f, z: Float) : this(v.x, v.y, z)

    val lengthSquared by lazy {
        x * x + y * y + z * z
    }

    val length by lazy {
        sqrt(lengthSquared)
    }

    val inverseLength by lazy {
        1f / length
    }

    val normal by lazy {
        Vector3f(x * inverseLength, y * inverseLength, z * inverseLength)
    }

    val negated by lazy {
        Vector3f(-x, -y, -z)
    }

    fun dot(other: Vector3f) : Float {
        return x * other.x + y * other.y + z * other.z
    }

    fun cross(other: Vector3f) : Vector3f {
        return Vector3f(
            y * other.z - z * other.y,
            z * other.x - x * other.z,
            x * other.y - y * other.x
        )
    }

    fun plus(other: Vector3f) : Vector3f {
        return Vector3f(x + other.x, y + other.y, z + other.z)
    }

    fun minus(other: Vector3f) : Vector3f {
        return Vector3f(x - other.x, y - other.y, z - other.z)
    }

    fun multiply(other: Vector3f) : Vector3f {
        return Vector3f(x * other.x, y * other.y, z * other.z);
    }

    fun divide(other: Vector3f) : Vector3f {
        return Vector3f(x / other.x, y / other.y, z / other.z);
    }

    fun scale(scale: Float) : Vector3f {
        return Vector3f(x * scale, y * scale, z * scale)
    }
}