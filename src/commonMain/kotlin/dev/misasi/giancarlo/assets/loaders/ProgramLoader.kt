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

import dev.misasi.giancarlo.getResourceAsLines
import dev.misasi.giancarlo.opengl.Attribute
import dev.misasi.giancarlo.opengl.OpenGl
import dev.misasi.giancarlo.opengl.Program
import dev.misasi.giancarlo.opengl.Shader

class ProgramLoader (
    private val gl: OpenGl,
    private val shaderLoader: AssetLoader<Shader>
) : AssetLoader<Program> {

    companion object {
        private const val SUFFIX = ".program"
        private const val PATH = "/programs/"
        private const val SHADER = "SHADER"
        private const val UNIFORM = "UNIFORM"
        private const val ATTRIBUTE = "ATTRIBUTE"
    }

    override fun load(): Map<String, Program> {
        val files = getResourceAsLines(PATH)
        val shaders = shaderLoader.load()
        return files.map { getName(it) to loadProgram(it, shaders) }
            .filter { it.second != null }
            .associate { it.first to it.second!! }
    }

    private fun loadProgram(
        fileName: String,
        shaderMap: Map<String, Shader>
    ): Program? {
        val lines = getResourceAsLines(PATH.plus(fileName))
        if (lines.isEmpty()) {
            return null
        }

        val shaders = getShaders(lines, shaderMap)
        if (shaders.isEmpty()) {
            return null
        }

        val uniforms = getUniforms(lines)

        val attributes = getAttributes(lines)
        if (attributes.isEmpty()) {
            return null
        }

        return Program(gl, shaders, uniforms, attributes)
    }

    private fun getName(fileName: String): String {
        return fileName.removeSuffix(SUFFIX)
    }

    private fun getShaders(lines: List<String>, shaderMap: Map<String, Shader>): List<Shader> {
        return getTokensWithPrefix(SHADER, 2, lines)
            .mapNotNull { shaderMap[it[1]] }
    }

    private fun getUniforms(lines: List<String>): List<String> {
        return getTokensWithPrefix(UNIFORM, 2, lines)
            .map { it[1] }
    }

    private fun getAttributes(lines: List<String>): List<Attribute> {
        return getTokensWithPrefix(ATTRIBUTE, 6, lines)
            .map { getAttribute(it) }
    }

    private fun getAttribute(tokens: List<String>): Attribute {
        val name = tokens[1]
        val type = Attribute.Type.valueOf(tokens[2])
        val count = tokens[3].toInt()
        val strideOffset = tokens[4].toInt()
        val totalStride = tokens[5].toInt()
        return Attribute(name, type, count, strideOffset, totalStride)
    }
}