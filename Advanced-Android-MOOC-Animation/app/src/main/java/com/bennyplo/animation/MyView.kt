package com.bennyplo.animation

import android.content.Context
import android.opengl.GLSurfaceView

class MyView(context: Context?) : GLSurfaceView(context) {

    private val mRenderer: MyRenderer

    init {
        setEGLContextClientVersion(2) // Create an OpenGL ES 2.0 context.
        mRenderer = MyRenderer() // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(mRenderer)
        renderMode = RENDERMODE_WHEN_DIRTY
    }
}