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

package dev.misasi.giancarlo.assets.caches

import dev.misasi.giancarlo.assets.Token
import dev.misasi.giancarlo.drawing.Bitmap
import dev.misasi.giancarlo.drawing.StaticMaterial
import dev.misasi.giancarlo.math.Aabb
import dev.misasi.giancarlo.math.Vector2f
import dev.misasi.giancarlo.math.Vector2i
import dev.misasi.giancarlo.system.System.Companion.crash
import dev.misasi.giancarlo.system.System.Companion.getResourceAsLines
import dev.misasi.giancarlo.system.System.Companion.getResourceName
import dev.misasi.giancarlo.system.System.Companion.getResources

class MaterialCache(bitmapCache: BitmapCache) : Cache<StaticMaterial> {
    private val materials = getResources(PATH)
        .flatMap { load(it, bitmapCache) }
        .associateBy { it.materialName }
        .toMutableMap()

    override fun keys() = materials.keys.toSet()

    override fun get(name: String) = materials[name] ?: crash("Material $name not found.")

    override fun put(name: String, value: StaticMaterial) {
        materials[name] = value
    }

    companion object {
        private const val PATH = "/materials"

        private fun load(path: String, bitmapCache: BitmapCache): List<StaticMaterial> {
            val name = getResourceName(path)
            val bitmap = bitmapCache.get(name)
            val lines = getResourceAsLines("$PATH/$path")
            return Token.getTokens(5, lines).map { staticMaterial(it, bitmap) }
        }

        private fun staticMaterial(tokens: List<String>, bitmap: Bitmap): StaticMaterial {
            val name = "${bitmap.name}${tokens[0]}"
            val position = Vector2f(tokens[1].toFloat(), tokens[2].toFloat())
            val size = Vector2i(tokens[3].toFloat(), tokens[4].toFloat())
            val uvPosition = position.div(bitmap.size.toVector2f())
            val uvSize = size.toVector2f().div(bitmap.size.toVector2f())
            return StaticMaterial(name, bitmap.name, Aabb.create(uvPosition, uvSize), size)
        }
    }
}