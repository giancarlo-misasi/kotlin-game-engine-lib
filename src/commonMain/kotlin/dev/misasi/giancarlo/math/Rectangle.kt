package dev.misasi.giancarlo.math

data class Rectangle constructor(
    val size: Vector2f,
    val tl: Vector2f,
    val br: Vector2f
) {
    constructor(size: Vector2f) : this(size, Vector2f(), size)
    constructor(position: Vector2f, size: Vector2f) : this(size, position, position.plus(size))

    val tr by lazy {
        Vector2f(br.x, tl.y)
    }

    val bl by lazy {
        Vector2f(tl.x, br.y)
    }

    fun move(delta: Vector2f) : Rectangle {
        return Rectangle(size, tl.plus(delta), br.plus(delta))
    }

    fun moveTo(to: Vector2f) : Rectangle {
        return move(to.minus(tl))
    }

    fun intersects(other: Rectangle) : Boolean {
        return tl.x < other.br.x && br.x > other.tl.x && tl.y < other.br.y && br.y > other.tl.y
    }
 }