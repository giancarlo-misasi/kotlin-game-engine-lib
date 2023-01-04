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

data class Rectangle constructor(
    val size: Vector2f,
    val tl: Vector2f,
    val br: Vector2f
) {
    constructor(size: Vector2f) : this(size, Vector2f(), size)
    constructor(position: Vector2f, size: Vector2f) : this(size, position, position.plus(size))

    val tr by lazy {
        Vector2f(br.x, tl.y)
    }

    val bl by lazy {
        Vector2f(tl.x, br.y)
    }

    val center by lazy {
        Vector2f(tl.x + 0.5f * size.x, tl.y + 0.5f * size.y)
    }

    val radius by lazy {
        sqrt(radiusSquared)
    }

    val radiusSquared by lazy {
        br.minus(center).lengthSquared
    }

    fun move(delta: Vector2f): Rectangle {
        return copy(tl = tl.plus(delta), br = br.plus(delta))
    }

    fun moveTo(to: Vector2f): Rectangle {
        return move(to.minus(tl))
    }

    fun contains(point: Vector2f): Boolean {
        return point.x > tl.x && point.x < br.x && point.y > tl.y && point.y < br.y;
    }
}