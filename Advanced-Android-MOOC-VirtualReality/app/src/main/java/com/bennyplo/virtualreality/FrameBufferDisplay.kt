package com.bennyplo.virtualreality

import android.opengl.GLES32
import android.opengl.Matrix
import com.bennyplo.virtualreality.MyRenderer.Companion.loadShader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer

class FrameBufferDisplay(pHeight: Int, pWidth: Int) {

    private var mMVPMatrixHandle = 0
    private var mPositionHandle = 0
    private var mTextureCoordHandle = 0
    private var mTextureHandle = 0

    private val frameBufferTextureId by lazy {
        IntArray(1)
    }

    private val renderBuffer by lazy {
        IntArray(1)
    }

    private val indexBuffer: IntBuffer
    private val program: Int
    private val textureBuffer: FloatBuffer
    private val vertexBuffer: FloatBuffer

    val frameBuffer by lazy {
        IntArray(1)
    }

    val height: Int
    val mProjMatrix = FloatArray(16)
    val width: Int

    init {
        val bb = ByteBuffer.allocateDirect(DISPLAY_VERTEX.size * Float.SIZE_BYTES)
        bb.order(ByteOrder.nativeOrder())
        vertexBuffer = bb.asFloatBuffer()
        vertexBuffer.put(DISPLAY_VERTEX)
        vertexBuffer.position(0)

        val ib = IntBuffer.allocate(DISPLAY_INDEX.size)
        indexBuffer = ib
        indexBuffer.put(DISPLAY_INDEX)
        indexBuffer.position(0)

        val tb = ByteBuffer.allocateDirect(DISPLAY_TEXTURE_COORDS.size * Float.SIZE_BYTES)
        tb.order(ByteOrder.nativeOrder())
        textureBuffer = tb.asFloatBuffer()
        textureBuffer.put(DISPLAY_TEXTURE_COORDS)
        textureBuffer.position(0)

        val vertexShader = loadShader(GLES32.GL_VERTEX_SHADER, VERTEX_SHADER_CODE)
        val fragmentShader = loadShader(GLES32.GL_FRAGMENT_SHADER, FRAGMENT_SHADER_CODE)
        program = GLES32.glCreateProgram()
        GLES32.glAttachShader(program, vertexShader)
        GLES32.glAttachShader(program, fragmentShader)
        GLES32.glLinkProgram(program)
        GLES32.glUseProgram(program)

        mPositionHandle = GLES32.glGetAttribLocation(program, "aVertexPosition")
        GLES32.glEnableVertexAttribArray(mPositionHandle)
        mTextureCoordHandle = GLES32.glGetAttribLocation(program, "aTextureCoord")
        GLES32.glEnableVertexAttribArray(mTextureCoordHandle)
        mTextureHandle = GLES32.glGetUniformLocation(program, "uSampler")
        mMVPMatrixHandle = GLES32.glGetUniformLocation(program, "uMVPMatrix")

        width = pWidth / 2
        height = pHeight
        val ratio = width.toFloat() / height
        val left = -ratio
        val right = ratio

        Matrix.frustumM(mProjMatrix, 0, left, right, -1f, 1f, 1.5f, 300f)

        createFrameBuffer(width, height)
    }

    fun draw(mvpMatrix: FloatArray) {
        GLES32.glUseProgram(program)

        GLES32.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0)
        GLES32.glActiveTexture(GLES32.GL_TEXTURE1)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, frameBufferTextureId[0])
        GLES32.glUniform1i(mTextureHandle, 1)
        GLES32.glVertexAttribPointer(
            mTextureCoordHandle,
            TEXTTURE_PER_VERTEX,
            GLES32.GL_FLOAT,
            false,
            TEXTURE_STRIDE,
            textureBuffer
        )
        GLES32.glVertexAttribPointer(
            mPositionHandle,
            COORDS_PER_VERTEX,
            GLES32.GL_FLOAT,
            false,
            VERTEX_STRIDE,
            vertexBuffer
        )
        GLES32.glDrawElements(
            GLES32.GL_TRIANGLES,
            DISPLAY_INDEX.size,
            GLES32.GL_UNSIGNED_INT,
            indexBuffer
        )
    }

    private fun createFrameBuffer(width: Int, height: Int) {
        GLES32.glGenFramebuffers(1, frameBuffer, 0)
        GLES32.glGenTextures(1, frameBufferTextureId, 0)
        initializeTexture(
            GLES32.GL_TEXTURE1,
            frameBufferTextureId[0],
            width,
            height,
            GLES32.GL_RGBA,
            GLES32.GL_UNSIGNED_BYTE
        )
        GLES32.glBindFramebuffer(GLES32.GL_DRAW_FRAMEBUFFER, frameBuffer[0])
        GLES32.glFramebufferTexture2D(
            GLES32.GL_FRAMEBUFFER,
            GLES32.GL_COLOR_ATTACHMENT0,
            GLES32.GL_TEXTURE_2D,
            frameBufferTextureId[0],
            0
        )
        GLES32.glGenRenderbuffers(1, renderBuffer, 0)
        GLES32.glBindRenderbuffer(GLES32.GL_RENDERBUFFER, renderBuffer[0])
        GLES32.glRenderbufferStorage(
            GLES32.GL_RENDERBUFFER,
            GLES32.GL_DEPTH_COMPONENT24,
            width,
            height
        )
        GLES32.glFramebufferRenderbuffer(
            GLES32.GL_FRAMEBUFFER,
            GLES32.GL_DEPTH_ATTACHMENT,
            GLES32.GL_RENDERBUFFER,
            renderBuffer[0]
        )
        val status = GLES32.glCheckFramebufferStatus(GLES32.GL_FRAMEBUFFER)
        if (status != GLES32.GL_FRAMEBUFFER_COMPLETE) {
            throw RuntimeException("Failed to initialize framebuffer object $status")
        }
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, 0)
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER, 0)
    }

    private fun initializeTexture(
        whichTexture: Int,
        textureId: Int,
        width: Int,
        height: Int,
        pixelFormat: Int,
        type: Int
    ) {
        GLES32.glActiveTexture(whichTexture)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, textureId)
        GLES32.glTexParameterf(
            GLES32.GL_TEXTURE_2D,
            GLES32.GL_TEXTURE_MIN_FILTER,
            GLES32.GL_NEAREST.toFloat()
        )
        GLES32.glTexParameterf(
            GLES32.GL_TEXTURE_2D,
            GLES32.GL_TEXTURE_MAG_FILTER,
            GLES32.GL_NEAREST.toFloat()
        )
        GLES32.glTexParameterf(
            GLES32.GL_TEXTURE_2D,
            GLES32.GL_TEXTURE_WRAP_S,
            GLES32.GL_CLAMP_TO_EDGE.toFloat()
        )
        GLES32.glTexParameterf(
            GLES32.GL_TEXTURE_2D,
            GLES32.GL_TEXTURE_WRAP_T,
            GLES32.GL_CLAMP_TO_EDGE.toFloat()
        )
        GLES32.glTexImage2D(
            GLES32.GL_TEXTURE_2D,
            0,
            pixelFormat,
            width,
            height,
            0,
            pixelFormat,
            type,
            null
        )
    }

    companion object {

        private const val COORDS_PER_VERTEX = 3
        private const val TEXTTURE_PER_VERTEX = 2

        private const val VERTEX_STRIDE = COORDS_PER_VERTEX * Float.SIZE_BYTES
        private const val TEXTURE_STRIDE = TEXTTURE_PER_VERTEX * Float.SIZE_BYTES

        private const val VERTEX_SHADER_CODE =
            "attribute vec3 aVertexPosition;" +
                    "uniform mat4 uMVPMatrix;" +
                    "attribute vec2 aTextureCoord;" +
                    "varying vec2 vTextureCoord;" +
                    "void main() {" +
                    "   gl_Position = uMVPMatrix * vec4(aVertexPosition, 1.0);" +
                    "   vTextureCoord = aTextureCoord;" +
                    "}"

        private const val FRAGMENT_SHADER_CODE =
            "precision lowp float;" +
                    "varying vec2 vTextureCoord;" +
                    "uniform sampler2D uSampler;" +
                    "void main() {" +
                    "   vec4 fragmentColor = texture2D(uSampler, vec2(vTextureCoord.s, vTextureCoord.t));" +
                    "   if (fragmentColor.r < 0.01 && fragmentColor.g < 0.01 && fragmentColor.b < 0.01) discard;" +
                    "    gl_FragColor = vec4(fragmentColor.rgb, fragmentColor.a);" +
                    "}"

        private val DISPLAY_VERTEX = floatArrayOf(
            -1f, -1f, 1f,
            1f, -1f, 1f,
            1f, 1f, 1f,
            -1f, 1f, 1f
        )

        private val DISPLAY_INDEX = intArrayOf(
            0, 1, 2,
            0, 2, 3
        )

        private val DISPLAY_TEXTURE_COORDS = floatArrayOf(
            0f, 0f,
            1f, 0f,
            1f, 1f,
            0f, 1f
        )
    }

}