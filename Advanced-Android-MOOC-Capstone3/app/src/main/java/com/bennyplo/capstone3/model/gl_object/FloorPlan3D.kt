package com.bennyplo.capstone3.model.gl_object

import android.opengl.GLES32
import com.bennyplo.capstone3.MyRenderer.Companion.checkGlError
import com.bennyplo.capstone3.model.GLObject
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer

class FloorPlan3D : GLObject() {

    private val _colorBuffer: FloatBuffer by lazy {
        ByteBuffer.allocateDirect(floorColors.size * COLORS_PER_VERTEX).apply {
            order(ByteOrder.nativeOrder())
        }.asFloatBuffer().apply {
            put(floorColors)
            position(0)
        }
    }

    private val _indexBuffer: IntBuffer by lazy {
        IntBuffer.allocate(INDEXES.size).apply {
            put(INDEXES)
            position(0)
        }
    }

    private val _mVPMatrixHandle: Int by lazy {
        // Get handle to shape's transformation matrix
        GLES32.glGetUniformLocation(program, "uMVPMatrix")
    }

    private val _vertexBuffer: FloatBuffer by lazy {
        // (# of coordinate values * 4 bytes per float)
        ByteBuffer.allocateDirect(VERTICES.size * Float.SIZE_BYTES).apply {
            order(ByteOrder.nativeOrder())
        }.asFloatBuffer().apply {
            put(VERTICES)
            position(0)
        }
    }

    private val _vertexCount // number of vertices
            : Int by lazy {
        VERTICES.size / COORDINATES_PER_VERTEX
    }

    private val floorColors by lazy {
        val numberOfVertices = VERTICES.size / COORDINATES_PER_VERTEX
        val colors = FloatArray(numberOfVertices * COLORS_PER_VERTEX)
        (0 until numberOfVertices).forEach {
            when {
                // Outer walls color
                (it in 0..7) || (it in 28..35) -> {
                    colors[it * COLORS_PER_VERTEX] = 0.4F
                    colors[it * COLORS_PER_VERTEX + 1] = 0.4F
                    colors[it * COLORS_PER_VERTEX + 2] = 0.4F
                    colors[it * COLORS_PER_VERTEX + 3] = 1.0F
                }

                // Floor colors
                it >= numberOfVertices - 4 -> {
                    colors[it * COLORS_PER_VERTEX] = 0.2F
                    colors[it * COLORS_PER_VERTEX + 1] = 0.2F
                    colors[it * COLORS_PER_VERTEX + 2] = 0.2F
                    colors[it * COLORS_PER_VERTEX + 3] = 1.0F
                }

                // Inner walls color
                else -> {
                    colors[it * COLORS_PER_VERTEX] = 0.6F
                    colors[it * COLORS_PER_VERTEX + 1] = 0.6F
                    colors[it * COLORS_PER_VERTEX + 2] = 0.6F
                    colors[it * COLORS_PER_VERTEX + 3] = 1.0F
                }
            }
        }
        colors
    }
    override var textureRatio: Float = 0.0F

    override val textureImageHandler: Int? = null

    override fun draw(mvpMatrix: FloatArray?) {
        super.draw(mvpMatrix)

        // Apply the projection and view transformation
        GLES32.glUniformMatrix4fv(_mVPMatrixHandle, 1, false, mvpMatrix, 0)
        checkGlError("glUniformMatrix4fv")

        // Set the attribute of the vertex to point to the vertex buffer
        GLES32.glVertexAttribPointer(
            positionHandle,
            COORDINATES_PER_VERTEX,
            GLES32.GL_FLOAT,
            false,
            VERTEX_STRIDE,
            _vertexBuffer
        )
        GLES32.glVertexAttribPointer(
            colorHandle,
            COORDINATES_PER_VERTEX,
            GLES32.GL_FLOAT,
            false,
            COLOR_STRIDE,
            _colorBuffer
        )

        // Draw the floor plan
//        GLES32.glDrawArrays(GLES32.GL_TRIANGLES, 0, _vertexCount)
        GLES32.glDrawElements(
            GLES32.GL_TRIANGLES,
            INDEXES.size,
            GLES32.GL_UNSIGNED_INT,
            _indexBuffer
        )
    }

    companion object {

        private var VERTICES = floatArrayOf(
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
            // Floor
            -3.0F, -3.0F, -1.0F, // 56
            -3.0F, 3.0F, -1.0F, // 57
            3.0F, 3.0F, -1.0F, // 58
            3.0F, -3.0F, -1.0F, // 59
        )

        private val INDEXES = intArrayOf(
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
            26, 27, 54, 27, 55, 54,
            56, 57, 58, 56, 58, 59
        )
    }

}