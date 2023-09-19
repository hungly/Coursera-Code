package com.bennyplo.virtualreality

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES32
import android.opengl.GLUtils
import com.bennyplo.designgraphicswithopengl.R
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer
import kotlin.math.cos
import kotlin.math.sin

class WorldSphere(private val context: Context) {

    private val attenuateHandle: Int
    private val colorBuffer: FloatBuffer
    private val colorStride = COLOR_PER_VERTEX * 4

    //--------
    private val diffuseColorHandle: Int

    private val diffuseLightLocationHandle: Int

    private val fragmentShaderCode =
        "precision lowp float;" +
                "varying vec4 vColor; " +
                "varying vec3 vLightWeighting;" +
                "varying vec4 vDiffuseColor;" +
                "varying float vDiffuseLightWeighting;" +
                "varying float vPointLightWeighting;" +
                "varying vec4 vSpecularColor;" +
                "varying float vSpecularLightWeighting; " +
                "varying vec2 vTextureCoordinate;" +
                "uniform sampler2D uTextureSampler;" +  //texture
                "void main() {" +
                "   vec4 diffuseColor = vDiffuseLightWeighting * vDiffuseColor;" +
                "   vec4 specularColor = vSpecularLightWeighting * vSpecularColor;" +
                "   vec4 fragmentColor = texture2D(uTextureSampler, vec2(vTextureCoordinate.s, vTextureCoordinate.t));" +  //load the color texture
                "   gl_FragColor = vec4(fragmentColor.rgb * vLightWeighting, fragmentColor.a) + specularColor + diffuseColor;" +  //the fragment color
                "}"

    private val indexBuffer: IntBuffer
    private val mColorHandle: Int
    private val mMVPMatrixHandle: Int
    private val mNormalHandle: Int
    private val mPositionHandle: Int
    private val mProgram: Int

    //--------
    private val mTextureCoordHandle: Int

    private val materialShininessHandle: Int
    private val normalBuffer: FloatBuffer
    private val pointLightColorHandle: Int
    private val pointLightingLocationHandle: Int
    private val specularColorHandle: Int
    private val specularLightLocationHandle: Int
    private val textureBuffer: FloatBuffer

    //--------
    private val textureDataHandle: Int

    private val textureHandle: Int
    private val textureStride = TEXTURE_PER_VERTEX * 4 //bytes per texture coordinates
    private val uAmbientColorHandle: Int
    private val vertexBuffer: FloatBuffer

    private val vertexShaderCode =
        "attribute vec3 aVertexPosition;" +
                "uniform mat4 uMVPMatrix;varying vec4 vColor;" +
                "attribute vec3 aVertexNormal;" +
                "attribute vec4 aVertexColor;" +
                "uniform vec3 uPointLightingLocation;" +
                "uniform vec3 uPointLightingColor;" +
                "uniform vec3 uAmbientColor;" +
                "varying vec3 vLightWeighting;" +
                "uniform vec3 uDiffuseLightLocation;" +
                "uniform vec4 uDiffuseColor;" +  //color of the diffuse light
                "varying vec4 vDiffuseColor;" +
                "varying float vPointLightWeighting;" +
                "varying float vDiffuseLightWeighting;" +
                "uniform vec3 uAttenuation;" +  //light attenuation
                "uniform vec4 uSpecularColor;" +
                "varying vec4 vSpecularColor;" +
                "varying float vSpecularLightWeighting; " +
                "uniform vec3 uSpecularLightLocation;" +
                "uniform float uMaterialShininess;" +
                "attribute vec2 aTextureCoordinate; " +  //texture coordinate
                "varying vec2 vTextureCoordinate;" +
                "void main() {" +
                "   gl_Position = uMVPMatrix * vec4(aVertexPosition, 1.0);" +
                "   vLightWeighting = vec3(1.0, 1.0, 1.0);     " +
                "   vec4 mvPosition = uMVPMatrix * vec4(aVertexPosition, 1.0);" +
                "   vec3 lightDirection = normalize(uPointLightingLocation - mvPosition.xyz);" +
                "   vec3 diffuseLightDirection = normalize(uDiffuseLightLocation - mvPosition.xyz);" +
                "   vec3 transformedNormal = normalize((uMVPMatrix * vec4(aVertexNormal, 0.0)).xyz);" +
                "   vLightWeighting = uAmbientColor;" +
                "   gl_PointSize = 40.0;" +
                "   vDiffuseColor = uDiffuseColor;" +
                "   vSpecularColor = uSpecularColor; " +
                "   float specularLightWeighting = 0.0;" +
                "   vec3 eyeDirection = normalize(-mvPosition.xyz);" +
                "   vec3 specularlightDirection = normalize(uSpecularLightLocation - mvPosition.xyz);" +
                "   vec3 inverseLightDirection = normalize(uPointLightingLocation);" +
                "   vec3 reflectionDirection = reflect(-lightDirection, transformedNormal);" +
                "   vPointLightWeighting = distance(uPointLightingLocation, mvPosition.xyz);" +
                "   vPointLightWeighting = 10.0 / (vPointLightWeighting * vPointLightWeighting);" +
                "   vec3 vertexToLightSource = mvPosition.xyz - uPointLightingLocation;" +
                "   float diff_light_dist = length(vertexToLightSource);" +
                "   float attenuation = 1.0 / (uAttenuation.x" +
                "                           + uAttenuation.y * diff_light_dist" +
                "                           + uAttenuation.z * diff_light_dist * diff_light_dist);" +
                "   float diffuseLightWeighting = 0.0;" +
                "   diffuseLightWeighting = attenuation*max(dot(transformedNormal, lightDirection), 0.0);" +
                "   vDiffuseLightWeighting = diffuseLightWeighting;" +
                "   specularLightWeighting = attenuation*pow(max(dot(reflectionDirection,eyeDirection), 0.0), uMaterialShininess);" +
                "   vSpecularLightWeighting = specularLightWeighting;" +
                "   vColor=aVertexColor;" +
                "   vTextureCoordinate = aTextureCoordinate;" +
                "}"

    //---------
    private val vertexStride = COORDS_PER_VERTEX * 4 // 4 bytes per vertex

    init {
        createSphere(5f, 30, 30)
        // initialize vertex byte buffer for shape coordinates
        val bb =
            ByteBuffer.allocateDirect(SphereVertex.size * 4) // (# of coordinate values * 4 bytes per float)
        bb.order(ByteOrder.nativeOrder())
        vertexBuffer = bb.asFloatBuffer()
        vertexBuffer.put(SphereVertex)
        vertexBuffer.position(0)
        val ib = IntBuffer.allocate(SphereIndex.size)
        indexBuffer = ib
        indexBuffer.put(SphereIndex)
        indexBuffer.position(0)
        val cb = ByteBuffer.allocateDirect(SphereColor.size * 4)
        cb.order(ByteOrder.nativeOrder())
        colorBuffer = cb.asFloatBuffer()
        colorBuffer.put(SphereColor)
        colorBuffer.position(0)
        val nb =
            ByteBuffer.allocateDirect(SphereNormal.size * 4) // (# of coordinate values * 4 bytes per float)
        nb.order(ByteOrder.nativeOrder())
        normalBuffer = nb.asFloatBuffer()
        normalBuffer.put(SphereNormal)
        normalBuffer.position(0)
        ///============
        val tb = ByteBuffer.allocateDirect(TextureCoordinateData.size * 4)
        tb.order(ByteOrder.nativeOrder())
        textureBuffer = tb.asFloatBuffer()
        textureBuffer.put(TextureCoordinateData)
        textureBuffer.position(0)
        ///============
        LightLocation[0] = 10f
        LightLocation[1] = 10f
        LightLocation[2] = 10f
        DiffuseLightLocation[0] = 2f
        DiffuseLightLocation[1] = 0.2f
        DiffuseLightLocation[2] = 2f
        SpecularColor[0] = 1f
        SpecularColor[1] = 1f
        SpecularColor[2] = 1f
        SpecularColor[3] = 1f
        SpecularLightLocation[0] = -7f
        SpecularLightLocation[1] = -4f
        SpecularLightLocation[2] = 2f
        ///============
        textureDataHandle = loadTexture(context, R.drawable.campus)
        //////////////////////
        // prepare shaders and OpenGL program
        val vertexShader: Int =
            MyRenderer.Companion.loadShader(GLES32.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader: Int =
            MyRenderer.Companion.loadShader(GLES32.GL_FRAGMENT_SHADER, fragmentShaderCode)
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
        MyRenderer.Companion.checkGlError("glVertexAttribPointer")
        mColorHandle = GLES32.glGetAttribLocation(mProgram, "aVertexColor")
        GLES32.glEnableVertexAttribArray(mColorHandle)
        GLES32.glVertexAttribPointer(
            mColorHandle,
            COLOR_PER_VERTEX,
            GLES32.GL_FLOAT,
            false,
            colorStride,
            colorBuffer
        )
        mNormalHandle = GLES32.glGetAttribLocation(mProgram, "aVertexNormal")
        GLES32.glEnableVertexAttribArray(mNormalHandle)
        GLES32.glVertexAttribPointer(
            mNormalHandle,
            COORDS_PER_VERTEX,
            GLES32.GL_FLOAT,
            false,
            vertexStride,
            normalBuffer
        )
        MyRenderer.Companion.checkGlError("glVertexAttribPointer")
        // get handle to shape's transformation matrix
        pointLightingLocationHandle =
            GLES32.glGetUniformLocation(mProgram, "uPointLightingLocation")
        diffuseLightLocationHandle = GLES32.glGetUniformLocation(mProgram, "uDiffuseLightLocation")
        diffuseColorHandle = GLES32.glGetUniformLocation(mProgram, "uDiffuseColor")
        DiffuseColor[0] = 1f
        DiffuseColor[1] = 1f
        DiffuseColor[2] = 1f
        DiffuseColor[3] = 1f
        attenuateHandle = GLES32.glGetUniformLocation(mProgram, "uAttenuation")
        Attenuation[0] = 1f
        Attenuation[1] = 0.14f
        Attenuation[2] = 0.07f
        pointLightColorHandle = GLES32.glGetUniformLocation(mProgram, "uPointLightingColor")
        uAmbientColorHandle = GLES32.glGetUniformLocation(mProgram, "uAmbientColor")
        MyRenderer.Companion.checkGlError("uAmbientColor")
        specularColorHandle = GLES32.glGetUniformLocation(mProgram, "uSpecularColor")
        specularLightLocationHandle =
            GLES32.glGetUniformLocation(mProgram, "uSpecularLightLocation")
        materialShininessHandle = GLES32.glGetUniformLocation(mProgram, "uMaterialShininess")
        mTextureCoordHandle =
            GLES32.glGetAttribLocation(mProgram, "aTextureCoordinate") //texture coordinates
        GLES32.glEnableVertexAttribArray(mTextureCoordHandle)
        GLES32.glVertexAttribPointer(
            mTextureCoordHandle,
            TEXTURE_PER_VERTEX,
            GLES32.GL_FLOAT,
            false,
            textureStride,
            textureBuffer
        )
        textureHandle = GLES32.glGetUniformLocation(mProgram, "uTextureSampler") //texture
        mMVPMatrixHandle = GLES32.glGetUniformLocation(mProgram, "uMVPMatrix")
        MyRenderer.Companion.checkGlError("glGetUniformLocation-mMVPMatrixHandle")
    }

    fun draw(mvpMatrix: FloatArray?) {
        GLES32.glUseProgram(mProgram) // Add program to OpenGL environment
        // Apply the projection and view transformation
        GLES32.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0)
        MyRenderer.checkGlError("glUniformMatrix4fv")
        GLES32.glUniform3fv(pointLightingLocationHandle, 1, LightLocation, 0)
        GLES32.glUniform3fv(diffuseLightLocationHandle, 1, DiffuseLightLocation, 0)
        GLES32.glUniform4fv(diffuseColorHandle, 1, DiffuseColor, 0)
        GLES32.glUniform3fv(attenuateHandle, 1, Attenuation, 0)
        GLES32.glUniform3f(pointLightColorHandle, 0.3f, 0.3f, 0.3f)
        GLES32.glUniform3f(uAmbientColorHandle, 0.6f, 0.6f, 0.6f)
        GLES32.glUniform4fv(specularColorHandle, 1, SpecularColor, 0)
        GLES32.glUniform1f(materialShininessHandle, MaterialShininess)
        GLES32.glUniform3fv(specularLightLocationHandle, 1, SpecularLightLocation, 0)
        //===================
        GLES32.glActiveTexture(GLES32.GL_TEXTURE0) //set the active texture to unit 0
        GLES32.glBindTexture(
            GLES32.GL_TEXTURE_2D,
            textureDataHandle
        ) //bind the texture to this unit
        GLES32.glUniform1i(textureHandle, 0) //tell the uniform sampler to use this texture i
        GLES32.glVertexAttribPointer(
            mTextureCoordHandle,
            TEXTURE_PER_VERTEX,
            GLES32.GL_FLOAT,
            false,
            textureStride,
            textureBuffer
        )
        //===================
        //set the attribute of the vertex to point to the vertex buffer
        GLES32.glVertexAttribPointer(
            mPositionHandle, COORDS_PER_VERTEX,
            GLES32.GL_FLOAT, false, vertexStride, vertexBuffer
        )
        GLES32.glVertexAttribPointer(
            mColorHandle, COLOR_PER_VERTEX,
            GLES32.GL_FLOAT, false, colorStride, colorBuffer
        )
        GLES32.glVertexAttribPointer(
            mNormalHandle, COORDS_PER_VERTEX,
            GLES32.GL_FLOAT, false, vertexStride, normalBuffer
        )
        // Draw the sphere
        GLES32.glDrawElements(
            GLES32.GL_TRIANGLES,
            SphereIndex.size,
            GLES32.GL_UNSIGNED_INT,
            indexBuffer
        )
    }

    fun setLightLocation(px: Float, py: Float, pz: Float) {
        LightLocation[0] = px
        LightLocation[1] = py
        LightLocation[2] = pz
    }

    //--------s
    private fun createSphere(radius: Float, noLatitude: Int, noLongitude: Int) {
        val vertices = FloatArray(65535)
        val normal = FloatArray(65535)
        val pIndex = IntArray(65535)
        val pColor = FloatArray(65535)
        val textureCoordData = FloatArray(65535)
        var vertexIndex = 0
        var normIndex = 0
        var colorIndex = 0
        var textureIndex = 0
        var indx = 0
        val dist = 0f
        for (row in 0..noLatitude) {
            val theta = row * Math.PI / noLatitude
            val sinTheta = sin(theta)
            val cosTheta = cos(theta)
            for (col in 0..noLongitude) {
                val phi = col * 2 * Math.PI / noLongitude
                val sinPhi = sin(phi)
                val cosPhi = cos(phi)
                val x = cosPhi * sinTheta
                val z = sinPhi * sinTheta
                normal[normIndex++] = x.toFloat()
                normal[normIndex++] = cosTheta.toFloat()
                normal[normIndex++] = z.toFloat()
                vertices[vertexIndex++] = (radius * x).toFloat()
                vertices[vertexIndex++] = (radius * cosTheta).toFloat() + dist
                vertices[vertexIndex++] = (radius * z).toFloat()
                pColor[colorIndex++] = 1f
                pColor[colorIndex++] = 0f
                pColor[colorIndex++] = 0f
                pColor[colorIndex++] = 1f
                val u = ((col / noLongitude.toFloat() - 0.5f) * -1) + 0.5f
                val v = row / noLatitude.toFloat()
                textureCoordData[textureIndex++] = u
                textureCoordData[textureIndex++] = v
            }
        }
        for (row in 0 until noLatitude) {
            for (col in 0 until noLongitude) {
                val first = row * (noLongitude + 1) + col
                val second = first + noLongitude + 1
                pIndex[indx++] = first
                pIndex[indx++] = second
                pIndex[indx++] = first + 1
                pIndex[indx++] = second
                pIndex[indx++] = second + 1
                pIndex[indx++] = first + 1
            }
        }
        SphereVertex = vertices.copyOf(vertexIndex)
        SphereIndex = pIndex.copyOf(indx)
        SphereNormal = normal.copyOf(normIndex)
        SphereColor = pColor.copyOf(colorIndex)
        TextureCoordinateData = textureCoordData.copyOf(textureIndex)
    }

    private fun loadTexture(
        context: Context,
        resourceId: Int
    ): Int { //load texture image from resources
        val textureHandle = IntArray(1)
        GLES32.glGenTextures(1, textureHandle, 0)
        if (textureHandle[0] != 0) {
            val options = BitmapFactory.Options()
            options.inScaled = false
            val bitmap = BitmapFactory.decodeResource(context.resources, resourceId, options)
            GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, textureHandle[0])
            //set filtering
            GLES32.glTexParameteri(
                GLES32.GL_TEXTURE_2D,
                GLES32.GL_TEXTURE_MIN_FILTER,
                GLES32.GL_NEAREST
            )
            GLES32.glTexParameteri(
                GLES32.GL_TEXTURE_2D,
                GLES32.GL_TEXTURE_MAG_FILTER,
                GLES32.GL_NEAREST
            )
            //load bitmap into bound texture
            GLUtils.texImage2D(GLES32.GL_TEXTURE_2D, 0, bitmap, 0)
            bitmap.recycle()
        } else {
            throw RuntimeException("Error loading texture!")
        }
        return textureHandle[0]
    }

    companion object {
        //--------
        // number of coordinates per vertex in this array
        private const val COORDS_PER_VERTEX = 3
        private const val COLOR_PER_VERTEX = 4

        //---------
        const val TEXTURE_PER_VERTEX = 2 //no of texture coordinates per vertex
        private lateinit var SphereVertex: FloatArray
        private lateinit var SphereColor: FloatArray
        private lateinit var SphereIndex: IntArray
        private lateinit var SphereNormal: FloatArray
        private var LightLocation = FloatArray(3)
        private var DiffuseLightLocation = FloatArray(3)
        private var Attenuation = FloatArray(3) //light attenuation
        private var DiffuseColor = FloatArray(4) //diffuse light colour
        private var SpecularColor = FloatArray(4) //specular highlight colour
        private var MaterialShininess = 10f //material shininess
        private var SpecularLightLocation = FloatArray(3) //specular light location

        //--------
        private lateinit var TextureCoordinateData: FloatArray
    }

}