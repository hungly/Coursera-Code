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
        "attribute vec3 aVertexPosition;" +
                "uniform mat4 uMVPMatrix;varying vec4 vColor;" +
                "attribute vec3 aVertexNormal;" +//attribute variable for normal vectors
                "attribute vec4 aVertexColor;" +//attribute variable for vertex colors
                "uniform vec3 uLightSourceLocation;" +//location of the light source (for diffuse and specular light)
                "uniform vec3 uAmbientColor;" +//uniform variable for Ambient color
                "varying vec3 vAmbientColor;" +
                "uniform vec4 uDiffuseColor;" +//color of the diffuse light
                "varying vec4 vDiffuseColor;" +
                "varying float vDiffuseLightWeighting;" +//diffuse light intensity
                "uniform vec3 uAttenuation;" +//light attenuation
                "uniform vec4 uSpecularColor;" +
                "varying vec4 vSpecularColor;" +
                "varying float vSpecularLightWeighting; " +
                "uniform float uMaterialShininess;" +
                //----------
                "void main() {" +
                "   gl_Position = uMVPMatrix *vec4(aVertexPosition, 1.0);" +
                "   vec4 mvPosition = uMVPMatrix * vec4(aVertexPosition, 1.0);" +
                "   vec3 lightDirection = normalize(uLightSourceLocation - mvPosition.xyz);" +
                "   vec3 transformedNormal = normalize((uMVPMatrix * vec4(aVertexNormal, 0.0)).xyz);" +
                "   vAmbientColor = uAmbientColor;" +
                "   vDiffuseColor = uDiffuseColor;" +
                "   vSpecularColor = uSpecularColor; " +
                "   vec3 eyeDirection = normalize(-mvPosition.xyz);" +
                "   vec3 reflectionDirection = reflect(-lightDirection, transformedNormal);" +
                "   vec3 vertexToLightSource = mvPosition.xyz - uLightSourceLocation;" +
                "   float diff_light_dist = length(vertexToLightSource);" +
                "   float attenuation = 1.0 / (uAttenuation.x" +
                "                           + uAttenuation.y * diff_light_dist" +
                "                           + uAttenuation.z * diff_light_dist * diff_light_dist);" +
                "   vDiffuseLightWeighting = attenuation*max(dot(transformedNormal,lightDirection),0.0);" +
                "   vSpecularLightWeighting = attenuation*pow(max(dot(reflectionDirection, eyeDirection), 0.0), uMaterialShininess);" +
                "   vColor = aVertexColor;" +
                "}" //get the colour from the application program
    private val fragmentShaderCode =
        "precision mediump float;" +  //define the precision of float
                "varying vec4 vColor;" +
                "varying vec3 vAmbientColor;" +
                "varying vec4 vDiffuseColor;" +
                "varying float vDiffuseLightWeighting;" +
                "varying vec4 vSpecularColor;" +
                "varying float vSpecularLightWeighting; " +
                "void main() {" +
                "   vec4 diffuseColor = vDiffuseLightWeighting * vDiffuseColor;" +
                "   vec4 specularColor = vSpecularLightWeighting * vSpecularColor;" +
                "   gl_FragColor = vec4(vColor.xyz * vAmbientColor, 1) + specularColor + diffuseColor;" +
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

//    private val pointLightingLocationHandle: Int

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

    private lateinit var sphere1Normal: FloatArray
    private lateinit var sphere2Normal: FloatArray
    private lateinit var ringNormal: FloatArray

    private val normal1Buffer: FloatBuffer
    private val normal2Buffer: FloatBuffer
    private val ringNormalBuffer: FloatBuffer

    private val mNormalHandle: Int
    private val diffuseColorHandle: Int
    private val lightLocationHandle: Int
    private val uAmbientColorHandle: Int
    private val specularColorHandle: Int
    private val materialShininessHandle: Int
    private val attenuateHandle: Int

    private fun createSphere(radius: Float, noLatitude: Int, noLongitude: Int) {
        val normals1 = FloatArray(65535)
        val normals2 = FloatArray(65535)
        val ringNormals = FloatArray(65535)
        var normal1Indx = 0
        var normal2Indx = 0
        var ringNormIndx = 0

        val vertices = FloatArray(65535)
        val index = IntArray(65535)
        val color = FloatArray(65535)
        var pNormLen = (noLongitude + 1) * 3 * 3
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

                    ringNormals[ringNormIndx++] = (radius * x).toFloat()
                    ringNormals[ringNormIndx++] = (radius * cosTheta).toFloat() + dist
                    ringNormals[ringNormIndx++] = (radius * z).toFloat()
                }
                if (row == 15) {
                    ringVertices[rVIndx++] = (radius * x).toFloat() / 2
                    ringVertices[rVIndx++] = (radius * cosTheta).toFloat() / 2 + 0.2f * dist
                    ringVertices[rVIndx++] = (radius * z).toFloat() / 2
                    ringColor[rCIndex++] = 1f
                    ringColor[rCIndex++] = abs(tColor)
                    ringColor[rCIndex++] = 0f
                    ringColor[rCIndex++] = 1f

                    ringNormals[ringNormIndx++] = (radius * x).toFloat() / 2
                    ringNormals[ringNormIndx++] = (radius * cosTheta).toFloat() / 2 + 0.2f * dist
                    ringNormals[ringNormIndx++] = (radius * z).toFloat() / 2
                }
                if (row == 10) {
                    ringVertices[rVIndx++] = (radius * x).toFloat() / 2
                    ringVertices[rVIndx++] = (radius * cosTheta).toFloat() / 2 - 0.1f * dist
                    ringVertices[rVIndx++] = (radius * z).toFloat() / 2
                    ringColor[rCIndex++] = 0f
                    ringColor[rCIndex++] = 1f
                    ringColor[rCIndex++] = abs(tColor)
                    ringColor[rCIndex++] = 1f

                    ringNormals[ringNormIndx++] = (radius * x).toFloat() / 2
                    ringNormals[ringNormIndx++] = (radius * cosTheta).toFloat() / 2 - 0.1f * dist
                    ringNormals[ringNormIndx++] = (radius * z).toFloat() / 2
                }
                if (row == 20) {
                    ringVertices[pLen++] = (radius * x).toFloat()
                    ringVertices[pLen++] = (-radius * cosTheta).toFloat() - dist
                    ringVertices[pLen++] = (radius * z).toFloat()
                    ringColor[pColorLen++] = 0f
                    ringColor[pColorLen++] = 1f
                    ringColor[pColorLen++] = abs(tColor)
                    ringColor[pColorLen++] = 1f

                    ringNormals[pNormLen++] = (radius * x).toFloat()
                    ringNormals[pNormLen++] = (-radius * cosTheta).toFloat() - dist
                    ringNormals[pNormLen++] = (radius * z).toFloat()
                    //-------
                }
                tColor += tColorInc

                normals1[normal1Indx++] = (radius * x).toFloat()
                normals1[normal1Indx++] = (radius * cosTheta).toFloat() + dist
                normals1[normal1Indx++] = (radius * z).toFloat()
                normals2[normal2Indx++] = (radius * x).toFloat()
                normals2[normal2Indx++] = (radius * cosTheta).toFloat() - dist
                normals2[normal2Indx++] = (radius * z).toFloat()
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

        ringNormIndx = (noLongitude + 1) * 3 * 4

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

        sphere1Normal = normals1.copyOf(normal1Indx)
        sphere2Normal = normals2.copyOf(normal2Indx)
        ringNormal = ringNormals.copyOf(ringNormIndx)
    }

    init {
        LightLocation[0] = 2F
        LightLocation[1] = 2F
        LightLocation[2] = 0F

        DiffuseColor[0] = 1F
        DiffuseColor[1] = 1F
        DiffuseColor[2] = 1F
        DiffuseColor[3] = 1F

        Attenuation[0] = 1F
        Attenuation[1] = 0.14F
        Attenuation[2] = 0.07F

        SpecularColor[0] = 0F
        SpecularColor[1] = 0F
        SpecularColor[2] = 1F
        SpecularColor[3] = 1F

        createSphere(2f, 30, 30)

        val nb1 = ByteBuffer.allocateDirect(sphere1Normal.size * 4)
        nb1.order(ByteOrder.nativeOrder())
        normal1Buffer = nb1.asFloatBuffer()
        normal1Buffer.put(sphere1Normal)
        normal1Buffer.position(0)

        val nb2 = ByteBuffer.allocateDirect(sphere2Normal.size * 4)
        nb2.order(ByteOrder.nativeOrder())
        normal2Buffer = nb2.asFloatBuffer()
        normal2Buffer.put(sphere2Normal)
        normal2Buffer.position(0)

        val rnb = ByteBuffer.allocateDirect(ringNormal.size * 4)
        rnb.order(ByteOrder.nativeOrder())
        ringNormalBuffer = rnb.asFloatBuffer()
        ringNormalBuffer.put(ringNormal)
        ringNormalBuffer.position(0)

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
        MyRenderer.checkGlError("check - load shader")
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
        mNormalHandle = GLES32.glGetAttribLocation(mProgram, "aVertexNormal")
        GLES32.glEnableVertexAttribArray(mNormalHandle)
        MyRenderer.checkGlError("check - glGetAttribLocation - aVertexNormal")

        lightLocationHandle = GLES32.glGetUniformLocation(mProgram, "uLightSourceLocation")
        diffuseColorHandle = GLES32.glGetUniformLocation(mProgram, "uDiffuseColor")
        attenuateHandle = GLES32.glGetUniformLocation(mProgram, "uAttenuation")
        uAmbientColorHandle = GLES32.glGetUniformLocation(mProgram, "uAmbientColor")
        // MyRenderer.checkGlError("uAmbientColor")
        specularColorHandle = GLES32.glGetUniformLocation(mProgram, "uSpecularColor")
        materialShininessHandle = GLES32.glGetUniformLocation(mProgram, "uMaterialShininess")
        // MyRenderer.checkGlError("glGetUniformLocation-mMVPMatrixHandle")
        //---------
        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES32.glGetUniformLocation(mProgram, "uMVPMatrix")
    }

    fun draw(mvpMatrix: FloatArray?) {
        // Apply the projection and view transformation
        GLES32.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0)
        //---------
        GLES32.glUniform3fv(lightLocationHandle, 1, LightLocation, 0);
        GLES32.glUniform4fv(diffuseColorHandle, 1, DiffuseColor, 0);
        GLES32.glUniform3fv(attenuateHandle, 1, Attenuation, 0);
        GLES32.glUniform3f(uAmbientColorHandle, 0.6f, 0.6f, 0.6f);
        GLES32.glUniform4fv(specularColorHandle, 1, SpecularColor, 0);
        GLES32.glUniform1f(materialShininessHandle, MaterialShininess);

        GLES32.glVertexAttribPointer(
            mNormalHandle,
            COORDS_PER_VERTEX,
            GLES32.GL_FLOAT,
            false,
            vertexStride,
            normal1Buffer
        )

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

        GLES32.glVertexAttribPointer(mNormalHandle, COORDS_PER_VERTEX, GLES32.GL_FLOAT, false, vertexStride, normal2Buffer)

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

        GLES32.glVertexAttribPointer(mNormalHandle, COORDS_PER_VERTEX, GLES32.GL_FLOAT, false, vertexStride, ringNormalBuffer)

        GLES32.glDrawElements(
            GLES32.GL_TRIANGLES,
            ringIndex.size,
            GLES32.GL_UNSIGNED_INT,
            ringIndexBuffer
        )
    }

    fun setLightLocation(pX:Float, pY:Float, pZ:Float) {
        LightLocation[0] = pX
        LightLocation[1] = pY
        LightLocation[2] = pZ
    }

    companion object {
        //---------
        // number of coordinates per vertex in this array
        const val COORDS_PER_VERTEX = 3
        const val COLOR_PER_VERTEX = 4

        private val LightLocation = FloatArray(3)
        private val Attenuation = FloatArray(3)
        private val DiffuseColor = FloatArray(4)
        private val SpecularColor = FloatArray(4)
        private const val MaterialShininess = 5F
    }
}