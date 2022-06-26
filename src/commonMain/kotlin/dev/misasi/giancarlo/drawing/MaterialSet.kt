package dev.misasi.giancarlo.drawing

import dev.misasi.giancarlo.crash

data class MaterialSet (
    val name: String,
    val frames: List<Material>,
    val frameDurationMillis: Int
) {
    init {
        if (frames.size < 2) {
            crash("MaterialSet requires at least 2 materials.")
        }
    }
}
