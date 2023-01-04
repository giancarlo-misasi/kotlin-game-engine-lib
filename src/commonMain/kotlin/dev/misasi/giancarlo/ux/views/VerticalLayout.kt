package dev.misasi.giancarlo.ux.views

import dev.misasi.giancarlo.ux.ViewGroup
import dev.misasi.giancarlo.ux.attributes.HorizontalAlignment

class VerticalLayout : ViewGroup() {
    var horizontalAlignment = HorizontalAlignment.MIDDLE

//    override fun onMeasureContentSize(context: ResourceContext): Vector2i {
//        val measurements = children.map { it.onMeasureContentSize(context) }
//        return Vector2i(measurements.maxOf { it.x }, measurements.sumOf { it.y })
//    }
}