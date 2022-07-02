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

package dev.misasi.giancarlo.assets.generators

import dev.misasi.giancarlo.crash
import dev.misasi.giancarlo.getResourceAsLines
import dev.misasi.giancarlo.opengl.Shader

/**
 * Simple packager which reads the current shaders in the resources directory
 * and prints them out as kotlin variables.
 */
class ShaderGenerator {
    companion object {
        private const val PATH = "/shaders/"
        private const val SUFFIX = ".glsl"
        private const val VERTEX = "Vertex.glsl"
        private const val FRAGMENT = "Fragment.glsl"
    }

    fun generate() {
        getResourceAsLines(PATH).map { it to PATH.plus(it) }
            .associate { getName(it.first) to Shader.Spec(getType(it.first), it.second) }
            .forEach {
                println(minifySource(it.value.type, it.value.source))
            }
    }

    private fun getName(fileName: String): String {
        return fileName.removeSuffix(SUFFIX)
            .replaceFirstChar { it.lowercase() }
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

    private fun minifySource(type: Shader.Type, path: String): String {
        val lines = getResourceAsLines(path)
        val version = lines.take(1)[0];
        val code = version.plus("\\n")
            .plus(lines.drop(1)
                .filter { !it.startsWith("//") }
                .joinToString("") { it.trim() })
        return "Shader.Spec(Shader.Type.${type.name}, \"$code\"),"
    }
}