package com.bennyplo.openglpipeline

import android.opengl.GLES32
import com.bennyplo.openglpipeline.MyRenderer.Companion.checkGlError
import com.bennyplo.openglpipeline.MyRenderer.Companion.loadShader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer
import kotlin.math.cos
import kotlin.math.sin

class Pentagon {

    private val vertices by lazy {
        val front = getPentagonFaceVertex(0.5f)
        val back = getPentagonFaceVertex(-0.5f)
        arrayListOf<Float>().apply {
            addAll(front.toTypedArray())
            removeLast()
            removeLast()
            removeLast()
            addAll(back.toTypedArray())
            removeLast()
            removeLast()
            removeLast()
            var start = 0
            for (i in 1 until 5) {
                start = i * COORDS_PER_VERTEX
                addAll(front.subList(start, start + COORDS_PER_VERTEX))
                addAll(back.subList(start, start + COORDS_PER_VERTEX))
                start += 1 * COORDS_PER_VERTEX
                addAll(back.subList(start, start + COORDS_PER_VERTEX))
                addAll(front.subList(start, start + COORDS_PER_VERTEX))
            }
            start = 5 * COORDS_PER_VERTEX
            addAll(front.subList(start, start + COORDS_PER_VERTEX))
            addAll(back.subList(start, start + COORDS_PER_VERTEX))
            start = 1 * COORDS_PER_VERTEX
            addAll(back.subList(start, start + COORDS_PER_VERTEX))
            addAll(front.subList(start, start + COORDS_PER_VERTEX))
        }.toFloatArray()
    }

    private val colorBuffer: FloatBuffer
    private val colorStride = COLOR_PER_VERTEX * Float.SIZE_BYTES // 4 bytes per vertex

    private val fragmentShaderCode =
        "precision mediump float;" +
                "varying vec4 vColor; " +
                "void main() {" +
                "gl_FragColor = vColor;" +
                "}"

    private val indexBuffer: IntBuffer
    private val mColorHandle: Int
    private val mMVPMatrixHandle: Int
    private val mPositionHandle: Int
    private val mProgram: Int
    private val vertexBuffer: FloatBuffer

    private val vertexCount // number of vertices
            : Int

    private val vertexShaderCode =
        "attribute vec3 aVertexPosition;" +
                "attribute vec4 aVertexColor;" +
                "uniform mat4 uMVPMatrix;" +
                "varying vec4 vColor;" +
                "void main() {" +
                "gl_Position = uMVPMatrix *vec4(aVertexPosition,1.0);" +
                "vColor=aVertexColor;" +
                "}"

    private val vertexStride = COORDS_PER_VERTEX * Float.SIZE_BYTES // 4 bytes per vertex

    init {
        // initialize vertex byte buffer for shape coordinates
        val bb = ByteBuffer.allocateDirect(
            vertices.size * Float.SIZE_BYTES
        ) // (# of coordinate values * 4 bytes per float)
        bb.order(ByteOrder.nativeOrder())
        vertexBuffer = bb.asFloatBuffer()
        vertexBuffer.put(vertices)
        vertexBuffer.position(0)
        vertexCount = vertices.size / COORDS_PER_VERTEX
        // color buffer
        val cb = ByteBuffer.allocateDirect(
            color.size * Float.SIZE_BYTES
        )
        cb.order(ByteOrder.nativeOrder())
        colorBuffer = cb.asFloatBuffer()
        colorBuffer.put(color)
        colorBuffer.position(0)
        // index buffer
        val ib = IntBuffer.allocate(verticesIndex.size)
        indexBuffer = ib
        indexBuffer.put(verticesIndex)
        indexBuffer.position(0)
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
        mColorHandle = GLES32.glGetAttribLocation(mProgram, "aVertexColor")
        // Enable a handle to the triangle vertices
        GLES32.glEnableVertexAttribArray(mPositionHandle)
        GLES32.glEnableVertexAttribArray(mColorHandle)
        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES32.glGetUniformLocation(mProgram, "uMVPMatrix")
        checkGlError("glGetUniformLocation")
    }

    fun draw(mvpMatrix: FloatArray?) {
        // Apply the projection and view transformation
        GLES32.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0)
        checkGlError("glUniformMatrix4fv")
//        //set the attribute of the vertex to point to the vertex buffer
        GLES32.glVertexAttribPointer(
            mPositionHandle,
            COORDS_PER_VERTEX,
            GLES32.GL_FLOAT,
            false,
            vertexStride,
            vertexBuffer
        )
        GLES32.glVertexAttribPointer(
            mColorHandle,
            COLOR_PER_VERTEX,
            GLES32.GL_FLOAT,
            false,
            colorStride,
            colorBuffer
        )
        // Draw the cube
        GLES32.glDrawElements(
            GLES32.GL_TRIANGLES,
            verticesIndex.size,
            GLES32.GL_UNSIGNED_INT,
            indexBuffer
        )
    }

    private fun getPentagonFaceVertex(z: Float): List<Float> {
        val angleIncrement = FULL_CIRCLE_ANGLE / RESOLUTION
        val result = arrayListOf<Float>()
        var rad: Float

        result.add(0.0f)
        result.add(0.0f)
        result.add(z)

        var angle = 0.0

        while (angle <= FULL_CIRCLE_ANGLE) {
            rad = Math.toRadians(angle + 90).toFloat()
            val x = RADIUS * cos(rad)
            val y = RADIUS * sin(rad)
            result.add(x * (0.5f / 0.5877852f))
            result.add(y * (0.5f / 0.5877852f))
            result.add(z)
            angle += angleIncrement
        }

        return result
    }

    companion object {
        // number of coordinates per vertex in this array
        private const val COORDS_PER_VERTEX = 3
        private const val COLOR_PER_VERTEX = 4
        private const val RADIUS = 1.0f
        private const val RESOLUTION = 5
        private const val FULL_CIRCLE_ANGLE = 360.0

        private val verticesIndex = intArrayOf(
//            // Front
            0, 1, 2,
            0, 2, 3,
            0, 3, 4,
            0, 4, 5,
            0, 5, 1,
            // Back
            6, 7, 8,
            6, 8, 9,
            6, 9, 10,
            6, 10, 11,
            6, 11, 7,
            // 1st
            12, 13, 14,
            12, 14, 15,
            // 2nd
            16, 17, 18,
            16, 18, 19,
            // 3rd
            20, 21, 22,
            20, 22, 23,
            // 4th
            24, 25, 26,
            24, 26, 27,
            // 5th
            28, 29, 30,
            28, 30, 31,
        )

        private val color = floatArrayOf(
            // Front
            1.0f, 0.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 0.0f, 1.0f,
            // Back
            0.0f, 1.0f, 0.0f, 1.0f,
            0.0f, 1.0f, 0.0f, 1.0f,
            0.0f, 1.0f, 0.0f, 1.0f,
            0.0f, 1.0f, 0.0f, 1.0f,
            0.0f, 1.0f, 0.0f, 1.0f,
            0.0f, 1.0f, 0.0f, 1.0f,
            // 1st
            0.0f, 0.0f, 1.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,
            // 2nd
            1.0f, 1.0f, 0.0f, 1.0f,
            1.0f, 1.0f, 0.0f, 1.0f,
            1.0f, 1.0f, 0.0f, 1.0f,
            1.0f, 1.0f, 0.0f, 1.0f,
            // 3rd
            1.0f, 0.0f, 1.0f, 1.0f,
            1.0f, 0.0f, 1.0f, 1.0f,
            1.0f, 0.0f, 1.0f, 1.0f,
            1.0f, 0.0f, 1.0f, 1.0f,
            // 4th
            0.0f, 1.0f, 1.0f, 1.0f,
            0.0f, 1.0f, 1.0f, 1.0f,
            0.0f, 1.0f, 1.0f, 1.0f,
            0.0f, 1.0f, 1.0f, 1.0f,
            // 5th
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
        )
    }

}
