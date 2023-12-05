package com.bennyplo.capstone3.model

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES32
import android.opengl.GLUtils
import com.bennyplo.capstone3.MyRenderer

abstract class GLObject {

    internal abstract var textureRatio: Float

    internal val colorHandle: Int by lazy {
        GLES32.glGetAttribLocation(program, "aVertexColor")
    }

    internal val normalHandle: Int by lazy {
        GLES32.glGetAttribLocation(program, "aVertexNormal")
    }

    internal val attenuateHandle: Int by lazy {
        GLES32.glGetUniformLocation(program, "uAttenuation")
    }

    internal val ambientColorHandle: Int by lazy {
        GLES32.glGetUniformLocation(program, "uAmbientColor")
    }

    internal val diffuseColorHandle: Int by lazy {
        GLES32.glGetUniformLocation(program, "uDiffuseColor")
    }

    internal val diffuseLightLocationHandle: Int by lazy {
        GLES32.glGetUniformLocation(program, "uDiffuseLightLocation")
    }

    internal val positionHandle: Int by lazy {
        // Get handle to vertex shader's vPosition member
        GLES32.glGetAttribLocation(program, "aVertexPosition")
    }

    internal val program: Int by lazy {
        GLES32.glCreateProgram() // Create empty OpenGL Program
    }

    internal val textureCoordinateHandle: Int by lazy {
        GLES32.glGetAttribLocation(program, "aTextureCoordinate")
    }

    internal val textureSamplerHandle: Int by lazy {
        GLES32.glGetUniformLocation(program, "uTextureSampler")
    }

    internal val useTextureHandle: Int by lazy {
        GLES32.glGetUniformLocation(program, "uUseTexture")
    }

    private val mVPMatrixHandle: Int by lazy {
        // Get handle to shape's transformation matrix
        GLES32.glGetUniformLocation(program, "uMVPMatrix")
    }

    private val lightModelMatrixHandle: Int by lazy {
        // Get handle to shape's transformation matrix
        GLES32.glGetUniformLocation(program, "uMMatrix")
    }

    internal abstract val textureImageHandler: Int?
    var initialRotation = Rotation(xRotation = 0.0F, yRotation = 0.0F, zRotation = 0.0F)
    var initialScale = Scale(xScale = 1.0F, yScale = 1.0F, zScale = 1.0F)

    var initialTranslation = Translation(
        xTranslation = 0.0F,
        yTranslation = 0.0F,
        zTranslation = 0.0F
    )

    init {
        // Prepare shaders and OpenGL program
        val vertexShader = MyRenderer.loadShader(GLES32.GL_VERTEX_SHADER, VERTEX_SHADER_CODE)
        val fragmentShader = MyRenderer.loadShader(GLES32.GL_FRAGMENT_SHADER, FRAGMENT_SHADER_CODE)
        GLES32.glAttachShader(program, vertexShader) // Add the vertex shader to program
        GLES32.glAttachShader(program, fragmentShader) // Add the fragment shader to program
        GLES32.glLinkProgram(program) // Link the  OpenGL program to create an executable
        // Enable a handle to the triangle vertices
        GLES32.glEnableVertexAttribArray(positionHandle)
        // Enable a handle to the colour
        GLES32.glEnableVertexAttribArray(colorHandle)
        // Enable a handle to the texture coordinates
        GLES32.glEnableVertexAttribArray(textureCoordinateHandle)
        // Enable a handle to the normal
        GLES32.glEnableVertexAttribArray(normalHandle)
        MyRenderer.checkGlError("glGetUniformLocation")
    }

    open fun draw(mvpMatrix: FloatArray?, mLightModelMatrix: FloatArray?) {
        GLES32.glUseProgram(program) // Add program to OpenGL environment

        GLES32.glDisable(GLES32.GL_BLEND)
        GLES32.glDisable(GLES32.GL_CULL_FACE)

        GLES32.glUniform1i(useTextureHandle, 0)

        // Apply the projection and view transformation
        GLES32.glUniformMatrix4fv(mVPMatrixHandle, 1, false, mvpMatrix, 0)

        GLES32.glUniformMatrix4fv(lightModelMatrixHandle, 1, false, mLightModelMatrix, 0)

        MyRenderer.checkGlError("glUniformMatrix4fv")
    }

    internal fun calculateDefaultNormalMap(verticesArray: FloatArray): FloatArray {
        val normals = arrayListOf<Float>()
        repeat(verticesArray.size / COORDINATES_PER_VERTEX) {
            normals.add(0.0F)
            normals.add(0.0F)
            normals.add(1.0F)
        }
        return normals.toFloatArray()
    }

    internal fun loadTextureFromResource(resourceId: Int, context: Context?): Int {
        val textureHandle = IntArray(1)
        GLES32.glGenTextures(1, textureHandle, 0)

        if (textureHandle[0] != 0) {
            val options = BitmapFactory.Options()
            options.inScaled = false // No pre-scaling
            // Read in the resource
            val bitmap = BitmapFactory.decodeResource(context?.resources, resourceId, options)
            // Bind to the texture in OpenGL
            GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, textureHandle[0])

            textureRatio = bitmap.width.toFloat() / bitmap.height.toFloat()

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

            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES32.GL_TEXTURE_2D, 0, bitmap, 0)
            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle()
        }
        if (textureHandle[0] == 0) {
            throw RuntimeException("Error loading texture.")
        }
        return textureHandle[0]
    }

    companion object {

        internal const val FRAGMENT_SHADER_CODE =
            "precision mediump float;" +
                    "varying vec4 vColor;" +
                    "varying vec2 vTextureCoordinate;" +
                    "uniform bool uUseTexture;" +
                    "uniform sampler2D uTextureSampler;" +
                    "varying float vDiffuseLightWeighting;" +
                    "varying vec4 vDiffuseColor;" +
                    "varying vec3 vLightWeighting;" +
                    "void main() {" +
                    "   vec4 diffuseColor = vDiffuseLightWeighting * vDiffuseColor;" +
                    "   if (uUseTexture) {" +
                    "       vec4 fragmentColor = texture2D(uTextureSampler, vec2(vTextureCoordinate.x, vTextureCoordinate.y));" +
                    "       if (fragmentColor.a < 0.1) discard;" +
                    "       gl_FragColor = vec4(fragmentColor.rgb * vLightWeighting, fragmentColor.a) + diffuseColor;" +
                    "   } else {" +
                    "       gl_FragColor = vColor;" +
                    "   };" +
                    "}"

        internal const val VERTEX_SHADER_CODE =
            "attribute vec3 aVertexPosition;" +
                    "uniform mat4 uMVPMatrix;" +
                    "uniform mat4 uMMatrix;" +
                    "varying vec4 vColor;" +
                    "attribute vec4 aVertexColor;" +  // The colour  of the object
                    "attribute vec2 aTextureCoordinate;" +
                    "varying vec2 vTextureCoordinate;" +
                    "attribute vec3 aVertexNormal;" +
                    // Diffuse light properties
                    "uniform vec3 uDiffuseLightLocation;" +
                    "uniform vec4 uDiffuseColor;" +
                    "varying vec4 vDiffuseColor;" +
                    "varying float vDiffuseLightWeighting;" +
                    // Light attenuation
                    "uniform vec3 uAttenuation;" +
                    "varying vec3 vLightWeighting;" +
                    "uniform vec3 uAmbientColor;" +
                    "void main() {" +
                    "   vec4 mPosition = uMMatrix * vec4(aVertexPosition, 1.0);" +
                    "   vec3 diffuseLightDirection = normalize(uDiffuseLightLocation - mPosition.xyz);" +
                    "   vec3 vertexToLightSource = mPosition.xyz - uDiffuseLightLocation;" +
                    "   float diffLightDist = length(vertexToLightSource);" +
                    "   vec3 transformedNormal = normalize((uMMatrix * vec4(aVertexNormal, 0.0)).xyz);" +
                    "   float attenuation = 1.0 / (uAttenuation.x" +
                    "                           + uAttenuation.y * diffLightDist" +
                    "                           + uAttenuation.z * diffLightDist * diffLightDist);" +
                    "   vDiffuseLightWeighting = attenuation * max(dot(transformedNormal, diffuseLightDirection), 0.0);" +
                    "   gl_Position = uMVPMatrix * vec4(aVertexPosition, 1.0);" +
                    "   gl_PointSize = 40.0;" +
                    "   vColor = aVertexColor;" +
                    "   vTextureCoordinate = aTextureCoordinate;" +
                    "   vDiffuseColor = uDiffuseColor;" +
                    "   vLightWeighting = uAmbientColor;" +
                    "}" // Get the colour from the application program

        // Number of coordinates per vertex in this array
        internal const val COORDINATES_PER_VERTEX = 3
        internal const val COLORS_PER_VERTEX = 4
        internal const val TEXTURE_COORDINATES_PER_VERTEX = 2

        internal const val COLOR_STRIDE = COLORS_PER_VERTEX * Float.SIZE_BYTES // 4 bytes per color
        internal const val TEXTURE_STRIDE =
            TEXTURE_COORDINATES_PER_VERTEX * Float.SIZE_BYTES // 4 bytes per texture coordinate
        internal const val VERTEX_STRIDE =
            COORDINATES_PER_VERTEX * Float.SIZE_BYTES // 4 bytes per vertex

        internal val ATTENUATION = floatArrayOf(
            1.0F, 0.14F, 0.07F
        )

        internal val AMBIENT_COLOR = floatArrayOf(
            0.3F, 0.3F, 0.3F
        )

        internal val DIFFUSE_COLOR = floatArrayOf(
            0.3F, 0.3F, 0.3F, 1.0F
        )

        internal  val DIFFUSE_LIGHT_LOCATION = floatArrayOf(
            0.0F, 0.0F, -3.0F
        )
    }

}