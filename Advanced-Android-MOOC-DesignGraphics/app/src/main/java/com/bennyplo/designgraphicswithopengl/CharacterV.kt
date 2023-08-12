package com.bennyplo.designgraphicswithopengl

import android.opengl.GLES32
import com.bennyplo.designgraphicswithopengl.MyRenderer.Companion.checkGlError
import com.bennyplo.designgraphicswithopengl.MyRenderer.Companion.loadShader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer

class CharacterV {

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
            CharVVertex.size * Float.SIZE_BYTES
        ) // (# of coordinate values * 4 bytes per float)
        bb.order(ByteOrder.nativeOrder())
        vertexBuffer = bb.asFloatBuffer()
        vertexBuffer.put(CharVVertex)
        vertexBuffer.position(0)
        vertexCount = CharVVertex.size / COORDS_PER_VERTEX
        val cb = ByteBuffer.allocateDirect(
            CharVColor.size * Float.SIZE_BYTES
        ) // (# of coordinate values * 4 bytes per float)
        cb.order(ByteOrder.nativeOrder())
        colorBuffer = cb.asFloatBuffer()
        colorBuffer.put(CharVColor)
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
        private val CharVVertex = floatArrayOf(
            -2f, 2f, 1f,
            -1f, 2f, 1f,
            0f, 0f, 1f,
            1f, 2f, 1f,
            2f, 2f, 1f,
            0f, -2f, 1f,
            -2f, 2f, -1f,
            -1f, 2f, -1f,
            0f, 0f, -1f,
            1f, 2f, -1f,
            2f, 2f, -1f,
            0f, -2f, -1f,
        )
        private var CharIndex = intArrayOf(
            // Front
            0, 1, 5, 1, 2, 5,
            2, 3, 5, 3, 4, 5,
            // Back
            6, 7, 11, 7, 8, 11,
            8, 9, 11, 9, 10, 11,
            // Top left
            0, 1, 6, 1, 6, 7,
            // Top right
            3, 4, 9, 4, 9, 10,
            // Outer left
            0, 5, 6, 5, 6, 11,
            // Outer right
            4, 5, 11, 4, 10, 11,
            // Inner left
            1, 2, 6, 2, 6, 7,
            // Inner right
            2, 3, 8, 3, 8, 9
        )
        private var CharVColor = floatArrayOf(
            0.0f, 0.0f, 1.0f, 1.0f,  //0
            0.0f, 0.0f, 1.0f, 1.0f,  //1
            0.0f, 0.0f, 1.0f, 1.0f,  //2
            0.0f, 0.0f, 1.0f, 1.0f,  //3
            0.0f, 0.0f, 1.0f, 1.0f,  //4
            0.0f, 0.0f, 1.0f, 1.0f,  //5
            0.0f, 1.0f, 1.0f, 1.0f,  //6
            0.0f, 1.0f, 1.0f, 1.0f,  //7
            0.0f, 1.0f, 1.0f, 1.0f,  //8
            0.0f, 1.0f, 1.0f, 1.0f,  //9
            0.0f, 1.0f, 1.0f, 1.0f,  //10
            0.0f, 1.0f, 1.0f, 1.0f,  //11
        )
    }

}