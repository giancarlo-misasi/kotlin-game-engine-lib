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

package dev.misasi.giancarlo.utilities

import dev.misasi.giancarlo.math.Vector2i
import java.awt.Color
import java.awt.Font
import java.awt.FontMetrics
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class BitmapFontPacker {
    data class Glyph(
        val char: Char,
        val image: BufferedImage,
        var position: Vector2i,
        var size: Vector2i,
        val font: Font,
    )

    companion object {
        fun <T> useGraphics(block: (graphics: Graphics2D) -> T) =
            useGraphics(BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), block)

        fun <T> useGraphics(image: BufferedImage, block: (graphics: Graphics2D) -> T): T {
            val g = image.createGraphics()
            g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY)
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY)
            g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE)
            g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON)
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
            g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE)
            g.color = Color.WHITE
            val result = block(g)
            g.dispose()
            return result
        }

        fun getFonts(name: String, start: Int, end: Int, step: Int) =
            (start..end step step).map { Font(name, Font.PLAIN, it) }

        fun getGlyphs(text: String, font: Font, g: Graphics2D) =
            getCharacterMetrics(text, font, g).map { createImage(it.key, it.value, font) }

        fun getKernings(text: String, font: Font, g: Graphics2D): Map<Pair<Char, Char>, Int> {
            g.font = font
            return text.flatMap { left -> text.mapNotNull { right -> getKerning(g.fontMetrics, left, right) } }
                .associate { it.first to it.second }
        }

        private fun getKerning(metrics: FontMetrics, left: Char, right: Char): Pair<Pair<Char, Char>, Int>? {
            val k = metrics.stringWidth("$left$right") - metrics.stringWidth("$left") - metrics.stringWidth("$right")
            return if (k == 0) null else Pair(Pair(left, right), k)
        }

        private fun getCharacterMetrics(string: String, font: Font, g: Graphics2D): Map<Char, Vector2i> {
            g.font = font
            return string.associate { it to Vector2i(g.fontMetrics.stringWidth(it.toString()), g.fontMetrics.height) }
        }

        private fun createImage(char: Char, size: Vector2i, font: Font): Glyph {
            val image = BufferedImage(size.x, size.y, BufferedImage.TYPE_INT_ARGB)
            useGraphics(image) { g ->
                g.font = font
                g.drawString(char.toString(), 0, g.fontMetrics.ascent)
            }
            return Glyph(char, image, Vector2i(), Vector2i(image.width, image.height), font)
        }
    }
}

fun pack(g: Graphics2D) {
    val fontName = "Roboto"
    val text = " abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.,!?-+\\\\/():;%&`'*#\$=[]@^{}_~\\\"><"

    // Generate all the fonts we want to pack
    val fonts = BitmapFontPacker.getFonts(fontName, 16, 64, 4)

    // Generate glyphs for each character
    val glyphs = fonts.flatMap { BitmapFontPacker.getGlyphs(text, it, g) }.sortedByDescending { it.size.y }

    // Combine the images into a single texture
    val result = BufferedImage(2048, 2048, BufferedImage.TYPE_INT_ARGB)
    BitmapFontPacker.useGraphics(result) { resultGraphics ->
        // Simple algorithm:
        //  Sort by height, place each row until no more fit, then drop to next row
        //  Most glyphs should be similar size, so it works out to be good enough
        var x = 0
        var y = 0
        var largestThisRow = 0
        for (glyph in glyphs) {
            if (x + glyph.size.x > result.width) {
                y += largestThisRow
                x = 0
                largestThisRow = 0
            }

            if (y + glyph.size.y > result.height) throw IllegalStateException("Failed to pack, they do not fit!")

            glyph.position = Vector2i(x, y)
            resultGraphics.drawImage(glyph.image, x, y, null)
            x += glyph.size.x

            if (glyph.size.y > largestThisRow) {
                largestThisRow = glyph.size.y
            }
        }
    }

    // Write out the texture png image
    ImageIO.write(result, "png", File("$fontName.png"));

    // Generate BMFont file for each font
    for (font in fonts) {
        val content = buildString {
            // NOTE: Not using all BMFont fields so some are excluded here
            append("info\tface=${font.fontName}\tsize=${font.size}\n")
            append("common\tlineHeight=${g.fontMetrics.height}\n")

            for (c in glyphs.filter { it.font == font }) {
                val xadvance = c.size.x // todo
                // I can set  x/y offset to 0 since each glyph is already offset in the texture
                append("char\tid=${c.char.code}\tx=${c.position.x}\ty=${c.position.y}\twidth=${c.size.x}\theight=${c.size.y}\txoffset=0\tyoffset=0\txadvance=${xadvance}\n")
            }

            val kernings = BitmapFontPacker.getKernings(text, font, g)
            for (k in kernings) {
                append("kerning\tfirst=${k.key.first.code}\tsecond=${k.key.second.code}\tamount=${k.value}\n")
            }
        }
        File("$fontName${font.size}.fnt").writeText(content)
    }
}

fun main() {
    BitmapFontPacker.useGraphics { pack(it) }
}