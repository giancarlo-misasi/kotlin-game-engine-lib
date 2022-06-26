package dev.misasi.giancarlo.events.input.touch

import dev.misasi.giancarlo.crash

data class TouchEvent (
    val type: Type,
    val time: Long,
    val points: List<TouchPoint>
) {
    init {
        if (points.isEmpty()) {
            crash("TouchEvent must have at least one point.")
        }
    }

    val valid by lazy {
        Type.None != type
    }

    val numberOfPoints by lazy {
        points.size
    }

    val firstPoint by lazy {
        points[0]
    }

    fun getPointByIndex(index: Int) : TouchPoint? {
        return points[index]
    }

    fun getPointById(id: Int) : TouchPoint? {
        return points.find { id == it.id }
    }

    fun hasSamePoints(other: TouchEvent) : Boolean {
        if (!valid || points.size != other.points.size) {
            return false
        }

        return points.all { other.getPointById(it.id) != null }
    }

    enum class Type {
        None,
        Begin,
        Move,
        End,
        Cancel
    }
}
