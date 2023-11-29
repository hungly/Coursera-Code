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
        MyRenderer.checkGlError("glGetUniformLocation")
    }

    open fun draw(mvpMatrix: FloatArray?) {
        GLES32.glUseProgram(program) // Add program to OpenGL environment

        GLES32.glDisable(GLES32.GL_BLEND)
        GLES32.glDisable(GLES32.GL_CULL_FACE)

        GLES32.glUniform1i(useTextureHandle, 0)
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
                    "void main() {" +
                    "   if (uUseTexture) {" +
                    "       vec4 fragmentColor = texture2D(uTextureSampler, vec2(vTextureCoordinate.x, vTextureCoordinate.y));" +
                    "       if (fragmentColor.a < 0.1) discard;" +
                    "       gl_FragColor = fragmentColor * vColor;" +
                    "   } else {" +
                    "       gl_FragColor = vColor;" +
                    "   };" +
                    "}"

        internal const val VERTEX_SHADER_CODE =
            "attribute vec3 aVertexPosition;" +
                    "uniform mat4 uMVPMatrix;" +
                    "varying vec4 vColor;" +
                    "attribute vec4 aVertexColor;" +  // The colour  of the object
                    "attribute vec2 aTextureCoordinate;" +
                    "varying vec2 vTextureCoordinate;" +
                    "void main() {" +
                    "   gl_Position = uMVPMatrix * vec4(aVertexPosition, 1.0);" +
                    "   gl_PointSize = 40.0;" +
                    "   vColor = aVertexColor;" +
                    "   vTextureCoordinate = aTextureCoordinate;" +
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
    }

}