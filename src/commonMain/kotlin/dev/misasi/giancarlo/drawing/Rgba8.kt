package dev.misasi.giancarlo.drawing

import dev.misasi.giancarlo.crash

data class Rgba8 (
    val r: Int = 0,
    val g: Int = 0,
    val b: Int = 0,
    val a: Int = 255
) {
    init {
        checkRange(r)
        checkRange(g)
        checkRange(b)
        checkRange(a)
    }

    val floatR by lazy {
        r.toFloat() / max.toFloat()
    }

    val floatG by lazy {
        g.toFloat() / max.toFloat()
    }

    val floatB by lazy {
        b.toFloat() / max.toFloat()
    }

    val floatA by lazy {
        a.toFloat() / max.toFloat()
    }

    companion object {
        private const val min = 0
        private const val max = 255

        private fun checkRange(value: Int) {
            if (value !in min..max) {
                crash("Value $value out of acceptable range.")
            }
        }

        val MAROON = Rgba8(128, 0, 0)
        val DARK_RED = Rgba8(139, 0, 0)
        val BROWN = Rgba8(165, 42, 42)
        val FIREBRICK = Rgba8(178, 34, 34)
        val CRIMSON = Rgba8(220, 20, 60)
        val RED = Rgba8(255, 0, 0)
        val TOMATO = Rgba8(255, 99, 71)
        val CORAL = Rgba8(255, 127, 80)
        val INDIAN_RED = Rgba8(205, 92, 92)
        val LIGHT_CORAL = Rgba8(240, 128, 128)
        val DARK_SALMON = Rgba8(233, 150, 122)
        val SALMON = Rgba8(250, 128, 114)
        val LIGHT_SALMON = Rgba8(255, 160, 122)
        val ORANGE_RED = Rgba8(255, 69, 0)
        val DARK_ORANGE = Rgba8(255, 140, 0)
        val ORANGE = Rgba8(255, 165, 0)
        val GOLD = Rgba8(255, 215, 0)
        val DARK_GOLDEN_ROD = Rgba8(184, 134, 11)
        val GOLDEN_ROD = Rgba8(218, 165, 32)
        val PALE_GOLDEN_ROD = Rgba8(238, 232, 170)
        val DARK_KHAKI = Rgba8(189, 183, 107)
        val KHAKI = Rgba8(240, 230, 140)
        val OLIVE = Rgba8(128, 128, 0)
        val YELLOW = Rgba8(255, 255, 0)
        val YELLOW_GREEN = Rgba8(154, 205, 50)
        val DARK_OLIVE_GREEN = Rgba8(85, 107, 47)
        val OLIVE_DRAB = Rgba8(107, 142, 35)
        val LAWN_GREEN = Rgba8(124, 252, 0)
        val CHART_REUSE = Rgba8(127, 255, 0)
        val GREEN_YELLOW = Rgba8(173, 255, 47)
        val DARK_GREEN = Rgba8(0, 100, 0)
        val GREEN = Rgba8(0, 128, 0)
        val FOREST_GREEN = Rgba8(34, 139, 34)
        val LIME = Rgba8(0, 255, 0)
        val LIME_GREEN = Rgba8(50, 205, 50)
        val LIGHT_GREEN = Rgba8(144, 238, 144)
        val PALE_GREEN = Rgba8(152, 251, 152)
        val DARK_SEA_GREEN = Rgba8(143, 188, 143)
        val MEDIUM_SPRING_GREEN = Rgba8(0, 250, 154)
        val SPRING_GREEN = Rgba8(0, 255, 127)
        val SEA_GREEN = Rgba8(46, 139, 87)
        val MEDIUM_AQUA_MARINE = Rgba8(102, 205, 170)
        val MEDIUM_SEA_GREEN = Rgba8(60, 179, 113)
        val LIGHT_SEA_GREEN = Rgba8(32, 178, 170)
        val DARK_SLATE_GRAY = Rgba8(47, 79, 79)
        val TEAL = Rgba8(0, 128, 128)
        val DARK_CYAN = Rgba8(0, 139, 139)
        val AQUA = Rgba8(0, 255, 255)
        val CYAN = Rgba8(0, 255, 255)
        val LIGHT_CYAN = Rgba8(224, 255, 255)
        val DARK_TURQUOISE = Rgba8(0, 206, 209)
        val TURQUOISE = Rgba8(64, 224, 208)
        val MEDIUM_TURQUOISE = Rgba8(72, 209, 204)
        val PALE_TURQUOISE = Rgba8(175, 238, 238)
        val AQUA_MARINE = Rgba8(127, 255, 212)
        val POWDER_BLUE = Rgba8(176, 224, 230)
        val CADET_BLUE = Rgba8(95, 158, 160)
        val STEEL_BLUE = Rgba8(70, 130, 180)
        val CORN_FLOWER_BLUE = Rgba8(100, 149, 237)
        val DEEP_SKY_BLUE = Rgba8(0, 191, 255)
        val DODGER_BLUE = Rgba8(30, 144, 255)
        val LIGHT_BLUE = Rgba8(173, 216, 230)
        val SKY_BLUE = Rgba8(135, 206, 235)
        val LIGHT_SKY_BLUE = Rgba8(135, 206, 250)
        val MIDNIGHT_BLUE = Rgba8(25, 25, 112)
        val NAVY = Rgba8(0, 0, 128)
        val DARK_BLUE = Rgba8(0, 0, 139)
        val MEDIUM_BLUE = Rgba8(0, 0, 205)
        val BLUE = Rgba8(0, 0, 255)
        val ROYAL_BLUE = Rgba8(65, 105, 225)
        val BLUE_VIOLET = Rgba8(138, 43, 226)
        val INDIGO = Rgba8(75, 0, 130)
        val DARK_SLATE_BLUE = Rgba8(72, 61, 139)
        val SLATE_BLUE = Rgba8(106, 90, 205)
        val MEDIUM_SLATE_BLUE = Rgba8(123, 104, 238)
        val MEDIUM_PURPLE = Rgba8(147, 112, 219)
        val DARK_MAGENTA = Rgba8(139, 0, 139)
        val DARK_VIOLET = Rgba8(148, 0, 211)
        val DARK_ORCHID = Rgba8(153, 50, 204)
        val MEDIUM_ORCHID = Rgba8(186, 85, 211)
        val PURPLE = Rgba8(128, 0, 128)
        val THISTLE = Rgba8(216, 191, 216)
        val PLUM = Rgba8(221, 160, 221)
        val VIOLET = Rgba8(238, 130, 238)
        val MAGENTA_FUCHSIA = Rgba8(255, 0, 255)
        val ORCHID = Rgba8(218, 112, 214)
        val MEDIUM_VIOLET_RED = Rgba8(199, 21, 133)
        val PALE_VIOLET_RED = Rgba8(219, 112, 147)
        val DEEP_PINK = Rgba8(255, 20, 147)
        val HOT_PINK = Rgba8(255, 105, 180)
        val LIGHT_PINK = Rgba8(255, 182, 193)
        val PINK = Rgba8(255, 192, 203)
        val ANTIQUE_WHITE = Rgba8(250, 235, 215)
        val BEIGE = Rgba8(245, 245, 220)
        val BISQUE = Rgba8(255, 228, 196)
        val BLANCHED_ALMOND = Rgba8(255, 235, 205)
        val WHEAT = Rgba8(245, 222, 179)
        val CORN_SILK = Rgba8(255, 248, 220)
        val LEMON_CHIFFON = Rgba8(255, 250, 205)
        val LIGHT_GOLDEN_ROD_YELLOW = Rgba8(250, 250, 210)
        val LIGHT_YELLOW = Rgba8(255, 255, 224)
        val SADDLE_BROWN = Rgba8(139, 69, 19)
        val SIENNA = Rgba8(160, 82, 45)
        val CHOCOLATE = Rgba8(210, 105, 30)
        val PERU = Rgba8(205, 133, 63)
        val SANDY_BROWN = Rgba8(244, 164, 96)
        val BURLY_WOOD = Rgba8(222, 184, 135)
        val TAN = Rgba8(210, 180, 140)
        val ROSY_BROWN = Rgba8(188, 143, 143)
        val MOCCASIN = Rgba8(255, 228, 181)
        val NAVAJO_WHITE = Rgba8(255, 222, 173)
        val PEACH_PUFF = Rgba8(255, 218, 185)
        val MISTY_ROSE = Rgba8(255, 228, 225)
        val LAVENDER_BLUSH = Rgba8(255, 240, 245)
        val LINEN = Rgba8(250, 240, 230)
        val OLD_LACE = Rgba8(253, 245, 230)
        val PAPAYA_WHIP = Rgba8(255, 239, 213)
        val SEA_SHELL = Rgba8(255, 245, 238)
        val MINT_CREAM = Rgba8(245, 255, 250)
        val SLATE_GRAY = Rgba8(112, 128, 144)
        val LIGHT_SLATE_GRAY = Rgba8(119, 136, 153)
        val LIGHT_STEEL_BLUE = Rgba8(176, 196, 222)
        val LAVENDER = Rgba8(230, 230, 250)
        val FLORAL_WHITE = Rgba8(255, 250, 240)
        val ALICE_BLUE = Rgba8(240, 248, 255)
        val GHOST_WHITE = Rgba8(248, 248, 255)
        val HONEYDEW = Rgba8(240, 255, 240)
        val IVORY = Rgba8(255, 255, 240)
        val AZURE = Rgba8(240, 255, 255)
        val SNOW = Rgba8(255, 250, 250)
        val BLACK = Rgba8(0, 0, 0)
        val DIM_GRAY = Rgba8(105, 105, 105)
        val GRAY = Rgba8(128, 128, 128)
        val DARK_GRAY = Rgba8(169, 169, 169)
        val SILVER = Rgba8(192, 192, 192)
        val LIGHT_GRAY = Rgba8(211, 211, 211)
        val GAINSBORO = Rgba8(220, 220, 220)
        val WHITE_SMOKE = Rgba8(245, 245, 245)
        val WHITE = Rgba8(255, 255, 255)
    }
}