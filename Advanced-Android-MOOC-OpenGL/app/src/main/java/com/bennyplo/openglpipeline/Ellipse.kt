package com.bennyplo.openglpipeline

import android.opengl.GLES32
import com.bennyplo.openglpipeline.MyRenderer.Companion.checkGlError
import com.bennyplo.openglpipeline.MyRenderer.Companion.loadShader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.cos
import kotlin.math.sin

class Ellipse {

    private val ellipseVertex by lazy {
        val angleIncrement = FULL_CIRCLE_ANGLE / RESOLUTION
        val result = arrayListOf<Float>()
        var rad: Float

        result.add(0.0f)
        result.add(0.0f)
        result.add(1.0f)

        var angle = 0.0

        while (angle <= FULL_CIRCLE_ANGLE) {
            rad = Math.toRadians(angle).toFloat()
            val x = RADIUS * cos(rad)
            val y = RADIUS * sin(rad)
            result.add(x * X_SCALE)
            result.add(y * Y_SCALE)
            result.add(1.0f)
            angle += angleIncrement
        }

        FloatArray(result.size) { result[it] }
    }

    private val fragmentShaderCode = "precision mediump float;uniform vec4 vColor; " +
            "void main() {gl_FragColor = vColor;}"

    private val mColorHandle: Int
    private val mMVPMatrixHandle: Int
    private val mPositionHandle: Int
    private val mProgram: Int
    private val vertexBuffer: FloatBuffer

    private val vertexCount // number of vertices
            : Int

    private val vertexShaderCode =
        "attribute vec3 aVertexPosition;" + "uniform mat4 uMVPMatrix;" +
                "void main() {gl_Position = uMVPMatrix *vec4(aVertexPosition,1.0);}"

    private val vertexStride = COORDS_PER_VERTEX * Float.SIZE_BYTES // 4 bytes per vertex

    init {
        // initialize vertex byte buffer for shape coordinates
        val bb = ByteBuffer.allocateDirect(
            ellipseVertex.size * Float.SIZE_BYTES
        ) // (# of coordinate values * 4 bytes per float)
        bb.order(ByteOrder.nativeOrder())
        vertexBuffer = bb.asFloatBuffer()
        vertexBuffer.put(ellipseVertex)
        vertexBuffer.position(0)
        vertexCount = ellipseVertex.size / COORDS_PER_VERTEX

        // prepare shaders and OpenGL program
        val vertexShader = loadShader(GLES32.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES32.GL_FRAGMENT_SHADER, fragmentShaderCode)
        mProgram = GLES32.glCreateProgram() // create empty OpenGL Program
        GLES32.glAttachShader(mProgram, vertexShader) // add the vertex shader to program
        GLES32.glAttachShader(mProgram, fragmentShader) // add the fragment shader to program
        GLES32.glLinkProgram(mProgram) // link the  OpenGL program to create an executable
        GLES32.glUseProgram(mProgram) // Add program to OpenGL environment
        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES32.glGetAttribLocation(mProgram, "aVertexPosition")
        // Enable a handle to the triangle vertices
        GLES32.glEnableVertexAttribArray(mPositionHandle)
        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES32.glGetUniformLocation(mProgram, "uMVPMatrix")
        checkGlError("glGetUniformLocation")
        mColorHandle = GLES32.glGetUniformLocation(mProgram, "vColor")
        checkGlError("glGetUniformLocation")
    }

    fun draw(mvpMatrix: FloatArray?) {
        // Apply the projection and view transformation
        GLES32.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0)
        checkGlError("glUniformMatrix4fv")
        //set the attribute of the vertex to point to the vertex buffer
        GLES32.glVertexAttribPointer(
            mPositionHandle,
            COORDS_PER_VERTEX,
            GLES32.GL_FLOAT,
            false,
            vertexStride,
            vertexBuffer
        )

        GLES32.glUniform4fv(mColorHandle, 1, fillColor, 0)
        checkGlError("glUniform4fv")
        // Draw the triangle
        GLES32.glDrawArrays(GLES32.GL_TRIANGLE_FAN, 0, vertexCount)

        GLES32.glUniform4fv(mColorHandle, 1, borderColor, 0)
        checkGlError("glUniform4fv")
        GLES32.glLineWidth(BORDER_THICKNESS)
        // Draw the border
        GLES32.glDrawArrays(GLES32.GL_LINE_STRIP, 1, vertexCount)
    }

    companion object {
        // number of coordinates per vertex in this array
        private const val COORDS_PER_VERTEX = 3
        private const val BORDER_THICKNESS = 5.0f
        private const val RADIUS = 1.0f
        private const val X_SCALE = 1.5f
        private const val Y_SCALE = 2.5f
        private const val RESOLUTION = 90
        private const val FULL_CIRCLE_ANGLE = 360.0
        private val fillColor = floatArrayOf(1.0f, 0.0f, 0.0f, 1.0f)
        private val borderColor = floatArrayOf(0.0f, 1.0f, 0.0f, 1.0f)
    }

}
