package com.bennyplo.capstone2_opengles.gl_object

import android.opengl.GLES32
import com.bennyplo.capstone2_opengles.MyRenderer.Companion.checkGlError
import com.bennyplo.capstone2_opengles.MyRenderer.Companion.loadShader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer
import kotlin.math.cos
import kotlin.math.sin

class HalfCone : GLObject() {

    private var halfConeColors = floatArrayOf()
    private var halfConeIndexes = intArrayOf()
    private var halfConeVertices = floatArrayOf()

    private val colorBuffer: FloatBuffer by lazy {
        ByteBuffer.allocateDirect(halfConeColors.size * COLOR_PER_VERTEX).apply {
            order(ByteOrder.nativeOrder())
        }.asFloatBuffer().apply {
            put(halfConeColors)
            position(0)
        }
    }

    private val indexBuffer: IntBuffer by lazy {
        IntBuffer.allocate(halfConeIndexes.size).apply {
            put(halfConeIndexes)
            position(0)
        }
    }

    private val vertexBuffer: FloatBuffer by lazy {
        // (# of coordinate values * 4 bytes per float)
        ByteBuffer.allocateDirect(halfConeVertices.size * 4).apply {
            order(ByteOrder.nativeOrder())
        }.asFloatBuffer().apply {
            put(halfConeVertices)
            position(0)
        }
    }

    private val colorStride = COLOR_PER_VERTEX * Float.SIZE_BYTES //4 bytes per vertex

    private val fragmentShaderCode = "precision mediump float;" +  //define the precision of float
            "varying vec4 vColor;" +  //variable from the vertex shader
            "void main() {" +
            "gl_FragColor = vColor;" +
            "}" //change the colour based on the variable from the vertex shader

    private val mColorHandle: Int
    private val mMVPMatrixHandle: Int
    private val mPositionHandle: Int
    private val mProgram: Int

    private val vertexShaderCode = "attribute vec3 aVertexPosition;" +  //vertex of an object
            "attribute vec4 aVertexColor;" +  //the colour  of the object
            "uniform mat4 uMVPMatrix;" +  //model view  projection matrix
            "varying vec4 vColor;" +  //variable to be accessed by the fragment shader
            "void main() {" +
            "gl_Position = uMVPMatrix* vec4(aVertexPosition, 1.0);" +  //calculate the position of the vertex
            "vColor=aVertexColor;" +
            "}" //get the colour from the application program

    private val vertexStride = COORDS_PER_VERTEX * Float.SIZE_BYTES // 4 bytes per vertex

    override var initialScale: Triple<Float, Float, Float>
        get() = super.initialScale
        set(value) {
            super.initialScale = value
            createShape()
        }

    init {
        createShape()
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

    override fun draw(mvpMatrix: FloatArray?) {
        GLES32.glUseProgram(mProgram) //use the object's shading programs
        // Apply the projection and view transformation
        GLES32.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0)
        checkGlError("glUniformMatrix4fv")

        GLES32.glDisable(GLES32.GL_BLEND)
        GLES32.glDisable(GLES32.GL_CULL_FACE)

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
            halfConeIndexes.size,
            GLES32.GL_UNSIGNED_INT,
            indexBuffer
        )
    }

    private fun createShape() {
        val vertices = arrayListOf<Float>()
        val indexes = arrayListOf<Int>()
        val colors = arrayListOf<Float>()
        val radius1 = 1.5F
        val radius2 = 1F

        val angleIncrement = 360 / 36
        var rad: Float
        var angle = 0.0

        while (angle <= 360.0) {
            angle = angle.coerceIn(0.0, 360.0)
            rad = Math.toRadians(angle).toFloat()
            val x = radius1 * cos(rad)
            val y = radius1 * sin(rad)

            // Base
            vertices.add(x * initialScale.first)
            vertices.add(y * initialScale.second)
            vertices.add(1f * initialScale.third)
            // Base color
            colors.add(1F)
            colors.add(1F)
            colors.add(0F)
            colors.add(1F)

            angle += angleIncrement
        }

        angle = 0.0
        while (angle <= 360.0) {
            angle = angle.coerceIn(0.0, 360.0)
            rad = Math.toRadians(angle).toFloat()
            val x = radius2 * cos(rad)
            val y = radius2 * sin(rad)

            // Base
            vertices.add(x * initialScale.first)
            vertices.add(y * initialScale.second)
            vertices.add(-1f * initialScale.third)
            // Base color
            colors.add(0F)
            colors.add(1F)
            colors.add(1F)
            colors.add(1F)

            angle += angleIncrement
        }

        for (i in 0..(360 / angleIncrement)) {
            indexes.add(i)
            indexes.add(i + 1)
            indexes.add(i + (360 / angleIncrement) + 1)
            indexes.add(i + 1)
            indexes.add(i + (360 / angleIncrement) + 1)
            indexes.add(i + (360 / angleIncrement) + 2)
        }

        halfConeVertices = vertices.toFloatArray()
        halfConeIndexes = indexes.toIntArray()
        halfConeColors = colors.toFloatArray()
    }

    companion object {
        // number of coordinates per vertex in this array
        private const val COORDS_PER_VERTEX = 3
        private const val COLOR_PER_VERTEX = 4
    }

}