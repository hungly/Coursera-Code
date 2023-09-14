package com.bennyplo.virtualreality

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import android.view.ViewConfiguration
import com.bennyplo.virtualreality.ref.MyView
import kotlin.math.abs
import kotlin.math.sqrt

class MyView(context: Context) : GLSurfaceView(context) {

    private var m1TouchEventX = 0f
    private var m1TouchEventY = 0f //1st finger touch location
    private var m2TouchEventX = 0f
    private var m2TouchEventY = 0f //2nd finger touch location
    private var mPreviousX = 0f //previous touch x position
    private var mPreviousY = 0f //previous touch y position
    private var mTouchDistance = 0f //distance between the 2 finger touches
    private var ptcount = 0 //touch counter

    private val renderer: MyRenderer by lazy {
        MyRenderer(context) // Set the Renderer for drawing on the GLSurfaceView
    }

    private val mViewScaledTouchSlop //number of pixels that a finger is allowed to move
            : Float

    init {
        setEGLContextClientVersion(2) // Create an OpenGL ES 2.0 context.
        setRenderer(renderer)
        val viewConfig = ViewConfiguration.get(context) //get the view configuration
        //number of pixels that a finger is allowed to move
        mViewScaledTouchSlop = viewConfig.scaledTouchSlop.toFloat()
        // Render the view only when there is a change in the drawing data
        renderMode = RENDERMODE_WHEN_DIRTY
    }

    override fun onTouchEvent(e: MotionEvent): Boolean { //touch event
        val x = e.x //x position of the touch
        val y = e.y //y position of the touch
        when (e.action) {
            MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_UP -> {
                ptcount-- //decrement the counter
                if (ptcount < -2) //if it is less than -2 -> reset the 2nd touch event positions
                {
                    m2TouchEventX = -1f
                    m2TouchEventY = -1f
                }
                if (ptcount < -1) //if it is less than -1 -> reset the 1st touch event positions
                {
                    m1TouchEventX = -1f
                    m1TouchEventY = -1f
                }
            }

            MotionEvent.ACTION_POINTER_DOWN, MotionEvent.ACTION_DOWN -> {
                ptcount++
                if (ptcount == 1) //1 finger
                {
                    m1TouchEventX = e.getX(0)
                    m1TouchEventY = e.getY(0)
                } else if (ptcount == 2) //2 finger
                {
                    m2TouchEventX = e.getX(0)
                    m2TouchEventY = e.getY(0)
                }
            }

            MotionEvent.ACTION_MOVE -> {
                m2TouchEventX = e.getX(0)
                m2TouchEventY = e.getY(0)
                if (isPinchGesture(e)) { //check to see if it is a pinch gesture
                    m2TouchEventX = e.getX(0)
                    m2TouchEventY = e.getY(0)
                    mTouchDistance = distance(e, 0, 1) //calculate the distance
                    renderer.setZoom(mTouchDistance * TOUCH_ZOOM_FACTOR) // set the zoom
                    requestRender() //update the screen
                } else {
                    var dx = x - mPreviousX
                    var dy = y - mPreviousY
                    // reverse direction of rotation above the mid-line
                    if (y > height / 2) {
                        dx *= -1
                    }
                    // reverse direction of rotation to left of the mid-line
                    if (x < width / 2) {
                        dy *= -1
                    }
                    //set the rotation angles
                    renderer.yAngle = renderer.yAngle + dx * TOUCH_SCALE_FACTOR
                    renderer.xAngle = renderer.xAngle + dy * TOUCH_SCALE_FACTOR
                    requestRender()
                }
            }
        }
        mPreviousX = x
        mPreviousY = y
        return true
    }

    fun sensorRotates(pitch: Double, yaw: Double, roll: Double) {
        renderer.xAngle = pitch.toFloat()
        renderer.yAngle = yaw.toFloat()
        renderer.zAngle = roll.toFloat()
        requestRender()
    }

    private fun distance(
        e: MotionEvent,
        first: Int,
        second: Int
    ): Float { //distance between 2 touch motion events
        return if (e.pointerCount >= 2) {
            val x = e.getX(first) - e.getX(second)
            val y = e.getY(first) - e.getY(second)
            sqrt((x * x + y * y).toDouble()).toFloat() //Euclidean distance
        } else {
            0f
        }
    }

    private fun isPinchGesture(event: MotionEvent): Boolean { //check if it is a pinch gesture
        if (event.pointerCount == 2) { //multi-touch
            //check the distances between the touch locations
            val distanceCurrent = distance(event, 0, 1)
            val diffPrimX = m1TouchEventX - event.getX(0)
            val diffPrimY = m1TouchEventY - event.getY(0)
            val diffSecX = m2TouchEventX - event.getX(1)
            val diffSecY = m2TouchEventY - event.getY(1)
            if (abs(distanceCurrent - mTouchDistance) > mViewScaledTouchSlop && diffPrimY * diffSecY <= 0 && diffPrimX * diffSecX <= 0) {
                //if the distance between the touch is above the threshold and the fingers are moving in opposing directions
                return true
            }
        }
        return false
    }

    companion object {

        private const val TOUCH_SCALE_FACTOR = 180.0f / 320 //scale factor for the touch motions
        private const val TOUCH_ZOOM_FACTOR = 1.0f / 320 //zoom factor
    }

}