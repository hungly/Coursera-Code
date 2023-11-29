package com.bennyplo.capstone3.model

import android.opengl.GLES32

abstract class GLObject {

    internal val colorStride by lazy {
        COLORS_PER_VERTEX * Float.SIZE_BYTES // 4 bytes per vertex
    }

    internal val program: Int by lazy {
        GLES32.glCreateProgram() // Create empty OpenGL Program
    }

    internal val vertexStride by lazy {
        COORDS_PER_VERTEX * Float.SIZE_BYTES // 4 bytes per vertex
    }

    var initialRotation = Rotation(xRotation = 0.0F, yRotation = 0.0F, zRotation = 0.0F)
    var initialScale = Scale(xScale = 1.0F, yScale = 1.0F, zScale = 1.0F)
    var initialTranslation = Translation(
        xTranslation = 0.0F,
        yTranslation = 0.0F,
        zTranslation = 0.0F
    )

    open fun draw(mvpMatrix: FloatArray?) {
        GLES32.glUseProgram(program) // Add program to OpenGL environment

        GLES32.glDisable(GLES32.GL_BLEND)
        GLES32.glDisable(GLES32.GL_CULL_FACE)
    }

    companion object {
        // Number of coordinates per vertex in this array
        internal const val COORDS_PER_VERTEX = 3
        internal const val COLORS_PER_VERTEX = 4
    }

}