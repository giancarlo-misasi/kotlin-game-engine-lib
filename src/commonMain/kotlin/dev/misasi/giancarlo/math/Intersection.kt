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

class Intersection {
    companion object {

        // Circle - Circle
        fun intersects(c: Circle, other: Circle): Boolean {
            val delta = c.position.minus(other.position)
            val radiusSum = c.radius + other.radius
            return delta.lengthSquared < radiusSum.times(radiusSum)
        }

        // Circle - Circle
        fun intersection(c: Circle, other: Circle): Vector2f? {
            val delta = c.position.minus(other.position)
            val radiusSum = c.radius + other.radius
            return if (delta.lengthSquared < radiusSum.times(radiusSum)) {
                delta.normal.multiply(radiusSum - delta.length)
            } else {
                null
            }
        }

        // Rectangle - Rectangle (Axis aligned)
        fun intersects(r: Rectangle, other: Rectangle): Boolean {
            return r.tl.x < other.br.x
                    && r.br.x > other.tl.x
                    && r.tl.y < other.br.y
                    && r.br.y > other.tl.y
        }

        // Rectangle - Rectangle (Axis aligned)
        fun intersection(r: Rectangle, other: Rectangle): Vector2f? {
            val lx = max(r.tl.x, other.tl.x)
            val rx = min(r.br.x, other.br.x)
            if (lx >= rx) {
                return null
            }

            val ty = max(r.tl.y, other.tl.y)
            val by = min(r.br.y, other.br.y)
            if (ty >= by) {
                return null
            }

            val x = (rx - lx).times(if (r.tl.x < other.tl.x) -1f else 1f)
            val y = (by - ty).times(if (r.tl.y < other.tl.y) -1f else 1f)
            return Vector2f(x, y)
        }

        // Circle - Rectangle (Axis aligned)
        fun intersects(c: Circle, other: Rectangle): Boolean {
            // find the closest point on the rectangle
            val cx = if (c.position.x < other.tl.x) {
                other.tl.x
            } else if (c.position.x > other.br.x) {
                other.br.x
            } else {
                c.position.x
            }

            val cy = if (c.position.y < other.tl.y) {
                other.tl.y
            } else if (c.position.y > other.br.y) {
                other.br.y
            } else {
                c.position.y
            }

            val delta = Vector2f(c.position.x - cx, c.position.y - cy)
            return delta.lengthSquared < c.radiusSquared
        }

        // Circle - Rectangle (Axis aligned)
        fun intersection(c: Circle, other: Rectangle): Vector2f? {
            // find the closest point on the rectangle
            val cx = if (c.position.x < other.tl.x) {
                other.tl.x
            } else if (c.position.x > other.br.x) {
                other.br.x
            } else {
                c.position.x
            }

            val cy = if (c.position.y < other.tl.y) {
                other.tl.y
            } else if (c.position.y > other.br.y) {
                other.br.y
            } else {
                c.position.y
            }

            val delta = Vector2f(c.position.x - cx, c.position.y - cy)
            return if (delta.x == 0f && delta.y == 0f) {
                // uh-oh were inside, let's treat the rectangle as a circle
                val d = c.position.minus(other.center)
                d.normal.multiply(c.radius + other.radius - d.length)
            } else if (delta.lengthSquared < c.radiusSquared) {
                delta.normal.multiply(c.radius - delta.length)
            } else {
                null
            }
        }

        // Rectangle (Axis aligned) - Circle
        fun intersects(r: Rectangle, other: Circle): Boolean {
            return intersects(other, r)
        }

        // Rectangle (Axis aligned) - Circle
        fun intersection(r: Rectangle, other: Circle): Vector2f? {
            return intersection(other, r)?.negated
        }
    }
}