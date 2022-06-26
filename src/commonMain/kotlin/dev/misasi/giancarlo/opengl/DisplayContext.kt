package dev.misasi.giancarlo.opengl

import dev.misasi.giancarlo.events.Event
import dev.misasi.giancarlo.math.Vector2f

interface DisplayContext {

    var title: String
    var targetResolution: Vector2f
    var windowSize: Vector2f
    var fullScreen: Boolean
    var vsync: Boolean
    var refreshRate: Int

    fun reconfigure()
    fun swapBuffers()
    fun pollEvents()
    fun getNextEvent() : Event?
    fun close()
    fun shouldClose(): Boolean
    fun getPrimaryMonitorResolution(): Vector2f
    fun getActualWindowSize(): Vector2f
}