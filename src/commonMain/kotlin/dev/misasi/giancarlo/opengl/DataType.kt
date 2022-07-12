package dev.misasi.giancarlo.opengl

enum class DataType(val size: Int) {
    FLOAT(4),
    INT(4),
    UNSIGNED_INT(4),
    SHORT(2),
    UNSIGNED_SHORT(2),
    BYTE(1),
    UNSIGNED_BYTE(1)
}