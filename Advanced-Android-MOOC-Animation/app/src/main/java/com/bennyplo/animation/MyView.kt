package com.bennyplo.animation

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent
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

    private var mPreviousX = 0F
    private var mPreviousY = 0F

    private var pX = 0F
    private var pY = 0F
    private var pZ = 0F
    private var dir = true

    private fun startRotating() {
        rotateJob?.cancel()
        rotateJob = CoroutineScope(Dispatchers.Default + SupervisorJob()).launch {
            while (isRotating) {
                delay(10)
//                mRenderer.setAngleX(mAngle)
//                requestRender()
//                mAngle++
//                if (mAngle >= 360) mAngle = 0F

                mRenderer.setLightLocation(pX, pY, pZ)
                requestRender()
                if (dir) {
                    pX += 0.1F
                    pY += 0.1F
                    if (pX >= 10) dir = false
                } else {
                    pX -= 0.1F
                    pY -= 0.1F
                    if (pX <= -10) dir = true
                }
            }
        }
    }

    init {
        setEGLContextClientVersion(2) // Create an OpenGL ES 2.0 context.
        mRenderer = MyRenderer() // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(mRenderer)
        renderMode = RENDERMODE_WHEN_DIRTY
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val x = event?.x ?: 0F
        val y = event?.y ?: 0F
        when (event?.action) {
            MotionEvent.ACTION_MOVE -> {
                var dX = x - mPreviousX
                var dY = y - mPreviousY
                if (y > (height / 2)) dX *= -1
                if (x < (width / 2)) dY *= -1
                mRenderer.setAngleY(mRenderer.getAngleY() + (dX * TOUCH_SCALE_FACTOR))
                mRenderer.setAngleX(mRenderer.getAngleX() + (dY * TOUCH_SCALE_FACTOR))
                requestRender()
            }
        }
        mPreviousX = x
        mPreviousY = y
        return true
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

    companion object {
        private const val TOUCH_SCALE_FACTOR = 180F / 320
    }
}