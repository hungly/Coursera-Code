package com.bennyplo.capstone2_opengles.gl_object

import android.opengl.GLES32
import com.bennyplo.capstone2_opengles.MyRenderer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer
import kotlin.math.pow
import kotlin.math.sin

class Vase : GLObject() {

    private var noLayer = 0

    private val colorBuffer: FloatBuffer by lazy {
        ByteBuffer.allocateDirect(colors.size * COLORS_PER_VERTEX).apply {
            order(ByteOrder.nativeOrder())
        }.asFloatBuffer().apply {
            put(colors)
            position(0)
        }
    }

    private val colorStride by lazy {
        COLORS_PER_VERTEX * Float.SIZE_BYTES
    }

    private val colors by lazy {
        val colors = arrayListOf<Float>()

//        (0 until vertices.size / COORDS_PER_VERTEX).forEach {
//            colors[it * COLORS_PER_VERTEX] = 0.0F
//            colors[it * COLORS_PER_VERTEX + 1] = 0.0F
//            colors[it * COLORS_PER_VERTEX + 2] = 1.0F
//            colors[it * COLORS_PER_VERTEX + 3] = 1.0F
//        }

        var offsetIndex = 0
        val noCoordinatePerLayer = vertices.size / noLayer
        val noVertexPerLayer = noCoordinatePerLayer / COORDS_PER_VERTEX
        (0 until noLayer).forEach { layerIndex ->
            offsetIndex = layerIndex * noCoordinatePerLayer
            val (greenValue, blueValue) = (1.0F / noLayer * layerIndex).let {
                (1.0F - it) to it
            }
            repeat((0 until noVertexPerLayer).map { it * COLORS_PER_VERTEX }.size) {
                colors.add(0.0F)
                colors.add(greenValue)
                colors.add(blueValue)
                colors.add(1.0F)
            }
        }

        colors.toFloatArray()
    }

    private val indexBuffer: IntBuffer by lazy {
        IntBuffer.allocate(indexes.size).apply {
            put(indexes)
            position(0)
        }
    }

    private val indexes by lazy {
        val indexes = arrayListOf<Int>()

//        val layerIndex = 0
        val noVertexPerLayer = vertices.size / COORDS_PER_VERTEX / noLayer
//        (0 until noVertex - 1).forEach { vertex ->
//            indexes.add(vertex + layerIndex * noVertex)
//            indexes.add(vertex + layerIndex * noVertex + 1)
//        }
//        indexes.add(layerIndex * noVertex + noVertex - 1)
//        indexes.add(layerIndex * noVertex)

        (0 until noLayer - 1).forEach { layerIndex ->
            (0 until noVertexPerLayer - 1).forEach {
                indexes.add(layerIndex * noVertexPerLayer + it)
                indexes.add((layerIndex + 1) * noVertexPerLayer + it)
                indexes.add(layerIndex * noVertexPerLayer + it + 1)

                indexes.add(layerIndex * noVertexPerLayer + it + 1)
                indexes.add((layerIndex + 1) * noVertexPerLayer + it)
                indexes.add((layerIndex + 1) * noVertexPerLayer + it + 1)
            }

            indexes.add(layerIndex * noVertexPerLayer + noVertexPerLayer - 1) // 49
            indexes.add((layerIndex + 1) * noVertexPerLayer + noVertexPerLayer - 1) // 99
            indexes.add(layerIndex * noVertexPerLayer) // 0

            indexes.add(layerIndex * noVertexPerLayer) // 0
            indexes.add((layerIndex + 1) * noVertexPerLayer + noVertexPerLayer - 1) // 99
            indexes.add((layerIndex + 1) * noVertexPerLayer) // 50
        }

        indexes.toIntArray()
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

    private val vertices by lazy {
        val vertices = arrayListOf<Float>()
        val noSeg = VASE_BASE_COORDINATES_X.size / 3
        var centroidX = 0F
        var centroidY = 0F
        (VASE_BASE_COORDINATES_X.indices step 2).forEach {
            centroidX += VASE_BASE_COORDINATES_X[it]
            centroidY += VASE_BASE_COORDINATES_Y[it]
        }
        centroidX /= VASE_BASE_COORDINATES_X.size
        centroidY /= VASE_BASE_COORDINATES_Y.size
        var r = 1.0F
        (-10 until 10).apply {
            noLayer = count()
            map {
                it / 10.0F
            }.forEach { y ->
                var tIndex = 0
                r = sin(2 * y) + 1.8F
                repeat((0 until noSeg).count()) {
                    (0 until 10).map {
                        it / 10.0F
                    }.forEach { t ->
                        val x = (1 - t).pow(3) * VASE_BASE_COORDINATES_X[tIndex] +
                                3 * (1 - t).pow(2) * t * VASE_BASE_COORDINATES_X[tIndex + 1] +
                                3 * (1 - t) * t.pow(2) * VASE_BASE_COORDINATES_X[tIndex + 2] +
                                t.pow(3) * VASE_BASE_COORDINATES_X[tIndex + 3]
                        val z = (1 - t).pow(3) * VASE_BASE_COORDINATES_Y[tIndex] +
                                3 * (1 - t).pow(2) * t * VASE_BASE_COORDINATES_Y[tIndex + 1] +
                                3 * (1 - t) * t.pow(2) * VASE_BASE_COORDINATES_Y[tIndex + 2] +
                                t.pow(3) * VASE_BASE_COORDINATES_Y[tIndex + 3]
                        vertices.add((r * x - centroidX) * initialScale.first)
                        vertices.add((y * 3) * initialScale.second)
                        vertices.add((r * z - centroidY) * initialScale.third)
                    }
                    tIndex += 3
                }
            }
        }
        vertices.toFloatArray()
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
        GLES32.glDisable(GLES32.GL_CULL_FACE)

        // Set the attribute of the vertex to point to the vertex buffer
        GLES32.glLineWidth(4.0F)
        GLES32.glVertexAttribPointer(
            positionHandle,
            COORDS_PER_VERTEX,
            GLES32.GL_FLOAT,
            false,
            vertexStride,
            vertexBuffer
        )
        GLES32.glVertexAttribPointer(
            colorHandle,
            COLORS_PER_VERTEX,
            GLES32.GL_FLOAT,
            false,
            colorStride,
            colorBuffer
        )
        // Draw the graph
        GLES32.glDrawElements(
            GLES32.GL_TRIANGLES,
            indexes.size,
            GLES32.GL_UNSIGNED_INT,
            indexBuffer
        )
    }

    private fun getLayer(layerIndex: Int): FloatArray {
        val noCoordinatesPerLayer = vertices.size / noLayer
        return (layerIndex.takeIf { layerIndex in 0 until noLayer } ?: 0).let {
            val start = noCoordinatesPerLayer * it
            val end = start + noCoordinatesPerLayer
            vertices.sliceArray(start until end)
        }
    }

    companion object {
        // Number of coordinates per vertex in this array
        private const val COORDS_PER_VERTEX = 3
        private const val COLORS_PER_VERTEX = 4

        private val VASE_BASE_COORDINATES_X = floatArrayOf(
            2.0F,
            2.5F,
            2.2F,
            2.0F,
            0.0F,
            -3.5F,
            -1.5F,
            1.5F,
            2.5F,
            2.0F,
            3.5F,
            2.8F,
            2.0F,
            3.2F,
            2.5F,
            2.0F
        )
        private val VASE_BASE_COORDINATES_Y = floatArrayOf(
            3.5F,
            3.7F,
            4.0F,
            4.5F,
            4.0F,
            3.0F,
            -1.5F,
            -1.0F,
            0.0F,
            0.5F,
            0.8F,
            1.3F,
            2.0F,
            2.5F,
            3.0F,
            3.5F
        )
    }

}