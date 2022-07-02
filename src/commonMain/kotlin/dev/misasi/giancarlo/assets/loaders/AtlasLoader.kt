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

package dev.misasi.giancarlo.assets.loaders

import dev.misasi.giancarlo.drawing.Atlas
import dev.misasi.giancarlo.drawing.MaterialSet
import dev.misasi.giancarlo.drawing.StaticMaterial
import dev.misasi.giancarlo.getResourceAsLines
import dev.misasi.giancarlo.math.Point4
import dev.misasi.giancarlo.math.Vector2f
import dev.misasi.giancarlo.opengl.Texture

class AtlasLoader (
    private val textureLoader: AssetLoader<Texture>
) : AssetLoader<Atlas> {

    companion object {
        private const val DELIMITER = " "
        private const val SUFFIX = ".atlas"
        private const val PATH = "/atlases/"
        private const val TEXTURE = "TEXTURE"
        private const val MATERIAL = "MATERIAL"
        private const val SET = "SET"
    }

    override fun load(): Map<String, Atlas> {
        val files = getResourceAsLines(PATH)
        val textures = textureLoader.load()
        return files.map { getName(it) to loadAtlas(it, textures) }
            .filter { it.second != null }
            .associate { it.first to it.second!! }
    }

    private fun loadAtlas(
        fileName: String,
        textureMap: Map<String, Texture>
    ): Atlas? {
        val lines = getResourceAsLines(PATH.plus(fileName))
        if (lines.isEmpty()) {
            return null
        }
        val textureName = getTextureName(lines) ?: return null
        val texture = textureMap[textureName] ?: return null
        val materials = getStaticMaterials(lines, texture)
        val sets = getSets(lines, materials)
        return Atlas(materials, sets)
    }

    private fun getName(fileName: String): String {
        return fileName.removeSuffix(SUFFIX)
    }

    private fun getTextureName(lines: List<String>): String? {
        return getTokensWithPrefix(TEXTURE, 2, lines)
            .map { it[1] }
            .firstOrNull()
    }

    private fun getStaticMaterials(lines: List<String>, texture: Texture): Map<String, StaticMaterial> {
        return getTokensWithPrefix(MATERIAL, 6, lines)
            .map { createStaticMaterial(it, texture) }
            .associateBy { it.name() }
    }

    private fun getSets(lines: List<String>, materials: Map<String, StaticMaterial>): Map<String, MaterialSet> {
        return getTokensWithPrefix(SET, 3, lines)
            .map { createSet(it, materials) }
            .associateBy { it.name }
    }

    private fun createStaticMaterial(tokens: List<String>, texture: Texture): StaticMaterial {
        val name = tokens[1]
        val position = Vector2f(tokens[2].toFloat(), tokens[3].toFloat())
        val size = Vector2f(tokens[4].toFloat(), tokens[5].toFloat())
        val uvPosition = position.divide(texture.size)
        val uvSize = size.divide(texture.size)
        return StaticMaterial(name, texture.textureHandle, Point4.create(uvPosition, uvSize), size)
    }

    private fun createSet(tokens: List<String>, materials: Map<String, StaticMaterial>): MaterialSet {
        val prefix = tokens[1]
        val frames = materials.values
            .filter { it.name().startsWith(prefix) }
            .sortedBy { it.name() }
        val frameDurationMillis = tokens[2].toInt()
        return MaterialSet(prefix, frames, frameDurationMillis)
    }
}