package com.bennyplo.capstone2_opengles.gl_object

import android.opengl.GLES32
import com.bennyplo.capstone2_opengles.MyRenderer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer
import kotlin.math.abs

class ECG : GLObject() {

    private val backgroundColorBuffer: FloatBuffer by lazy {
        ByteBuffer.allocateDirect(BACKGROUND_COLORS.size * COLORS_PER_VERTEX).apply {
            order(ByteOrder.nativeOrder())
        }.asFloatBuffer().apply {
            put(BACKGROUND_COLORS)
            position(0)
        }
    }

    private val backgroundIndexBuffer: IntBuffer by lazy {
        IntBuffer.allocate(BACKGROUND_INDEXES.size).apply {
            put(BACKGROUND_INDEXES)
            position(0)
        }
    }

    private val backgroundVertexBuffer: FloatBuffer by lazy {
        // (# of coordinate values * 4 bytes per float)
        ByteBuffer.allocateDirect(backgroundVertices.size * 4).apply {
            order(ByteOrder.nativeOrder())
        }.asFloatBuffer().apply {
            put(backgroundVertices)
            position(0)
        }
    }

    private val backgroundVertices by lazy {
        floatArrayOf(
            -3.0F * initialScale.first,
            -3.0F * initialScale.second,
            0.00001F * initialScale.third, // 0
            -3.0F * initialScale.first,
            3.0F * initialScale.second,
            0.00001F * initialScale.third, // 1
            3.0F * initialScale.first,
            3.0F * initialScale.second,
            0.00001F * initialScale.third, // 2
            3.0F * initialScale.first,
            -3.0F * initialScale.second,
            0.00001F * initialScale.third // 3
        )
    }

    private val colorStride by lazy {
        COLORS_PER_VERTEX * Float.SIZE_BYTES
    }

    private val graphColor by lazy {
        val colors = FloatArray(graphVertices.size / COORDS_PER_VERTEX * COLORS_PER_VERTEX)
        (0 until graphVertices.size / COORDS_PER_VERTEX).forEach {
            colors[it * COLORS_PER_VERTEX] = 0.0F
            colors[it * COLORS_PER_VERTEX + 1] = 0.0F
            colors[it * COLORS_PER_VERTEX + 2] = 1.0F
            colors[it * COLORS_PER_VERTEX + 3] = 1.0F
        }
        colors
    }

    private val graphColorBuffer: FloatBuffer by lazy {
        ByteBuffer.allocateDirect(graphColor.size * COLORS_PER_VERTEX).apply {
            order(ByteOrder.nativeOrder())
        }.asFloatBuffer().apply {
            put(graphColor)
            position(0)
        }
    }

    private val graphIndexBuffer: IntBuffer by lazy {
        IntBuffer.allocate(graphIndexes.size).apply {
            put(graphIndexes)
            position(0)
        }
    }

    private val graphIndexes by lazy {
        val vertexCount = graphVertices.size / COORDS_PER_VERTEX
        val indexes = IntArray(vertexCount * 2)
        (0 until vertexCount - 1).forEach {
            indexes[it * 2] = it
            indexes[it * 2 + 1] = it + 1
        }
        indexes
    }

    private val graphVertexBuffer: FloatBuffer by lazy {
        // (# of coordinate values * 4 bytes per float)
        ByteBuffer.allocateDirect(graphVertices.size * 4).apply {
            order(ByteOrder.nativeOrder())
        }.asFloatBuffer().apply {
            put(graphVertices)
            position(0)
        }
    }

    private val graphVertices by lazy {
        val dataPointCount = Constant.ECG_DATA.size
        val min = ((Constant.ECG_DATA.min() / 100 - 1) * 100)
        val range = ((Constant.ECG_DATA.max() / 100 + 3) * 100) - min
        val width = abs(backgroundVertices[0] - backgroundVertices[3 * COORDS_PER_VERTEX])
        val height = abs(backgroundVertices[1] - backgroundVertices[1 * COORDS_PER_VERTEX + 1])
        val ratio = height / range
        val xIncrement = width / (dataPointCount - 1)
        val data = FloatArray(dataPointCount * COORDS_PER_VERTEX)
        Constant.ECG_DATA.forEachIndexed { index, value ->
            data[index * COORDS_PER_VERTEX] = backgroundVertices[0] + index * xIncrement
            data[index * COORDS_PER_VERTEX + 1] = (value - min - range / 2) * ratio
            data[index * COORDS_PER_VERTEX + 2] = backgroundVertices[2] + 0.00001F
        }
        data
    }

    private val program: Int by lazy {
        GLES32.glCreateProgram()
    }

    // Number of vertices
    private val vertexCount: Int by lazy {
        backgroundVertices.size / COORDS_PER_VERTEX
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
        // Set the attribute of the vertex to point to the vertex buffer
        GLES32.glVertexAttribPointer(
            positionHandle,
            COORDS_PER_VERTEX,
            GLES32.GL_FLOAT,
            false,
            vertexStride,
            backgroundVertexBuffer
        )
        GLES32.glVertexAttribPointer(
            colorHandle,
            COLORS_PER_VERTEX,
            GLES32.GL_FLOAT,
            false,
            colorStride,
            backgroundColorBuffer
        )
        // Draw the background
        GLES32.glDrawElements(
            GLES32.GL_TRIANGLES,
            BACKGROUND_INDEXES.size,
            GLES32.GL_UNSIGNED_INT,
            backgroundIndexBuffer
        )

        GLES32.glLineWidth(4.0F)
        GLES32.glVertexAttribPointer(
            positionHandle,
            COORDS_PER_VERTEX,
            GLES32.GL_FLOAT,
            false,
            vertexStride,
            graphVertexBuffer
        )
        GLES32.glVertexAttribPointer(
            colorHandle,
            COLORS_PER_VERTEX,
            GLES32.GL_FLOAT,
            false,
            colorStride,
            graphColorBuffer
        )
        // Draw the graph
        GLES32.glDrawElements(
            GLES32.GL_LINES,
            graphIndexes.size,
            GLES32.GL_UNSIGNED_INT,
            graphIndexBuffer
        )
    }

    companion object {
        // Number of coordinates per vertex in this array
        private const val COORDS_PER_VERTEX = 3
        private const val COLORS_PER_VERTEX = 4

        private val BACKGROUND_COLORS = floatArrayOf(
            1.0F, 1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F, 1.0F
        )

        private val BACKGROUND_INDEXES = intArrayOf(
            0, 1, 2, 0, 2, 3
        )
    }

}