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
    constructor(x: Number, y: Number) : this(x.toFloat(), y.toFloat())

    val xy by lazy { x * y }
    val lengthSquared by lazy { x * x + y * y }
    val length by lazy { sqrt(lengthSquared) }
    val inverseLength by lazy { 1f / length  }
    val aspectRatio by lazy { x / y }
    val normal by lazy { Vector2f(x * inverseLength, y * inverseLength) }
    val negated by lazy { Vector2f(-x, -y) }
    val min by lazy { if (x < y) x else y }
    val max by lazy { if (x > y) x else y }

    fun dot(other: Vector2f) : Float = x * other.x + y * other.y
    fun plus(other: Vector2f) : Vector2f = Vector2f(x + other.x, y + other.y)
    fun minus(other: Vector2f) : Vector2f = Vector2f(x - other.x, y - other.y)
    fun multiply(other: Vector2f) : Vector2f = Vector2f(x * other.x, y * other.y)
    fun divide(other: Vector2f) : Vector2f = Vector2f(x / other.x, y / other.y)
    fun multiply(scale: Number) : Vector2f = Vector2f(scale.toFloat() * x, scale.toFloat() * y)
    fun min(other: Vector2f) : Vector2f = Vector2f(min(x, other.x), min(y, other.y))
    fun max(other: Vector2f) : Vector2f = Vector2f(max(x, other.x), max(y, other.y))
    fun half(): Vector2f = Vector2f(x / 2f, y / 2f)
    fun toVector2i(): Vector2i = Vector2i(x, y)
}

data class Vector2i (val x: Int = 0, val y: Int = 0) {
    constructor(x: Number, y: Number) : this(x.toInt(), y.toInt())

    val xy by lazy { x * y }
    val lengthSquared by lazy { (x * x + y * y) }
    val length by lazy { sqrt(lengthSquared.toFloat()) }
    val inverseLength by lazy { 1f / length  }
    val aspectRatio by lazy { x.toFloat() / y.toFloat() }
    val normal by lazy { Vector2i(x * inverseLength, y * inverseLength) }
    val negated by lazy { Vector2i(-x, -y) }
    val min by lazy { if (x < y) x else y }
    val max by lazy { if (x > y) x else y }

    fun dot(other: Vector2i) : Float = (x * other.x + y * other.y).toFloat()
    fun plus(other: Vector2i) : Vector2i = Vector2i(x + other.x, y + other.y)
    fun minus(other: Vector2i) : Vector2i = Vector2i(x - other.x, y - other.y)
    fun multiply(other: Vector2i) : Vector2i = Vector2i(x * other.x, y * other.y)
    fun divide(other: Vector2i) : Vector2i = Vector2i(x / other.x, y / other.y)
    fun multiply(scale: Number) : Vector2i = Vector2i(scale.toFloat() * x, scale.toFloat() * y)
    fun min(other: Vector2i) : Vector2i = Vector2i(min(x, other.x), min(y, other.y))
    fun max(other: Vector2i) : Vector2i = Vector2i(max(x, other.x), max(y, other.y))
    fun half(): Vector2i = Vector2i(x / 2, y / 2)
    fun toVector2f(): Vector2f = Vector2f(x, y)
}

data class Vector3f (val x: Float = 0f, val y: Float = 0f, val z: Float = 0f) {
    constructor(x: Number, y: Number, z: Number) : this(x.toFloat(), y.toFloat(), z.toFloat())
    constructor(v: Vector2f, z: Float) : this(v.x, v.y, z)

    val lengthSquared by lazy { x * x + y * y + z * z }
    val length by lazy { sqrt(lengthSquared) }
    val inverseLength by lazy { 1f / length }
    val normal by lazy { Vector3f(x * inverseLength, y * inverseLength, z * inverseLength) }
    val negated by lazy { Vector3f(-x, -y, -z) }

    fun dot(other: Vector3f) : Float = x * other.x + y * other.y + z * other.z
    fun cross(other: Vector3f) : Vector3f = Vector3f(y * other.z - z * other.y, z * other.x - x * other.z, x * other.y - y * other.x)
    fun plus(other: Vector3f) : Vector3f = Vector3f(x + other.x, y + other.y, z + other.z)
    fun minus(other: Vector3f) : Vector3f = Vector3f(x - other.x, y - other.y, z - other.z)
    fun multiply(other: Vector3f) : Vector3f = Vector3f(x * other.x, y * other.y, z * other.z)
    fun divide(other: Vector3f) : Vector3f = Vector3f(x / other.x, y / other.y, z / other.z)
    fun multiply(scale: Number) : Vector3f = Vector3f(scale.toFloat() * x, scale.toFloat() * y, scale.toFloat() * z)
    fun min(other: Vector3f) : Vector3f = Vector3f(min(x, other.x), min(y, other.y), min(z, other.z))
    fun max(other: Vector3f) : Vector3f = Vector3f(max(x, other.x), max(y, other.y), max(z, other.z))
    fun toVector3i(): Vector3i = Vector3i(x, y, z)
}

data class Vector3i (val x: Int = 0, val y: Int = 0, val z: Int = 0) {
    constructor(x: Number, y: Number, z: Number) : this(x.toInt(), y.toInt(), z.toInt())
    constructor(v: Vector2i, z: Number) : this(v.x, v.y, z.toInt())

    val lengthSquared by lazy { (x * x + y * y + z * z) }
    val length by lazy { sqrt(lengthSquared.toFloat()) }
    val inverseLength by lazy { 1f / length }
    val normal by lazy { Vector3i(x * inverseLength, y * inverseLength, z * inverseLength) }
    val negated by lazy { Vector3i(-x, -y, -z) }

    fun dot(other: Vector3i) : Float = (x * other.x + y * other.y + z * other.z).toFloat()
    fun cross(other: Vector3i) : Vector3i = Vector3i(y * other.z - z * other.y, z * other.x - x * other.z, x * other.y - y * other.x)
    fun plus(other: Vector3i) : Vector3i = Vector3i(x + other.x, y + other.y, z + other.z)
    fun minus(other: Vector3i) : Vector3i = Vector3i(x - other.x, y - other.y, z - other.z)
    fun multiply(other: Vector3i) : Vector3i = Vector3i(x * other.x, y * other.y, z * other.z)
    fun divide(other: Vector3i) : Vector3i = Vector3i(x / other.x, y / other.y, z / other.z)
    fun multiply(scale: Number) : Vector3i = Vector3i(scale.toFloat() * x, scale.toFloat() * y, scale.toFloat() * z)
    fun min(other: Vector3i) : Vector3i = Vector3i(min(x, other.x), min(y, other.y), min(z, other.z))
    fun max(other: Vector3i) : Vector3i = Vector3i(max(x, other.x), max(y, other.y), max(z, other.z))
    fun toVector3f(): Vector3f = Vector3f(x, y, z)
}

fun Iterable<Vector2f>.sum(): Vector2f = reduce(Vector2f::plus)
fun Iterable<Vector2i>.sum(): Vector2i = reduce(Vector2i::plus)
fun Iterable<Vector3f>.sum(): Vector3f = reduce(Vector3f::plus)
fun Iterable<Vector3i>.sum(): Vector3i = reduce(Vector3i::plus)