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

package dev.misasi.giancarlo.events.input.touch

import dev.misasi.giancarlo.system.System.Companion.crash

data class TouchEvent (
    val window: Long,
    val type: Type,
    val time: Long,
    val points: List<TouchPoint>
) {
    init {
        if (points.isEmpty()) {
            crash("TouchEvent must have at least one point.")
        }
    }

    val valid by lazy {
        Type.None != type
    }

    val numberOfPoints by lazy {
        points.size
    }

    val firstPoint by lazy {
        points[0]
    }

    fun getPointByIndex(index: Int) : TouchPoint? {
        return points[index]
    }

    fun getPointById(id: Int) : TouchPoint? {
        return points.find { id == it.id }
    }

    fun hasSamePoints(other: TouchEvent) : Boolean {
        if (!valid || points.size != other.points.size) {
            return false
        }

        return points.all { other.getPointById(it.id) != null }
    }

    enum class Type {
        None,
        Begin,
        Move,
        End,
        Cancel
    }
}
