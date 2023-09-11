package com.bennyplo.virtualreality.ref

import android.opengl.GLES32
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

@Suppress("MagicNumber","MaxLineLength")
class MyRenderer : GLSurfaceView.Renderer {

    private var mCharA: CharacterA? = null
    private var mCharS: CharacterS? = null
    private var mZoom = 0f //zoom factor
    private var mleftview: StereoView? = null
    private var mrightview: StereoView? = null
    private var viewportheight = 0

    //private Sphere msphere;
    private var viewportwidth = 0

    private val mMVMatrix = FloatArray(16) //model view matrix
    private val mMVPMatrix = FloatArray(16) //model view projection matrix
    private val mModelMatrix = FloatArray(16) //model  matrix
    private val mProjectionMatrix = FloatArray(16) //projection mastrix
    private val mViewMatrix = FloatArray(16) //view matrix
    var xAngle = 0f //x-rotation angle

    //set the rotational angles and zoom factors
    var yAngle = 0f //y-rotation angle

    var zAngle = 0f //z-rotatino angle

    override fun onDrawFrame(unused: GL10) {
        val mRotationMatrix = FloatArray(16)
        val mRotationMatrix2 = FloatArray(16)
        val mRotationMatrix3 = FloatArray(16)
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
        Matrix.setRotateM(mRotationMatrix, 0, yAngle, 0f, 1.0f, 0f) //rotate around the y-axis
        Matrix.setRotateM(mRotationMatrix2, 0, xAngle, 1.0f, 0f, 0f) //rotate around the x-axis

        // Set the camera position (View matrix)
        Matrix.setLookAtM(
            mViewMatrix, 0,
            0.0f, 0f, 1.0f,  //camera is at (0,0,1)
            0f, 0f, 0f,  //looks at the origin
            0f, 1f, 0.0f
        ) //head is down (set to (0,1,0) to look from the top)
        Matrix.translateM(mModelMatrix, 0, 0.0f, 0.0f, -5f + mZoom) //move backward for 5 units
        Matrix.multiplyMM(mModelMatrix, 0, mModelMatrix, 0, mRotationMatrix, 0)
        Matrix.multiplyMM(mModelMatrix, 0, mModelMatrix, 0, mRotationMatrix2, 0)
        // Calculate the projection and view transformation
        //calculate the model view matrix
        Matrix.multiplyMM(mMVMatrix, 0, mViewMatrix, 0, mModelMatrix, 0)
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVMatrix, 0)

        //msphere.draw(mMVPMatrix);

        //draw the frame buffer
        GLES32.glViewport(0, 0, viewportwidth, viewportheight)
        Matrix.setIdentityM(mModelMatrix, 0) //set the model matrix to an identity matrix
        if (mleftview != null) {
            GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER, mleftview!!.frameBuffer[0])
            GLES32.glViewport(0, 0, mleftview!!.width, mleftview!!.height)
            GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)
            val pmatrix = mleftview!!.getModelMatrix(xAngle, yAngle, zAngle)
            Matrix.multiplyMM(mMVMatrix, 0, mleftview!!.mFrameViewMatrix, 0, pmatrix, 0)
            Matrix.multiplyMM(mMVPMatrix, 0, mleftview!!.mProjectionMatrix, 0, mMVMatrix, 0)
            //msphere.draw(mMVPMatrix);
            mCharA!!.draw(mMVPMatrix)
            mCharS!!.draw(mMVPMatrix)
            GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER, 0) //render onto the screen
        }
        if (mrightview != null) {
            GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER, mrightview!!.frameBuffer[0])
            GLES32.glViewport(0, 0, mrightview!!.width, mrightview!!.height)
            GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)
            val pmatrix = mrightview!!.getModelMatrix(xAngle, yAngle, zAngle)
            Matrix.multiplyMM(mMVMatrix, 0, mrightview!!.mFrameViewMatrix, 0, pmatrix, 0)
            Matrix.multiplyMM(mMVPMatrix, 0, mrightview!!.mProjectionMatrix, 0, mMVMatrix, 0)
            //msphere.draw(mMVPMatrix);
            mCharA!!.draw(mMVPMatrix)
            mCharS!!.draw(mMVPMatrix)
            GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER, 0) //render onto the screen
        }
        //draw the framebuffer
        GLES32.glViewport(0, 0, viewportwidth, viewportheight)
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)
        mleftview!!.draw()
        mrightview!!.draw()
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        // Adjust the view based on view window changes, such as screen rotation
        GLES32.glViewport(0, 0, width, height)
        var ratio = width.toFloat() / height
        val left = -ratio
        val right = ratio
        //Matrix.frustumM(mProjectionMatrix, 0, left,right, -1.0f, 1.0f, 1.0f, 8.0f);
        if (width > height) {
            ratio = width.toFloat() / height.toFloat()
            Matrix.orthoM(mProjectionMatrix, 0, -ratio, ratio, -1f, 1f, -10f, 200f)
        } else {
            ratio = height.toFloat() / width.toFloat()
            Matrix.orthoM(mProjectionMatrix, 0, -1f, 1f, -ratio, ratio, -10f, 200f)
        }
        viewportwidth = width
        viewportheight = height
        mleftview = StereoView(width, height, 0) //left
        mrightview = StereoView(width, height, 1) //right
    }

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        // Set the background frame color to black
        GLES32.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        //msphere=new Sphere();
        mCharA = CharacterA()
        mCharS = CharacterS()
        mZoom = 1.0f
    }

    fun setZoom(zoom: Float) {
        mZoom = zoom
    }

    companion object {
        @JvmStatic
        fun checkGlError(glOperation: String) {
            var error: Int
            if (GLES32.glGetError().also { error = it } != GLES32.GL_NO_ERROR) {
                Log.e("MyRenderer", "$glOperation: glError $error")
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