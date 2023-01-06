/*
 * MIT License
 *
 * Copyright (c) 2022 Giancarlo Misasi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package dev.misasi.giancarlo.ux

import dev.misasi.giancarlo.assets.Assets
import dev.misasi.giancarlo.drawing.Alpha
import dev.misasi.giancarlo.drawing.DrawCommand
import dev.misasi.giancarlo.drawing.DrawOptions
import dev.misasi.giancarlo.drawing.Effect
import dev.misasi.giancarlo.drawing.Rgba8
import dev.misasi.giancarlo.drawing.StaticMaterial
import dev.misasi.giancarlo.events.input.mouse.CursorMode
import dev.misasi.giancarlo.math.Aabb
import dev.misasi.giancarlo.math.AffineTransform
import dev.misasi.giancarlo.math.Matrix4f
import dev.misasi.giancarlo.math.Reflection
import dev.misasi.giancarlo.math.Vector2f
import dev.misasi.giancarlo.math.Vector2i
import dev.misasi.giancarlo.openal.OpenAl
import dev.misasi.giancarlo.opengl.Attribute
import dev.misasi.giancarlo.opengl.Attribute.Companion.sizeInBytes
import dev.misasi.giancarlo.opengl.Buffer
import dev.misasi.giancarlo.opengl.DrawBuffer
import dev.misasi.giancarlo.opengl.FrameBuffer
import dev.misasi.giancarlo.opengl.OpenGl
import dev.misasi.giancarlo.opengl.Program
import dev.misasi.giancarlo.opengl.Texture
import dev.misasi.giancarlo.opengl.Viewport
import dev.misasi.giancarlo.opengl.Window
import dev.misasi.giancarlo.system.Clock
import dev.misasi.giancarlo.system.DataType
import dev.misasi.giancarlo.ux.transitions.Transition
import dev.misasi.giancarlo.ux.transitions.ViewOrchestrator

class App(
    title: String,
    size: Vector2i,
    fullScreen: Boolean,
    refreshRate: Int,
    vsync: Boolean,
    designedResolution: Vector2i,
) : AppContext {
    override val gl = OpenGl.gl
    override val al = OpenAl.al
    override val clock = Clock()
    override val assets = Assets(gl, al)

    private val window = Window(gl, title, size, fullScreen, refreshRate, vsync, designedResolution)

    private val program = createProgram(this)
    private val drawBuffer = DrawBuffer(gl, program, attributeSpecs, Buffer.Usage.STREAM, spriteCapacity)
    private val drawState = AppDrawState(assets, spriteSizeInBytes, spriteCapacity)

    private val postProcessProgram = createProgram(this)
    private val postProcessFrameBuffer = FrameBuffer(gl)
    private val postProcessTexture = Texture(gl, "post", designedResolution, postProcessFrameBuffer)
    private val postProcessMaterial = StaticMaterial("post", "post", postProcessUv, designedResolution)
    private val postProcessDrawBuffer = DrawBuffer(gl, postProcessProgram, attributeSpecs, Buffer.Usage.STATIC, 1)
    private val postProcessDrawState = AppDrawState(assets, spriteSizeInBytes, 1)

    private val viewOrchestrator = ViewOrchestrator()

    init {
        assets.put(postProcessTexture)
        assets.put(postProcessMaterial)

        program.bind()
        drawState.updateIndexes(drawBuffer)

        postProcessProgram.bind()
        postProcessDrawState.updateIndexes(postProcessDrawBuffer)
        updatePostProcessing()

        gl.setClearColor(Rgba8.BLACK)
    }

    override val viewport: Viewport get() = window.viewport

    override fun go(nextRootView: View, outTransition: Transition, inTransition: Transition) {
        viewOrchestrator.go(nextRootView, outTransition, inTransition)
    }

    override fun setFullScreen(size: Vector2i, refreshRate: Int) {
        window.setFullScreen(size, refreshRate)
        updatePostProcessing()
    }

    override fun setWindowed(size: Vector2i) {
        window.setWindowed(size)
        updatePostProcessing()
    }

    override fun setVsync(vsync: Boolean) = window.setVsync(vsync)
    override fun setCursorMode(mode: CursorMode) = window.setCursorMode(mode)
    override fun enableResizeEvents(enable: Boolean) = window.enableResizeEvents(enable)
    override fun enableKeyboardEvents(enable: Boolean) = window.enableKeyboardEvents(enable)
    override fun enableTextEvents(enable: Boolean) = window.enableTextEvents(enable)
    override fun enableMouseEvents(enable: Boolean) = window.enableMouseEvents(enable)
    override fun enableMouseButtonEvents(enable: Boolean) = window.enableMouseButtonEvents(enable)
    override fun enableScrollEvents(enable: Boolean) = window.enableScrollEvents(enable)
    override fun close() = window.close()

    fun run(appRootView: View) {
        go(appRootView)
        while (!window.shouldClose()) {
            onElapsed(clock.elapsedSinceUpdateMs(1000))
            onUpdateDrawState()
            onDraw()
            onProcessEvents()
        }
    }

    private fun onElapsed(elapsedMs: Long) {
        clock.update()
        viewOrchestrator.onElapsed(elapsedMs)
    }

    private fun onUpdateDrawState() {
        drawState.reset()

        val transitions = viewOrchestrator.activeTransitions
        if (transitions.isNotEmpty()) {
            transitions.filter { it.value.active }.forEach { (view, transition) ->
                view.onSize(this, viewport.designedResolution) // todo: track to avoid recalculation every loop
                drawState.applyOptionsScoped(calculateDrawOptions(transition)) {
                    view.onUpdateDrawState(this, drawState)
                }
            }
        } else {
            viewOrchestrator.rootView?.let { view ->
                view.onSize(this, viewport.designedResolution) // todo: track to avoid recalculation every loop
                drawState.applyOptionsScoped(DrawOptions(viewport.bounds)) {
                    view.onUpdateDrawState(this, drawState)
                }
            }
        }

        drawState.updateVertexes(drawBuffer)
    }

    private fun onDraw() {
        postProcessFrameBuffer.bind()

        // Draw using designed resolution and then scale with post-processing to fit screen size
        gl.setViewport(viewport.designedResolution)
        gl.clear()

        program.bind()
        var offset = 0
        for (command in drawState.getCommands()) {
            // Update uniform values
            updateUniforms(command)

            // Get the texture (load if not already loaded)
            val texture = assets.texture(command.textureName)

            // Issue draw commands
            drawBuffer.applyScissorScoped(viewport, command.options.scissor) {
                offset = drawBuffer.draw(texture, offset, command.count)
            }
        }

        FrameBuffer.unbind(gl)

        // First clear off the screen
        gl.setViewport(viewport.actualScreenSize)
        gl.clear()

        // Now draw the post-processed texture
        postProcessProgram.bind()
        postProcessDrawBuffer.draw(postProcessTexture, 0, 1)

        // Finally, let's swap the buffers and see the fruit of our labours
        window.swapBuffers()
    }

    private fun onProcessEvents() {
        gl.pollEvents()
        while (true) {
            val event = window.getNextEvent() ?: break
            if (viewOrchestrator.isEmpty) {
                viewOrchestrator.rootView?.onEvent(this, event)
            }
        }
    }

    private fun updateUniforms(command: DrawCommand) {
        // TODO: Compare current / previous to avoid unnecessary calculations
        val affine = command.options.affine ?: AffineTransform()
        val mvp = calculateModelViewProjection(viewport.designedResolution, affine)
        program.setUniformMatrix4f(Uniform.MODEL_VIEW_PROJECTION.id, mvp)
        program.setUniformVector2f(Uniform.TRANSLATION.id, affine.translation)
        program.setUniformFloat(Uniform.ALPHA.id, Alpha.normalize(command.options.alpha))
        program.setUniformBoolean(Uniform.SEPIA.id, command.options.effect == Effect.SEPIA)
        program.setUniformBoolean(Uniform.RETRO.id, command.options.effect == Effect.RETRO)
        program.setUniformBoolean(Uniform.INVERT.id, command.options.effect == Effect.INVERT)
    }

    private fun updatePostProcessing() {
        // TODO: Compare current / previous to avoid unnecessary calculations
        postProcessProgram.bind()
        val mvp = calculateModelViewProjection(viewport.adjustedScreenSize, AffineTransform())
        postProcessProgram.setUniformMatrix4f(Uniform.MODEL_VIEW_PROJECTION.id, mvp)
        postProcessDrawState.reset()
        postProcessDrawState.putSprite(postProcessMaterial.materialName, AffineTransform(
            scale = viewport.adjustedScreenSize.toVector2f(),
            reflection = Reflection.VERTICAL,
            translation = viewport.offset,
        ))
        postProcessDrawState.updateVertexes(postProcessDrawBuffer)
    }

    private fun calculateDrawOptions(transition: Transition) = DrawOptions(
        affine = transition.currentAffine,
        alpha = transition.currentAlpha,
    )

    private fun calculateModelViewProjection(size: Vector2i, affine: AffineTransform): Matrix4f {
        return Matrix4f.ortho(size.toVector2f())
            .multiply(Matrix4f.lookAt(Vector2f()))
            .multiply(Matrix4f.linearTransform(affine))
    }

    private enum class Uniform(val id: String) {
        MODEL_VIEW_PROJECTION("uModelViewProjection"),
        TRANSLATION("uTranslation"),
        ALPHA("uAlpha"),
        SEPIA("uSepia"),
        RETRO("uRetro"),
        INVERT("uInvert"),
    }

    companion object {
        private val attributeSpecs: List<Attribute.Spec> = listOf(
            Attribute.Spec("inXy", DataType.FLOAT, 2),
            Attribute.Spec("inUv", DataType.UNSIGNED_SHORT, 2, normalize = true),
            Attribute.Spec("inAlpha", DataType.FLOAT, 1)
        )
        private val spriteSizeInBytes = 4 * attributeSpecs.sizeInBytes()
        private const val spriteCapacity = 10000
        private val postProcessUv = Aabb(
            Vector2f(0, 0),
            Vector2f(1, 0),
            Vector2f(1, 1),
            Vector2f(0, 1)
        )

        private fun createProgram(context: AppContext): Program {
            return Program(context.gl, listOf(
                context.assets.shader("SpriteVertex"),
                context.assets.shader("SpriteFragment"),
            ), Uniform.values().map { it.id })
        }
    }
}