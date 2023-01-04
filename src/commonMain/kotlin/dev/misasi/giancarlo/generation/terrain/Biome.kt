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
// https://education.nationalgeographic.org/resource/five-major-types-biomes
enum class Biome {
    // Aquatic, bodies of water surrounded by land
    WATER,
    BEACH,

    // Grasslands, open regions that are dominated by grass and have a warm, dry climate
    TROPICAL_GRASSLANDS, // SAVANNAS, have few scattered trees
    // Temperate grasslands
    PRAIRIES, // have tall grass, do not have any trees or shrubs, and receive less precipitation
    STEPPES,  // have shorter grass, do not have any trees or shrubs, and receive less precipitation

    // Forest, dominated by trees
    TROPICAL_FOREST,  // warm, humid, and found close to the equator
    TEMPERATE_FOREST, // higher latitudes, experience all four seasons
    BOREAL_FOREST,    // TAIGA, highest latitudes, coldest/driest climate, precipitation occurs as snow

    // Deserts, dry areas where rainfall is less than 50 centimeters per year
    COLD_DESERT,
    HOT_DESERT,

    // Tundra, extremely inhospitable conditions, lowest temperatures, biodiversity and low precipitation
    TUNDRA
}

