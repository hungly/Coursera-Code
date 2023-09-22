package com.bennyplo.virtualreality

import android.content.Context
import android.opengl.GLES32
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private var mLeftView: StereoView? = null
    private var mRightView: StereoView? = null
    private var mViewportWidth = 0
    private var mZoom = 0f // zoom factor
    private var viewportheight = 0

    private val mCharA: CharacterA by lazy {
        CharacterA()
    }

    private val mCharS: CharacterS by lazy {
        CharacterS()
    }

    private val mWorldSphere by lazy {
        WorldSphere(context)
    }

    private val mSphere by lazy {
        Sphere(context)
    }

    private val mFlatSurface by lazy {
        FlatSurface(context)
    }

    private val mMyChar by lazy {
        FlatSurfaceMyChar(context)
    }

    private val mVMatrix = FloatArray(16) // model view matrix
    private val mVPMatrix = FloatArray(16) // model view projection matrix
//    private val modelMatrix = FloatArray(16) // model  matrix
    private val projectionMatrix = FloatArray(16) // projection matrix
    private val viewMatrix = FloatArray(16) // view matrix
    private val sphereMVMatrix = FloatArray(16) // model view matrix
    private val sphereMVPMatrix = FloatArray(16) // model view projection matrix
    private val charMVMatrix = FloatArray(16) // model view matrix
    private val charMVPMatrix = FloatArray(16) // model view projection matrix
    var xAngle = 0f // x-rotation angle

    //set the rotational angles and zoom factors
    var yAngle = 0f // y-rotation angle

    var zAngle = 0f // z-rotation angle

    var ySphereAngle = 0f

    var yCharAngle = 0f
    var yCharRotation = 0f
    var zCharRotation = 0f
    var zCharPos = 4f

    override fun onDrawFrame(unused: GL10) {
//        val mRotationMatrixX = FloatArray(16)
        val mRotationMatrixY = FloatArray(16)
//        val mRotationMatrixZ = FloatArray(16)
        // Draw background color
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)
        GLES32.glClearDepthf(1.0f) // set up the depth buffer
        GLES32.glEnable(GLES32.GL_DEPTH_TEST) // enable depth test (so, it will not look through the surfaces)
        GLES32.glDepthFunc(GLES32.GL_LEQUAL) // indicate what type of depth test
//        Matrix.setIdentityM(
//            mVPMatrix,
//            0
//        ) // set the model view projection matrix to an identity matrix
//        Matrix.setIdentityM(mVMatrix, 0) // set the model view  matrix to an identity matrix
//        Matrix.setIdentityM(modelMatrix, 0) // set the model matrix to an identity matrix
//        Matrix.setRotateM(mRotationMatrixX, 0, xAngle, 1.0f, 0f, 0f) // rotate around the x-axis
        Matrix.setRotateM(mRotationMatrixY, 0, yAngle, 0f, 1.0f, 0f) // rotate around the y-axis

        // Set the camera position (View matrix)
//        Matrix.setLookAtM(
//            viewMatrix,
//            0,
//            0.0f,
//            0f,
//            1.0f,  // camera is at (0,0,1)
//            0f,
//            0f,
//            0f,  // looks at the origin
//            0f,
//            1f,
//            0.0f
//        ) // head is down (set to (0,1,0) to look from the top)
//        Matrix.translateM(modelMatrix, 0, 0.0f, 0.0f, -5f + mZoom) // move backward for 5 units
//        Matrix.multiplyMM(modelMatrix, 0, modelMatrix, 0, mRotationMatrixX, 0)
//        Matrix.multiplyMM(modelMatrix, 0, modelMatrix, 0, mRotationMatrixY, 0)
        // Calculate the projection and view transformation
        // Calculate the model view matrix
//        Matrix.multiplyMM(mVMatrix, 0, viewMatrix, 0, modelMatrix, 0)
//        Matrix.multiplyMM(mVPMatrix, 0, projectionMatrix, 0, mVMatrix, 0)

        // mSphere.draw(mMVPMatrix);

        // Draw the frame buffer
        GLES32.glViewport(0, 0, mViewportWidth, viewportheight)
//        Matrix.setIdentityM(modelMatrix, 0) // set the model matrix to an identity matrix
        mLeftView?.let {
            GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER, it.frameBuffer[0])
            GLES32.glViewport(0, 0, it.width, it.height)
            GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

            val pMatrix = it.getModelMatrix(xAngle, yAngle, zAngle)
            Matrix.multiplyMM(mVMatrix, 0, it.mFrameViewMatrix, 0, pMatrix, 0)
            Matrix.multiplyMM(mVPMatrix, 0, it.mProjectionMatrix, 0, mVMatrix, 0)
            mWorldSphere.draw(mVPMatrix)
            mFlatSurface.draw(mVPMatrix)

            val pSphereMatrix = it.getModelMatrix(xAngle, yAngle + ySphereAngle, zAngle)
            Matrix.multiplyMM(sphereMVMatrix, 0, it.mFrameViewMatrix, 0, pSphereMatrix, 0)
            Matrix.multiplyMM(sphereMVPMatrix, 0, it.mProjectionMatrix, 0, sphereMVMatrix, 0)
            mSphere.setLightLocation(5f,0f,-8f)
            mSphere.draw(sphereMVPMatrix)

            val pCharMatrix = it.getModelMatrix(
                xAngle,
                yAngle + yCharAngle,
                zAngle,
                0f,
                -(yAngle + ySphereAngle) + yCharRotation,
                zCharRotation,
                zCharPos
            )
            Matrix.multiplyMM(charMVMatrix, 0, it.mFrameViewMatrix, 0, pCharMatrix, 0)
            Matrix.multiplyMM(charMVPMatrix, 0, it.mProjectionMatrix, 0, charMVMatrix, 0)
            mMyChar.draw(charMVPMatrix)

//            mCharA.draw(mVPMatrix)
//            mCharS.draw(mVPMatrix)
            GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER, 0) //render onto the screen
        }
        mRightView?.let {
            GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER, it.frameBuffer[0])
            GLES32.glViewport(0, 0, it.width, it.height)
            GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

            val pMatrix = it.getModelMatrix(xAngle, yAngle, zAngle)
            Matrix.multiplyMM(mVMatrix, 0, it.mFrameViewMatrix, 0, pMatrix, 0)
            Matrix.multiplyMM(mVPMatrix, 0, it.mProjectionMatrix, 0, mVMatrix, 0)
            mWorldSphere.draw(mVPMatrix)
            mFlatSurface.draw(mVPMatrix)

            val pSphereMatrix = it.getModelMatrix(xAngle, yAngle + ySphereAngle, zAngle)
            Matrix.multiplyMM(sphereMVMatrix, 0, it.mFrameViewMatrix, 0, pSphereMatrix, 0)
            Matrix.multiplyMM(sphereMVPMatrix, 0, it.mProjectionMatrix, 0, sphereMVMatrix, 0)
            mSphere.setLightLocation(-5f,0f,-8f)
            mSphere.draw(sphereMVPMatrix)

            val pCharMatrix = it.getModelMatrix(
                xAngle,
                yAngle + yCharAngle,
                zAngle,
                0f,
                -(yAngle + ySphereAngle) + yCharRotation,
                zCharRotation,
                zCharPos
            )
            Matrix.multiplyMM(charMVMatrix, 0, it.mFrameViewMatrix, 0, pCharMatrix, 0)
            Matrix.multiplyMM(charMVPMatrix, 0, it.mProjectionMatrix, 0, charMVMatrix, 0)
            mMyChar.draw(charMVPMatrix)

//            mCharA.draw(mVPMatrix)
//            mCharS.draw(mVPMatrix)
            GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER, 0) // render onto the screen
        }
        // Draw the framebuffer
        GLES32.glViewport(0, 0, mViewportWidth, viewportheight)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)
        mLeftView?.draw()
        mRightView?.draw()
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        // Adjust the view based on view window changes, such as screen rotation
        GLES32.glViewport(0, 0, width, height)
        var ratio = width.toFloat() / height
        val left = -ratio
        val right = ratio
        // Matrix.frustumM(mProjectionMatrix, 0, left, right, -1.0f, 1.0f, 1.0f, 8.0f);
        if (width > height) {
            ratio = width.toFloat() / height.toFloat()
            Matrix.orthoM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, -10f, 200f)
        } else {
            ratio = height.toFloat() / width.toFloat()
            Matrix.orthoM(projectionMatrix, 0, -1f, 1f, -ratio, ratio, -10f, 200f)
        }
        mViewportWidth = width
        viewportheight = height
        mLeftView = StereoView(true, height, width) //left
        mRightView = StereoView(false, height, width) //right
    }

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        // Set the background frame color to black
        GLES32.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        mZoom = 1.0f
    }

    fun setZoom(zoom: Float) {
        mZoom = zoom
    }

    fun setLightLocation(pX:Float, pY:Float, pZ:Float) {
        mSphere.setLightLocation(pX, pY, pZ)
    }

    companion object {
        fun checkGlError(glOperation: String) {
            var error: Int
            if (GLES32.glGetError().also { error = it } != GLES32.GL_NO_ERROR) {
                Log.e("MyRenderer", "$glOperation: glError $error")
            }
        }

        fun loadShader(type: Int, shaderCode: String?): Int {
            // create a vertex shader  (GLES32.GL_VERTEX_SHADER) or a fragment shader (GLES32.GL_FRAGMENT_SHADER)
            val shader = GLES32.glCreateShader(type)
            GLES32.glShaderSource(
                shader,
                shaderCode
            ) // add the source code to the shader and compile it
            GLES32.glCompileShader(shader)
            return shader
        }
    }

}
