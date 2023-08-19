package com.bennyplo.designgraphicswithopengl

import android.opengl.GLES32
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.random.Random
import kotlin.random.nextInt

class MyRenderer : GLSurfaceView.Renderer {

    private val mCharA by lazy {
        CharacterA()
    }
    private val mCharE by lazy {
        CharacterE()
    }
    private val mCharI by lazy {
        CharacterI()
    }
    private val mCharL by lazy {
        CharacterL()
    }
    private val mCharM by lazy {
        CharacterM()
    }
    private val mCharP by lazy {
        CharacterP()
    }
    private val mCharR by lazy {
        CharacterR()
    }
    private val mCharS by lazy {
        CharacterS()
    }
    private val mCharV by lazy {
        CharacterV()
    }
    private val mSphere by lazy {
        Sphere()
    }
    private val mArbitrary by lazy {
        Arbitrary()
    }
    private val mHalfCone by lazy {
        HalfCone()
    }
    private val mImperial by lazy {
        Imperial()
    }
    private val mMVMatrix = FloatArray(MATRIX_SIZE) //model view matrix
    private val mMVPMatrix = FloatArray(MATRIX_SIZE) //model view projection matrix
    private val mModelMatrix = FloatArray(MATRIX_SIZE) //model  matrix
    private val mProjectionMatrix = FloatArray(MATRIX_SIZE) //projection mastrix
    private val mViewMatrix = FloatArray(MATRIX_SIZE) //view matrix

    private val increment = 1f

    private var xRotateDirection = 1
    private var yRotateDirection = 1
    private var zRotateDirection = 1

    private var xRotateTarget = Random.nextInt(10..60)
    private var yRotateTarget = Random.nextInt(10..60)
    private var zRotateTarget = Random.nextInt(10..60)

    private var angleX = 0f
    private var angleY = 0f
    private var angleZ = 0f

    override fun onDrawFrame(unused: GL10) {
        if ((xRotateDirection < 0 && angleX <= xRotateTarget) || (xRotateDirection >0 && angleX >= xRotateTarget)) {
            xRotateDirection *= -1
            xRotateTarget = Random.nextInt(10..60) * xRotateDirection
        }

        if ((yRotateDirection < 0 && angleY <= yRotateTarget) || (yRotateDirection >0 && angleY >= yRotateTarget)) {
            yRotateDirection *= -1
            yRotateTarget = Random.nextInt(10..60) * yRotateDirection
        }

        if ((zRotateDirection < 0 && angleZ <= zRotateTarget) || (zRotateDirection >0 && angleZ >= zRotateTarget)) {
            zRotateDirection *= -1
            zRotateTarget = Random.nextInt(10..60) * zRotateDirection
        }

        angleX += increment * xRotateDirection
        angleY += increment * yRotateDirection
        angleZ += increment * zRotateDirection

        val mRotationMatrixX = FloatArray(MATRIX_SIZE)
        val mRotationMatrixY = FloatArray(MATRIX_SIZE)
        val mRotationMatrixZ = FloatArray(MATRIX_SIZE)
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
        Matrix.setRotateM(mRotationMatrixX, 0, angleX, 1f, 0f, 0f) //rotate around the x-axis
        Matrix.setRotateM(mRotationMatrixY, 0, angleY, 0f, 1f, 0f) //rotate around the y-axis
        Matrix.setRotateM(mRotationMatrixZ, 0, angleZ, 0f, 1f, 0f) //rotate around the z-axis
        // Set the camera position (View matrix)
        Matrix.setLookAtM(
            mViewMatrix, 0,
            0.0f, 0f, 1.0f,  //camera is at (0,0,1)
            0f, 0f, 0f,  //looks at the origin
            0f, 1f, 0.0f
        ) //head is down (set to (0,1,0) to look from the top)
        Matrix.translateM(mModelMatrix, 0, 0.0f, 0.0f, -5f) //move backward for 5 units
        Matrix.multiplyMM(mModelMatrix, 0, mModelMatrix, 0, mRotationMatrixX, 0)
        Matrix.multiplyMM(mModelMatrix, 0, mModelMatrix, 0, mRotationMatrixY, 0)
        Matrix.multiplyMM(mModelMatrix, 0, mModelMatrix, 0, mRotationMatrixZ, 0)
        // Calculate the projection and view transformation
        //calculate the model view matrix
        Matrix.multiplyMM(mMVMatrix, 0, mViewMatrix, 0, mModelMatrix, 0)
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVMatrix, 0)
//        mCharA.draw(mMVPMatrix)
//        mCharE.draw(mMVPMatrix)
//        mCharI.draw(mMVPMatrix)
//        mCharL.draw(mMVPMatrix)
//        mCharM.draw(mMVPMatrix)
//        mCharP.draw(mMVPMatrix)
//        mCharR.draw(mMVPMatrix)
//        mCharS.draw(mMVPMatrix)
//        mCharV.draw(mMVPMatrix)
//        mCharV.draw(mMVPMatrix)
//        mSphere.draw(mMVPMatrix)
//        mArbitrary.draw(mMVPMatrix)
//        mHalfCone.draw(mMVPMatrix)
        mImperial.draw(mMVPMatrix)
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        // Adjust the view based on view window changes, such as screen rotation
        GLES32.glViewport(0, 0, width, height)
        val ratio = width.toFloat() / height
        val left = -ratio
        Matrix.frustumM(mProjectionMatrix, 0, left, ratio, -1.0f, 1.0f, 1.0f, 25.0f)
    }

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        // Set the background frame color to black
        GLES32.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
    }

    companion object {

        private const val MATRIX_SIZE = 16

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