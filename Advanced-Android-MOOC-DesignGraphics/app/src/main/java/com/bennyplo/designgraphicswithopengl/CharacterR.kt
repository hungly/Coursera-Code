package com.bennyplo.designgraphicswithopengl

import android.opengl.GLES32
import com.bennyplo.designgraphicswithopengl.MyRenderer.Companion.checkGlError
import com.bennyplo.designgraphicswithopengl.MyRenderer.Companion.loadShader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer

class CharacterR {

    private val colorBuffer: FloatBuffer
    private val colorStride = COLOR_PER_VERTEX * Float.SIZE_BYTES //4 bytes per vertex

    private val fragmentShaderCode = "precision mediump float;" +  //define the precision of float
            "varying vec4 vColor;" +  //variable from the vertex shader
            "void main() {" +
            "gl_FragColor = vColor;" +
            "}" //change the colour based on the variable from the vertex shader

    private val indexBuffer: IntBuffer
    private val mColorHandle: Int
    private val mMVPMatrixHandle: Int
    private val mPositionHandle: Int
    private val mProgram: Int
    private val vertexBuffer: FloatBuffer

    private val vertexCount // number of vertices
            : Int

    private val vertexShaderCode = "attribute vec3 aVertexPosition;" +  //vertex of an object
            "attribute vec4 aVertexColor;" +  //the colour  of the object
            "uniform mat4 uMVPMatrix;" +  //model view  projection matrix
            "varying vec4 vColor;" +  //variable to be accessed by the fragment shader
            "void main() {" +
            "gl_Position = uMVPMatrix* vec4(aVertexPosition, 1.0);" +  //calculate the position of the vertex
            "vColor=aVertexColor;" +
            "}" //get the colour from the application program

    private val vertexStride = COORDS_PER_VERTEX * Float.SIZE_BYTES // 4 bytes per vertex

    init {
        // initialize vertex byte buffer for shape coordinates
        val bb = ByteBuffer.allocateDirect(
            CharVertex.size * Float.SIZE_BYTES
        ) // (# of coordinate values * 4 bytes per float)
        bb.order(ByteOrder.nativeOrder())
        vertexBuffer = bb.asFloatBuffer()
        vertexBuffer.put(CharVertex)
        vertexBuffer.position(0)
        vertexCount = CharVertex.size / COORDS_PER_VERTEX
        val cb = ByteBuffer.allocateDirect(
            CharColor.size * Float.SIZE_BYTES
        ) // (# of coordinate values * 4 bytes per float)
        cb.order(ByteOrder.nativeOrder())
        colorBuffer = cb.asFloatBuffer()
        colorBuffer.put(CharColor)
        colorBuffer.position(0)
        val ib = IntBuffer.allocate(CharIndex.size)
        indexBuffer = ib
        indexBuffer.put(CharIndex)
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
        // Enable a handle to the triangle vertices
        GLES32.glEnableVertexAttribArray(mPositionHandle)
        mColorHandle = GLES32.glGetAttribLocation(mProgram, "aVertexColor")
        // Enable a handle to the  colour
        GLES32.glEnableVertexAttribArray(mColorHandle)
        // Prepare the colour coordinate data
        GLES32.glVertexAttribPointer(
            mColorHandle,
            COLOR_PER_VERTEX,
            GLES32.GL_FLOAT,
            false,
            colorStride,
            colorBuffer
        )
        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES32.glGetUniformLocation(mProgram, "uMVPMatrix")
        checkGlError("glGetUniformLocation")
    }

    fun draw(mvpMatrix: FloatArray?) {
        GLES32.glUseProgram(mProgram) //use the object's shading programs
        // Apply the projection and view transformation
        GLES32.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0)
        checkGlError("glUniformMatrix4fv")
        //set the attribute of the vertex to point to the vertex buffer
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
            COORDS_PER_VERTEX,
            GLES32.GL_FLOAT,
            false,
            colorStride,
            colorBuffer
        )
        // Draw the 3D character A
        GLES32.glDrawElements(
            GLES32.GL_TRIANGLES,
            CharIndex.size,
            GLES32.GL_UNSIGNED_INT,
            indexBuffer
        )
    }

    companion object {
        // number of coordinates per vertex in this array
        private const val COORDS_PER_VERTEX = 3
        private const val COLOR_PER_VERTEX = 4
        private val CharVertex = floatArrayOf(
            -1.5f,    2f,  0.5f, // 0
               0f,    2f,  0.5f, // 1
               0f,    0f,  0.5f, // 2
            -0.5f,    0f,  0.5f, // 3
            -0.5f,   -2f,  0.5f, // 4
            -1.5f,   -2f,  0.5f, // 5
            -0.5f,  1.4f,  0.5f, // 6
               0f,  1.4f,  0.5f, // 7
               0f,  0.6f,  0.5f, // 8
            -0.5f,  0.6f,  0.5f, // 9
             1.5f,   -2f,  0.5f, //10
             0.5f,   -2f,  0.5f, //11
            -0.5f,   -1f,  0.5f, //12
            -1.5f,    2f, -0.5f, //13
               0f,    2f, -0.5f, //14
               0f,    0f, -0.5f, //15
            -0.5f,    0f, -0.5f, //16
            -0.5f,   -2f, -0.5f, //17
            -1.5f,   -2f, -0.5f, //18
            -0.5f,  1.4f, -0.5f, //19
               0f,  1.4f, -0.5f, //20
               0f,  0.6f, -0.5f, //21
            -0.5f,  0.6f, -0.5f, //22
             1.5f,   -2f, -0.5f, //23
             0.5f,   -2f, -0.5f, //24
            -0.5f,   -1f, -0.5f, //25
        )
        private var CharIndex = intArrayOf(
            // Front
             0,  6,  4,  4,  5,  0,
             0,  1,  7,  6,  7,  0,
             2,  3,  8,  8,  9,  3,
             3, 10, 11, 11, 12,  3,
            // Back
            13, 19, 17, 17, 18, 13,
            13, 14, 20, 19, 20, 13,
            15, 16, 21, 21, 22, 16,
            16, 24, 25, 23, 24, 16,
            // Left
             0,  5, 13, 13, 18,  5,
            // Right
             6,  9, 19, 19, 22,  9,
            12,  4, 17, 17, 25, 12,
             3, 10, 23, 16, 23,  3,
            // Top
             0,  1, 13, 13, 14,  1,
             8,  9, 21, 21, 22,  9,
            // Bottom
             6,  7, 19, 19, 20,  7,
             4,  5, 17, 17, 18,  5,
            10, 11, 23, 23, 24, 11,
        )
        private var CharColor = floatArrayOf(
             1.0f,  1.0f,  1.0f, 1.0f,  // 0
             1.0f,  1.0f,  1.0f, 1.0f,  // 1
             1.0f,  1.0f,  1.0f, 1.0f,  // 2
             1.0f,  1.0f,  1.0f, 1.0f,  // 3
             1.0f,  1.0f,  1.0f, 1.0f,  // 4
             1.0f,  1.0f,  1.0f, 1.0f,  // 5
             1.0f,  1.0f,  1.0f, 1.0f,  // 6
             1.0f,  1.0f,  1.0f, 1.0f,  // 7
             1.0f,  1.0f,  1.0f, 1.0f,  // 8
             1.0f,  1.0f,  1.0f, 1.0f,  // 9
             1.0f,  1.0f,  1.0f, 1.0f,  //10
             1.0f,  1.0f,  1.0f, 1.0f,  //11
             1.0f,  1.0f,  1.0f, 1.0f,  //12
            0.25f, 0.25f, 0.25f, 1.0f,  //13
            0.25f, 0.25f, 0.25f, 1.0f,  //14
            0.25f, 0.25f, 0.25f, 1.0f,  //15
            0.25f, 0.25f, 0.25f, 1.0f,  //16
            0.25f, 0.25f, 0.25f, 1.0f,  //17
            0.25f, 0.25f, 0.25f, 1.0f,  //18
            0.25f, 0.25f, 0.25f, 1.0f,  //19
            0.25f, 0.25f, 0.25f, 1.0f,  //20
            0.25f, 0.25f, 0.25f, 1.0f,  //21
            0.25f, 0.25f, 0.25f, 1.0f,  //22
            0.25f, 0.25f, 0.25f, 1.0f,  //23
            0.25f, 0.25f, 0.25f, 1.0f,  //24
            0.25f, 0.25f, 0.25f, 1.0f,  //25
        )
    }

}