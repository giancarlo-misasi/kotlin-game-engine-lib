package dev.misasi.giancarlo.math

import kotlin.math.sqrt

data class Vector3f (val x: Float = 0f, val y: Float = 0f, val z: Float = 0f) {

    constructor(v: Vector2f, z: Float) : this(v.x, v.y, z)

    val lengthSquared by lazy {
        x * x + y * y + z * z
    }

    val length by lazy {
        sqrt(lengthSquared)
    }

    val inverseLength by lazy {
        1f / length
    }

    val normal by lazy {
        Vector3f(x * inverseLength, y * inverseLength, z * inverseLength)
    }

    val negated by lazy {
        Vector3f(-x, -y, -z)
    }

    fun dot(other: Vector3f) : Float {
        return x * other.x + y * other.y + z * other.z
    }

    fun cross(other: Vector3f) : Vector3f {
        return Vector3f(
            y * other.z - z * other.y,
            z * other.x - x * other.z,
            x * other.y - y * other.x
        )
    }

    fun plus(other: Vector3f) : Vector3f {
        return Vector3f(x + other.x, y + other.y, z + other.z)
    }

    fun minus(other: Vector3f) : Vector3f {
        return Vector3f(x - other.x, y - other.y, z - other.z)
    }

    fun multiply(other: Vector3f) : Vector3f {
        return Vector3f(x * other.x, y * other.y, z * other.z);
    }

    fun divide(other: Vector3f) : Vector3f {
        return Vector3f(x / other.x, y / other.y, z / other.z);
    }

    fun scale(scale: Float) : Vector3f {
        return Vector3f(x * scale, y * scale, z * scale)
    }
}