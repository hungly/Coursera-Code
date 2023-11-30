package com.bennyplo.capstone3.model.gl_object

import android.content.Context
import android.opengl.GLES32
import androidx.annotation.DrawableRes
import com.bennyplo.capstone3.MyRenderer.Companion.checkGlError
import com.bennyplo.capstone3.model.GLObject
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer
import kotlin.math.abs

class Painting(context: Context?, @DrawableRes resourceId: Int) : GLObject() {


    private val _colorBuffer: FloatBuffer by lazy {
        ByteBuffer.allocateDirect(COLORS.size * COLORS_PER_VERTEX).apply {
            order(ByteOrder.nativeOrder())
        }.asFloatBuffer().apply {
            put(COLORS)
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

    private val _textureBuffer: FloatBuffer by lazy {
        ByteBuffer.allocateDirect(TEXTURE_COORDINATE_DATA.size * Float.SIZE_BYTES).apply {
            order(ByteOrder.nativeOrder())
        }.asFloatBuffer().apply {
            put(TEXTURE_COORDINATE_DATA)
            position(0)
        }
    }

    private val _vertexBuffer: FloatBuffer by lazy {
        // (# of coordinate values * 4 bytes per float)
        ByteBuffer.allocateDirect(scaledVertices.size * Float.SIZE_BYTES).apply {
            order(ByteOrder.nativeOrder())
        }.asFloatBuffer().apply {
            put(scaledVertices)
            position(0)
        }
    }

    private val _vertexCount // number of vertices
            : Int by lazy {
        VERTICES.size / COORDINATES_PER_VERTEX
    }

    private val scaledVertices = VERTICES.clone()

    override var textureRatio: Float = 0.0F
        set(value) {
            field = value
            calculateVerticesScaled()
        }

    // Don't lazy load here because auto scaling need bitmap height and width ASAP
    override val textureImageHandler = loadTextureFromResource(resourceId, context)

    init {
        // Setup texture
        GLES32.glActiveTexture(GLES32.GL_TEXTURE0) // Always use texture 0
        GLES32.glUniform1i(textureSamplerHandle, 0) // Always use point texture sampler index to 0
        GLES32.glVertexAttribPointer(
            textureCoordinateHandle,
            TEXTURE_COORDINATES_PER_VERTEX,
            GLES32.GL_FLOAT,
            false,
            TEXTURE_STRIDE,
            _textureBuffer
        )
    }

    override fun draw(mvpMatrix: FloatArray?) {
        super.draw(mvpMatrix)

        GLES32.glEnable(GLES32.GL_CULL_FACE)
        GLES32.glCullFace(GLES32.GL_FRONT)
        GLES32.glFrontFace(GLES32.GL_CW)

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

        // Setup texture
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, textureImageHandler)
        GLES32.glUniform1i(useTextureHandle, 1)

        // Draw the floor plan
        GLES32.glDrawElements(
            GLES32.GL_TRIANGLES,
            INDEXES.size,
            GLES32.GL_UNSIGNED_INT,
            _indexBuffer
        )
    }

    private fun calculateVerticesScaled() {
        val originalHeight = abs(VERTICES[8] - VERTICES[2])
        val originalWidth = abs(VERTICES[3] - VERTICES[0])
        when {
            textureRatio < 0 -> {
                val newWidth = originalWidth * abs(textureRatio)
                val reduceAmount = (originalWidth - newWidth) / 2
                for (i in 0..VERTICES.lastIndex step 3) {
                    if (VERTICES[i] < 0) {
                        scaledVertices[i] = VERTICES[i] - reduceAmount
                    } else {
                        scaledVertices[i] = VERTICES[i] + reduceAmount
                    }
                }
            }

            textureRatio > 0 -> {
                val newHeight = originalHeight / abs(textureRatio)
                val reduceAmount = (originalHeight - newHeight) / 2
                for (i in 2..VERTICES.lastIndex step 3) {
                    if (VERTICES[i] < 0) {
                        scaledVertices[i] = VERTICES[i] + reduceAmount
                    } else {
                        scaledVertices[i] = VERTICES[i] - reduceAmount
                    }
                }
            }
        }
    }

    companion object {

        private var VERTICES = floatArrayOf(
            -2.0F, 0.0F, -2.0F,
            2.0F, 0.0F, -2.0F,
            2.0F, 0.0F, 2.0F,
            -2.0F, 0.0F, 2.0F,
        )

        private val INDEXES = intArrayOf(
            0, 1, 2,
            0, 2, 3
        )

        private val COLORS = floatArrayOf(
            1.0F, 1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F, 1.0F,
            1.0F, 1.0F, 1.0F, 1.0F,
        )

        private val TEXTURE_COORDINATE_DATA = floatArrayOf(
            0.0F, 1.0F,
            1.0F, 1.0F,
            1.0F, 0.0F,
            0.0F, 0.0F,
        )
    }

}