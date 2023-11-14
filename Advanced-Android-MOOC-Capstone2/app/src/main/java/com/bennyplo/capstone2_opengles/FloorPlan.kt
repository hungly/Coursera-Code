package com.bennyplo.capstone2_opengles

import android.opengl.GLES32
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer

class FloorPlan {

    private val indexBuffer: IntBuffer by lazy {
        IntBuffer.allocate(CUBE_INDEXES.size).apply {
            put(CUBE_INDEXES)
            position(0)
        }
    }

    private val program: Int by lazy {
        GLES32.glCreateProgram()
    }

    private val vertexBuffer: FloatBuffer by lazy {
        // (# of coordinate values * 4 bytes per float)
        ByteBuffer.allocateDirect(FLOOR_PLAN_VERTICES.size * 4).apply {
            order(ByteOrder.nativeOrder())
        }.asFloatBuffer().apply {
            put(FLOOR_PLAN_VERTICES)
            position(0)
        }
    }

    // Number of vertices
    private val vertexCount: Int by lazy {
        FLOOR_PLAN_VERTICES.size / COORDS_PER_VERTEX
    }

    private val fragmentShaderCode =
        "precision mediump float;" +
                "varying vec4 vColor;" +
                "void main() {" +
                "   gl_FragColor = vColor;" +
                "}"

    private val mVPMatrixHandle: Int
    private val positionHandle: Int

    private val vertexShaderCode =
        "attribute vec3 aVertexPosition;" +
                "uniform mat4 uMVPMatrix;" +
                "varying vec4 vColor;" +
                "void main() {" +
                "   gl_Position = uMVPMatrix *vec4(aVertexPosition, 1.0);" +
                "   gl_PointSize = 40.0;" +
                "   vColor = vec4(1.0, 0.0, 0.0, 1.0);" +
                "}"

    private val vertexStride = COORDS_PER_VERTEX * 4 // 4 bytes per vertex

    init {
        // Initialize vertex byte buffer for shape coordinates
//        val bb =
//            ByteBuffer.allocateDirect(FLOOR_PLAN_VERTICES.size * 4) // (# of coordinate values * 4 bytes per float)
//        bb.order(ByteOrder.nativeOrder())
//        vertexBuffer = bb.asFloatBuffer()
//        vertexBuffer.put(FLOOR_PLAN_VERTICES)
//        vertexBuffer.position(0)

        // Prepare shaders and OpenGL program
//        val vertexShader: Int = MyRenderer.loadShader(GLES32.GL_VERTEX_SHADER, vertexShaderCode)
//        val fragmentShader: Int = MyRenderer.loadShader(GLES32.GL_FRAGMENT_SHADER, fragmentShaderCode)

        // Create empty OpenGL Program
        GLES32.glAttachShader(
            program,
            MyRenderer.loadShader(GLES32.GL_VERTEX_SHADER, vertexShaderCode)
        ) // Add the vertex shader to program
        GLES32.glAttachShader(
            program,
            MyRenderer.loadShader(GLES32.GL_FRAGMENT_SHADER, fragmentShaderCode)
        ) // Add the fragment shader to program
        GLES32.glLinkProgram(program) // Link the  OpenGL program to create an executable
        // Get handle to vertex shader's vPosition member
        positionHandle = GLES32.glGetAttribLocation(program, "aVertexPosition")
        // Enable a handle to the triangle vertices
        GLES32.glEnableVertexAttribArray(positionHandle)
        // Get handle to shape's transformation matrix
        mVPMatrixHandle = GLES32.glGetUniformLocation(program, "uMVPMatrix")
        MyRenderer.checkGlError("glGetUniformLocation")
    }

    fun draw(mvpMatrix: FloatArray?) {
        GLES32.glUseProgram(program) // Add program to OpenGL environment
        // Apply the projection and view transformation
        GLES32.glUniformMatrix4fv(mVPMatrixHandle, 1, false, mvpMatrix, 0)
        MyRenderer.checkGlError("glUniformMatrix4fv")
        // Set the attribute of the vertex to point to the vertex buffer
        GLES32.glVertexAttribPointer(
            positionHandle,
            COORDS_PER_VERTEX,
            GLES32.GL_FLOAT,
            false,
            vertexStride,
            vertexBuffer
        )
        // Draw the floor plan
        //GLES32.glDrawArrays(GLES32.GL_TRIANGLES, 0, vertexCount);
//        GLES32.glDrawArrays(GLES32.GL_LINES, 0, vertexCount)
        GLES32.glDrawElements(
            GLES32.GL_TRIANGLES,
            CUBE_INDEXES.size,
            GLES32.GL_UNSIGNED_INT,
            indexBuffer
        )
    }

    companion object {
        // Number of coordinates per vertex in this array
        private const val COORDS_PER_VERTEX = 3

        private val CUBE_INDEXES = intArrayOf(
            0, 1, 28, 1, 29, 28,
            2, 3, 30, 3, 31, 30,
            4, 5, 32, 5, 33, 32,
            6, 7, 34, 7, 35, 34,
            8, 9, 36, 9, 37, 36,
            10, 11, 38, 11, 39, 38,
            12, 13, 40, 13, 41, 40,
            14, 15, 42, 15, 43, 42,
            16, 17, 44, 17, 45, 44,
            18, 19, 46, 19, 47, 46,
            20, 21, 48, 21, 49, 48,
            22, 23, 50, 23, 51, 50,
            24, 25, 52, 25, 53, 52,
            26, 27, 54, 27, 55, 54
        )

        private val FLOOR_PLAN_VERTICES = floatArrayOf(
            // Bottom
            // Left
            -3.0F, -3.0F, -1.0F, // 0
            -3.0F, 3.0F, -1.0F, // 1
            // Top
            -3.0F, 3.0F, -1.0F, // 2
            3.0F, 3.0F, -1.0F, // 3
            // Right
            -3.0F, -3.0F, -1.0F, // 4
            3.0F, -3.0F, -1.0F, // 5
            // Bottom
            3.0F, -3.0F, -1.0F, // 6
            3.0F, 3.0F, -1.0F, // 7
            // Inner - Horizontal - Left
            -3.0F, -1.0F, -1.0F, // 8
            -1.0F, -1.0F, -1.0F, // 9
            -3.0F, 1.0F, -1.0F, // 10
            -1.0F, 1.0F, -1.0F, // 11
            // Inner - Horizontal - Right
            3.0F, 1.0F, -1.0F, // 12
            1.0F, 1.0F, -1.0F, // 13
            3.0F, -1.0F, -1.0F, // 14
            1.0F, -1.0F, -1.0F, // 15
            // Inner - Vertical - Left
            -1.0F, -3.0F, -1.0F, // 16
            -1.0F, -1.5F, -1.0F, // 17
            -1.0F, -0.5F, -1.0F, // 18
            -1.0F, 0.5F, -1.0F, // 19
            -1.0F, 1.5F, -1.0F, // 20
            -1.0F, 3.0F, -1.0F, // 21
            // Inner - Vertical - Right
            1.0F, 3.0F, -1.0F, // 22
            1.0F, 1.5F, -1.0F, // 23
            1.0F, 0.5F, -1.0F, // 24
            1.0F, -0.5F, -1.0F, // 25
            1.0F, -1.5F, -1.0F, // 26
            1.0F, -3.0F, -1.0F, // 27
            // Top
            // Left
            -3.0F, -3.0F, 1.0F, // 28
            -3.0F, 3.0F, 1.0F, // 29
            // Top
            -3.0F, 3.0F, 1.0F, // 30
            3.0F, 3.0F, 1.0F, // 31
            // Right
            -3.0F, -3.0F, 1.0F, // 32
            3.0F, -3.0F, 1.0F, // 33
            // Bottom
            3.0F, -3.0F, 1.0F, // 34
            3.0F, 3.0F, 1.0F, // 35
            // Inner - Horizontal - Left
            -3.0F, -1.0F, 1.0F, // 36
            -1.0F, -1.0F, 1.0F, // 37
            -3.0F, 1.0F, 1.0F, // 38
            -1.0F, 1.0F, 1.0F, // 39
            // Inner - Horizontal - Right
            3.0F, 1.0F, 1.0F, // 40
            1.0F, 1.0F, 1.0F, // 41
            3.0F, -1.0F, 1.0F, // 42
            1.0F, -1.0F, 1.0F, // 43
            // Inner - Vertical - Left
            -1.0F, -3.0F, 1.0F, // 44
            -1.0F, -1.5F, 1.0F, // 45
            -1.0F, -0.5F, 1.0F, // 46
            -1.0F, 0.5F, 1.0F, // 47
            -1.0F, 1.5F, 1.0F, // 48
            -1.0F, 3.0F, 1.0F, // 49
            // Inner - Vertical - Right
            1.0F, 3.0F, 1.0F, // 50
            1.0F, 1.5F, 1.0F, // 51
            1.0F, 0.5F, 1.0F, // 52
            1.0F, -0.5F, 1.0F, // 53
            1.0F, -1.5F, 1.0F, // 54
            1.0F, -3.0F, 1.0F, // 55
        )
    }

}