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

import dev.misasi.giancarlo.crash
import dev.misasi.giancarlo.getResourceAsLines
import dev.misasi.giancarlo.getResourceAsString
import dev.misasi.giancarlo.opengl.Shader

class ShaderLoader : AssetLoader<Shader> {

    companion object {
        private const val PATH = "/shaders/"
        private const val SUFFIX = ".glsl"
        private const val VERTEX = "Vertex.glsl"
        private const val FRAGMENT = "Fragment.glsl"
    }

    override fun load(): Map<String, Shader> {
        val files = getResourceAsLines(PATH)
        return files.map { it to getSource(it) }
            .filter { it.second != null }
            .associate { getName(it.first) to Shader(getType(it.first), it.second!!) }
    }

    private fun getName(fileName: String): String {
        return fileName.removeSuffix(SUFFIX)
    }

    private fun getType(fileName: String): Shader.Type {
        return if (fileName.endsWith(VERTEX)) {
            Shader.Type.VERTEX
        } else if (fileName.endsWith(FRAGMENT)) {
            Shader.Type.FRAGMENT
        } else {
            crash("Unknown shader type: $fileName")
        }
    }

    private fun getSource(fileName: String): String? {
        return getResourceAsString(PATH.plus(fileName))
    }
}