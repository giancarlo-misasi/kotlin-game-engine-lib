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

package dev.misasi.giancarlo

import dev.misasi.giancarlo.drawing.Bitmap
import java.io.File
import javax.imageio.ImageIO
import kotlin.system.exitProcess

fun getTimeMillis(): Long {
    return System.currentTimeMillis()
}

fun listFiles(path: String): List<String> {
    return File(path).walk().map { it.absolutePath }.toList()
}

fun readFileToString(path: String): String {
    return File(path).readText()
}

fun readFileToLines(path: String): List<String> {
    return File(path).readLines()
}

fun readFileToBytes(path: String): ByteArray {
    return File(path).readBytes()
}

fun readFileToBitmap(path: String): Bitmap {
    // Read the image
    val bufferedImage = ImageIO.read(File(path))

    // Read the pixels
    val pixels = IntArray(bufferedImage.width * bufferedImage.height)
    bufferedImage.getRGB(0, 0, bufferedImage.width, bufferedImage.height, pixels, 0, bufferedImage.width);

    return Bitmap(pixels, bufferedImage.width, bufferedImage.height)
}

fun crash(message: String): Nothing {
    println("ERROR: $message")
    exitProcess(-1)
}
