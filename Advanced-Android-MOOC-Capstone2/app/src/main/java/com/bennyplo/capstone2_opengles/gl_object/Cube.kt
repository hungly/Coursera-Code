package com.bennyplo.capstone2_opengles.gl_object

import android.opengl.GLES32
import com.bennyplo.capstone2_opengles.MyRenderer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer

class Cube : GLObject() {

    private val colorBuffer: FloatBuffer by lazy {
        ByteBuffer.allocateDirect(COLORS.size * COLORS_PER_VERTEX).apply {
            order(ByteOrder.nativeOrder())
        }.asFloatBuffer().apply {
            put(COLORS)
            position(0)
        }
    }

    private val colorStride by lazy {
        COLORS_PER_VERTEX * Float.SIZE_BYTES
    }

    private val indexBuffer: IntBuffer by lazy {
        IntBuffer.allocate(INDEXES.size).apply {
            put(INDEXES)
            position(0)
        }
    }

    private val vertices by lazy {
        floatArrayOf(
            -1.0F * initialScale.first,
            -1.0F * initialScale.second,
            1.0F * initialScale.third, // 0
            1.0F * initialScale.first,
            -1.0F * initialScale.second,
            1.0F * initialScale.third, // 1
            1.0F * initialScale.first,
            1.0F * initialScale.second,
            1.0F * initialScale.third, // 2
            -1.0F * initialScale.first,
            1.0F * initialScale.second,
            1.0F * initialScale.third, // 3
            -1.0F * initialScale.first,
            -1.0F * initialScale.second,
            -1.0F * initialScale.third, // 4
            1.0F * initialScale.first,
            -1.0F * initialScale.second,
            -1.0F * initialScale.third, // 5
            1.0F * initialScale.first,
            1.0F * initialScale.second,
            -1.0F * initialScale.third, // 6
            -1.0F * initialScale.first,
            1.0F * initialScale.second,
            -1.0F * initialScale.third, // 7
        )
    }

    private val program: Int by lazy {
        GLES32.glCreateProgram()
    }

    private val vertexBuffer: FloatBuffer by lazy {
        // (# of coordinate values * 4 bytes per float)
        ByteBuffer.allocateDirect(vertices.size * 4).apply {
            order(ByteOrder.nativeOrder())
        }.asFloatBuffer().apply {
            put(vertices)
            position(0)
        }
    }

    private val vertexStride by lazy {
        COORDS_PER_VERTEX * Float.SIZE_BYTES // 4 bytes per vertex
    }

    private val colorHandle: Int

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
                "attribute vec4 aVertexColor;" +//attribute variable for vertex colors
                "void main() {" +
                "   gl_Position = uMVPMatrix *vec4(aVertexPosition, 1.0);" +
                "   gl_PointSize = 40.0;" +
                "   vColor = aVertexColor;" +
                "}"

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

        colorHandle = GLES32.glGetAttribLocation(program, "aVertexColor")
        GLES32.glEnableVertexAttribArray(colorHandle)

        // Get handle to shape's transformation matrix
        mVPMatrixHandle = GLES32.glGetUniformLocation(program, "uMVPMatrix")
        MyRenderer.checkGlError("glGetUniformLocation")
    }

    override fun draw(mvpMatrix: FloatArray?) {
        GLES32.glUseProgram(program) // Add program to OpenGL environment
        // Apply the projection and view transformation
        GLES32.glUniformMatrix4fv(mVPMatrixHandle, 1, false, mvpMatrix, 0)
        MyRenderer.checkGlError("glUniformMatrix4fv")

        GLES32.glEnable(GLES32.GL_CULL_FACE)
        GLES32.glCullFace(GLES32.GL_FRONT)
        GLES32.glFrontFace(GLES32.GL_CCW)

        // Set the attribute of the vertex to point to the vertex buffer
        GLES32.glVertexAttribPointer(
            colorHandle,
            COLORS_PER_VERTEX,
            GLES32.GL_FLOAT,
            false,
            colorStride,
            colorBuffer
        )
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
            GLES32.GL_TRIANGLE_STRIP,
            INDEXES.size,
            GLES32.GL_UNSIGNED_INT,
            indexBuffer
        )
    }

    companion object {
        // Number of coordinates per vertex in this array
        private const val COORDS_PER_VERTEX = 3
        private const val COLORS_PER_VERTEX = 4

        private val INDEXES = intArrayOf(
            0, 1, 2, 0, 2, 3, // front
            1, 5, 6, 1, 6, 2, // right
            3, 2, 6, 3, 6, 7, // top
            4, 5, 1, 4, 1, 0, // left
            4, 0, 3, 4, 3, 7, // bottom
            7, 6, 5, 7, 5, 4, // back
        )

        private val COLORS = floatArrayOf(
            0.75F, 0.75F, 0.75F, 1.0F,
            0.75F, 0.75F, 0.75F, 1.0F,
            0.75F, 0.75F, 0.75F, 1.0F,
            0.75F, 0.75F, 0.75F, 1.0F,
            0.25F, 0.25F, 0.25F, 1.0F,
            0.25F, 0.25F, 0.25F, 1.0F,
            0.25F, 0.25F, 0.25F, 1.0F,
            0.25F, 0.25F, 0.25F, 1.0F,
        )
    }

}