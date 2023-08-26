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
    private val vertexShaderCode =
        "attribute vec3 aVertexPosition;" +  //vertex of an object
                "attribute vec4 aVertexColor;" +  //the colour  of the object
                "uniform mat4 uMVPMatrix;" +  //model view  projection matrix
                "varying vec4 vColor;" +  //variable to be accessed by the fragment shader
                "void main() {" +
                "   gl_Position = uMVPMatrix* vec4(aVertexPosition, 1.0);" +  //calculate the position of the vertex
                "   vColor=aVertexColor;" +
                "}" //get the colour from the application program
    private val fragmentShaderCode =
        "precision mediump float;" +  //define the precision of float
                "varying vec4 vColor;" +  //variable from the vertex shader
                //---------
                "void main() {" +
                "   gl_FragColor = vColor;" +
                "}" //change the colour based on the variable from the vertex shader
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
    private lateinit var sphere1Vertex: FloatArray
    private lateinit var sphere1Index: IntArray
    private lateinit var sphere1Color: FloatArray

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
        val indexes = IntArray(65535)
        val colors = FloatArray(65535)
        val pNormLen = (noLongitude + 1) * 3 * 3
        var vertexIndex = 0
        var colorIndex = 0
        var index = 0
        val vertices2 = FloatArray(65535)
        val indexes2 = IntArray(65535)
        val colors2 = FloatArray(65525)
        var vertex2index = 0
        var color2index = 0
        var index2 = 0
        val ringVertices = FloatArray(65535)
        val ringIndexes = IntArray(65535)
        val ringColors = FloatArray(65525)
        var rVIndex = 0
        var rCIndex = 0
        var rIndex = 0
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
                colors[colorIndex++] = 1f
                colors[colorIndex++] = abs(tColor)
                colors[colorIndex++] = 0f
                colors[colorIndex++] = 1f
                colors2[color2index++] = 0f
                colors2[color2index++] = 1f
                colors2[color2index++] = abs(tColor)
                colors2[color2index++] = 1f
                if (row == 20) {
                    ringVertices[rVIndex++] = (radius * x).toFloat()
                    ringVertices[rVIndex++] = (radius * cosTheta).toFloat() + dist
                    ringVertices[rVIndex++] = (radius * z).toFloat()
                    ringColors[rCIndex++] = 1f
                    ringColors[rCIndex++] = abs(tColor)
                    ringColors[rCIndex++] = 0f
                    ringColors[rCIndex++] = 1f
                }
                if (row == 15) {
                    ringVertices[rVIndex++] = (radius * x).toFloat() / 2
                    ringVertices[rVIndex++] = (radius * cosTheta).toFloat() / 2 + 0.2f * dist
                    ringVertices[rVIndex++] = (radius * z).toFloat() / 2
                    ringColors[rCIndex++] = 1f
                    ringColors[rCIndex++] = abs(tColor)
                    ringColors[rCIndex++] = 0f
                    ringColors[rCIndex++] = 1f
                }
                if (row == 10) {
                    ringVertices[rVIndex++] = (radius * x).toFloat() / 2
                    ringVertices[rVIndex++] = (radius * cosTheta).toFloat() / 2 - 0.1f * dist
                    ringVertices[rVIndex++] = (radius * z).toFloat() / 2
                    ringColors[rCIndex++] = 0f
                    ringColors[rCIndex++] = 1f
                    ringColors[rCIndex++] = abs(tColor)
                    ringColors[rCIndex++] = 1f
                }
                if (row == 20) {
                    ringVertices[pLen++] = (radius * x).toFloat()
                    ringVertices[pLen++] = (-radius * cosTheta).toFloat() - dist
                    ringVertices[pLen++] = (radius * z).toFloat()
                    ringColors[pColorLen++] = 0f
                    ringColors[pColorLen++] = 1f
                    ringColors[pColorLen++] = abs(tColor)
                    ringColors[pColorLen++] = 1f
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
                indexes[index++] = p1
                indexes[index++] = p0
                indexes[index++] = p0 + 1
                indexes[index++] = p1 + 1
                indexes[index++] = p1
                indexes[index++] = p0 + 1
                indexes2[index2++] = p1
                indexes2[index2++] = p0
                indexes2[index2++] = p0 + 1
                indexes2[index2++] = p1 + 1
                indexes2[index2++] = p1
                indexes2[index2++] = p0 + 1
            }
        }
        rVIndex = (noLongitude + 1) * 3 * 4
        rCIndex = (noLongitude + 1) * 4 * 4
        pLen = noLongitude + 1
        for (j in 0 until pLen - 1) {
            ringIndexes[rIndex++] = j
            ringIndexes[rIndex++] = j + pLen
            ringIndexes[rIndex++] = j + 1
            ringIndexes[rIndex++] = j + pLen + 1
            ringIndexes[rIndex++] = j + 1
            ringIndexes[rIndex++] = j + pLen
            ringIndexes[rIndex++] = j + pLen
            ringIndexes[rIndex++] = j + pLen * 2
            ringIndexes[rIndex++] = j + pLen + 1
            ringIndexes[rIndex++] = j + pLen * 2 + 1
            ringIndexes[rIndex++] = j + pLen + 1
            ringIndexes[rIndex++] = j + pLen * 2
            ringIndexes[rIndex++] = j + pLen * 3
            ringIndexes[rIndex++] = j
            ringIndexes[rIndex++] = j + 1
            ringIndexes[rIndex++] = j + 1
            ringIndexes[rIndex++] = j + pLen * 3 + 1
            ringIndexes[rIndex++] = j + pLen * 3
        }


        //set the buffers
        sphere1Vertex = vertices.copyOf(vertexIndex)
        sphere1Index = indexes.copyOf(index)
        sphere1Color = colors.copyOf(colorIndex)
        sphere2Vertex = vertices2.copyOf(vertex2index)
        sphere2Index = indexes2.copyOf(index2)
        sphere2Color = colors2.copyOf(color2index)
        ringVertex = ringVertices.copyOf(rVIndex)
        ringColor = ringColors.copyOf(rCIndex)
        this.ringIndex = ringIndexes.copyOf(rIndex)
    }

    init {
        createSphere(2f, 30, 30)
        // initialize vertex byte buffer for shape coordinates
        val bb =
            ByteBuffer.allocateDirect(sphere1Vertex.size * 4) // (# of coordinate values * 4 bytes per float)
        bb.order(ByteOrder.nativeOrder())
        vertexBuffer = bb.asFloatBuffer()
        vertexBuffer.put(sphere1Vertex)
        vertexBuffer.position(0)
        val cb =
            ByteBuffer.allocateDirect(sphere1Color.size * 4) // (# of coordinate values * 4 bytes per float)
        cb.order(ByteOrder.nativeOrder())
        colorBuffer = cb.asFloatBuffer()
        colorBuffer.put(sphere1Color)
        colorBuffer.position(0)
        val ib = IntBuffer.allocate(sphere1Index.size)
        indexBuffer = ib
        indexBuffer.put(sphere1Index)
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
        index2Buffer.put(sphere2Index)
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
        val vertexShader = MyRenderer.loadShader(GLES32.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = MyRenderer.loadShader(GLES32.GL_FRAGMENT_SHADER, fragmentShaderCode)
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
        // Draw the Sphere
        GLES32.glDrawElements(
            GLES32.GL_TRIANGLES,
            sphere1Index.size,
            GLES32.GL_UNSIGNED_INT,
            indexBuffer
        )
        //---------
        //2nd sphere
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
    }
}