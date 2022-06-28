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

data class DrawOrder (
    val count: Int = 1,
    val type: Type = Type.SQUARE,
    val textureHandle: Int?,
    val color: Rgba8?
) {
    constructor(textureHandle: Int) : this(1, Type.SQUARE, textureHandle, null)
    constructor(color: Rgba8) : this(1, Type.SQUARE, null, color)
    constructor(type: Type, color: Rgba8) : this(1, type, null, color)

    enum class Type {
        LINE,
        SQUARE
    }

    val numberOfVertex by lazy {
        when (type) {
            Type.SQUARE -> 3 * 2 * count
            Type.LINE -> 2 * count
        }
    }
}