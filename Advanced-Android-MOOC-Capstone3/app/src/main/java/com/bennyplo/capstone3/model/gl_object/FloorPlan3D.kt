package com.bennyplo.capstone3.model.gl_object

import android.content.Context
import android.opengl.GLES32
import com.bennyplo.capstone3.MyRenderer.Companion.checkGlError
import com.bennyplo.capstone3.R
import com.bennyplo.capstone3.model.GLObject
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer

class FloorPlan3D(context: Context?, val doorWidth: Float, val wallThickness: Float) :
    GLObject() {

    private val _exteriorWallsColorBuffer: FloatBuffer by lazy {
        ByteBuffer.allocateDirect(exteriorWallColors.size * COLORS_PER_VERTEX).apply {
            order(ByteOrder.nativeOrder())
        }.asFloatBuffer().apply {
            put(exteriorWallColors)
            position(0)
        }
    }

    private val _exteriorWallsIndexBuffer: IntBuffer by lazy {
        IntBuffer.allocate(EXTERIOR_WALLS_INDEXES.size).apply {
            put(EXTERIOR_WALLS_INDEXES)
            position(0)
        }
    }

    private val _exteriorWallsTextureBuffer: FloatBuffer by lazy {
        ByteBuffer.allocateDirect(TEXTURE_COORDINATE_DATA.size * Float.SIZE_BYTES).apply {
            order(ByteOrder.nativeOrder())
        }.asFloatBuffer().apply {
            put(TEXTURE_COORDINATE_DATA)
            position(0)
        }
    }

    private val _exteriorWallsVertexBuffer: FloatBuffer by lazy {
        // (# of coordinate values * 4 bytes per float)
        ByteBuffer.allocateDirect(EXTERIOR_WALLS_VERTICES.size * Float.SIZE_BYTES).apply {
            order(ByteOrder.nativeOrder())
        }.asFloatBuffer().apply {
            put(EXTERIOR_WALLS_VERTICES)
            position(0)
        }
    }

    private val _floorColorBuffer: FloatBuffer by lazy {
        ByteBuffer.allocateDirect(floorColors.size * COLORS_PER_VERTEX).apply {
            order(ByteOrder.nativeOrder())
        }.asFloatBuffer().apply {
            put(floorColors)
            position(0)
        }
    }

    private val _floorIndexBuffer: IntBuffer by lazy {
        IntBuffer.allocate(FLOOR_INDEXES.size).apply {
            put(FLOOR_INDEXES)
            position(0)
        }
    }

    private val _floorTextureBuffer: FloatBuffer by lazy {
        ByteBuffer.allocateDirect(FLOOR_TEXTURE_COORDINATE_DATA.size * Float.SIZE_BYTES).apply {
            order(ByteOrder.nativeOrder())
        }.asFloatBuffer().apply {
            put(FLOOR_TEXTURE_COORDINATE_DATA)
            position(0)
        }
    }

    private val _floorVertexBuffer: FloatBuffer by lazy {
        // (# of coordinate values * 4 bytes per float)
        ByteBuffer.allocateDirect(FLOOR_VERTICES.size * Float.SIZE_BYTES).apply {
            order(ByteOrder.nativeOrder())
        }.asFloatBuffer().apply {
            put(FLOOR_VERTICES)
            position(0)
        }
    }

    private val _interiorWallVertexBuffer: FloatBuffer by lazy {
        // (# of coordinate values * 4 bytes per float)
        ByteBuffer.allocateDirect(interiorWallsVertices.size * Float.SIZE_BYTES).apply {
            order(ByteOrder.nativeOrder())
        }.asFloatBuffer().apply {
            put(interiorWallsVertices)
            position(0)
        }
    }

    private val _interiorWallsColorBuffer: FloatBuffer by lazy {
        ByteBuffer.allocateDirect(interiorWallColors.size * COLORS_PER_VERTEX).apply {
            order(ByteOrder.nativeOrder())
        }.asFloatBuffer().apply {
            put(interiorWallColors)
            position(0)
        }
    }

    private val _interiorWallsIndexBuffer: IntBuffer by lazy {
        IntBuffer.allocate(INTERIOR_WALLS_INDEXES.size).apply {
            put(INTERIOR_WALLS_INDEXES)
            position(0)
        }
    }

    private val _interiorWallsTextureBuffer: FloatBuffer by lazy {
        ByteBuffer.allocateDirect(TEXTURE_COORDINATE_DATA.size * Float.SIZE_BYTES).apply {
            order(ByteOrder.nativeOrder())
        }.asFloatBuffer().apply {
            put(TEXTURE_COORDINATE_DATA)
            position(0)
        }
    }

    private val _mVPMatrixHandle: Int by lazy {
        // Get handle to shape's transformation matrix
        GLES32.glGetUniformLocation(program, "uMVPMatrix")
    }

    private val exteriorWallColors by lazy {
        val numberOfVertices = EXTERIOR_WALLS_VERTICES.size / COORDINATES_PER_VERTEX
        val colors = arrayListOf<Float>()
        repeat((0 until numberOfVertices).count()) {
            colors.add(0.4F)
            colors.add(0.4F)
            colors.add(0.4F)
            colors.add(1.0F)
        }
        colors.toFloatArray()
    }

    private val exteriorWallsTextureImageHandler by lazy {
        loadTextureFromResource(R.drawable.exterior_walls, context)
    }

    private val floorColors by lazy {
        val numberOfVertices = FLOOR_VERTICES.size / COORDINATES_PER_VERTEX
        val colors = arrayListOf<Float>()
        repeat((0 until numberOfVertices).count()) {
            colors.add(0.2F)
            colors.add(0.2F)
            colors.add(0.2F)
            colors.add(1.0F)
        }
        colors.toFloatArray()
    }

    private val floorTextureImageHandler by lazy {
        loadTextureFromResource(R.drawable.floor, context)
    }

    private val interiorWallColors by lazy {
        val numberOfVertices = interiorWallsVertices.size / COORDINATES_PER_VERTEX
        val colors = arrayListOf<Float>()
        repeat((0 until numberOfVertices).count()) {
            colors.add(0.6F)
            colors.add(0.6F)
            colors.add(0.6F)
            colors.add(1.0F)
        }
        colors.toFloatArray()
    }

    private val interiorWallsTextureImageHandler by lazy {
        loadTextureFromResource(R.drawable.interior_walls, context)
    }

    private val interiorWallsVertices by lazy {
        val offsetAmount = wallThickness / 2.0F
        floatArrayOf(
            // Vertical - Left - Bottom
            -1.0F - offsetAmount, -3.0F, -1.0F, // 0
            -1.0F - offsetAmount, -3.0F, 1.0F, // 1
            -1.0F - offsetAmount, -1.0F - doorWidth, 1.0F, // 2
            -1.0F - offsetAmount, -1.0F - doorWidth, -1.0F, // 3
            -1.0F + offsetAmount, -3.0F, -1.0F, // 4
            -1.0F + offsetAmount, -3.0F, 1.0F, // 5
            -1.0F + offsetAmount, -1.0F - doorWidth, 1.0F, // 6
            -1.0F + offsetAmount, -1.0F - doorWidth, -1.0F, // 7

            // Horizontal - Left - Bottom
            -3.0F, -1.0F - offsetAmount, -1.0F, // 8
            -3.0F, -1.0F - offsetAmount, 1.0F, // 9
            -3.0F, -1.0F + offsetAmount, 1.0F, // 10
            -3.0F, -1.0F + offsetAmount, -1.0F, // 11
            -1.0F + offsetAmount, -1.0F - offsetAmount, -1.0F, // 12
            -1.0F + offsetAmount, -1.0F - offsetAmount, 1.0F, // 13
            -1.0F + offsetAmount, -1.0F + offsetAmount, 1.0F, // 14
            -1.0F + offsetAmount, -1.0F + offsetAmount, -1.0F, // 15

            // Vertical - Left - Middle
            -1.0F - offsetAmount, -1.0F + doorWidth, -1.0F, // 16
            -1.0F - offsetAmount, -1.0F + doorWidth, 1.0F, // 17
            -1.0F - offsetAmount, 1.0F - doorWidth, 1.0F, // 18
            -1.0F - offsetAmount, 1.0F - doorWidth, -1.0F, // 19
            -1.0F + offsetAmount, -1.0F + doorWidth, -1.0F, // 20
            -1.0F + offsetAmount, -1.0F + doorWidth, 1.0F, // 21
            -1.0F + offsetAmount, 1.0F - doorWidth, 1.0F, // 22
            -1.0F + offsetAmount, 1.0F - doorWidth, -1.0F, // 23

            // Horizontal - Left - Top
            -3.0F, 1.0F - offsetAmount, -1.0F, // 24
            -3.0F, 1.0F - offsetAmount, 1.0F, // 25
            -3.0F, 1.0F + offsetAmount, 1.0F, // 26
            -3.0F, 1.0F + offsetAmount, -1.0F, // 27
            -1.0F + offsetAmount, 1.0F - offsetAmount, -1.0F, // 28
            -1.0F + offsetAmount, 1.0F - offsetAmount, 1.0F, // 29
            -1.0F + offsetAmount, 1.0F + offsetAmount, 1.0F, // 30
            -1.0F + offsetAmount, 1.0F + offsetAmount, -1.0F, // 31

            // Vertical - Left - Top
            -1.0F - offsetAmount, 1.0F + doorWidth, -1.0F, // 32
            -1.0F - offsetAmount, 1.0F + doorWidth, 1.0F, // 33
            -1.0F - offsetAmount, 3.0F, 1.0F, // 34
            -1.0F - offsetAmount, 3.0F, -1.0F, // 35
            -1.0F + offsetAmount, 1.0F + doorWidth, -1.0F, // 36
            -1.0F + offsetAmount, 1.0F + doorWidth, 1.0F, // 37
            -1.0F + offsetAmount, 3.0F, 1.0F, // 38
            -1.0F + offsetAmount, 3.0F, -1.0F, // 39

            // Vertical - Right - Top
            1.0F - offsetAmount, 1.0F + doorWidth, -1.0F, // 40
            1.0F - offsetAmount, 1.0F + doorWidth, 1.0F, // 41
            1.0F - offsetAmount, 3.0F, 1.0F, // 42
            1.0F - offsetAmount, 3.0F, -1.0F, // 43
            1.0F + offsetAmount, 1.0F + doorWidth, -1.0F, // 44
            1.0F + offsetAmount, 1.0F + doorWidth, 1.0F, // 45
            1.0F + offsetAmount, 3.0F, 1.0F, // 46
            1.0F + offsetAmount, 3.0F, -1.0F, // 47

            // Horizontal - Right - Top
            1.0F - offsetAmount, 1.0F - offsetAmount, -1.0F, // 48
            1.0F - offsetAmount, 1.0F - offsetAmount, 1.0F, // 49
            1.0F - offsetAmount, 1.0F + offsetAmount, 1.0F, // 50
            1.0F - offsetAmount, 1.0F + offsetAmount, -1.0F, // 51
            3.0F, 1.0F - offsetAmount, -1.0F, // 52
            3.0F, 1.0F - offsetAmount, 1.0F, // 53
            3.0F, 1.0F + offsetAmount, 1.0F, // 54
            3.0F, 1.0F + offsetAmount, -1.0F, // 55

            // Vertical - Right - Middle
            1.0F - offsetAmount, -1.0F + doorWidth, -1.0F, // 56
            1.0F - offsetAmount, -1.0F + doorWidth, 1.0F, // 57
            1.0F - offsetAmount, 1.0F - doorWidth, 1.0F, // 58
            1.0F - offsetAmount, 1.0F - doorWidth, -1.0F, // 59
            1.0F + offsetAmount, -1.0F + doorWidth, -1.0F, // 60
            1.0F + offsetAmount, -1.0F + doorWidth, 1.0F, // 61
            1.0F + offsetAmount, 1.0F - doorWidth, 1.0F, // 62
            1.0F + offsetAmount, 1.0F - doorWidth, -1.0F, // 63

            // Horizontal - Right - Bottom
            1.0F - offsetAmount, -1.0F - offsetAmount, -1.0F, // 64
            1.0F - offsetAmount, -1.0F - offsetAmount, 1.0F, // 65
            1.0F - offsetAmount, -1.0F + offsetAmount, 1.0F, // 66
            1.0F - offsetAmount, -1.0F + offsetAmount, -1.0F, // 67
            3.0F, -1.0F - offsetAmount, -1.0F, // 68
            3.0F, -1.0F - offsetAmount, 1.0F, // 69
            3.0F, -1.0F + offsetAmount, 1.0F, // 70
            3.0F, -1.0F + offsetAmount, -1.0F, // 71

            // Vertical - Right - Bottom
            1.0F - offsetAmount, -3.0F, -1.0F, // 72
            1.0F - offsetAmount, -3.0F, 1.0F, // 73
            1.0F - offsetAmount, -1.0F - doorWidth, 1.0F, // 74
            1.0F - offsetAmount, -1.0F - doorWidth, -1.0F, // 75
            1.0F + offsetAmount, -3.0F, -1.0F, // 76
            1.0F + offsetAmount, -3.0F, 1.0F, // 77
            1.0F + offsetAmount, -1.0F - doorWidth, 1.0F, // 78
            1.0F + offsetAmount, -1.0F - doorWidth, -1.0F, // 79
        )
    }

    override var textureRatio: Float = 0.0F

    override val textureImageHandler: Int? = null

    override fun draw(mvpMatrix: FloatArray?) {
        super.draw(mvpMatrix)

        // Apply the projection and view transformation
        GLES32.glUniformMatrix4fv(_mVPMatrixHandle, 1, false, mvpMatrix, 0)
        checkGlError("glUniformMatrix4fv")

        draw(
            vertexBuffer = _floorVertexBuffer,
            colorBuffer = _floorColorBuffer,
            indexBuffer = _floorIndexBuffer,
            indexSize = FLOOR_INDEXES.size,
            textureBuffer = _floorTextureBuffer,
            textureImageHandler = floorTextureImageHandler,
            useTexture = 1
        )
        draw(
            vertexBuffer = _exteriorWallsVertexBuffer,
            colorBuffer = _exteriorWallsColorBuffer,
            indexBuffer = _exteriorWallsIndexBuffer,
            indexSize = EXTERIOR_WALLS_INDEXES.size,
            textureBuffer = _exteriorWallsTextureBuffer,
            textureImageHandler = exteriorWallsTextureImageHandler,
            useTexture = 0
        )

        GLES32.glEnable(GLES32.GL_CULL_FACE)
        GLES32.glCullFace(GLES32.GL_FRONT)
        GLES32.glFrontFace(GLES32.GL_CW)

        draw(
            vertexBuffer = _interiorWallVertexBuffer,
            colorBuffer = _interiorWallsColorBuffer,
            indexBuffer = _interiorWallsIndexBuffer,
            indexSize = INTERIOR_WALLS_INDEXES.size,
            textureBuffer = _interiorWallsTextureBuffer,
            textureImageHandler = interiorWallsTextureImageHandler,
            useTexture = 0
        )
    }

    private fun draw(
        vertexBuffer: FloatBuffer,
        colorBuffer: FloatBuffer,
        indexBuffer: IntBuffer,
        indexSize: Int,
        textureBuffer: FloatBuffer,
        textureImageHandler: Int,
        useTexture: Int
    ) {
        // Set the attribute of the vertex to point to the vertex buffer
        GLES32.glVertexAttribPointer(
            positionHandle,
            COORDINATES_PER_VERTEX,
            GLES32.GL_FLOAT,
            false,
            VERTEX_STRIDE,
            vertexBuffer
        )
        GLES32.glVertexAttribPointer(
            colorHandle,
            COORDINATES_PER_VERTEX,
            GLES32.GL_FLOAT,
            false,
            COLOR_STRIDE,
            colorBuffer
        )

        // Set up texture
        GLES32.glVertexAttribPointer(
            textureCoordinateHandle,
            TEXTURE_COORDINATES_PER_VERTEX,
            GLES32.GL_FLOAT,
            false,
            TEXTURE_STRIDE,
            textureBuffer
        )
        GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, textureImageHandler)
        GLES32.glUniform1i(textureSamplerHandle, 0)
        GLES32.glUniform1i(useTextureHandle, useTexture)

        GLES32.glDrawElements(
            GLES32.GL_TRIANGLES,
            indexSize,
            GLES32.GL_UNSIGNED_INT,
            indexBuffer
        )
    }

    companion object {

        private val FLOOR_VERTICES = floatArrayOf(
            -3.0F, -3.0F, -1.0F, // 0
            -3.0F, 3.0F, -1.0F, // 1
            3.0F, 3.0F, -1.0F, // 2
            3.0F, -3.0F, -1.0F, // 3
        )

        private val FLOOR_INDEXES = intArrayOf(
            0, 1, 2, 0, 2, 3
        )

        private val FLOOR_TEXTURE_COORDINATE_DATA = floatArrayOf(
            0.0F, 4.0F,
            0.0F, 0.0F,
            4.0F, 0.0F,
            4.0F, 4.0F,
        )

        private val EXTERIOR_WALLS_VERTICES = floatArrayOf(
            // Left
            -3.0F, -3.0F, -1.0F, // 0
            -3.0F, -3.0F, 1.0F, // 1
            -3.0F, 3.0F, 1.0F, // 2
            -3.0F, 3.0F, -1.0F, // 3
            // Top
            -3.0F, 3.0F, -1.0F, // 4
            -3.0F, 3.0F, 1.0F, // 5
            3.0F, 3.0F, 1.0F, // 6
            3.0F, 3.0F, -1.0F, // 7
            // Right
            3.0F, 3.0F, -1.0F, // 8
            3.0F, 3.0F, 1.0F, // 9
            3.0F, -3.0F, 1.0F, // 10
            3.0F, -3.0F, -1.0F, // 11
            // Bottom
            -3.0F, -3.0F, -1.0F, // 12
            -3.0F, -3.0F, 1.0F, // 13
            3.0F, -3.0F, 1.0F, // 14
            3.0F, -3.0F, -1.0F, // 15
        )

        private val EXTERIOR_WALLS_INDEXES = intArrayOf(
            0, 1, 2, 0, 2, 3, // Left
            4, 5, 6, 4, 6, 7, // Top
            8, 9, 10, 8, 10, 11, // Right
            12, 13, 14, 12, 14, 15, // Bottom
        )

        private val INTERIOR_WALLS_INDEXES = intArrayOf(
            // Vertical - Left - Bottom
            0, 1, 2, 0, 2, 3, // Left
            4, 6, 5, 4, 7, 6, // Right
            2, 6, 3, 3, 6, 7, // Top
//            0, 4, 1, 1, 4, 5, // Bottom
            1, 5, 2, 2, 5, 6, // Front

            // Horizontal - Left - Bottom
//            8, 9, 10, 8, 10, 11, // Left
            12, 14, 13, 12, 15, 14, // Right
            10, 14, 11, 11, 14, 15, // Top
            8, 12, 9, 9, 12, 13, // Bottom
            9, 13, 10, 10, 13, 14, // Front

            // Vertical - Left - Middle
            16, 17, 18, 16, 18, 19, // Left
            20, 22, 21, 20, 23, 22, // Right
            18, 22, 19, 19, 22, 23, // Top
            16, 20, 17, 17, 20, 21, // Bottom
            17, 21, 18, 18, 21, 22, // Front

            // Horizontal - Left - Top
//            24, 25, 26, 24, 26, 27, // Left
            28, 30, 29, 28, 31, 30, // Right
            26, 30, 27, 27, 30, 31, // Top
            24, 28, 25, 25, 28, 29, // Bottom
            25, 29, 26, 26, 29, 30, // Front

            // Vertical - Left - Top
            32, 33, 34, 32, 34, 35, // Left
            36, 38, 37, 36, 39, 38, // Right
//            34, 38, 35, 35, 38, 39, // Top
            32, 36, 33, 33, 36, 37, // Bottom
            33, 37, 34, 34, 37, 38, // Front

            // Vertical - Right - Top
            40, 41, 42, 40, 42, 43, // Left
            44, 46, 45, 44, 47, 46, // Right
//            42, 46, 43, 43, 46, 47, // Top
            40, 44, 41, 41, 44, 45, // Bottom
            41, 45, 42, 42, 45, 46, // Front

            // Horizontal - Right - Top
            48, 49, 50, 48, 50, 51, // Left
//            52, 54, 53, 52, 55, 54, // Right
            50, 54, 51, 51, 54, 55, // Top
            48, 52, 49, 49, 52, 53, // Bottom
            49, 53, 50, 50, 53, 54, // Front

            // Vertical - Right - Middle
            56, 57, 58, 56, 58, 59, // Left
            60, 62, 61, 60, 63, 62, // Right
            58, 62, 59, 59, 62, 63, // Top
            56, 60, 57, 57, 60, 61, // Bottom
            57, 61, 58, 58, 61, 62, // Front

            // Horizontal - Right - Bottom
            64, 65, 66, 64, 66, 67, // Left
//            68, 70, 69, 68, 71, 70, // Right
            66, 70, 67, 67, 70, 71, // Top
            64, 68, 65, 65, 68, 69, // Bottom
            65, 69, 66, 66, 69, 70, // Front

            // Vertical - Right - Bottom
            72, 73, 74, 72, 74, 75, // Left
            76, 78, 77, 76, 79, 78, // Right
            74, 78, 75, 75, 78, 79, // Top
//            72, 76, 73, 73, 76, 77, // Bottom
            73, 77, 74, 74, 77, 78, // Front
        )

        private val TEXTURE_COORDINATE_DATA = floatArrayOf(
            0.0F, 1.0F,
            1.0F, 1.0F,
            1.0F, 0.0F,
            0.0F, 0.0F,
        )
    }

}