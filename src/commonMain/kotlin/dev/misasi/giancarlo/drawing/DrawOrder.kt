package dev.misasi.giancarlo.drawing

data class DrawOrder (
    val count: Int = 1,
    val textureHandle: Int?,
    val color: Rgba8?
) {
    constructor(textureHandle: Int) : this(1, textureHandle, null)
    constructor(color: Rgba8) : this(1, null, color)

    val numberOfTriangles by lazy {
        2 * count
    }
}
