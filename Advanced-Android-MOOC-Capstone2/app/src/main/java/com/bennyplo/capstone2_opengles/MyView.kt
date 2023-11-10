package com.bennyplo.capstone2_opengles

import android.content.Context
import android.opengl.GLSurfaceView
import java.util.Timer
import java.util.TimerTask

class MyView(context: Context?) : GLSurfaceView(context) {

    private val _renderer by lazy {
        MyRenderer() // Set the Renderer for drawing on the GLSurfaceView
    }

    private val _task by lazy {
        object : TimerTask() {
            override fun run() {
                _renderer.setXAngle(pXAngle) // spinning about the X-axis
                _renderer.setYAngle(pYAngle) // spinning about the y-axis
                _renderer.setZAngle(pZAngle) // spinning about the z-axis
                requestRender()
                /*pyangle+=1;//spining about the y-axis
                if (pyangle>=360)pyangle=0;
                pxangle++;//rotate about the x-axis
                if (pxangle>=360)pxangle=0;
                pzangle++;//rotate about the z-axis
                if (pzangle>=360)pzangle=0;*/
            }
        }
    }

    private val _timer by lazy {
        Timer()
    }

    private val pXAngle: Float = 0F
    private val pYAngle: Float = 0F
    private val pZAngle: Float = 0F

    init {
        setEGLContextClientVersion(2) // Create an OpenGL ES 2.0 context.
        setRenderer(_renderer)
        // Render the view only when there is a change in the drawing data
        renderMode = RENDERMODE_WHEN_DIRTY
        _timer.scheduleAtFixedRate(_task, 1000, 100)
    }

}