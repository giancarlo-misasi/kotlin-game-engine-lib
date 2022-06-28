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

class Physics {
    companion object {

        fun staticCollision(velocity: Vector2f, intersection: Vector2f, bounce: Float, slide: Float): Vector2f {
            val dotProduct = velocity.dot(intersection.normal)

            // this means the velocity and the direction to project out of collision
            // are in the same 180 degree direction, and in those cases we don't want
            // to change our speed otherwise we will actually go faster
            if (dotProduct >= 0) {
                return velocity
            }

            val parallelComponent = intersection.normal.scale(dotProduct)
            val perpendicularComponent = velocity.minus(parallelComponent)
            return velocity.minus(parallelComponent.scale(bounce)).minus(perpendicularComponent.scale(slide))
        }

        fun dynamicElasticCollision(
            velocityA: Vector2f, massA: Float,
            velocityB: Vector2f, massB: Float,
            intersection: Vector2f
        ): Pair<Vector2f, Vector2f> {
            // m1v1i + m2v2i = m1v1f + m2v2f
            // p = mv
            // trick is to only calculate in the intersection direction using dot product

            val massTotal = massA + massB
            val massDelta = massA - massB

            val momentumAi = velocityA.dot(intersection)
            val momentumBi = velocityB.dot(intersection)

            val momentumAf = (momentumAi * massDelta + 2 * massB * momentumBi) / massTotal
            val momentumBf = (momentumBi * -massDelta + 2 * massA * momentumAi) / massTotal

            return Pair(
                velocityA.plus(intersection.normal.scale(momentumAf - momentumAi)),
                velocityB.plus(intersection.normal.scale(momentumBf - momentumBi))
            )
        }
    }
}