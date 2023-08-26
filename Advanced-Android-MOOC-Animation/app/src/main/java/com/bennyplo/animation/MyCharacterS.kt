package com.bennyplo.animation

import android.opengl.GLES32
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer
import kotlin.math.pow

class MyCharacterS {
    private val vertexShaderCode = "attribute vec3 aVertexPosition;" +  //vertex of an object
            " attribute vec4 aVertexColor;" +  //the colour  of the object
            "     uniform mat4 uMVPMatrix;" +  //model view  projection matrix
            "    varying vec4 vColor;" +  //variable to be accessed by the fragment shader
            "void main() {" +
            "        gl_Position = uMVPMatrix* vec4(aVertexPosition, 1.0);" +  //calculate the position of the vertex
            "        vColor=aVertexColor;}" //get the colour from the application program
    private val fragmentShaderCode =
        "precision lowp float;" +  //need to set to low in order to show the depth map
                "varying vec4 vColor;" +  //variable from the vertex shader
                "void main() {" +
                "float depth=1.0-gl_FragCoord.z;" +  //to show closer surface to be brighter, and further away surface darker
                "gl_FragColor = vColor;" +
                "}"
    private val vertexBuffer: FloatBuffer
    private val colorBuffer: FloatBuffer
    private val indexBuffer: IntBuffer
    private val mProgram: Int
    private val mPositionHandle: Int
    private val mColorHandle: Int
    private val mMVPMatrixHandle: Int
    private val vertexCount // number of vertices
            : Int
    private val vertexStride = COORDS_PER_VERTEX * 4 // 4 bytes per vertex
    private val colorStride = COLOR_PER_VERTEX * 4 //4 bytes per vertex

    private lateinit var charSVertex: FloatArray
    private lateinit var charSIndex: IntArray
    private lateinit var charSColor: FloatArray

    private fun createCurve(controlPtsRight: FloatArray, controlPtsLeft: FloatArray) {
        val vertices = FloatArray(65535)
        val color = FloatArray(65535)
        val pIndex = IntArray(65535)
        var vertexIndex = 0
        var colorIndex = 0
        var indx = 0
        var controlPtIndex = 0
        val noSegments = controlPtsRight.size / 2 / 3
        var t: Double
        var x: Double
        var y: Double
        var xl: Double
        var yl: Double
        val z = 0.3
        var centreX = 0.0
        var centreY = 0.0
        var i = 0
        while (i < controlPtsRight.size) {
            centreX += controlPtsRight[i].toDouble()
            centreY += controlPtsRight[i + 1].toDouble()
            i += 2
        }
        centreX /= (controlPtsRight.size / 2.0).toFloat().toDouble()
        centreY /= (controlPtsRight.size / 2.0).toFloat().toDouble()
        for (segment in 0 until noSegments) {
            t = 0.0
            while (t < 1.0) {
                x =
                    (1.0 - t).pow(3.0) * controlPtsRight[controlPtIndex] + controlPtsRight[controlPtIndex + 2] * 3 * t * (1 - t).pow(
                        2.0
                    ) + controlPtsRight[controlPtIndex + 4] * 3 * t * t * (1 - t) + controlPtsRight[controlPtIndex + 6] * t.pow(
                        3.0
                    )
                y =
                    (1.0 - t).pow(3.0) * controlPtsRight[controlPtIndex + 1] + controlPtsRight[controlPtIndex + 3] * 3 * t * (1 - t).pow(
                        2.0
                    ) + controlPtsRight[controlPtIndex + 5] * 3 * t * t * (1 - t) + controlPtsRight[controlPtIndex + 7] * t.pow(
                        3.0
                    )
                xl =
                    (1.0 - t).pow(3.0) * controlPtsLeft[controlPtIndex] + controlPtsLeft[controlPtIndex + 2] * 3 * t * (1 - t).pow(
                        2.0
                    ) + controlPtsLeft[controlPtIndex + 4] * 3 * t * t * (1 - t) + controlPtsLeft[controlPtIndex + 6] * t.pow(
                        3.0
                    )
                yl =
                    (1.0 - t).pow(3.0) * controlPtsLeft[controlPtIndex + 1] + controlPtsLeft[controlPtIndex + 3] * 3 * t * (1 - t).pow(
                        2.0
                    ) + controlPtsLeft[controlPtIndex + 5] * 3 * t * t * (1 - t) + controlPtsLeft[controlPtIndex + 7] * t.pow(
                        3.0
                    )
                vertices[vertexIndex++] = (x - centreX).toFloat()
                vertices[vertexIndex++] = (y - centreY).toFloat()
                vertices[vertexIndex++] = z.toFloat()
                vertices[vertexIndex++] = (xl - centreX).toFloat()
                vertices[vertexIndex++] = (yl - centreY).toFloat()
                vertices[vertexIndex++] = z.toFloat()
                vertices[vertexIndex++] = (x - centreX).toFloat()
                vertices[vertexIndex++] = (y - centreY).toFloat()
                vertices[vertexIndex++] = (-z).toFloat()
                vertices[vertexIndex++] = (xl - centreX).toFloat()
                vertices[vertexIndex++] = (yl - centreY).toFloat()
                vertices[vertexIndex++] = (-z).toFloat()
                color[colorIndex++] = 1f
                color[colorIndex++] = 1f
                color[colorIndex++] = 0f
                color[colorIndex++] = 1f
                color[colorIndex++] = 1f
                color[colorIndex++] = 1f
                color[colorIndex++] = 0f
                color[colorIndex++] = 1f
                color[colorIndex++] = 1f
                color[colorIndex++] = 0f
                color[colorIndex++] = 0f
                color[colorIndex++] = 1f
                color[colorIndex++] = 1f
                color[colorIndex++] = 0f
                color[colorIndex++] = 0f
                color[colorIndex++] = 1f
                t += 0.1
            }
            controlPtIndex += 6
        }
        var v0 = 0
        var v1 = 1
        var v2 = 4
        var v3 = 5
        var v4 = 2
        var v5 = 3
        var v6 = 6
        var v7 = 7
        while (v7 < vertexIndex / 3) {
            //the front
            pIndex[indx++] = v0
            pIndex[indx++] = v1
            pIndex[indx++] = v2
            pIndex[indx++] = v2
            pIndex[indx++] = v1
            pIndex[indx++] = v3
            //back
            pIndex[indx++] = v4
            pIndex[indx++] = v5
            pIndex[indx++] = v6
            pIndex[indx++] = v6
            pIndex[indx++] = v5
            pIndex[indx++] = v7
            //bottom
            pIndex[indx++] = v4
            pIndex[indx++] = v0
            pIndex[indx++] = v2
            pIndex[indx++] = v2
            pIndex[indx++] = v6
            pIndex[indx++] = v4
            //top
            pIndex[indx++] = v5
            pIndex[indx++] = v1
            pIndex[indx++] = v3
            pIndex[indx++] = v3
            pIndex[indx++] = v7
            pIndex[indx++] = v5
            v0 += 4
            v1 += 4
            v2 += 4
            v3 += 4
            v4 += 4
            v5 += 4
            v6 += 4
            v7 += 4
        }
        //cover bottom end
        pIndex[indx++] = 1
        pIndex[indx++] = 0
        pIndex[indx++] = 2
        pIndex[indx++] = 2
        pIndex[indx++] = 3
        pIndex[indx++] = 1
        //cover the top end
        pIndex[indx++] = v1
        pIndex[indx++] = v0
        pIndex[indx++] = v4
        pIndex[indx++] = v4
        pIndex[indx++] = v5
        pIndex[indx++] = v1
        charSVertex = vertices.copyOf(vertexIndex)
        charSIndex = pIndex.copyOf(indx)
        charSColor = color.copyOf(colorIndex)
    }

    init {
        val controlPtsRight = FloatArray(14)
        val controlPtsLeft = FloatArray(14)
        var ci = 0
        controlPtsRight[ci++] = 2f
        controlPtsRight[ci++] = 0f
        controlPtsRight[ci++] = 3.2f
        controlPtsRight[ci++] = 0f
        controlPtsRight[ci++] = 4f
        controlPtsRight[ci++] = 0.8f
        controlPtsRight[ci++] = 2.8f
        controlPtsRight[ci++] = 1.3f
        controlPtsRight[ci++] = 2f
        controlPtsRight[ci++] = 1.5f
        controlPtsRight[ci++] = 2f
        controlPtsRight[ci++] = 2f
        controlPtsRight[ci++] = 3.2f
        controlPtsRight[ci++] = 2f
        ci = 0
        controlPtsLeft[ci++] = 2f
        controlPtsLeft[ci++] = 0.2f
        controlPtsLeft[ci++] = 2.2f
        controlPtsLeft[ci++] = 0.2f
        controlPtsLeft[ci++] = 3.6f
        controlPtsLeft[ci++] = 0.4f
        controlPtsLeft[ci++] = 2.8f
        controlPtsLeft[ci++] = 1.0f
        controlPtsLeft[ci++] = 1.4f
        controlPtsLeft[ci++] = 1.5f
        controlPtsLeft[ci++] = 1.6f
        controlPtsLeft[ci++] = 2.2f
        controlPtsLeft[ci++] = 3.2f
        controlPtsLeft[ci++] = 2.2f
        createCurve(controlPtsRight, controlPtsLeft)
        // initialize vertex byte buffer for shape coordinates
        val bb =
            ByteBuffer.allocateDirect(charSVertex.size * 4) // (# of coordinate values * 4 bytes per float)
        bb.order(ByteOrder.nativeOrder())
        vertexBuffer = bb.asFloatBuffer()
        vertexBuffer.put(charSVertex)
        vertexBuffer.position(0)
        vertexCount = charSVertex.size / COORDS_PER_VERTEX
        val cb =
            ByteBuffer.allocateDirect(charSColor.size * 4) // (# of coordinate values * 4 bytes per float)
        cb.order(ByteOrder.nativeOrder())
        colorBuffer = cb.asFloatBuffer()
        colorBuffer.put(charSColor)
        colorBuffer.position(0)
        val ib = IntBuffer.allocate(charSIndex.size)
        indexBuffer = ib
        indexBuffer.put(charSIndex)
        indexBuffer.position(0)
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
        // Prepare the triangle coordinate data
        GLES32.glVertexAttribPointer(
            mPositionHandle,
            COORDS_PER_VERTEX,
            GLES32.GL_FLOAT,
            false,
            vertexStride,
            vertexBuffer
        )
        //get the handle to vertex shader's aVertexColor member
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
    }

    fun draw(mvpMatrix: FloatArray?) {
        // Apply the projection and view transformation
        GLES32.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0)
        //set the attribute of the vertex to point to the vertex buffer
        GLES32.glVertexAttribPointer(
            mPositionHandle, COORDS_PER_VERTEX,
            GLES32.GL_FLOAT, false, vertexStride, vertexBuffer
        )
        GLES32.glVertexAttribPointer(
            mColorHandle, COORDS_PER_VERTEX,
            GLES32.GL_FLOAT, false, colorStride, colorBuffer
        )
        GLES32.glDrawElements(
            GLES32.GL_TRIANGLES,
            charSIndex.size,
            GLES32.GL_UNSIGNED_INT,
            indexBuffer
        )
    }

    companion object {
        // number of coordinates per vertex in this array
        const val COORDS_PER_VERTEX = 3
        const val COLOR_PER_VERTEX = 4
    }
}