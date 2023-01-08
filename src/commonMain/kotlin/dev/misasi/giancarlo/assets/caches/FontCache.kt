/*
 * MIT License
 *
 * Copyright (c) 2023 Giancarlo Misasi
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

package dev.misasi.giancarlo.assets.caches

import dev.misasi.giancarlo.drawing.Bitmap
import dev.misasi.giancarlo.drawing.Font
import dev.misasi.giancarlo.drawing.StaticMaterial
import dev.misasi.giancarlo.math.Aabb
import dev.misasi.giancarlo.math.Vector2f
import dev.misasi.giancarlo.math.Vector2i
import dev.misasi.giancarlo.system.System.Companion.crash
import dev.misasi.giancarlo.system.System.Companion.getResourceAsLines
import dev.misasi.giancarlo.system.System.Companion.getResourceName
import dev.misasi.giancarlo.system.System.Companion.getResources

class FontCache(bitmapCache: BitmapCache, materialCache: MaterialCache) : Cache<Font> {
    private val fonts = getResources(PATH)
        .map { load(it, bitmapCache, materialCache) }
        .associateBy { it.fontName }
        .toMutableMap()

    override fun get(name: String) = fonts[name] ?: crash("Font $name not found.")

    override fun put(name: String, value: Font) {
        fonts[name] = value
    }

    companion object {
        private const val PATH = "/fonts"
        private val SPACE_OR_TAB = "[ \t]".toRegex()

        private fun load(path: String, bitmapCache: BitmapCache, materialCache: MaterialCache): Font {
            val fontName = getResourceName(path)
            val bitmap = bitmapCache.get(fontName.trimEnd('0', '1', '2', '3', '4', '5', '6', '7', '8', '9'))
            val lines = getResourceAsLines("${PATH}/$path")
            val common = lines.firstNotNullOf { common(it) }
            val characters = lines
                .mapNotNull { characters(it, fontName, bitmap, materialCache) }
                .associateBy { it.codePoint }
            val kernings = lines.mapNotNull { kernings(it) }.associateBy { it.id }
            return Font(fontName, common, characters, kernings)
        }

        private fun common(line: String): Font.Properties? {
            if (!line.startsWith("common")) return null

            val map = parseLine(line)
            if (!map.keys.containsAll(commonKeys)) return null

            return Font.Properties(
                map["lineHeight"]!!.toInt(),
            )
        }

        private fun characters(
            line: String,
            fontName: String,
            bitmap: Bitmap,
            materialCache: MaterialCache
        ): Font.Character? {
            if (!line.startsWith("char")) return null

            val map = parseLine(line)
            if (!map.keys.containsAll(characterKeys)) return null

            val material = parseMaterial(map, fontName, bitmap)
            materialCache.put(material.materialName, material)

            return Font.Character(
                map["id"]!!.toInt().toChar(),
                parseSize(map),
                parseOffset(map),
                map["xadvance"]!!.toInt(),
            )
        }

        private fun kernings(line: String): Font.Kerning? {
            if (!line.startsWith("kerning")) return null

            val map = parseLine(line)
            if (!map.keys.containsAll(kerningKeys)) return null

            return Font.Kerning(
                map["first"]!!.toInt().toChar(),
                map["second"]!!.toInt().toChar(),
                map["amount"]!!.toInt(),
            )
        }

        private fun parseMaterial(map: Map<String, String>, fontName: String, bitmap: Bitmap): StaticMaterial {
            val name = "${fontName}${map["id"]!!.toInt().toChar()}"
            val position = Vector2f(map["x"]!!.toInt(), map["y"]!!.toInt())
            val size = Vector2i(map["width"]!!.toInt(), map["height"]!!.toInt())
            val uvPosition = position.divide(bitmap.size.toVector2f())
            val uvSize = size.toVector2f().divide(bitmap.size.toVector2f())
            return StaticMaterial(name, bitmap.name, Aabb.create(uvPosition, uvSize), size)
        }

        private fun parseSize(map: Map<String, String>): Vector2f {
            return Vector2f(map["width"]!!.toInt(), map["height"]!!.toInt())
        }

        private fun parseOffset(map: Map<String, String>): Vector2f {
            return Vector2f(map["xoffset"]!!.toInt(), map["yoffset"]!!.toInt())
        }

        private fun parseLine(line: String): Map<String, String> {
            return line.split(SPACE_OR_TAB)
                .map { it.split('=') }
                .filter { it.size == 2 }
                .associate { it.first() to it.last() }
        }

        private val commonKeys = setOf(
            "lineHeight",
        )

        private val characterKeys = setOf(
            "id",
            "x",
            "y",
            "width",
            "height",
            "xoffset",
            "yoffset",
            "xadvance",
        )

        private val kerningKeys = setOf(
            "first",
            "second",
            "amount",
        )
    }
}