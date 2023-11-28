package com.bennyplo.capstone3

import android.content.Context
import android.opengl.GLSurfaceView
import java.util.Timer
import java.util.TimerTask

class MyView(context: Context?) : GLSurfaceView(context) {

    private var pXAngle = 0.0F
    private var pYAngle = 0.0F
    private var pZAngle = 0.0F

    private val _task: TimerTask by lazy {
        object : TimerTask() {
            override fun run() {
                mRenderer.setXAngle(pXAngle) // Spinning about the y-axis
                mRenderer.setYAngle(pYAngle) // Spinning about the y-axis
                requestRender()
                pYAngle += 1.0F // Rotate about the y-axis
                if (pYAngle >= 360) pYAngle = 0.0F
                pXAngle += 1.0F // Rotate about the x-axis
                if (pXAngle >= 360) pXAngle = 0.0F
                pZAngle += 1.0F // Rotate about the z-axis
                if (pZAngle >= 360) pZAngle = 0.0F
            }
        }
    }

    private val _timer: Timer by lazy {
        Timer()
    }

    private val mRenderer: MyRenderer by lazy {
        MyRenderer() // Set the Renderer for drawing on the GLSurfaceView
    }

    init {
        setEGLContextClientVersion(2) // Create an OpenGL ES 2.0 context.
        setRenderer(mRenderer)
        // Render the view only when there is a change in the drawing data
        renderMode = RENDERMODE_WHEN_DIRTY

        _timer.scheduleAtFixedRate(_task, 1000, 100)
    }

}