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

import dev.misasi.giancarlo.opengl.Shader
import dev.misasi.giancarlo.system.System.Companion.crash
import dev.misasi.giancarlo.system.System.Companion.getResources
import dev.misasi.giancarlo.system.System.Companion.getResourceName
import dev.misasi.giancarlo.system.System.Companion.getResourceAsString
import java.util.zip.ZipInputStream

class ShaderCache : Cache<Shader.Spec> {
    private val shaders = getResources(PATH)
        .map { shaderSource(it) }
        .associateBy { it.name }
        .toMutableMap()

    override fun keys() = shaders.keys.toSet()

    override fun get(name: String) = shaders[name] ?: crash("Shader $name not found.")

    override fun put(name: String, value: Shader.Spec) {
        shaders[name] = value
    }

    companion object {
        private const val PATH = "/shaders"

        private fun shaderSource(path: String): Shader.Spec {
            val name = getResourceName(path)
            return Shader.Spec(name, getResourceAsString("$PATH/$path"))
        }
    }
}