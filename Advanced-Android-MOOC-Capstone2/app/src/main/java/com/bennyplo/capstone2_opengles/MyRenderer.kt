package com.bennyplo.capstone2_opengles

import android.opengl.GLES32
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.bennyplo.capstone2_opengles.gl_object.Base
import com.bennyplo.capstone2_opengles.gl_object.Constant
import com.bennyplo.capstone2_opengles.gl_object.Cube
import com.bennyplo.capstone2_opengles.gl_object.ECG
import com.bennyplo.capstone2_opengles.gl_object.FloorPlan
import com.bennyplo.capstone2_opengles.gl_object.HalfCone
import com.bennyplo.capstone2_opengles.gl_object.Pyramid
import com.bennyplo.capstone2_opengles.gl_object.Sphere
import com.bennyplo.capstone2_opengles.gl_object.Vase
import timber.log.Timber
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyRenderer : GLSurfaceView.Renderer {

    private var mAngleX = 0.0F
    private var mAngleY = 0.0F
    private var mAngleZ = 0.0F

    private val ecg by lazy {
        ECG().apply {
            initialScale = Triple(1.0F / 5, 1.0F / 5, 1.0F)
            initialRotation = Triple(90.0F, 0.0F, 0.0F)
            initialTranslation = Triple(0.0F, 0.0F, -3.0F + 0.008F)
        }
    }

    private val cube by lazy {
        Cube().apply {
            initialScale = Triple(0.2F, 0.2F, 0.2F)
            initialTranslation = Triple(-2.0F, -2.0F, 0.0F)
        }
    }

    private val pyramid by lazy {
        Pyramid().apply {
            initialScale = Triple(0.2F, 0.2F, 0.2F)
            initialRotation = Triple(90.0F, 0.0F, 0.0F)
            initialTranslation = Triple(-2.0F, 0.0F, 2.0F)
        }
    }

    private val halfCone by lazy {
        HalfCone().apply {
            initialScale = Triple(0.2F, 0.2F, 0.2F)
            initialRotation = Triple(0.0F, 0.0F, 0.0F)
            initialTranslation = Triple(-2.0F, 2.0F, 0.0F)
        }
    }

    private val sphere by lazy {
        Sphere().apply {
            initialScale = Triple(0.15F, 0.15F, 0.15F)
            initialRotation = Triple(90.0F, 0.0F, 0.0F)
            initialTranslation = Triple(2.0F, 0.0F, -2.0F)
        }
    }

    private val floorPlan by lazy {
        FloorPlan()
    }

    private val glObjects by lazy {
        arrayOf(
            floorPlan,
            ecg,
            vase,
            pyramid,
            cube,
            halfCone,
            sphere,
            Base().apply {
                initialScale = Triple(0.25F, 0.25F, 0.25F)
                initialTranslation = Triple(0.0F, 0.0F, -0.5F)
            },
            Base().apply {
                initialScale = Triple(0.25F, 0.25F, 0.25F)
                initialTranslation = Triple(-2.0F, -2.0F, -0.5F)
            },
            Base().apply {
                initialScale = Triple(0.25F, 0.25F, 0.25F)
                initialTranslation = Triple(-2.0F, 2.0F, -0.5F)
            },
            Base().apply {
                initialScale = Triple(0.25F, 0.25F, 0.25F)
                initialTranslation = Triple(2.0F, 2.0F, -0.5F)
            },
            Base().apply {
                initialScale = Triple(0.25F, 0.25F, 0.25F)
                initialTranslation = Triple(2.0F, -2.0F, -0.5F)
            }
        )
    }

    private val vase by lazy {
        Vase().apply {
            initialScale = Triple(0.05F, 0.05F, 0.05F)
            initialRotation = Triple(90.0F, 0.0F, 0.0F)
            initialTranslation = Triple(0.0F, -0.1F, 0.0F)
        }
    }

    private val mVMatrix = FloatArray(Constant.MATRIX_SIZE) // Model view matrix
    private val mVPMatrix = FloatArray(Constant.MATRIX_SIZE) // Model view projection matrix
    private val modelMatrix = FloatArray(Constant.MATRIX_SIZE) // Model  matrix
    private val projectionMatrix = FloatArray(Constant.MATRIX_SIZE) // Projection matrix
    private val viewMatrix = FloatArray(Constant.MATRIX_SIZE) // View matrix

    override fun onDrawFrame(unused: GL10) {
        val xRotationMatrix = FloatArray(Constant.MATRIX_SIZE)
        val yRotationMatrix = FloatArray(Constant.MATRIX_SIZE)
        val zRotationMatrix = FloatArray(Constant.MATRIX_SIZE)

        // Draw background color
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)
        GLES32.glClearDepthf(1.0F) // Set up the depth buffer
        GLES32.glEnable(GLES32.GL_DEPTH_TEST) // Enable depth test (so, it will not look through the surfaces)
        GLES32.glDepthFunc(GLES32.GL_LEQUAL) // Indicate what type of depth test

        glObjects.forEach {
            // Set the model view projection matrix to an identity matrix
            Matrix.setIdentityM(mVPMatrix, 0)
            Matrix.setIdentityM(mVMatrix, 0) // Set the model view  matrix to an identity matrix
            Matrix.setIdentityM(modelMatrix, 0) // Set the model matrix to an identity matrix
            // Set the camera position (View matrix)
            Matrix.setLookAtM(
                viewMatrix,
                0,
                0.0F,
                0.0F,
                1.0F,  // Camera is at (0,0,1)
                0.0F,
                0.0F,
                0.0F,  // Looks at the origin
                0.0F,
                1.0F,
                0.0F
            ) // Head is down (set to (0,1,0) to look from the top)

            Matrix.translateM(modelMatrix, 0, 0.0F, 0.0F, -5.0f) // Move backward for 5 units
            Matrix.setRotateM(
                xRotationMatrix,
                0,
                mAngleX + it.initialRotation.first,
                1.0F,
                0.0F,
                0.0F
            ) // Rotate around the x-axis
            Matrix.setRotateM(
                yRotationMatrix,
                0,
                mAngleY + it.initialRotation.second,
                0.0F,
                1.0F,
                0.0F
            ) // Rotate around the y-axis
            Matrix.setRotateM(
                zRotationMatrix,
                0,
                mAngleZ + it.initialRotation.third,
                0.0F,
                0.0F,
                1.0F
            ) // Rotate around the z-axis
            Matrix.multiplyMM(modelMatrix, 0, modelMatrix, 0, yRotationMatrix, 0)
            Matrix.multiplyMM(modelMatrix, 0, modelMatrix, 0, xRotationMatrix, 0)
            Matrix.multiplyMM(modelMatrix, 0, modelMatrix, 0, zRotationMatrix, 0)

            Matrix.translateM(
                modelMatrix,
                0,
                it.initialTranslation.first,
                it.initialTranslation.second,
                it.initialTranslation.third
            )

            // Calculate the projection and view transformation
            // Calculate the model view matrix
            Matrix.multiplyMM(mVMatrix, 0, viewMatrix, 0, modelMatrix, 0)
            Matrix.multiplyMM(mVPMatrix, 0, projectionMatrix, 0, mVMatrix, 0)

            it.draw(mVPMatrix)
        }
//        floorPlan.draw(mVPMatrix)
//        ecg.draw(mVPMatrix)
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        // Adjust the view based on view window changes, such as screen rotation
        GLES32.glViewport(0, 0, width, height)
        val ratio = width.toFloat() / height
        val left = -ratio
        Matrix.frustumM(projectionMatrix, 0, left, ratio, -1.0F, 1.0F, 1.0F, 16.0F)
    }

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        // Set the background frame color to black
        GLES32.glClearColor(0.0F, 0.0F, 0.0F, 1.0F)
    }

    fun getAngleX(): Float {
        return mAngleX
    }

    fun getAngleY(): Float {
        return mAngleY
    }

    fun setAngleX(angle: Float) {
        mAngleX = angle
        Timber.d("setAngleX: $mAngleX")
    }

    fun setAngleY(angle: Float) {
        mAngleY = angle
        Timber.d("setAngleY: $mAngleX")
    }

    fun setAngleZ(angle: Float) {
        mAngleZ = angle
    }

    companion object {

        fun checkGlError(glOperation: String) {
            var error: Int
            if (GLES32.glGetError().also { error = it } != GLES32.GL_NO_ERROR) {
                Timber.tag("MyRenderer").e("$glOperation: glError $error")
            }
        }

        fun loadShader(type: Int, shaderCode: String?): Int {
            // Create a vertex shader  (GLES32.GL_VERTEX_SHADER) or a fragment shader (GLES32.GL_FRAGMENT_SHADER)
            val shader = GLES32.glCreateShader(type)
            // Add the source code to the shader and compile it
            GLES32.glShaderSource(shader, shaderCode)
            GLES32.glCompileShader(shader)
            return shader
        }
    }

}