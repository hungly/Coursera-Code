package com.bennyplo.capstone2_opengles

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import java.util.Timer
import java.util.TimerTask

class MyView(context: Context?) : GLSurfaceView(context) {

    private val _renderer by lazy {
        MyRenderer() // Set the Renderer for drawing on the GLSurfaceView
    }

    private val _task by lazy {
        object : TimerTask() {
            override fun run() {
//                _renderer.setAngleX(pXAngle) // spinning about the X-axis
//                _renderer.setAngleY(pYAngle) // spinning about the y-axis
//                _renderer.setAngleZ(pZAngle) // spinning about the z-axis
//                requestRender()
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

    private var mPreviousX = 0F
    private var mPreviousY = 0F

    init {
        setEGLContextClientVersion(2) // Create an OpenGL ES 2.0 context.
        setRenderer(_renderer)
        // Render the view only when there is a change in the drawing data
        renderMode = RENDERMODE_WHEN_DIRTY
        _timer.scheduleAtFixedRate(_task, 1000, 100)
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
                _renderer.setAngleY(_renderer.getAngleY() + (dX * TOUCH_SCALE_FACTOR))
                _renderer.setAngleX(_renderer.getAngleX() + (dY * TOUCH_SCALE_FACTOR))
                requestRender()
            }
        }
        mPreviousX = x
        mPreviousY = y
        return true
    }

    companion object {
        private const val TOUCH_SCALE_FACTOR = 180F / 320
    }

}