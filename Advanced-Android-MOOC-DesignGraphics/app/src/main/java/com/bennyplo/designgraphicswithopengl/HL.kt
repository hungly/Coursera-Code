package com.bennyplo.designgraphicswithopengl

import android.opengl.GLES32
import android.util.SparseArray
import androidx.core.util.forEach
import com.bennyplo.designgraphicswithopengl.MyRenderer.Companion.checkGlError
import com.bennyplo.designgraphicswithopengl.MyRenderer.Companion.loadShader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer
import kotlin.math.pow

class HL {

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

    private val buffers by lazy {
        arrayListOf<Triple<FloatBuffer, FloatBuffer, IntBuffer>>()
    }
    private val curveBuffers by lazy {
        SparseArray<Triple<FloatBuffer, FloatBuffer, IntBuffer>>()
    }

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
        CharCurvePoints.forEachIndexed { index, pair ->
            pair?.let { createCurve(pair.first, pair.second, index) }
        }
        CharData.forEach { (vertex, color, index) ->
            // initialize vertex byte buffer for shape coordinates
            val bb = ByteBuffer.allocateDirect(
                vertex.size * Float.SIZE_BYTES
            ) // (# of coordinate values * 4 bytes per float)
            bb.order(ByteOrder.nativeOrder())
            val vertexBuffer = bb.asFloatBuffer()
            vertexBuffer.put(vertex)
            vertexBuffer.position(0)

            val cb = ByteBuffer.allocateDirect(
                color.size * Float.SIZE_BYTES
            ) // (# of coordinate values * 4 bytes per float)
            cb.order(ByteOrder.nativeOrder())
            val colorBuffer = cb.asFloatBuffer()
            colorBuffer.put(color)
            colorBuffer.position(0)

            val ib = IntBuffer.allocate(index.size)
            ib.put(index)
            ib.position(0)

            buffers.add(Triple(vertexBuffer, colorBuffer, ib))
        }
        CharCurveData.forEachIndexed { i, data ->
            data?.let {
                val (vertex, color, index) = it
                // initialize vertex byte buffer for shape coordinates
                val bb = ByteBuffer.allocateDirect(
                    vertex.size * Float.SIZE_BYTES
                ) // (# of coordinate values * 4 bytes per float)
                bb.order(ByteOrder.nativeOrder())
                val vertexBuffer = bb.asFloatBuffer()
                vertexBuffer.put(vertex)
                vertexBuffer.position(0)

                val cb = ByteBuffer.allocateDirect(
                    color.size * Float.SIZE_BYTES
                ) // (# of coordinate values * 4 bytes per float)
                cb.order(ByteOrder.nativeOrder())
                val colorBuffer = cb.asFloatBuffer()
                colorBuffer.put(color)
                colorBuffer.position(0)

                val ib = IntBuffer.allocate(index.size)
                ib.put(index)
                ib.position(0)

                curveBuffers.put(i, Triple(vertexBuffer, colorBuffer, ib))
            }
        }

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
        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES32.glGetUniformLocation(mProgram, "uMVPMatrix")
        checkGlError("glGetUniformLocation")
    }

    fun draw(mvpMatrix: FloatArray?) {
        GLES32.glUseProgram(mProgram) //use the object's shading programs
        // Apply the projection and view transformation
        GLES32.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0)
        checkGlError("glUniformMatrix4fv")
        buffers.forEachIndexed { index, (vertexBuffer, colorBuffer, indexBuffer) ->
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
                CharData[index].third.size,
                GLES32.GL_UNSIGNED_INT,
                indexBuffer
            )
        }
//        curveBuffers.forEach { key, (vertexBuffer, colorBuffer, indexBuffer) ->
//            //set the attribute of the vertex to point to the vertex buffer
//            GLES32.glVertexAttribPointer(
//                mPositionHandle,
//                COORDS_PER_VERTEX,
//                GLES32.GL_FLOAT,
//                false,
//                vertexStride,
//                vertexBuffer
//            )
//            GLES32.glVertexAttribPointer(
//                mColorHandle,
//                COORDS_PER_VERTEX,
//                GLES32.GL_FLOAT,
//                false,
//                colorStride,
//                colorBuffer
//            )
//            // Draw the 3D character A
//            GLES32.glDrawElements(
//                GLES32.GL_TRIANGLES,
//                CharCurveData[key]?.third?.size ?: 0,
//                GLES32.GL_UNSIGNED_INT,
//                indexBuffer
//            )
//        }
    }

    private fun createCurve(controlPtsP: FloatArray, controlPtsQ: FloatArray, curveIndex: Int) {
        val vertices = arrayListOf<Float>()
        val color = arrayListOf<Float>()
        val index = arrayListOf<Int>()
        var vi = 0
        var cIndX = 0
        var indx = 0
        var px = 0
        var x = 0f
        var y = 0f
        var z = .5f
        val noSegments = controlPtsP.size / 2 / 3
        val offset = CharXOffset.getOrNull(curveIndex) ?: 0f
        for (segment in 0 until noSegments) {
            for (temp in 0 until 10) {
                val t = temp / 10.0
                x =
                    ((1 - t).pow(3) * controlPtsP[px + 0] + controlPtsP[px + 2] * 3 * t * (1 - t).pow(
                        2
                    ) + controlPtsP[px + 4] * 3 * t * t * (1 - t) + controlPtsP[px + 6] * t.pow(
                        3
                    )).toFloat()
                y =
                    ((1 - t).pow(3) * controlPtsP[px + 1] + controlPtsP[px + 3] * 3 * t * (1 - t).pow(
                        2
                    ) + controlPtsP[px + 5] * 3 * t * t * (1 - t) + controlPtsP[px + 7] * t.pow(
                        3
                    )).toFloat()
                vertices.add(vi++, x + offset)
                vertices.add(vi++, y)
                vertices.add(vi++, z)
                color.add(cIndX++, 1F)
                color.add(cIndX++, 1F)
                color.add(cIndX++, 1F)
                color.add(cIndX++, 1F)
            }
            px += 6
        }
        px = 0
        var vj = vi
        for (segment in 0 until noSegments) {
            for (temp in 0 until 10) {
                val t = temp / 10.0
                x =
                    ((1 - t).pow(3) * controlPtsQ[px + 0] + controlPtsQ[px + 2] * 3 * t * (1 - t).pow(
                        2
                    ) + controlPtsQ[px + 4] * 3 * t * t * (1 - t) + controlPtsQ[px + 6] * t.pow(
                        3
                    )).toFloat()
                y =
                    ((1 - t).pow(3) * controlPtsQ[px + 1] + controlPtsQ[px + 3] * 3 * t * (1 - t).pow(
                        2
                    ) + controlPtsQ[px + 5] * 3 * t * t * (1 - t) + controlPtsQ[px + 7] * t.pow(
                        3
                    )).toFloat()
                vertices.add(vj++, x + offset)
                vertices.add(vj++, y)
                vertices.add(vj++, z)
                color.add(cIndX++, 1F)
                color.add(cIndX++, 1F)
                color.add(cIndX++, 1F)
                color.add(cIndX++, 1F)
            }
            px += 6
        }
        var noVertices = vj
        var v0 = 0
        var v1 = 1
        var v2 = vi / 3
        var v3 = vi / 3 + 1
        while (v3 < noVertices / 3) {
            index.add(indx++, v0)
            index.add(indx++, v1)
            index.add(indx++, v2)
            index.add(indx++, v1)
            index.add(indx++, v2)
            index.add(indx++, v3)
            v0++
            v1++
            v2++
            v3++
        }
        var vk = noVertices
        var i = 0
        while (i < noVertices) {
            vertices.add(vk++, vertices[i++])
            vertices.add(vk++, vertices[i++])
            vertices.add(vk++, -vertices[i++])
            color.add(cIndX++, 0.25F)
            color.add(cIndX++, 0.25F)
            color.add(cIndX++, 0.25F)
            color.add(cIndX++, 1F)
        }
        noVertices = vk
        v0 = vj / 3
        v1 = vj / 3 + 1
        v2 = (vj + vi) / 3
        v3 = (vj + vi) / 3 + 1
        while (v3 < noVertices / 3) {
            index.add(indx++, v0)
            index.add(indx++, v1)
            index.add(indx++, v2)
            index.add(indx++, v1)
            index.add(indx++, v2)
            index.add(indx++, v3)
            v0++
            v1++
            v2++
            v3++
        }
        v0 = 0
        v1 = 1
        v2 = vj / 3
        v3 = vj / 3 + 1
        while (v3 < (vi + vj) / 3) {
            index.add(indx++, v0)
            index.add(indx++, v1)
            index.add(indx++, v2)
            index.add(indx++, v1)
            index.add(indx++, v2)
            index.add(indx++, v3)
            v0++
            v1++
            v2++
            v3++
        }
        v0 = vi / 3
        v1 = vi / 3 + 1
        v2 = (vj + vi) / 3
        v3 = (vj + vi) / 3 + 1
        while (v3 < noVertices / 3) {
            index.add(indx++, v0)
            index.add(indx++, v1)
            index.add(indx++, v2)
            index.add(indx++, v1)
            index.add(indx++, v2)
            index.add(indx++, v3)
            v0++
            v1++
            v2++
            v3++
        }
        CharCurveData[curveIndex] = Triple(
            vertices.toFloatArray(),
            color.toFloatArray(),
            index.toIntArray()
        )
    }

    companion object {
        // number of coordinates per vertex in this array
        private const val COORDS_PER_VERTEX = 3
        private const val COLOR_PER_VERTEX = 4

        private val CharXOffset = arrayOf(
            -0.9f,
            0.9f,
        )

        private val CharData = arrayOf(
            Triple(CharacterH.CharVertex.copyOf(), CharacterH.CharColor.copyOf(), CharacterH.CharIndex.copyOf()),
            Triple(CharacterL.CharVertex.copyOf(), CharacterL.CharColor.copyOf(), CharacterL.CharIndex.copyOf()),
        ).apply {
            forEachIndexed { index, data ->
                var i = 0
                while (i in data.first.indices) {
                    data.first[i] = data.first[i++] + CharXOffset[index]
                    i += 2
                }
            }
        }

        private val CharCurvePoints = arrayOf(
            null,
            null,
            CharacterP.P.copyOf() to CharacterP.Q.copyOf(),
            null,
            CharacterR.P.copyOf() to CharacterR.Q.copyOf(),
            null,
            null,
            null,
        )

        private val CharCurveData =
            Array<Triple<FloatArray, FloatArray, IntArray>?>(CharCurvePoints.size) { null }
    }

}