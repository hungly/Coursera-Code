package com.bennyplo.animation

import android.content.Context
import android.opengl.GLSurfaceView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MyView(context: Context?) : GLSurfaceView(context) {

    private val mRenderer: MyRenderer

    private var rotateJob: Job? = null
    private var mAngle: Float = 0F
    private var isRotating = true

    private fun startRotating() {
        rotateJob?.cancel()
        rotateJob = CoroutineScope(Dispatchers.Default + SupervisorJob()).launch {
            while (isRotating) {
                delay(10)
                mRenderer.setAngle(mAngle)
                requestRender()
                mAngle++
                if (mAngle >= 360) mAngle = 0F
            }
        }
    }

    init {
        setEGLContextClientVersion(2) // Create an OpenGL ES 2.0 context.
        mRenderer = MyRenderer() // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(mRenderer)
        renderMode = RENDERMODE_WHEN_DIRTY
    }

    override fun onPause() {
        super.onPause()
        isRotating = false
        rotateJob?.cancel()
    }

    override fun onResume() {
        super.onResume()
        isRotating = true
        startRotating()
    }
}