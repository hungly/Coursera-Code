package com.bennyplo.openglpipeline

import android.opengl.GLES32
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import timber.log.Timber
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

@Suppress("UnusedPrivateProperty", "MagicNumber")
class MyRenderer : GLSurfaceView.Renderer {

    private val mCircle by lazy {
        Circle()
    }

    private val mEllipse by lazy {
        Ellipse()
    }

    private val mSquare by lazy {
        Square()
    }

    private val mTriangle by lazy {
        Triangle()
    }

    private val mPyramid by lazy {
        Pyramid()
    }

    private val mCube by lazy {
        Cube()
    }

    private val mPentagon by lazy {
        Pentagon()
    }

    private val mMVMatrix = FloatArray(MATRIX_SIZE) //model view matrix
    private val mMVPMatrix = FloatArray(MATRIX_SIZE) //model view projection matrix
    private val mModelMatrix = FloatArray(MATRIX_SIZE) //model  matrix
    private val mProjectionMatrix = FloatArray(MATRIX_SIZE) //projection matrix
    private val mViewMatrix = FloatArray(MATRIX_SIZE) //view matrix
    private val mRotationMatrixZ = FloatArray(MATRIX_SIZE)
    private val mRotationMatrixY = FloatArray(MATRIX_SIZE)

    override fun onDrawFrame(unused: GL10) {
        // Draw background color
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)
        GLES32.glClearDepthf(1.0f) //set up the depth buffer
        GLES32.glEnable(GLES32.GL_DEPTH_TEST) //enable depth test (so, it will not look through the surfaces)
        GLES32.glDepthFunc(GLES32.GL_LEQUAL) //indicate what type of depth test
        Matrix.setIdentityM(
            mMVPMatrix,
            0
        ) //set the model view projection matrix to an identity matrix
        Matrix.setIdentityM(mMVMatrix, 0) //set the model view  matrix to an identity matrix
        Matrix.setIdentityM(mModelMatrix, 0) //set the model matrix to an identity matrix
        // Set the camera position (View matrix)
        Matrix.setRotateM(mRotationMatrixZ, 0, 30.0f, 0.0f, 0.0f, 1.0f)
        Matrix.setRotateM(mRotationMatrixY, 0, 30.0f, 0.0f, 1.0f, 0.0f)
        Matrix.setLookAtM(
            mViewMatrix, 0,
            0.0f, 0f, 1.0f,  //camera is at (0,0,1)
            0f, 0f, 0f,  //looks at the origin
            0f, 1f, 0.0f
        ) //head is down (set to (0,1,0) to look from the top)
        Matrix.translateM(mModelMatrix, 0, 0.0f, 0.0f, -5f) //move backward for 5 units
        // Calculate the projection and view transformation
        //calculate the model view matrix
        Matrix.multiplyMM(mModelMatrix, 0, mModelMatrix, 0, mRotationMatrixZ, 0)
        Matrix.multiplyMM(mModelMatrix, 0, mModelMatrix, 0, mRotationMatrixY, 0)
        Matrix.multiplyMM(mMVMatrix, 0, mViewMatrix, 0, mModelMatrix, 0)
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVMatrix, 0)
//        mTriangle.draw(mMVPMatrix)
//        mSquare.draw(mMVPMatrix)
//        mCircle.draw(mMVPMatrix)
//        mEllipse.draw(mMVPMatrix)
//        mPyramid.draw(mMVPMatrix)
//        mCube.draw(mMVPMatrix)
        mPentagon.draw(mMVPMatrix)
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        // Adjust the view based on view window changes, such as screen rotation
        GLES32.glViewport(0, 0, width, height)
        val ratio = width.toFloat() / height
        val left = -ratio
        Matrix.frustumM(
            mProjectionMatrix,
            0,
            left,
            ratio,
            -1f,
            1f,
            1f,
            8f
        )
    }

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        // Set the background frame color to black
        GLES32.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
    }

    companion object {

        private const val MATRIX_SIZE = 16

        @JvmStatic
        fun checkGlError(glOperation: String) {
            var error: Int
            if (GLES32.glGetError().also { error = it } != GLES32.GL_NO_ERROR) {
                Timber.e("$glOperation: glError $error")
            }
        }

        @JvmStatic
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
