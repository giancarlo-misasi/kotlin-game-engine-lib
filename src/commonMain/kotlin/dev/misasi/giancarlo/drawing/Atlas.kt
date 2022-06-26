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

package dev.misasi.giancarlo.drawing

import dev.misasi.giancarlo.crash
import dev.misasi.giancarlo.opengl.OpenGl
import dev.misasi.giancarlo.opengl.Texture

class Atlas (
    gl: OpenGl,
    bitmap: Bitmap,
    private val materials: Map<String, Material>,
    private val materialSets: Map<String, MaterialSet>
) {
    private val texture: Texture = Texture(gl, bitmap)

    fun getMaterial(name: String) : Material {
        return materials[name]
            ?: crash("Material '$name' not found.")
    }

    fun getMaterialSet(name: String) : MaterialSet {
        return materialSets[name]
            ?: crash("MaterialSet '$name' not found.")
    }

//    companion object {
//        private const val DELIMITER = " "
//
//        private fun createMaterial(texture: Texture, data: List<String>) : Material {
//            val name = data[0]
//            val position = Vector2f(data[1].toFloat(), data[2].toFloat())
//            val size = Vector2f(data[3].toFloat(), data[4].toFloat())
//            val uvPosition = position.divide(texture.size)
//            val uvSize = size.divide(texture.size)
//            return Material(name, texture.textureHandle, Point4.create(uvPosition, uvSize), size)
//        }
//
//        private fun createMaterialSet(materials: Map<String, Material>, data: List<String>) : MaterialSet {
//            val prefix = data[0]
//            val frames = materials.values
//                .filter { it.name.startsWith(prefix) }
//                .toList()
//                .sortedBy { it.name }
//            val frameDurationMillis = data[1].toInt()
//            return MaterialSet(prefix, frames, frameDurationMillis)
//        }
//    }
}
