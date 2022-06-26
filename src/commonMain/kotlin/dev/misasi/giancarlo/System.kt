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
