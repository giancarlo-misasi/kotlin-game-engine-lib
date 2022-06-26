import dev.misasi.giancarlo.drawing.Rgba8
import dev.misasi.giancarlo.events.SystemClock
import dev.misasi.giancarlo.events.input.gestures.detector.PanDetector
import dev.misasi.giancarlo.events.input.gestures.detector.PinchDetector
import dev.misasi.giancarlo.events.input.gestures.detector.SingleTapDetector
import dev.misasi.giancarlo.math.Vector2f
import dev.misasi.giancarlo.opengl.LwjglGlfwDisplayContext
import dev.misasi.giancarlo.opengl.LwjglOpenGl

fun main() {
    println("Hello")

    val clock = SystemClock()
    val gestureDetectors = setOf(
        PanDetector(),
        SingleTapDetector(),
        PinchDetector()
    )

    val context = LwjglGlfwDisplayContext(
        gestureDetectors,
        "title",
        Vector2f(600f, 400f),
        Vector2f(600f, 400f),
        false,
        false,
        60
    )

    val gl = LwjglOpenGl()
    gl.setClearColor(Rgba8.BLACK)
    while (!context.shouldClose()) {
        gl.clear()
        context.swapBuffers()
        context.pollEvents()
    }
}