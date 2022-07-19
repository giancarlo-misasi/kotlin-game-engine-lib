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

package dev.misasi.giancarlo.noise

import dev.misasi.giancarlo.collections.mergeReduce
import dev.misasi.giancarlo.collections.sumOf
import dev.misasi.giancarlo.math.Vector2f
import dev.misasi.giancarlo.math.powList
import kotlin.random.Random

interface Noise {
    fun shuffle(seed: Long): Noise
    fun noise2d(x: Float, y: Float): Float

    companion object {
        fun octaves(
            seed: Long,
            count: Int,
            lacunarity: Float = 1.99f,
            gain: Float = 0.499f,
            noiseGenerator: () -> Noise
        ): List<NoiseOctave> {
            val seeds = Random(seed).let { r -> (0 until count).map { r.nextLong() } }
            val frequencies = lacunarity.powList(count)
            val amplitudes = gain.powList(count)
            return (0 until count).map { i -> NoiseOctave(noiseGenerator().shuffle(seeds[i]), frequencies[i], amplitudes[i]) }
        }

        fun points(width: Int, height: Int, step: Int = 1): List<NoisePoint> {
            return (0 until height step step).flatMap { y ->
                val ny = (y / height.toFloat() - 0.5f)
                (0 until width step step).map { x ->
                    val nx = (x / width.toFloat() - 0.5f)
                    NoisePoint(Vector2f(x.toFloat(), y.toFloat()), Vector2f(nx, ny))
                }
            }
        }

        fun List<NoiseOctave>.noise2d(points: List<NoisePoint>): Map<Vector2f, Float> {
            val noises = map { it.noise2d(points) }
            val max = noises.sumOf { it.values.max() }
            val noise = noises.mergeReduce(Float::plus).toMutableMap()
            return noise.onEach { noise[it.key] = it.value / max }
        }
    }
}

// TODO Make a cool function here
//                e = e.times(1.2f).pow(2.4f)
//                e = constrainValue(0f, 1f, e)