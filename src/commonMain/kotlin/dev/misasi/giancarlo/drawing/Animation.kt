package dev.misasi.giancarlo.drawing

class Animation (
    private val materialSet: MaterialSet
) {
    private var totalElapsedMillis: Int = 0
    private var index: Int = 0

    fun currentFrame() : Material = materialSet.frames[index]

    fun update(elapsedMillis: Int) {
        totalElapsedMillis += elapsedMillis
        if (totalElapsedMillis >= materialSet.frameDurationMillis) {
            totalElapsedMillis = 0
            index = (index + 1) % materialSet.frames.size
        }
    }

    fun restart() {
        totalElapsedMillis = 0
        index = 0
    }
}
