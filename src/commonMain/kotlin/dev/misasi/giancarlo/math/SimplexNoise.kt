/*
 * MIT License
 *
 * Copyright (c) 2022 Giancarlo Misasi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modiy, merge, publish, distribute, sublicense, and/or sell
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
 * Original author: Sebastien Rombauts (sebastien.rombauts@gmail.com)
 * Source: https://github.com/SRombauts/SimplexNoise/blob/master/src/SimplexNoise.cpp
 * Converted to Kotlin: Giancarlo Misasi
 *
 */

package dev.misasi.giancarlo.math

import kotlin.random.Random
import kotlin.math.pow

class SimplexNoise {
    private val permutations = (0 until 256).toMutableList()

    fun shuffle(seed: Long): SimplexNoise {
        permutations.shuffle(Random(seed))
        return this
    }

    fun noise2d(x: Float, y: Float): Float {
        // Skew the input space to determine which simplex cell we're in
        val s = (x + y) * F2 // Hairy factor for 2D
        val i = fastFloor(x + s)
        val j = fastFloor(y + s)

        // Unskew the cell origin back to (x,y) space
        val t = (i + j) * G2

        // The x,y distances from the cell origin
        val x0 = x - (i - t)
        val y0 = y - (j - t)

        // For the 2D case, the simplex shape is an equilateral triangle.
        // Determine which simplex we are in.

        // Offsets for second (middle) corner of simplex in (i,j) coords
        val i1: Int
        val j1: Int
        if (x0 > y0) {
            // lower triangle, XY order: (0,0)->(1,0)->(1,1)
            i1 = 1
            j1 = 0
        } else {
            // upper triangle, YX order: (0,0)->(0,1)->(1,1)
            i1 = 0
            j1 = 1
        }

        // A step of (1,0) in (i,j) means a step of (1-c,-c) in (x,y), and
        // a step of (0,1) in (i,j) means a step of (-c,1-c) in (x,y), where
        // c = (3-sqrt(3))/6

        // Offsets for middle corner in (x,y) unskewed coords
        val x1 = x0 - i1 + G2
        val y1 = y0 - j1 + G2

        // Offsets for last corner in (x,y) unskewed coords
        val x2 = x0 - 1f + 2f * G2
        val y2 = y0 - 1f + 2f * G2

        // Work out the hashed gradient indices of the three simplex corners
        val gi0 = hash(i + hash(j));
        val gi1 = hash(i + i1 + hash(j + j1));
        val gi2 = hash(i + 1 + hash(j + 1));

        // Calculate the contribution from the three corners
        val t0 = 0.5f - x0 * x0 - y0 * y0
        val n0 = if (t0 < 0f) {
            0f
        } else {
            t0.pow(4) * grad(gi0, x0, y0);
        }

        val t1 = 0.5f - x1 * x1 - y1 * y1
        val n1 = if (t1 < 0) {
            0f
        } else {
            t1.pow(4) * grad(gi1, x1, y1);
        }

        val t2 = 0.5f - x2 * x2 - y2 * y2
        val n2 = if (t2 < 0) {
            0f
        } else {
            t2.pow(4) * grad(gi2, x2, y2);
        }

        return convertRange(-1f, 1f, 0f, 1f, RANGE_SCALE * (n0 + n1 + n2))
    }

    private fun hash(value: Int): Int {
        return permutations[value.toUByte().toInt()]
    }

    companion object {
        private const val F2 = 0.3660254f // 0.5 * (sqrt(3.0) - 1.0)
        private const val G2 = 0.21132487f // (3.0 - sqrt(3.0)) / 6.0
        private const val RANGE_SCALE = 45.555f // Calculated experimentally by running a large number of iterations and taking average

        /**
         * @param octaves the noise functions to use (to make successive iterations independent instead of correlated)
         * @param lacunarity what makes the frequency grow for each successive octave
         * @param persistence what makes the amplitude shrink (or not) with each successive octave
         */
        fun fractal(octaves: Iterable<SimplexNoise>, x: Float, y: Float, lacunarity: Float = 1.99f, persistence: Float = 0.49f): Float {
            var output = 0f
            var denominator = 0f
            var frequency = 1f
            var amplitude = 1f

            for (octave in octaves) {
                output += amplitude * octave.noise2d(x * frequency, y * frequency)
                denominator += amplitude
                frequency *= lacunarity
                amplitude *= persistence
            }

            return output / denominator
        }

        private fun fastFloor(x: Float): Int {
            val xi = x.toInt()
            return if (x < xi) xi - 1 else xi
        }

        private fun grad(hash: Int, x: Float, y: Float): Float {
            val h = hash and 0x3F       // Convert low 3 bits of hash code  // 7
            val u = if (h < 4) x else y // into 8 simple gradient directions,
            val v = if (h < 4) y else x // and compute the dot product with (x,y).
            val left = if ((h and 1) != 0) -u else u
            val right = if ((h and 2) != 0) (-2f).times(v) else 2f.times(v)
            return left + right
        }
    }
}