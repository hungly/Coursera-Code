package com.bennyplo.capstone3.gl_object

import android.opengl.GLES32
import com.bennyplo.capstone3.MyRenderer.Companion.checkGlError
import com.bennyplo.capstone3.MyRenderer.Companion.loadShader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class FloorPlan3D {


    private val _colorBuffer: FloatBuffer by lazy {
        ByteBuffer.allocateDirect(COLORS.size * COLORS_PER_VERTEX).apply {
            order(ByteOrder.nativeOrder())
        }.asFloatBuffer().apply {
            put(COLORS)
            position(0)
        }
    }

    private val _colorHandle: Int by lazy {
        GLES32.glGetAttribLocation(_program, "aVertexColor")
    }

    private val _mVPMatrixHandle: Int by lazy {
        // Get handle to shape's transformation matrix
        GLES32.glGetUniformLocation(_program, "uMVPMatrix")
    }

    private val _positionHandle: Int by lazy {
        // Get handle to vertex shader's vPosition member
        GLES32.glGetAttribLocation(_program, "aVertexPosition")
    }

    private val _program: Int by lazy {
        GLES32.glCreateProgram() // create empty OpenGL Program
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
        VERTICES.size / COORDS_PER_VERTEX
    }

    private val _colorStride = COLORS_PER_VERTEX * Float.SIZE_BYTES // 4 bytes per vertex
    private val _vertexStride = COORDS_PER_VERTEX * Float.SIZE_BYTES // 4 bytes per vertex

    init {
        // Prepare shaders and OpenGL program
        val vertexShader = loadShader(GLES32.GL_VERTEX_SHADER, VERTEX_SHADER_CODE)
        val fragmentShader = loadShader(GLES32.GL_FRAGMENT_SHADER, FRAGMENT_SHADER_CODE)
        GLES32.glAttachShader(_program, vertexShader) // Add the vertex shader to program
        GLES32.glAttachShader(_program, fragmentShader) // Add the fragment shader to program
        GLES32.glLinkProgram(_program) // Link the  OpenGL program to create an executable
        // Enable a handle to the triangle vertices
        GLES32.glEnableVertexAttribArray(_positionHandle)
        // Enable a handle to the  colour
        GLES32.glEnableVertexAttribArray(_colorHandle)
        // Prepare the colour coordinate data
        GLES32.glVertexAttribPointer(
            _colorHandle,
            COLORS_PER_VERTEX,
            GLES32.GL_FLOAT,
            false,
            _colorStride,
            _colorBuffer
        )
        checkGlError("glGetUniformLocation")
    }

    fun draw(mvpMatrix: FloatArray?) {
        GLES32.glUseProgram(_program) // Add program to OpenGL environment
        // Apply the projection and view transformation
        GLES32.glUniformMatrix4fv(_mVPMatrixHandle, 1, false, mvpMatrix, 0)
        checkGlError("glUniformMatrix4fv")
        //set the attribute of the vertex to point to the vertex buffer
        GLES32.glVertexAttribPointer(
            _positionHandle,
            COORDS_PER_VERTEX,
            GLES32.GL_FLOAT,
            false,
            _vertexStride,
            _vertexBuffer
        )
        GLES32.glVertexAttribPointer(
            _colorHandle,
            COORDS_PER_VERTEX,
            GLES32.GL_FLOAT,
            false,
            _colorStride,
            _colorBuffer
        )
        // Draw the floor plan
        GLES32.glDrawArrays(GLES32.GL_TRIANGLES, 0, _vertexCount)
    }

    companion object {

        private const val FRAGMENT_SHADER_CODE = "precision mediump float;" +
                "varying vec4 vColor;" +
                "void main() {" +
                "   gl_FragColor = vColor;" +
                "}"

        private const val VERTEX_SHADER_CODE =
            "attribute vec3 aVertexPosition;" +
                    "uniform mat4 uMVPMatrix;" +
                    "varying vec4 vColor;" +
                    "attribute vec4 aVertexColor;" +  // The colour  of the object
                    "void main() {" +
                    "   gl_Position = uMVPMatrix *vec4(aVertexPosition, 1.0);" +
                    "   gl_PointSize = 40.0;" +
                    "   vColor = aVertexColor;" +
                    "}" // Get the colour from the application program

        // number of coordinates per vertex in this array
        private const val COORDS_PER_VERTEX = 3
        private const val COLORS_PER_VERTEX = 4
        var VERTICES = floatArrayOf( //exterior
            -3.0F,
            3.0F,
            -1.0F,
            -3.0F,
            -3.0F,
            -1.0F,
            -3.0F,
            -3.0F,
            1.0F,
            -3.0F,
            -3.0F,
            1.0F,
            -3.0F,
            3.0F,
            -1.0F,
            -3.0F,
            3.0F,
            1.0F,
            3.0F,
            3.0F,
            -1.0F,
            3.0F,
            -3.0F,
            -1.0F,
            3.0F,
            -3.0F,
            1.0F,
            3.0F,
            -3.0F,
            1.0F,
            3.0F,
            3.0F,
            -1.0F,
            3.0F,
            3.0F,
            1.0F,
            3.0F,
            3.0F,
            -1.0F,
            -3.0F,
            3.0F,
            -1.0F,
            -3.0F,
            3.0F,
            1.0F,
            -3.0F,
            3.0F,
            1.0F,
            3.0F,
            3.0F,
            -1.0F,
            3.0F,
            3.0F,
            1.0F,
            3.0F,
            -3.0F,
            -1.0F,
            -3.0F,
            -3.0F,
            -1.0F,
            -3.0F,
            -3.0F,
            1.0F,
            -3.0F,
            -3.0F,
            1.0F,
            3.0F,
            -3.0F,
            -1.0F,
            3.0F,
            -3.0F,
            1.0F,  //floor
            -3.0F,
            -3.0F,
            -1.0F,
            3.0F,
            3.0F,
            -1.0F,
            3.0F,
            -3.0F,
            -1.0F,
            -3.0F,
            3.0F,
            -1.0F,
            -3.0F,
            -3.0F,
            -1.0F,
            3.0F,
            3.0F,
            -1.0F
        )
        var COLORS = floatArrayOf( //exterior wall
            0.4F, 0.4F, 0.4F, 1.0F, 0.4F, 0.4F, 0.4F, 1.0F, 0.4F, 0.4F, 0.4F, 1.0F,
            0.4F, 0.4F, 0.4F, 1.0F, 0.4F, 0.4F, 0.4F, 1.0F, 0.4F, 0.4F, 0.4F, 1.0F,
            0.4F, 0.4F, 0.4F, 1.0F, 0.4F, 0.4F, 0.4F, 1.0F, 0.4F, 0.4F, 0.4F, 1.0F,
            0.4F, 0.4F, 0.4F, 1.0F, 0.4F, 0.4F, 0.4F, 1.0F, 0.4F, 0.4F, 0.4F, 1.0F,
            0.4F, 0.4F, 0.4F, 1.0F, 0.4F, 0.4F, 0.4F, 1.0F, 0.4F, 0.4F, 0.4F, 1.0F,
            0.4F, 0.4F, 0.4F, 1.0F, 0.4F, 0.4F, 0.4F, 1.0F, 0.4F, 0.4F, 0.4F, 1.0F,
            0.4F, 0.4F, 0.4F, 1.0F, 0.4F, 0.4F, 0.4F, 1.0F, 0.4F, 0.4F, 0.4F, 1.0F,
            0.4F, 0.4F, 0.4F, 1.0F, 0.4F, 0.4F, 0.4F, 1.0F, 0.4F, 0.4F, 0.4F, 1.0F,  //floor color
            0.2F, 0.2F, 0.2F, 1.0F, 0.2F, 0.2F, 0.2F, 1.0F, 0.2F, 0.2F, 0.2F, 1.0F,
            0.2F, 0.2F, 0.2F, 1.0F, 0.2F, 0.2F, 0.2F, 1.0F, 0.2F, 0.2F, 0.2F, 1.0f
        )
    }

}