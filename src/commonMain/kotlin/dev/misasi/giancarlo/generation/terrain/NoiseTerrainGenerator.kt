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

package dev.misasi.giancarlo.generation.terrain

import dev.misasi.giancarlo.math.Grid
import dev.misasi.giancarlo.noise.NoiseOctave
import dev.misasi.giancarlo.noise.NoiseOctave.Companion.noise2d
import dev.misasi.giancarlo.math.NormalizedPoint
import dev.misasi.giancarlo.noise.SimplexNoise
import java.util.*

class NoiseTerrainGenerator : TerrainGenerator {

    override fun generate(seed: Long, width: Int, height: Int): Grid<TerrainChunk> {
        val random = Random(seed)
        val grid = Grid(width, height) { TerrainChunk(Biome.WATER) }
        val points = NormalizedPoint.points(width, height)
        val elevationNoise = NoiseOctave.octaves(random.nextLong(), 3, ::SimplexNoise).noise2d(points)
        val moistureNoise = NoiseOctave.octaves(random.nextLong(), 3, ::SimplexNoise).noise2d(points)
        for (point in points) {
            val elevation = elevationNoise[point.point]!!
            val moisture = moistureNoise[point.point]!!
            grid.replace(point.point.x.toInt(), point.point.y.toInt(), TerrainChunk(biome(elevation, moisture)))
        }
        return grid
    }

    private fun biome(elevation: Float, moisture: Float): Biome {
        return Biome.BEACH
    }
}