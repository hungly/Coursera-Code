package com.bennyplo.capstone2_opengles.gl_object

import android.opengl.GLES32
import com.bennyplo.capstone2_opengles.MyRenderer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer

class Pyramid : GLObject() {

    private val colorBuffer: FloatBuffer by lazy {
        ByteBuffer.allocateDirect(COLORS.size * COLOR_PER_VERTEX).apply {
            order(ByteOrder.nativeOrder())
        }.asFloatBuffer().apply {
            put(COLORS)
            position(0)
        }
    }

    private val indexBuffer: IntBuffer by lazy {
        IntBuffer.allocate(INDEXES.size).apply {
            put(INDEXES)
            position(0)
        }
    }

    private val vertexBuffer: FloatBuffer by lazy {
        // (# of coordinate values * 4 bytes per float)
        ByteBuffer.allocateDirect(vertices.size * 4).apply {
            order(ByteOrder.nativeOrder())
        }.asFloatBuffer().apply {
            put(vertices)
            position(0)
        }
    }

    private val vertices by lazy {
        floatArrayOf(
            -1f * initialScale.first, -1f * initialScale.second, 1f * initialScale.third,
            1f * initialScale.first, -1f * initialScale.second, 1f * initialScale.third,
            1f * initialScale.first, -1f * initialScale.second, -1f * initialScale.third,
            -1f * initialScale.first, -1f * initialScale.second, -1f * initialScale.third,
            0f * initialScale.first, 2f * initialScale.second, 0f * initialScale.third
        )
    }

    private val attenuateHandle: Int
    private val colorStride = COLOR_PER_VERTEX * 4
    private val diffuseColorHandle: Int

    private val fragmentShaderCode =
        "precision mediump float;" +  //define the precision of float
                "varying vec4 vColor;" +
                "varying vec4 vAmbientColor;" +
                "varying vec4 vDiffuseColor;" +
                "varying float vDiffuseLightWeighting;" +
                "varying vec4 vSpecularColor;" +
                "varying float vSpecularLightWeighting; " +
                "varying vec2 vTextureCoordinate;" +
                "uniform bool uUseTexture;" +
                "uniform sampler2D uTextureSampler;" +
                "void main() {" +
                "   vec4 diffuseColor = vDiffuseLightWeighting * vDiffuseColor;" +
                "   vec4 specularColor = vSpecularLightWeighting * vSpecularColor;" +
//                "   gl_FragColor = vec4(vColor.xyz * vAmbientColor, 1) + specularColor + diffuseColor;" +
                "   if(uUseTexture) {" +
                "       vec4 fragmentColor = texture2D(uTextureSampler, vec2(vTextureCoordinate.x, vTextureCoordinate.y));" +
                "       gl_FragColor = fragmentColor + vAmbientColor + specularColor + diffuseColor;" +
                "   } else {" +
                "       gl_FragColor = vColor + vAmbientColor + specularColor + diffuseColor;" +
                "   };" +
                "}" //change the colour based on the variable from the vertex shader

    private val lightLocationHandle: Int
    private val mColorHandle: Int
    private val mMVPMatrixHandle: Int
    private val mNormalHandle: Int
    private val mPositionHandle: Int
    private val mProgram: Int
    private val materialShininessHandle: Int
    private val normalBuffer: FloatBuffer
    private val specularColorHandle: Int
    private val uAmbientColorHandle: Int
    private val useTextureHandle: Int

    private val vertexShaderCode =
        "attribute vec3 aVertexPosition;" +
                "uniform mat4 uMVPMatrix;varying vec4 vColor;" +
                "attribute vec3 aVertexNormal;" +//attribute variable for normal vectors
                "attribute vec4 aVertexColor;" +//attribute variable for vertex colors
                "uniform vec3 uLightSourceLocation;" +//location of the light source (for diffuse and specular light)
                "uniform vec4 uAmbientColor;" +//uniform variable for Ambient color
                "varying vec4 vAmbientColor;" +
                "uniform vec4 uDiffuseColor;" +//color of the diffuse light
                "varying vec4 vDiffuseColor;" +
                "varying float vDiffuseLightWeighting;" +//diffuse light intensity
                "uniform vec3 uAttenuation;" +//light attenuation
                "uniform vec4 uSpecularColor;" +
                "varying vec4 vSpecularColor;" +
                "varying float vSpecularLightWeighting; " +
                "uniform float uMaterialShininess;" +
                "attribute vec2 aTextureCoordinate;" +
                "varying vec2 vTextureCoordinate;" +
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
                "   vTextureCoordinate = aTextureCoordinate;" +
                "}" //get the colour from the application program

    private val vertexStride = COORDS_PER_VERTEX * 4 // 4 bytes per vertex

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

        SpecularColor[0] = 1F
        SpecularColor[1] = 1F
        SpecularColor[2] = 1F
        SpecularColor[3] = 1F

        val nb1 = ByteBuffer.allocateDirect(NORMALS.size * 4)
        nb1.order(ByteOrder.nativeOrder())
        normalBuffer = nb1.asFloatBuffer()
        normalBuffer.put(NORMALS)
        normalBuffer.position(0)

        //////////////////////
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
        MyRenderer.checkGlError("glVertexAttribPointer")
        mColorHandle = GLES32.glGetAttribLocation(mProgram, "aVertexColor")
        GLES32.glEnableVertexAttribArray(mColorHandle)
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
//        mTextureImageHandle = loadTextureFromFile()

        GLES32.glTexParameteri(
            GLES32.GL_TEXTURE_2D,
            GLES32.GL_TEXTURE_MIN_FILTER,
            GLES32.GL_LINEAR
        )

        GLES32.glTexParameteri(
            GLES32.GL_TEXTURE_2D,
            GLES32.GL_TEXTURE_MAG_FILTER,
            GLES32.GL_NEAREST
        )


        useTextureHandle = GLES32.glGetUniformLocation(mProgram, "uUseTexture")
        //---------
        mMVPMatrixHandle = GLES32.glGetUniformLocation(mProgram, "uMVPMatrix")
        MyRenderer.checkGlError("glGetUniformLocation-mMVPMatrixHandle")
    }

    override fun draw(mvpMatrix: FloatArray?) {
        //------------------------
        GLES32.glUseProgram(mProgram) // Add program to OpenGL environment
        //------------------------
        // Apply the projection and view transformation
        GLES32.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0)
        MyRenderer.checkGlError("glUniformMatrix4fv")
        //---------
        GLES32.glUniform3fv(lightLocationHandle, 1, LightLocation, 0)
        GLES32.glUniform4fv(diffuseColorHandle, 1, DiffuseColor, 0)
        GLES32.glUniform3fv(attenuateHandle, 1, Attenuation, 0)
        GLES32.glUniform4f(uAmbientColorHandle, 0.1f, 0.1f, 0.1f, 1f)
        GLES32.glUniform4fv(specularColorHandle, 1, SpecularColor, 0)
        GLES32.glUniform1f(materialShininessHandle, MaterialShininess)

        GLES32.glDisable(GLES32.GL_BLEND)
        GLES32.glDisable(GLES32.GL_CULL_FACE)

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
            COLOR_PER_VERTEX,
            GLES32.GL_FLOAT,
            false,
            colorStride,
            colorBuffer
        )

        GLES32.glVertexAttribPointer(
            mNormalHandle,
            COORDS_PER_VERTEX,
            GLES32.GL_FLOAT,
            false,
            vertexStride,
            normalBuffer
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
        //---------
        GLES32.glUniform1i(useTextureHandle, 0)
        //---------
        // Draw the sphere
        GLES32.glDrawElements(
            GLES32.GL_TRIANGLES,
            INDEXES.size,
            GLES32.GL_UNSIGNED_INT,
            indexBuffer
        )
    }

    companion object {
        // number of coordinates per vertex in this array
        private const val COORDS_PER_VERTEX = 3
        private const val COLOR_PER_VERTEX = 4

        private val LightLocation = FloatArray(3)
        private val Attenuation = FloatArray(3)
        private val DiffuseColor = FloatArray(4)
        private val SpecularColor = FloatArray(4)
        private const val MaterialShininess = 5F

        private val COLORS = floatArrayOf(
            1f, 0f, 0f, 1f,
            1f, 0f, 0f, 1f,
            1f, 0f, 0f, 1f,
            1f, 0f, 0f, 1f,
            1f, 0f, 0f, 1f,
        )
        private val INDEXES = intArrayOf(
            0, 1, 4,
            1, 2, 4,
            2, 3, 4,
            3, 0, 4,
//            0, 2, 1,
//            0, 3, 2
        )

        private val NORMALS = floatArrayOf(
            -1f, -1f, 1f,
            1f, -1f, 1f,
            1f, -1f, -1f,
            -1f, -1f, -1f,
            0f, 1f, 0f
        )
    }

}