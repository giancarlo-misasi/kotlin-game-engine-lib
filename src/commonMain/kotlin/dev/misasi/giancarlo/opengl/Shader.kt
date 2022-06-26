package dev.misasi.giancarlo.opengl

class Shader(val type: Type, val source: String) {
    enum class Type {
        VERTEX,
        FRAGMENT
    }
}