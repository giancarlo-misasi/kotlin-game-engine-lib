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

import dev.misasi.giancarlo.math.Grid
import dev.misasi.giancarlo.math.Vector2f
import dev.misasi.giancarlo.math.powList
import kotlin.random.Random

/**
 * To configure and combine multiple iterations of noise.
 */
data class NoiseOctave(private val noiseGenerator: Noise, val frequency: Float, val amplitude: Float) {

    /**
     * Generates a noise value that falls into the range [-amplitude to +amplitude].
     */
    fun noise2d(nxy: Vector2f): Float =
        amplitude * noiseGenerator.noise2d(nxy.times(frequency))

    companion object {

        /**
         * Generates a list of octaves to use to combine iterations of noise.
         */
        fun octaves(
            seed: Long,
            count: Int,
            noiseGenerator: () -> Noise,
            lacunarity: Float = 1.99f,
            gain: Float = 0.499f
        ): List<NoiseOctave> {
            val seeds = Random(seed).let { r -> (0 until count).map { r.nextLong() } }
            val frequencies = lacunarity.powList(count)
            val amplitudes = gain.powList(count)
            return (0 until count).map { i ->
                NoiseOctave(
                    noiseGenerator().shuffle(seeds[i]),
                    frequencies[i],
                    amplitudes[i]
                )
            }
        }

        /**
         * Generates a noise map by combining octaves where noise values fall into the range [0f to 1f].
         */
        fun List<NoiseOctave>.noise2d(
            width: Int,
            height: Int,
            normalize: (Int, Int) -> Vector2f = { x, y ->
                Vector2f(x, y).div(Vector2f(width, height)).minus(Vector2f(0.5f, 0.5f))
            }
        ): Grid<Float> {
            // generate noise map with values from [0f to 2f * amplitude], where the max value is the start amplitude
            val grid = Grid(width, height) { 0f }
            forEach { octave ->
                grid.forEach { cell ->
                    val nxy = normalize(cell.x, cell.y)
                    val sum = grid.at(cell.index) + octave.noise2d(nxy).plus(octave.amplitude)
                    grid.replace(cell.index, sum)
                }
            }

            // calculate the max to normalize the range of values
            val max = grid.maxOf { it.value }
            grid.forEach { grid.replace(it.index, it.value / max) }
            return grid
        }
    }
}