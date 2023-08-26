package com.bennyplo.animation

import android.opengl.GLES32
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

class ArbitraryShape {
    private val vertexShaderCode = "attribute vec3 aVertexPosition;" +  //vertex of an object
            "attribute vec4 aVertexColor;" +  //the colour  of the object
            "uniform mat4 uMVPMatrix;" +  //model view  projection matrix
            "varying vec4 vColor;" +  //variable to be accessed by the fragment shader
            "void main() {" +
            "gl_Position = uMVPMatrix* vec4(aVertexPosition, 1.0);" +  //calculate the position of the vertex
            "vColor=aVertexColor;}" //get the colour from the application program
    private val fragmentShaderCode = "precision mediump float;" +  //define the precision of float
            "varying vec4 vColor;" +  //variable from the vertex shader
            //---------
            "void main() {" +
            "   gl_FragColor = vColor; }" //change the colour based on the variable from the vertex shader
    private val vertexBuffer: FloatBuffer
    private val colorBuffer: FloatBuffer
    private val indexBuffer: IntBuffer
    private val vertex2Buffer: FloatBuffer
    private val color2Buffer: FloatBuffer
    private val index2Buffer: IntBuffer
    private val ringVertexBuffer: FloatBuffer
    private val ringColorBuffer: FloatBuffer
    private val ringIndexBuffer: IntBuffer
    private val mProgram: Int
    private val mPositionHandle: Int
    private val mColorHandle: Int
    private val mMVPMatrixHandle: Int
    private val vertexStride = COORDS_PER_VERTEX * 4 // 4 bytes per vertex
    private val colorStride = COLOR_PER_VERTEX * 4 //4 bytes per vertex

    // 1st sphere
    private lateinit var sphereVertex: FloatArray
    private lateinit var sphereIndex: IntArray
    private lateinit var sphereColor: FloatArray

    // 2nd sphere
    private lateinit var sphere2Vertex: FloatArray
    private lateinit var sphere2Index: IntArray
    private lateinit var sphere2Color: FloatArray

    // ring
    private lateinit var ringVertex: FloatArray
    private lateinit var ringIndex: IntArray
    private lateinit var ringColor: FloatArray

    private fun createSphere(radius: Float, noLatitude: Int, noLongitude: Int) {
        val vertices = FloatArray(65535)
        val index = IntArray(65535)
        val color = FloatArray(65535)
        val pNormLen = (noLongitude + 1) * 3 * 3
        var vertexIndex = 0
        var colorIndex = 0
        var indx = 0
        val vertices2 = FloatArray(65535)
        val index2 = IntArray(65535)
        val color2 = FloatArray(65525)
        var vertex2index = 0
        var color2index = 0
        var indx2 = 0
        val ringVertices = FloatArray(65535)
        val ringIndex = IntArray(65535)
        val ringColor = FloatArray(65525)
        var rVIndx = 0
        var rCIndex = 0
        var rIndx = 0
        val dist = 3f
        var pLen = (noLongitude + 1) * 3 * 3
        var pColorLen = (noLongitude + 1) * 4 * 3
        for (row in 0 until noLatitude + 1) {
            val theta = row * Math.PI / noLatitude
            val sinTheta = sin(theta)
            val cosTheta = cos(theta)
            var tColor = -0.5f
            val tColorInc = 1 / (noLongitude + 1).toFloat()
            for (col in 0 until noLongitude + 1) {
                val phi = col * 2 * Math.PI / noLongitude
                val sinPhi = sin(phi)
                val cosPhi = cos(phi)
                val x = cosPhi * sinTheta
                val z = sinPhi * sinTheta
                vertices[vertexIndex++] = (radius * x).toFloat()
                vertices[vertexIndex++] = (radius * cosTheta).toFloat() + dist
                vertices[vertexIndex++] = (radius * z).toFloat()
                vertices2[vertex2index++] = (radius * x).toFloat()
                vertices2[vertex2index++] = (radius * cosTheta).toFloat() - dist
                vertices2[vertex2index++] = (radius * z).toFloat()
                color[colorIndex++] = 1f
                color[colorIndex++] = abs(tColor)
                color[colorIndex++] = 0f
                color[colorIndex++] = 1f
                color2[color2index++] = 0f
                color2[color2index++] = 1f
                color2[color2index++] = abs(tColor)
                color2[color2index++] = 1f
                if (row == 20) {
                    ringVertices[rVIndx++] = (radius * x).toFloat()
                    ringVertices[rVIndx++] = (radius * cosTheta).toFloat() + dist
                    ringVertices[rVIndx++] = (radius * z).toFloat()
                    ringColor[rCIndex++] = 1f
                    ringColor[rCIndex++] = abs(tColor)
                    ringColor[rCIndex++] = 0f
                    ringColor[rCIndex++] = 1f
                }
                if (row == 15) {
                    ringVertices[rVIndx++] = (radius * x).toFloat() / 2
                    ringVertices[rVIndx++] = (radius * cosTheta).toFloat() / 2 + 0.2f * dist
                    ringVertices[rVIndx++] = (radius * z).toFloat() / 2
                    ringColor[rCIndex++] = 1f
                    ringColor[rCIndex++] = abs(tColor)
                    ringColor[rCIndex++] = 0f
                    ringColor[rCIndex++] = 1f
                }
                if (row == 10) {
                    ringVertices[rVIndx++] = (radius * x).toFloat() / 2
                    ringVertices[rVIndx++] = (radius * cosTheta).toFloat() / 2 - 0.1f * dist
                    ringVertices[rVIndx++] = (radius * z).toFloat() / 2
                    ringColor[rCIndex++] = 0f
                    ringColor[rCIndex++] = 1f
                    ringColor[rCIndex++] = abs(tColor)
                    ringColor[rCIndex++] = 1f
                }
                if (row == 20) {
                    ringVertices[pLen++] = (radius * x).toFloat()
                    ringVertices[pLen++] = (-radius * cosTheta).toFloat() - dist
                    ringVertices[pLen++] = (radius * z).toFloat()
                    ringColor[pColorLen++] = 0f
                    ringColor[pColorLen++] = 1f
                    ringColor[pColorLen++] = abs(tColor)
                    ringColor[pColorLen++] = 1f
                    //-------
                }
                tColor += tColorInc
            }
        }
        //index buffer
        for (row in 0 until noLatitude) {
            for (col in 0 until noLongitude) {
                val p0 = row * (noLongitude + 1) + col
                val p1 = p0 + noLongitude + 1
                index[indx++] = p1
                index[indx++] = p0
                index[indx++] = p0 + 1
                index[indx++] = p1 + 1
                index[indx++] = p1
                index[indx++] = p0 + 1
                index2[indx2++] = p1
                index2[indx2++] = p0
                index2[indx2++] = p0 + 1
                index2[indx2++] = p1 + 1
                index2[indx2++] = p1
                index2[indx2++] = p0 + 1
            }
        }
        rVIndx = (noLongitude + 1) * 3 * 4
        rCIndex = (noLongitude + 1) * 4 * 4
        pLen = noLongitude + 1
        for (j in 0 until pLen - 1) {
            ringIndex[rIndx++] = j
            ringIndex[rIndx++] = j + pLen
            ringIndex[rIndx++] = j + 1
            ringIndex[rIndx++] = j + pLen + 1
            ringIndex[rIndx++] = j + 1
            ringIndex[rIndx++] = j + pLen
            ringIndex[rIndx++] = j + pLen
            ringIndex[rIndx++] = j + pLen * 2
            ringIndex[rIndx++] = j + pLen + 1
            ringIndex[rIndx++] = j + pLen * 2 + 1
            ringIndex[rIndx++] = j + pLen + 1
            ringIndex[rIndx++] = j + pLen * 2
            ringIndex[rIndx++] = j + pLen * 3
            ringIndex[rIndx++] = j
            ringIndex[rIndx++] = j + 1
            ringIndex[rIndx++] = j + 1
            ringIndex[rIndx++] = j + pLen * 3 + 1
            ringIndex[rIndx++] = j + pLen * 3
        }


        //set the buffers
        sphereVertex = vertices.copyOf(vertexIndex)
        sphereIndex = index.copyOf(indx)
        sphereColor = color.copyOf(colorIndex)
        sphere2Vertex = vertices2.copyOf(vertex2index)
        sphere2Index = index2.copyOf(indx2)
        sphere2Color = color2.copyOf(color2index)
        ringVertex = ringVertices.copyOf(rVIndx)
        this.ringColor = ringColor.copyOf(rCIndex)
        this.ringIndex = ringIndex.copyOf(rIndx)
    }

    init {
        createSphere(2f, 30, 30)
        // initialize vertex byte buffer for shape coordinates
        val bb =
            ByteBuffer.allocateDirect(sphereVertex.size * 4) // (# of coordinate values * 4 bytes per float)
        bb.order(ByteOrder.nativeOrder())
        vertexBuffer = bb.asFloatBuffer()
        vertexBuffer.put(sphereVertex)
        vertexBuffer.position(0)
        val cb =
            ByteBuffer.allocateDirect(sphereColor.size * 4) // (# of coordinate values * 4 bytes per float)
        cb.order(ByteOrder.nativeOrder())
        colorBuffer = cb.asFloatBuffer()
        colorBuffer.put(sphereColor)
        colorBuffer.position(0)
        val ib = IntBuffer.allocate(sphereIndex.size)
        indexBuffer = ib
        indexBuffer.put(sphereIndex)
        indexBuffer.position(0)
        //2nd sphere
        val bb2 =
            ByteBuffer.allocateDirect(sphere2Vertex.size * 4) // (# of coordinate values * 4 bytes per float)
        bb2.order(ByteOrder.nativeOrder())
        vertex2Buffer = bb2.asFloatBuffer()
        vertex2Buffer.put(sphere2Vertex)
        vertex2Buffer.position(0)
        val cb2 =
            ByteBuffer.allocateDirect(sphere2Color.size * 4) // (# of coordinate values * 4 bytes per float)
        cb2.order(ByteOrder.nativeOrder())
        color2Buffer = cb2.asFloatBuffer()
        color2Buffer.put(sphere2Color)
        color2Buffer.position(0)
        val ib2 = IntBuffer.allocate(sphere2Index.size)
        index2Buffer = ib2
        index2Buffer.put(sphereIndex)
        index2Buffer.position(0)
        val rbb =
            ByteBuffer.allocateDirect(ringVertex.size * 4) // (# of coordinate values * 4 bytes per float)
        rbb.order(ByteOrder.nativeOrder())
        ringVertexBuffer = rbb.asFloatBuffer()
        ringVertexBuffer.put(ringVertex)
        ringVertexBuffer.position(0)
        val rcb =
            ByteBuffer.allocateDirect(ringColor.size * 4) // (# of coordinate values * 4 bytes per float)
        rcb.order(ByteOrder.nativeOrder())
        ringColorBuffer = rcb.asFloatBuffer()
        ringColorBuffer.put(ringColor)
        ringColorBuffer.position(0)
        val rib = IntBuffer.allocate(ringIndex.size)
        ringIndexBuffer = rib
        ringIndexBuffer.put(ringIndex)
        ringIndexBuffer.position(0)
        //----------
        // prepare shaders and OpenGL program
        val vertexShader: Int = MyRenderer.loadShader(GLES32.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader: Int =
            MyRenderer.loadShader(GLES32.GL_FRAGMENT_SHADER, fragmentShaderCode)
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
        //---------
        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES32.glGetUniformLocation(mProgram, "uMVPMatrix")
    }

    fun draw(mvpMatrix: FloatArray?) {
        // Apply the projection and view transformation
        GLES32.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0)
        //---------
        //set the attribute of the vertex to point to the vertex buffer
        GLES32.glVertexAttribPointer(
            mPositionHandle, COORDS_PER_VERTEX,
            GLES32.GL_FLOAT, false, vertexStride, vertexBuffer
        )
        GLES32.glVertexAttribPointer(
            mColorHandle, COORDS_PER_VERTEX,
            GLES32.GL_FLOAT, false, colorStride, colorBuffer
        )
        // Draw the Sphere
        GLES32.glDrawElements(
            GLES32.GL_TRIANGLES,
            sphereIndex.size,
            GLES32.GL_UNSIGNED_INT,
            indexBuffer
        )
        //---------
        //2nd sphere
        GLES32.glVertexAttribPointer(
            mPositionHandle, COORDS_PER_VERTEX,
            GLES32.GL_FLOAT, false, vertexStride, vertex2Buffer
        )
        GLES32.glVertexAttribPointer(
            mColorHandle, COORDS_PER_VERTEX,
            GLES32.GL_FLOAT, false, colorStride, color2Buffer
        )
        // Draw the Sphere
        GLES32.glDrawElements(
            GLES32.GL_TRIANGLES,
            sphere2Index.size,
            GLES32.GL_UNSIGNED_INT,
            index2Buffer
        )
        ///////////////////
        //Rings
        GLES32.glVertexAttribPointer(
            mPositionHandle, COORDS_PER_VERTEX,
            GLES32.GL_FLOAT, false, vertexStride, ringVertexBuffer
        )
        GLES32.glVertexAttribPointer(
            mColorHandle, COORDS_PER_VERTEX,
            GLES32.GL_FLOAT, false, colorStride, ringColorBuffer
        )
        GLES32.glDrawElements(
            GLES32.GL_TRIANGLES,
            ringIndex.size,
            GLES32.GL_UNSIGNED_INT,
            ringIndexBuffer
        )
    }

    companion object {
        //---------
        // number of coordinates per vertex in this array
        const val COORDS_PER_VERTEX = 3
        const val COLOR_PER_VERTEX = 4

        private var lightLocation = FloatArray(3) //point light source location
    }
}