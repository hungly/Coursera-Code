package com.bennyplo.capstone3

import android.annotation.SuppressLint
import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import java.util.Timer
import java.util.TimerTask

class MyView(context: Context?) : GLSurfaceView(context) {

    private var mIsRotating = true

    private var mPXAngle = 0.0F
    private var mPYAngle = 0.0F
    private var mPZAngle = 0.0F
    private var mPreviousX = 0.0F
    private var mPreviousY = 0.0F
    private var mOriginalX: Float? = null
    private var mOriginalY: Float? = null
    private var mOriginalZ: Float? = null

    private val _renderer: MyRenderer by lazy {
        MyRenderer(context) // Set the Renderer for drawing on the GLSurfaceView
    }

    private val _task: TimerTask by lazy {
        object : TimerTask() {
            override fun run() {
                if (mIsRotating) {
                    _renderer.setXAngle(mPXAngle) // Spinning about the y-axis
                    _renderer.setYAngle(mPYAngle) // Spinning about the y-axis
                    requestRender()
                    mPYAngle += 1.0F // Rotate about the y-axis
                    if (mPYAngle >= 360) mPYAngle = 0.0F
                    mPXAngle += 1.0F // Rotate about the x-axis
                    if (mPXAngle >= 360) mPXAngle = 0.0F
                    mPZAngle += 1.0F // Rotate about the z-axis
                    if (mPZAngle >= 360) mPZAngle = 0.0F
                }
            }
        }
    }

    private val _timer: Timer by lazy {
        Timer()
    }

    init {
        setEGLContextClientVersion(2) // Create an OpenGL ES 2.0 context.
        setRenderer(_renderer)
        // Render the view only when there is a change in the drawing data
        renderMode = RENDERMODE_WHEN_DIRTY
    }

    override fun onPause() {
        super.onPause()

        mIsRotating = false
        _timer.cancel()
    }

    override fun onResume() {
        super.onResume()

        mIsRotating = true
        startRotating()
    }

    private fun startRotating() {
        _timer.scheduleAtFixedRate(_task, 1000, 25)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val x = event?.x ?: 0F
        val y = event?.y ?: 0F
        when (event?.action) {
            MotionEvent.ACTION_MOVE -> {
                val dX = x - mPreviousX
                val dY = y - mPreviousY
//                if (y > (height / 2)) dX *= -1
//                if (x < (width / 2)) dY *= -1
                _renderer.setYAngle(_renderer.getYAngle() + (dX * TOUCH_SCALE_FACTOR))
                _renderer.setXAngle(_renderer.getXAngle() + (dY * TOUCH_SCALE_FACTOR))
                requestRender()
            }
        }
        mPreviousX = x
        mPreviousY = y
        return true
    }

    fun sensorRotates(pitch: Double, yaw: Double, roll: Double) {
        mOriginalX ?: kotlin.run { mOriginalX = pitch.toFloat() }
        mOriginalY ?: kotlin.run { mOriginalY = yaw.toFloat() }
        mOriginalZ ?: kotlin.run { mOriginalZ = roll.toFloat() }

        _renderer.setXAngle(pitch.toFloat() - (mOriginalX ?: 0.0F)) // Spinning about the x-axis
        _renderer.setYAngle(yaw.toFloat() - (mOriginalY ?: 0.0F)) // Spinning about the y-axis
        _renderer.setZAngle(roll.toFloat() - (mOriginalZ ?: 0.0F)) // Spinning about the z-axis
        requestRender()
    }

    companion object {
        private const val TOUCH_SCALE_FACTOR = 180F / 320
    }

}