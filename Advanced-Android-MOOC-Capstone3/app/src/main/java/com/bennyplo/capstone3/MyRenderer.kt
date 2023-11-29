package com.bennyplo.capstone3

import android.opengl.GLES32
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.bennyplo.capstone3.model.Constant.MATRIX_SIZE
import com.bennyplo.capstone3.model.gl_object.FloorPlan3D
import timber.log.Timber
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyRenderer : GLSurfaceView.Renderer {

    private var xAngle = 0.0F
    private var yAngle = 0.0F
    private var zAngle = 0.0F
    private val _rotationMatrixX by lazy { FloatArray(MATRIX_SIZE) }
    private val _rotationMatrixY by lazy { FloatArray(MATRIX_SIZE) }
    private val _rotationMatrixZ by lazy { FloatArray(MATRIX_SIZE) }

    private val floorPlan: FloorPlan3D by lazy {
        FloorPlan3D()
    }

    private val mVMatrix by lazy {
        FloatArray(MATRIX_SIZE) // Model view matrix
    }

    private val mVPMatrix by lazy {
        FloatArray(MATRIX_SIZE) // Model view projection matrix
    }

    private val modelMatrix by lazy {
        FloatArray(MATRIX_SIZE) // Model  matrix
    }

    private val objects by lazy {
        arrayOf(
            floorPlan
        )
    }

    private val projectionMatrix by lazy {
        FloatArray(MATRIX_SIZE) // Projection matrix
    }

    private val viewMatrix by lazy {
        FloatArray(MATRIX_SIZE) // View matrix
    }

    override fun onDrawFrame(unused: GL10) {
        // Draw background color
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)
        GLES32.glClearDepthf(1.0F) // Set up the depth buffer
        GLES32.glEnable(GLES32.GL_DEPTH_TEST) // Enable depth test (so, it will not look through the surfaces)
        GLES32.glDepthFunc(GLES32.GL_LEQUAL) // Indicate what type of depth test

        objects.forEach {
            // Reset matrices
            Matrix.setIdentityM(
                mVPMatrix,
                0
            ) // Set the model view projection matrix to an identity matrix
            Matrix.setIdentityM(mVMatrix, 0) // Set the model view  matrix to an identity matrix
            Matrix.setIdentityM(modelMatrix, 0) // Set the model matrix to an identity matrix

            // Set the camera position (View matrix)
            Matrix.setLookAtM(
                viewMatrix,
                0,
                0.0F,
                0.0F,
                1.0F,  //camera is at (0,0,1)
                0.0F,
                0.0F,
                0.0F,  //looks at the origin
                0.0F,
                1.0F,
                0.0F
            ) // Head is down (set to (0,1,0) to look from the top)

            Matrix.translateM(modelMatrix, 0, 0.0F, 0.0F, -5.0F) // Move backward for 5 units

            Matrix.setRotateM(
                _rotationMatrixX,
                0,
                xAngle + it.initialRotation.xRotation,
                1.0F,
                0.0F,
                0.0F
            ) // Rotate around the x-axis
            Matrix.setRotateM(
                _rotationMatrixY,
                0,
                yAngle + it.initialRotation.yRotation,
                0.0F,
                1.0F,
                0.0F
            ) // Rotate around the y-axis
            Matrix.setRotateM(
                _rotationMatrixZ,
                0,
                zAngle + it.initialRotation.zRotation,
                0.0F,
                0.0F,
                1.0F
            ) // Rotate around the z-axis
            Matrix.multiplyMM(modelMatrix, 0, modelMatrix, 0, _rotationMatrixY, 0)
            Matrix.multiplyMM(modelMatrix, 0, modelMatrix, 0, _rotationMatrixX, 0)
            Matrix.multiplyMM(modelMatrix, 0, modelMatrix, 0, _rotationMatrixZ, 0)

            Matrix.translateM(
                modelMatrix,
                0,
                it.initialTranslation.xTranslation,
                it.initialTranslation.yTranslation,
                it.initialTranslation.zTranslation
            )

            Matrix.scaleM(
                modelMatrix,
                0,
                it.initialScale.xScale,
                it.initialScale.yScale,
                it.initialScale.zScale
            )

            // Calculate the projection and view transformation
            // Calculate the model view matrix
            Matrix.multiplyMM(mVMatrix, 0, viewMatrix, 0, modelMatrix, 0)
            Matrix.multiplyMM(mVPMatrix, 0, projectionMatrix, 0, mVMatrix, 0)

            it.draw(mVPMatrix)
        }
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        // Adjust the view based on view window changes, such as screen rotation
        GLES32.glViewport(0, 0, width, height)
        val ratio = width.toFloat() / height
        val left = -ratio
        Matrix.frustumM(projectionMatrix, 0, left, ratio, -1.0F, 1.0F, 1F, 16.0F)
    }

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        // Set the background frame color to black
        GLES32.glClearColor(0.0F, 0.0F, 0.0F, 1.0F)
    }

    fun getXAngle(): Float {
        return xAngle
    }

    fun getYAngle(): Float {
        return yAngle
    }

    fun getZAngle(): Float {
        return zAngle
    }

    fun setXAngle(angle: Float) {
        xAngle = angle
    }

    fun setYAngle(angle: Float) {
        yAngle = angle
    }

    fun setZAngle(angle: Float) {
        zAngle = angle
    }

    companion object {
        @JvmStatic
        fun checkGlError(glOperation: String) {
            var error: Int
            if (GLES32.glGetError().also { error = it } != GLES32.GL_NO_ERROR) {
                Timber.e("$glOperation: glError $error")
            }
        }

        @JvmStatic
        fun loadShader(type: Int, shaderCode: String?): Int {
            // Create a vertex shader  (GLES32.GL_VERTEX_SHADER) or a fragment shader (GLES32.GL_FRAGMENT_SHADER)
            val shader = GLES32.glCreateShader(type)
            GLES32.glShaderSource(
                shader,
                shaderCode
            ) // Add the source code to the shader and compile it
            GLES32.glCompileShader(shader)
            return shader
        }
    }

}