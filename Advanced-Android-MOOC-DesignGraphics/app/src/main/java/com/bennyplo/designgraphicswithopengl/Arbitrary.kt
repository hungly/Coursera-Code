package com.bennyplo.designgraphicswithopengl

import android.opengl.GLES32
import android.util.SparseArray
import com.bennyplo.designgraphicswithopengl.MyRenderer.Companion.checkGlError
import com.bennyplo.designgraphicswithopengl.MyRenderer.Companion.loadShader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

class Arbitrary {

    private val color1Buffer: FloatBuffer
    private val color2Buffer: FloatBuffer
    private val ringColorBuffer: FloatBuffer
    private val colorStride = COLOR_PER_VERTEX * Float.SIZE_BYTES //4 bytes per vertex

    private val fragmentShaderCode = "precision mediump float;" +  //define the precision of float
            "varying vec4 vColor;" +  //variable from the vertex shader
            "void main() {" +
            "gl_FragColor = vColor;" +
            "}" //change the colour based on the variable from the vertex shader

    private val index1Buffer: IntBuffer
    private val index2Buffer: IntBuffer
    private val ringIndexBuffer: IntBuffer
    private val mColorHandle: Int
    private val mMVPMatrixHandle: Int
    private val mPositionHandle: Int
    private val mProgram: Int
    private val vertex1Buffer: FloatBuffer
    private val vertex2Buffer: FloatBuffer
    private val ringVertexBuffer: FloatBuffer

    private val vertexShaderCode = "attribute vec3 aVertexPosition;" +  //vertex of an object
            "attribute vec4 aVertexColor;" +  //the colour  of the object
            "uniform mat4 uMVPMatrix;" +  //model view  projection matrix
            "varying vec4 vColor;" +  //variable to be accessed by the fragment shader
            "void main() {" +
            "gl_Position = uMVPMatrix* vec4(aVertexPosition, 1.0);" +  //calculate the position of the vertex
            "vColor=aVertexColor;" +
            "}" //get the colour from the application program

    private val vertexStride = COORDS_PER_VERTEX * Float.SIZE_BYTES // 4 bytes per vertex

    private var sphere1Vertex = floatArrayOf()
    private var sphere1Colors = floatArrayOf()
    private var sphere1Index = intArrayOf()
    private var sphere2Vertex = floatArrayOf()
    private var sphere2Colors = floatArrayOf()
    private var sphere2Index = intArrayOf()
    private var ringVertex = floatArrayOf()
    private var ringColors = floatArrayOf()
    private var ringIndex = intArrayOf()

    private fun createSphere(radius: Float, noLatitude: Int, noLongitude: Int) {
        val vertices1 = arrayListOf<Float>()
        val index1 = arrayListOf<Int>()
        val color1 = arrayListOf<Float>()
        var vertexIndex1 = 0
        var colorIndex1 = 0
        var indx1 = 0
        val dist = 3F

        val vertices2 = arrayListOf<Float>()
        val index2 = arrayListOf<Int>()
        val color2 = arrayListOf<Float>()
        var vertexIndex2 = 0
        var colorIndex2 = 0
        var indx2 = 0

        val ringVertices = FloatArray(65535)
        val ringIndex = arrayListOf<Int>()
        val ringColor = FloatArray(65535)
        var ringVertexIndex = 0
        var ringColorIndex = 0
        var ringIndx = 0

        var pLen = (noLongitude + 1) * 3 * 3
        var pColorLen = (noLongitude + 1) * 4 * 3

        for (row in 0..noLatitude) {
            val theta = row * Math.PI / noLatitude
            val sinTheta = sin(theta)
            val cosTheta = cos(theta)
            var tColor = -0.5F
            val tColorInc = 1F / (noLongitude + 1)
            for (col in 0..noLongitude) {
                val phi = col * 2 * Math.PI / noLongitude
                val sinPhi = sin(phi).toFloat()
                val cosPhi = cos(phi).toFloat()
                val x = (cosPhi * sinTheta).toFloat()
                val y = cosTheta.toFloat()
                val z = (sinPhi * sinTheta).toFloat()

                vertices1.add(vertexIndex1++, radius * x)
                vertices1.add(vertexIndex1++, (radius * y) + dist)
                vertices1.add(vertexIndex1++, radius * z)

                vertices2.add(vertexIndex2++, radius * x)
                vertices2.add(vertexIndex2++, (radius * y) - dist)
                vertices2.add(vertexIndex2++, radius * z)

                color1.add(colorIndex1++, 1F)
                color1.add(colorIndex1++, abs(tColor))
                color1.add(colorIndex1++, 1F)
                color1.add(colorIndex1++, 1F)

                color2.add(colorIndex2++, 0F)
                color2.add(colorIndex2++, 1F)
                color2.add(colorIndex2++, abs(tColor))
                color2.add(colorIndex2++, 1F)

                if (row == 20) {
                    ringVertices[ringVertexIndex++] = radius * x
                    ringVertices[ringVertexIndex++] =radius * y + dist
                    ringVertices[ringVertexIndex++] = radius * z
                    ringColor[ringColorIndex++] = 1F
                    ringColor[ringColorIndex++] = abs(tColor)
                    ringColor[ringColorIndex++] = 0F
                    ringColor[ringColorIndex++] = 1F
                }

                if (row == 15) {
                    ringVertices[ringVertexIndex++] =(radius * x) / 2
                    ringVertices[ringVertexIndex++] = (radius * y) / 2 + 0.2F * dist
                    ringVertices[ringVertexIndex++] =(radius * z) / 2
                    ringColor[ringColorIndex++] = 1F
                    ringColor[ringColorIndex++] = abs(tColor)
                    ringColor[ringColorIndex++] = 0F
                    ringColor[ringColorIndex++] = 1F
                }

                if (row == 10) {
                    ringVertices[ringVertexIndex++] =(radius * x) / 2
                    ringVertices[ringVertexIndex++] =(radius * y) / 2 - 0.1F * dist
                    ringVertices[ringVertexIndex++] = (radius * z) / 2
                    ringColor[ringColorIndex++] =  1F
                    ringColor[ringColorIndex++] = abs(tColor)
                    ringColor[ringColorIndex++] = 0F
                    ringColor[ringColorIndex++] = 1F
                }

                if (row == 20) {
                    ringVertices[pLen++] = radius * x
                    ringVertices[pLen++] =  -radius * y - dist
                    ringVertices[pLen++] =  radius * z
                    ringColor[pColorLen++] = 1F
                    ringColor[pColorLen++] = abs(tColor)
                    ringColor[pColorLen++] = 0F
                    ringColor[pColorLen++] = 1F
                }

                tColor += tColorInc
            }
        }

        for (row in 0 until noLatitude) {
            for (col in 0 until noLongitude) {
                val p0 = (row * (noLongitude + 1)) + col
                val p1 = p0 + noLongitude + 1

                index1.add(indx1++, p0)
                index1.add(indx1++, p1)
                index1.add(indx1++, p0 + 1)
                index1.add(indx1++, p1)
                index1.add(indx1++, p1 + 1)
                index1.add(indx1++, p0 + 1)

                index2.add(indx2++, p0)
                index2.add(indx2++, p1)
                index2.add(indx2++, p0 + 1)
                index2.add(indx2++, p1)
                index2.add(indx2++, p1 + 1)
                index2.add(indx2++, p0 + 1)
            }
        }

        pLen = noLongitude + 1

        for (j in 0 until (pLen - 1)) {
            ringIndex.add(ringIndx++, j)
            ringIndex.add(ringIndx++, j + pLen)
            ringIndex.add(ringIndx++, j + 1)
            ringIndex.add(ringIndx++, j + 1)
            ringIndex.add(ringIndx++, j + pLen + 1)
            ringIndex.add(ringIndx++, j + pLen)

            ringIndex.add(ringIndx++, j + pLen)
            ringIndex.add(ringIndx++, j + pLen * 2)
            ringIndex.add(ringIndx++, j + pLen + 1)
            ringIndex.add(ringIndx++, j + pLen + 1)
            ringIndex.add(ringIndx++, j + pLen * 2 + 1)
            ringIndex.add(ringIndx++, j + pLen * 2)

            ringIndex.add(ringIndx++, j)
            ringIndex.add(ringIndx++, j + pLen * 3)
            ringIndex.add(ringIndx++, j + 1)
            ringIndex.add(ringIndx++, j + 1)
            ringIndex.add(ringIndx++, j + pLen * 3 + 1)
            ringIndex.add(ringIndx++, j + pLen * 3)
        }

        sphere1Vertex = vertices1.toFloatArray()
        sphere1Colors = color1.toFloatArray()
        sphere1Index = index1.toIntArray()

        sphere2Vertex = vertices2.toFloatArray()
        sphere2Colors = color2.toFloatArray()
        sphere2Index = index2.toIntArray()

        ringVertex = ringVertices
        ringColors = ringColor
        this.ringIndex = ringIndex.toIntArray()
    }

    init {
        createSphere(2F, 30, 30)
        // initialize vertex byte buffer for shape coordinates
        val bb1 = ByteBuffer.allocateDirect(
            sphere1Vertex.size * Float.SIZE_BYTES
        ) // (# of coordinate values * 4 bytes per float)
        bb1.order(ByteOrder.nativeOrder())
        vertex1Buffer = bb1.asFloatBuffer()
        vertex1Buffer.put(sphere1Vertex)
        vertex1Buffer.position(0)
        val cb1 = ByteBuffer.allocateDirect(
            sphere1Colors.size * Float.SIZE_BYTES
        ) // (# of coordinate values * 4 bytes per float)
        cb1.order(ByteOrder.nativeOrder())
        color1Buffer = cb1.asFloatBuffer()
        color1Buffer.put(sphere1Colors)
        color1Buffer.position(0)
        val ib1 = IntBuffer.allocate(sphere1Index.size)
        index1Buffer = ib1
        index1Buffer.put(sphere1Index)
        index1Buffer.position(0)

        val bb2 = ByteBuffer.allocateDirect(
            sphere2Vertex.size * Float.SIZE_BYTES
        ) // (# of coordinate values * 4 bytes per float)
        bb2.order(ByteOrder.nativeOrder())
        vertex2Buffer = bb2.asFloatBuffer()
        vertex2Buffer.put(sphere2Vertex)
        vertex2Buffer.position(0)
        val cb2 = ByteBuffer.allocateDirect(
            sphere2Colors.size * Float.SIZE_BYTES
        ) // (# of coordinate values * 4 bytes per float)
        cb2.order(ByteOrder.nativeOrder())
        color2Buffer = cb2.asFloatBuffer()
        color2Buffer.put(sphere2Colors)
        color2Buffer.position(0)
        val ib2 = IntBuffer.allocate(sphere2Index.size)
        index2Buffer = ib2
        index2Buffer.put(sphere2Index)
        index2Buffer.position(0)

        val bbr = ByteBuffer.allocateDirect(
            ringVertex.size * Float.SIZE_BYTES
        ) // (# of coordinate values * 4 bytes per float)
        bbr.order(ByteOrder.nativeOrder())
        ringVertexBuffer = bbr.asFloatBuffer()
        ringVertexBuffer.put(ringVertex)
        ringVertexBuffer.position(0)
        val cbr = ByteBuffer.allocateDirect(
            ringColors.size * Float.SIZE_BYTES
        ) // (# of coordinate values * 4 bytes per float)
        cbr.order(ByteOrder.nativeOrder())
        ringColorBuffer = cbr.asFloatBuffer()
        ringColorBuffer.put(ringColors)
        ringColorBuffer.position(0)
        val ibr = IntBuffer.allocate(ringIndex.size)
        ringIndexBuffer = ibr
        ringIndexBuffer.put(ringIndex)
        ringIndexBuffer.position(0)

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
            color1Buffer
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
            vertex1Buffer
        )
        GLES32.glVertexAttribPointer(
            mColorHandle,
            COORDS_PER_VERTEX,
            GLES32.GL_FLOAT,
            false,
            colorStride,
            color1Buffer
        )
        // Draw the 3D character A
        GLES32.glDrawElements(
            GLES32.GL_TRIANGLES,
            sphere1Index.size,
            GLES32.GL_UNSIGNED_INT,
            index1Buffer
        )

        GLES32.glVertexAttribPointer(
            mPositionHandle,
            COORDS_PER_VERTEX,
            GLES32.GL_FLOAT,
            false,
            vertexStride,
            vertex2Buffer
        )
        GLES32.glVertexAttribPointer(
            mColorHandle,
            COORDS_PER_VERTEX,
            GLES32.GL_FLOAT,
            false,
            colorStride,
            color2Buffer
        )
        // Draw the 3D character A
        GLES32.glDrawElements(
            GLES32.GL_TRIANGLES,
            sphere2Index.size,
            GLES32.GL_UNSIGNED_INT,
            index2Buffer
        )

        GLES32.glVertexAttribPointer(
            mPositionHandle,
            COORDS_PER_VERTEX,
            GLES32.GL_FLOAT,
            false,
            vertexStride,
            ringVertexBuffer
        )
        GLES32.glVertexAttribPointer(
            mColorHandle,
            COORDS_PER_VERTEX,
            GLES32.GL_FLOAT,
            false,
            colorStride,
            ringColorBuffer
        )
        // Draw the 3D character A
        GLES32.glDrawElements(
            GLES32.GL_TRIANGLES,
            ringIndex.size,
            GLES32.GL_UNSIGNED_INT,
            ringIndexBuffer
        )
    }

    companion object {
        // number of coordinates per vertex in this array
        private const val COORDS_PER_VERTEX = 3
        private const val COLOR_PER_VERTEX = 4
    }

}