package com.bennyplo.openglpipeline

import android.opengl.GLES32
import com.bennyplo.openglpipeline.MyRenderer.Companion.checkGlError
import com.bennyplo.openglpipeline.MyRenderer.Companion.loadShader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class Ellipse {

    private val fragmentShaderCode = "precision mediump float;uniform vec4 vColor; " +
            "void main() {gl_FragColor = vColor;}"

    private val mMVPMatrixHandle: Int
    private val mPositionHandle: Int
    private val mColorHandle: Int
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
            squareVertex.size * Float.SIZE_BYTES
        ) // (# of coordinate values * 4 bytes per float)
        bb.order(ByteOrder.nativeOrder())
        vertexBuffer = bb.asFloatBuffer()
        vertexBuffer.put(squareVertex)
        vertexBuffer.position(0)
        vertexCount = squareVertex.size / COORDS_PER_VERTEX

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
        GLES32.glDrawArrays(GLES32.GL_TRIANGLES, 0, vertexCount)

        GLES32.glUniform4fv(mColorHandle, 1, borderColor, 0)
        checkGlError("glUniform4fv")
        GLES32.glLineWidth(5.0f)
        // Draw the border
        GLES32.glDrawArrays(GLES32.GL_LINE_STRIP, 0, vertexCount)
    }

    companion object {
        // number of coordinates per vertex in this array
        const val COORDS_PER_VERTEX = 3
        const val VALUES_PER_COLOR = 4
        var squareVertex = floatArrayOf(
            1.0f, 1.0f, 1.0f,
            -1.0f, 1.0f, 1.0f,
            1.0f, -1.0f, 1.0f,
            1.0f, -1.0f, 1.0f,
            -1.0f, 1.0f, 1.0f,
            -1.0f, -1.0f, 1.0f,
        )
        private val fillColor = floatArrayOf(1.0f, 0.0f, 0.0f, 1.0f)
        private val borderColor = floatArrayOf(0.0f, 1.0f, 0.0f, 1.0f)
    }

}
