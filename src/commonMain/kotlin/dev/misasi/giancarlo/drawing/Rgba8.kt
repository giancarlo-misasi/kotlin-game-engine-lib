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

import dev.misasi.giancarlo.system.System.Companion.crash
import kotlin.random.Random

data class Rgb8(
    val r: Int = 0,
    val g: Int = 0,
    val b: Int = 0
) {
    init {
        checkRange(r)
        checkRange(g)
        checkRange(b)
    }

    val floatR by lazy {
        r.toFloat() / 255.toFloat()
    }

    val floatG by lazy {
        g.toFloat() / 255.toFloat()
    }

    val floatB by lazy {
        b.toFloat() / 255.toFloat()
    }

    fun toRgba(alpha: Int? = null) = Rgba8(r, g, b, alpha ?: 255)

    companion object {
        val MAROON = Rgb8(128, 0, 0)
        val DARK_RED = Rgb8(139, 0, 0)
        val BROWN = Rgb8(165, 42, 42)
        val FIREBRICK = Rgb8(178, 34, 34)
        val CRIMSON = Rgb8(220, 20, 60)
        val RED = Rgb8(255, 0, 0)
        val TOMATO = Rgb8(255, 99, 71)
        val CORAL = Rgb8(255, 127, 80)
        val INDIAN_RED = Rgb8(205, 92, 92)
        val LIGHT_CORAL = Rgb8(240, 128, 128)
        val DARK_SALMON = Rgb8(233, 150, 122)
        val SALMON = Rgb8(250, 128, 114)
        val LIGHT_SALMON = Rgb8(255, 160, 122)
        val ORANGE_RED = Rgb8(255, 69, 0)
        val DARK_ORANGE = Rgb8(255, 140, 0)
        val ORANGE = Rgb8(255, 165, 0)
        val GOLD = Rgb8(255, 215, 0)
        val DARK_GOLDEN_ROD = Rgb8(184, 134, 11)
        val GOLDEN_ROD = Rgb8(218, 165, 32)
        val PALE_GOLDEN_ROD = Rgb8(238, 232, 170)
        val DARK_KHAKI = Rgb8(189, 183, 107)
        val KHAKI = Rgb8(240, 230, 140)
        val OLIVE = Rgb8(128, 128, 0)
        val YELLOW = Rgb8(255, 255, 0)
        val YELLOW_GREEN = Rgb8(154, 205, 50)
        val DARK_OLIVE_GREEN = Rgb8(85, 107, 47)
        val OLIVE_DRAB = Rgb8(107, 142, 35)
        val LAWN_GREEN = Rgb8(124, 252, 0)
        val CHART_REUSE = Rgb8(127, 255, 0)
        val GREEN_YELLOW = Rgb8(173, 255, 47)
        val DARK_GREEN = Rgb8(0, 100, 0)
        val GREEN = Rgb8(0, 128, 0)
        val FOREST_GREEN = Rgb8(34, 139, 34)
        val LIME = Rgb8(0, 255, 0)
        val LIME_GREEN = Rgb8(50, 205, 50)
        val LIGHT_GREEN = Rgb8(144, 238, 144)
        val PALE_GREEN = Rgb8(152, 251, 152)
        val DARK_SEA_GREEN = Rgb8(143, 188, 143)
        val MEDIUM_SPRING_GREEN = Rgb8(0, 250, 154)
        val SPRING_GREEN = Rgb8(0, 255, 127)
        val SEA_GREEN = Rgb8(46, 139, 87)
        val MEDIUM_AQUA_MARINE = Rgb8(102, 205, 170)
        val MEDIUM_SEA_GREEN = Rgb8(60, 179, 113)
        val LIGHT_SEA_GREEN = Rgb8(32, 178, 170)
        val DARK_SLATE_GRAY = Rgb8(47, 79, 79)
        val TEAL = Rgb8(0, 128, 128)
        val DARK_CYAN = Rgb8(0, 139, 139)
        val AQUA = Rgb8(0, 255, 255)
        val CYAN = Rgb8(0, 255, 255)
        val LIGHT_CYAN = Rgb8(224, 255, 255)
        val DARK_TURQUOISE = Rgb8(0, 206, 209)
        val TURQUOISE = Rgb8(64, 224, 208)
        val MEDIUM_TURQUOISE = Rgb8(72, 209, 204)
        val PALE_TURQUOISE = Rgb8(175, 238, 238)
        val AQUA_MARINE = Rgb8(127, 255, 212)
        val POWDER_BLUE = Rgb8(176, 224, 230)
        val CADET_BLUE = Rgb8(95, 158, 160)
        val STEEL_BLUE = Rgb8(70, 130, 180)
        val CORN_FLOWER_BLUE = Rgb8(100, 149, 237)
        val DEEP_SKY_BLUE = Rgb8(0, 191, 255)
        val DODGER_BLUE = Rgb8(30, 144, 255)
        val LIGHT_BLUE = Rgb8(173, 216, 230)
        val SKY_BLUE = Rgb8(135, 206, 235)
        val LIGHT_SKY_BLUE = Rgb8(135, 206, 250)
        val MIDNIGHT_BLUE = Rgb8(25, 25, 112)
        val NAVY = Rgb8(0, 0, 128)
        val DARK_BLUE = Rgb8(0, 0, 139)
        val MEDIUM_BLUE = Rgb8(0, 0, 205)
        val BLUE = Rgb8(0, 0, 255)
        val ROYAL_BLUE = Rgb8(65, 105, 225)
        val BLUE_VIOLET = Rgb8(138, 43, 226)
        val INDIGO = Rgb8(75, 0, 130)
        val DARK_SLATE_BLUE = Rgb8(72, 61, 139)
        val SLATE_BLUE = Rgb8(106, 90, 205)
        val MEDIUM_SLATE_BLUE = Rgb8(123, 104, 238)
        val MEDIUM_PURPLE = Rgb8(147, 112, 219)
        val DARK_MAGENTA = Rgb8(139, 0, 139)
        val DARK_VIOLET = Rgb8(148, 0, 211)
        val DARK_ORCHID = Rgb8(153, 50, 204)
        val MEDIUM_ORCHID = Rgb8(186, 85, 211)
        val PURPLE = Rgb8(128, 0, 128)
        val THISTLE = Rgb8(216, 191, 216)
        val PLUM = Rgb8(221, 160, 221)
        val VIOLET = Rgb8(238, 130, 238)
        val MAGENTA_FUCHSIA = Rgb8(255, 0, 255)
        val ORCHID = Rgb8(218, 112, 214)
        val MEDIUM_VIOLET_RED = Rgb8(199, 21, 133)
        val PALE_VIOLET_RED = Rgb8(219, 112, 147)
        val DEEP_PINK = Rgb8(255, 20, 147)
        val HOT_PINK = Rgb8(255, 105, 180)
        val LIGHT_PINK = Rgb8(255, 182, 193)
        val PINK = Rgb8(255, 192, 203)
        val ANTIQUE_WHITE = Rgb8(250, 235, 215)
        val BEIGE = Rgb8(245, 245, 220)
        val BISQUE = Rgb8(255, 228, 196)
        val BLANCHED_ALMOND = Rgb8(255, 235, 205)
        val WHEAT = Rgb8(245, 222, 179)
        val CORN_SILK = Rgb8(255, 248, 220)
        val LEMON_CHIFFON = Rgb8(255, 250, 205)
        val LIGHT_GOLDEN_ROD_YELLOW = Rgb8(250, 250, 210)
        val LIGHT_YELLOW = Rgb8(255, 255, 224)
        val SADDLE_BROWN = Rgb8(139, 69, 19)
        val SIENNA = Rgb8(160, 82, 45)
        val CHOCOLATE = Rgb8(210, 105, 30)
        val PERU = Rgb8(205, 133, 63)
        val SANDY_BROWN = Rgb8(244, 164, 96)
        val BURLY_WOOD = Rgb8(222, 184, 135)
        val TAN = Rgb8(210, 180, 140)
        val ROSY_BROWN = Rgb8(188, 143, 143)
        val MOCCASIN = Rgb8(255, 228, 181)
        val NAVAJO_WHITE = Rgb8(255, 222, 173)
        val PEACH_PUFF = Rgb8(255, 218, 185)
        val MISTY_ROSE = Rgb8(255, 228, 225)
        val LAVENDER_BLUSH = Rgb8(255, 240, 245)
        val LINEN = Rgb8(250, 240, 230)
        val OLD_LACE = Rgb8(253, 245, 230)
        val PAPAYA_WHIP = Rgb8(255, 239, 213)
        val SEA_SHELL = Rgb8(255, 245, 238)
        val MINT_CREAM = Rgb8(245, 255, 250)
        val SLATE_GRAY = Rgb8(112, 128, 144)
        val LIGHT_SLATE_GRAY = Rgb8(119, 136, 153)
        val LIGHT_STEEL_BLUE = Rgb8(176, 196, 222)
        val LAVENDER = Rgb8(230, 230, 250)
        val FLORAL_WHITE = Rgb8(255, 250, 240)
        val ALICE_BLUE = Rgb8(240, 248, 255)
        val GHOST_WHITE = Rgb8(248, 248, 255)
        val HONEYDEW = Rgb8(240, 255, 240)
        val IVORY = Rgb8(255, 255, 240)
        val AZURE = Rgb8(240, 255, 255)
        val SNOW = Rgb8(255, 250, 250)
        val BLACK = Rgb8(0, 0, 0)
        val DIM_GRAY = Rgb8(105, 105, 105)
        val GRAY = Rgb8(128, 128, 128)
        val DARK_GRAY = Rgb8(169, 169, 169)
        val SILVER = Rgb8(192, 192, 192)
        val LIGHT_GRAY = Rgb8(211, 211, 211)
        val GAINSBORO = Rgb8(220, 220, 220)
        val WHITE_SMOKE = Rgb8(245, 245, 245)
        val WHITE = Rgb8(255, 255, 255)

        fun random(): Rgb8 {
            val r = Random(randomSeed++)
            return Rgb8(
                r.nextInt(256),
                r.nextInt(256),
                r.nextInt(256),
            )
        }

        private var randomSeed = 0
    }
}

data class Rgba8 (
    val r: Int = 0,
    val g: Int = 0,
    val b: Int = 0,
    val a: Int = 255
) {
    constructor(color: Rgb8?, alpha: Int?): this(
        color?.r ?: 0,
        color?.g ?: 0,
        color?.b ?: 0,
        alpha ?: 255
    )

    enum class Format {
        RGBA,
        BGRA
    }

    init {
        checkRange(r)
        checkRange(g)
        checkRange(b)
        checkRange(a)
    }

    val packedRgba by lazy {
        (r shl 24) or (g shl 16) or (b shl 8) or a
//        (a shl 24) or (b shl 16) or (g shl 8) or r
    }

    val floatR by lazy {
        r.toFloat() / 255.toFloat()
    }

    val floatG by lazy {
        g.toFloat() / 255.toFloat()
    }

    val floatB by lazy {
        b.toFloat() / 255.toFloat()
    }

    val floatA by lazy {
        a.toFloat() / 255.toFloat()
    }

    companion object {
        fun random() = Rgb8.random().toRgba()
    }
}

private fun checkRange(value: Int) {
    if (value !in 0..255) crash("Value $value out of acceptable range.")
}