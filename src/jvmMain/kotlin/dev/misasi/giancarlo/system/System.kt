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

package dev.misasi.giancarlo.system

import dev.misasi.giancarlo.drawing.Bitmap
import dev.misasi.giancarlo.drawing.Rgba8
import dev.misasi.giancarlo.math.Vector2i
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.zip.ZipInputStream
import javax.imageio.ImageIO
import kotlin.io.path.pathString
import kotlin.io.path.toPath
import kotlin.system.exitProcess

actual class System {
    actual companion object {
        actual fun getCurrentTimeMs(): Long = java.lang.System.currentTimeMillis()

        actual fun getResources(path: String): List<String> {
            return if (isJarScheme(path)) {
                listFilesInJar(path)
            } else {
                listFilesInIde(path)
            }
        }

        actual fun getResourceName(path: String): String = getResourceNameWithoutExtension(path)

        actual fun getResourceAsBytes(path: String): ByteArray = getResourceAsStream(path).use {
            return it.readAllBytes()
        }

        actual fun getResourceAsLines(path: String): List<String> = getResourceAsStream(path).use {
            return it.bufferedReader().use(BufferedReader::readLines)
        }

        actual fun getResourceAsString(path: String): String = getResourceAsStream(path).use {
            return it.bufferedReader().use(BufferedReader::readText)
        }

        actual fun getResourceAsBitmap(name: String, path: String): Bitmap = getResourceAsStream(path).use {
            val bufferedImage = ImageIO.read(it)
            val argb = IntArray(bufferedImage.width * bufferedImage.height)
            bufferedImage.getRGB(0, 0, bufferedImage.width, bufferedImage.height, argb, 0, bufferedImage.width);
            return Bitmap(name, argb, Rgba8.Format.BGRA, Vector2i(bufferedImage.width, bufferedImage.height))
        }

        actual fun crash(message: String): Nothing {
            println("ERROR: $message")
            exitProcess(-1)
        }

        private fun getResourceAsStream(path: String): InputStream =
            object {}.javaClass.getResourceAsStream(path) ?: crash("Failed to find resource: $path")

        private fun listFilesInIde(idePath: String): List<String> = getResourceAsStream(idePath).use {
            it.bufferedReader().use(BufferedReader::readLines)
        }

        private fun listFilesInJar(idePath: String): List<String> {
            val jarPath = idePath.trimStart('/')
            println("jp=$jarPath");
            return ZipInputStream(getJarLocation().openStream()).use {
                val result = mutableListOf<String>()
                while (true) {
                    val entry = it.nextEntry ?: break
                    if (!entry.name.startsWith(jarPath) || entry.isDirectory) continue
                    println(getResourceNameWithExtension(entry.name))
                    result.add(getResourceNameWithExtension(entry.name))
                }
                result
            }
        }

        private fun getJarLocation(): URL = {}.javaClass.protectionDomain.codeSource.location

        private fun isJarScheme(path: String) = {}.javaClass.getResource(path)?.toURI()?.scheme == "jar"

        private fun getResourceNameWithExtension(path: String) = path.split("/").last()

        private fun getResourceNameWithoutExtension(path: String) = getResourceNameWithExtension(path).split(".").first()
    }
}
