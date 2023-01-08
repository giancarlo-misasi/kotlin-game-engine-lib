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

package dev.misasi.giancarlo.drawing

import dev.misasi.giancarlo.math.AffineTransform
import dev.misasi.giancarlo.math.Vector2f
import dev.misasi.giancarlo.system.System.Companion.crash

data class Font(val name: String, val size: Int, val color: Rgb8) {

    val assetName = "$name$size"

    companion object {
        val ROBOTO16 = fnt("Roboto", 16)
        val ROBOTO20 = fnt("Roboto", 20)
        val ROBOTO24 = fnt("Roboto", 24)
        val ROBOTO28 = fnt("Roboto", 28)
        val ROBOTO32 = fnt("Roboto", 32)
        val ROBOTO36 = fnt("Roboto", 36)
        val ROBOTO40 = fnt("Roboto", 40)
        val ROBOTO44 = fnt("Roboto", 44)
        val ROBOTO48 = fnt("Roboto", 48)
        val ROBOTO52 = fnt("Roboto", 52)
        val ROBOTO56 = fnt("Roboto", 56)
        val ROBOTO60 = fnt("Roboto", 60)
        val ROBOTO64 = fnt("Roboto", 64)

        private fun fnt(name: String, size: Int) = Font("$name", size, Rgb8.WHITE)
    }
}

class BitmapFont(
    val fontName: String,
    private val properties: Properties,
    private val chars: Map<Char, Character>,
    private val kernings: Map<Pair<Char, Char>, Kerning>,
) {
    // use the largest advance as space advance
    private val spaceWidth = chars.map { it.value.xAdvance }.max()
    private val tabWidth = 2 * spaceWidth

    data class Properties(
        val lineHeight: Int,
    )

    data class Character(
        val codePoint: Char,
        val size: Vector2f,
        val offset: Vector2f,
        val xAdvance: Int,
    )

    data class Kerning(
        val first: Char,
        val second: Char,
        val amount: Int,
    ) {
        val id = Pair(first, second)
    }

    fun draw(string: String, affineTransform: AffineTransform, font: Font, alpha: Int?, drawState: DrawState) {
        val (ix, _) = affineTransform.translation.toVector2i()
        var x = 0
        var y = 0
        var prev = ' '
        for (raw in string) {
            when (raw) {
                ' ' -> x += spaceWidth
                '\t' -> x += tabWidth
                '\r' -> break
                '\n' -> x = ix.also { y += properties.lineHeight }
                else -> {
                    val c = chars[raw] ?: crash("Font $fontName cannot render '$raw'.")
                    val affine = AffineTransform(
                        translation = c.offset.plus(Vector2f(x, y)),
                        scale = c.size,
                    ).concatenate(affineTransform)
                    drawState.putSprite("$fontName${c.codePoint}", affine, font.color, alpha)

                    val k = Pair(prev, raw)
                    x += c.xAdvance + (kernings[k]?.amount ?: 0)
                }
            }
            prev = raw
        }
    }
}