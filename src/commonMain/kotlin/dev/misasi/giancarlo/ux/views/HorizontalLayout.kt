package dev.misasi.giancarlo.ux.views

import dev.misasi.giancarlo.ux.ViewGroup
import dev.misasi.giancarlo.ux.attributes.VerticalAlignment

class HorizontalLayout : ViewGroup() {
    var verticalAlignment = VerticalAlignment.MIDDLE

//    override fun onMeasureContentSize(context: ResourceContext): Vector2i {
//        val measurements = children.map { it.onMeasureContentSize(context) }
//        return Vector2i(measurements.sumOf { it.x }, measurements.maxOf { it.y })
//    }
}