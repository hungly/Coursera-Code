package com.bennyplo.capstone2_opengles.gl_object

import android.opengl.GLES32
import com.bennyplo.capstone2_opengles.MyRenderer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer
import kotlin.math.abs
import kotlin.random.Random

class Picture : GLObject() {

    private var bgColorIndex = 0
    private var patternColorIndex = 0
    private var pattern: Pair<FloatArray, IntArray>? = null

    private val backgroundColorBuffer: FloatBuffer by lazy {
        ByteBuffer.allocateDirect(backgroundColors.size * COLORS_PER_VERTEX).apply {
            order(ByteOrder.nativeOrder())
        }.asFloatBuffer().apply {
            put(backgroundColors)
            position(0)
        }
    }

    private val backgroundColors by lazy {
        val colors = arrayListOf<Float>()

        bgColorIndex = Random.nextInt(0, COLORS.size)
        val color = COLORS[bgColorIndex]
        repeat((0 until backgroundVertices.size / COORDS_PER_VERTEX).count()) {
            colors.addAll(color.toList())
        }

        colors.toFloatArray()
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
            0.00001F * initialScale.second,
            -3.0F * initialScale.third, // 0
            -3.0F * initialScale.first,
            0.00001F * initialScale.second,
            3.0F * initialScale.third, // 1
            3.0F * initialScale.first,
            0.00001F * initialScale.second,
            3.0F * initialScale.third, // 2
            3.0F * initialScale.first,
            0.00001F * initialScale.second,
            -3.0F * initialScale.third // 3
        )
    }

    private val colorStride by lazy {
        COLORS_PER_VERTEX * Float.SIZE_BYTES
    }

    private val patternColor by lazy {
        val colors = arrayListOf<Float>()

        patternColorIndex = Random.nextInt(0, COLORS.size)
        while (patternColorIndex == bgColorIndex) {
            patternColorIndex = Random.nextInt(0, COLORS.size)
        }
        val color = COLORS[patternColorIndex]
        repeat((0 until backgroundVertices.size / COORDS_PER_VERTEX).count()) {
            colors.addAll(color.toList())
        }

        colors.toFloatArray()
    }

    private val patternColorBuffer: FloatBuffer by lazy {
        ByteBuffer.allocateDirect(patternColor.size * COLORS_PER_VERTEX).apply {
            order(ByteOrder.nativeOrder())
        }.asFloatBuffer().apply {
            put(patternColor)
            position(0)
        }
    }

    private val patternIndexBuffer: IntBuffer by lazy {
        IntBuffer.allocate(patternIndexes.size).apply {
            put(patternIndexes)
            position(0)
        }
    }

    private val patternIndexes by lazy {
        if (pattern == null) pattern = getPattern()
        pattern?.second ?: intArrayOf()
    }

    private val patternVertexBuffer: FloatBuffer by lazy {
        // (# of coordinate values * 4 bytes per float)
        ByteBuffer.allocateDirect(patternVertices.size * 4).apply {
            order(ByteOrder.nativeOrder())
        }.asFloatBuffer().apply {
            put(patternVertices)
            position(0)
        }
    }

    private val patternVertices by lazy {
        if (pattern == null) pattern = getPattern()
        pattern?.first ?: floatArrayOf()
    }

    private fun getPattern(): Pair<FloatArray, IntArray> {
        return when (Random.nextInt(0, PATTERNS.size)) {
            PATTERN_1 -> getDiamondPattern()
            PATTERN_2 -> getSquarePattern()
            PATTERN_3 -> getTrianglePattern()
            else -> getSquarePattern()
        }
    }

    private fun getDiamondPattern(): Pair<FloatArray, IntArray> {
        val vertices = arrayListOf<Float>()

        val midHorizontal = (backgroundVertices[0] + backgroundVertices[6]) / 2
        val midVertical = (backgroundVertices[2] + backgroundVertices[5]) / 2

        vertices.add(backgroundVertices[0])
        vertices.add(backgroundVertices[1] - 0.00002F)
        vertices.add(midVertical)

        vertices.add(midHorizontal)
        vertices.add(backgroundVertices[1] - 0.00002F)
        vertices.add(backgroundVertices[5])

        vertices.add(backgroundVertices[6])
        vertices.add(backgroundVertices[1] - 0.00002F)
        vertices.add(midVertical)

        vertices.add(midHorizontal)
        vertices.add(backgroundVertices[1] - 0.00002F)
        vertices.add(backgroundVertices[2])

        return vertices.toFloatArray() to intArrayOf(0, 1, 2, 0, 2, 3)
    }


    private fun getSquarePattern(): Pair<FloatArray, IntArray> {
        val vertices = arrayListOf<Float>()

        val patternBottom =
            backgroundVertices[0] + (abs(backgroundVertices[0] - backgroundVertices[6]) / 4F)
        val patternTop =
            backgroundVertices[6] - (abs(backgroundVertices[0] - backgroundVertices[6]) / 4F)

        vertices.add(backgroundVertices[0])
        vertices.add(backgroundVertices[1] - 0.00002F)
        vertices.add(patternBottom)

        vertices.add(backgroundVertices[3])
        vertices.add(backgroundVertices[1] - 0.00002F)
        vertices.add(patternTop)

        vertices.add(backgroundVertices[6])
        vertices.add(backgroundVertices[1] - 0.00002F)
        vertices.add(patternTop)

        vertices.add(backgroundVertices[9])
        vertices.add(backgroundVertices[1] - 0.00002F)
        vertices.add(patternBottom)

        return vertices.toFloatArray() to intArrayOf(0, 1, 2, 0, 2, 3)
    }

    private fun getTrianglePattern(): Pair<FloatArray, IntArray> {
        val vertices = arrayListOf<Float>()

        vertices.add(backgroundVertices[0])
        vertices.add(backgroundVertices[1] - 0.00002F)
        vertices.add(backgroundVertices[2])

        vertices.add(backgroundVertices[3])
        vertices.add(backgroundVertices[4] - 0.00002F)
        vertices.add(backgroundVertices[5])

        vertices.add(backgroundVertices[6])
        vertices.add(backgroundVertices[7] - 0.00002F)
        vertices.add(backgroundVertices[8])

        return vertices.toFloatArray() to intArrayOf(0, 1, 2)
    }

    private val program: Int by lazy {
        GLES32.glCreateProgram()
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

        GLES32.glDisable(GLES32.GL_BLEND)
        GLES32.glEnable(GLES32.GL_CULL_FACE)
        GLES32.glCullFace(GLES32.GL_FRONT)
        GLES32.glFrontFace(GLES32.GL_CCW)

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
            patternVertexBuffer
        )
        GLES32.glVertexAttribPointer(
            colorHandle,
            COLORS_PER_VERTEX,
            GLES32.GL_FLOAT,
            false,
            colorStride,
            patternColorBuffer
        )
        // Draw the graph
        GLES32.glDrawElements(
            GLES32.GL_TRIANGLES,
            patternIndexes.size,
            GLES32.GL_UNSIGNED_INT,
            patternIndexBuffer
        )
    }

    companion object {
        // Number of coordinates per vertex in this array
        private const val COORDS_PER_VERTEX = 3
        private const val COLORS_PER_VERTEX = 4

        private val BACKGROUND_INDEXES = intArrayOf(
            0, 1, 2, 0, 2, 3
        )

        private val COLORS = arrayOf(
            floatArrayOf(1.0F, 0.0F, 0.0F, 1.0F),
            floatArrayOf(0.0F, 1.0F, 0.0F, 1.0F),
            floatArrayOf(0.0F, 0.0F, 1.0F, 1.0F),
            floatArrayOf(1.0F, 1.0F, 0.0F, 1.0F),
            floatArrayOf(0.0F, 1.0F, 1.0F, 1.0F),
            floatArrayOf(1.0F, 0.0F, 1.0F, 1.0F),
            floatArrayOf(1.0F, 0.5F, 0.5F, 1.0F),
            floatArrayOf(0.5F, 1.0F, 0.5F, 1.0F),
            floatArrayOf(0.5F, 0.5F, 1.0F, 1.0F),
            floatArrayOf(1.0F, 1.0F, 0.5F, 1.0F),
            floatArrayOf(0.5F, 1.0F, 1.0F, 1.0F),
            floatArrayOf(1.0F, 0.5F, 1.0F, 1.0F),
            floatArrayOf(1.0F, 1.0F, 1.0F, 1.0F),
        )

        private const val PATTERN_1 = 0
        private const val PATTERN_2 = 1
        private const val PATTERN_3 = 2

        private val PATTERNS = intArrayOf(
            PATTERN_1,
            PATTERN_2,
            PATTERN_3
        )
    }

}