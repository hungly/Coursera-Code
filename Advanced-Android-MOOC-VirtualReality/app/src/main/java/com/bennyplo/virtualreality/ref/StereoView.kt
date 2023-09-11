package com.bennyplo.virtualreality.ref

import android.opengl.GLES32
import android.opengl.Matrix
import android.util.Log
import com.bennyplo.virtualreality.ref.MyRenderer.Companion.loadShader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer

@Suppress("LongParameterList", "MagicNumber", "MaxLineLength")
class StereoView(pWidth: Int, pHeight: Int, leftOrRight: Int) {

    //-------
    //public float depthZ = -1;
    private var depthZ = -5f

    private var frameBufferTextureID: IntArray
    private var modeltranslation = 0f
    private var renderBuffer: IntArray

    private val aspect //screen aspect ratio
            : Float

    private val fragmentShaderCode = "precision lowp float;" +
            "varying vec2 vTextureCoordinate;" +
            "uniform sampler2D uTextureSampler;" +  //texture
            "void main() {" +
            "vec4 fragmentColor=texture2D(uTextureSampler,vec2(vTextureCoordinate.s,vTextureCoordinate.t));" +  //load the color texture
            "gl_FragColor=vec4(fragmentColor.rgb,fragmentColor.a);" +  //the fragment color
            "}"

    private val indexBuffer: IntBuffer

    private val mDisplayProjectionMatrix =
        FloatArray(16) //project matrix for showing the framebuffer

    //--------------
    //for drawing the object in the framebuffer
    private val mFrameModelMatrix = FloatArray(16) //model matrix

    //-------------
    //for drawing the framebuffer as a surface on the screen
    private val mMVPMatrix = FloatArray(16) //model view projection matrix

    private val mMVPMatrixHandle: Int
    private val mModelMatrix = FloatArray(16) //model  matrix
    private val mPositionHandle: Int
    private val mProgram: Int
    private val mTextureCoordHandle: Int
    private val mViewMatrix = FloatArray(16) //view matrix
    private val textureBuffer: FloatBuffer
    private val textureHandle: Int
    private val textureStride = TEXTURE_PER_VERTEX * 4 //bytes per texture coordinates
    private val vertexBuffer: FloatBuffer

    private val vertexShaderCode = "attribute vec3 aVertexPosition;" + "uniform mat4 uMVPMatrix;" +
            "attribute vec4 aVertexColor;" +
            "attribute vec2 aTextureCoordinate; " +  //texture coordinate
            "varying vec2 vTextureCoordinate;" +
            "void main() {" +
            "gl_Position = uMVPMatrix *vec4(aVertexPosition,1.0);" +
            "vTextureCoordinate=aTextureCoordinate;" +
            "}"

    private val vertexStride = COORDS_PER_VERTEX * 4 // 4 bytes per vertex

    //--------
    var frameBuffer: IntArray

    var height: Int

    //public float[] mProjMatrix = new float[16];//projection matrix
    var mProjectionMatrix = FloatArray(16)

    var width: Int
    val mFrameViewMatrix = FloatArray(16) //view matrix - for object to draw in the framebuffer

    init {

        // initialize vertex byte buffer for shape coordinates
        val bb =
            ByteBuffer.allocateDirect(Plane2DVertex.size * 4) // (# of coordinate values * 4 bytes per float)
        bb.order(ByteOrder.nativeOrder())
        vertexBuffer = bb.asFloatBuffer()
        vertexBuffer.put(Plane2DVertex)
        vertexBuffer.position(0)
        val ib = IntBuffer.allocate(Plane2DIndex.size)
        indexBuffer = ib
        indexBuffer.put(Plane2DIndex)
        indexBuffer.position(0)
        val tb = ByteBuffer.allocateDirect(Plane2DTextureCoords.size * 4)
        tb.order(ByteOrder.nativeOrder())
        textureBuffer = tb.asFloatBuffer()
        textureBuffer.put(Plane2DTextureCoords)
        textureBuffer.position(0)
        //////////////////////
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
        // Prepare the triangle coordinate data
        GLES32.glVertexAttribPointer(
            mPositionHandle,
            COORDS_PER_VERTEX,
            GLES32.GL_FLOAT,
            false,
            vertexStride,
            vertexBuffer
        )
        // get handle to shape's transformation matrix
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
        width = pWidth / 2
        height = pHeight
        aspect = width.toFloat() / height
        if (pWidth > pHeight) {
            val pratio = pWidth.toFloat() / pHeight.toFloat()
            Matrix.orthoM(mDisplayProjectionMatrix, 0, -pratio, pratio, -1f, 1f, -10f, 200f)
        } else {
            val pratio = pHeight.toFloat() / pWidth.toFloat()
            Matrix.orthoM(mDisplayProjectionMatrix, 0, -1f, 1f, -pratio, pratio, -10f, 200f)
        }
        Matrix.setLookAtM(
            mViewMatrix, 0, 0f, 0f, 0.1f,
            0f, 0f, 0f,  //looks at the origin
            0f, 1f, 0f
        ) //head is down (set to (0,1,0) to look from the top)
        Matrix.setIdentityM(mModelMatrix, 0) //set the model matrix to an identity matrix
        Matrix.scaleM(mModelMatrix, 0, width.toFloat() / height.toFloat(), 1f, 1f)
        if (leftOrRight == 0) //left
            Matrix.translateM(mModelMatrix, 0, -1f, 0.0f, 0f) //move to the left
        else Matrix.translateM(mModelMatrix, 0, 1f, 0.0f, 0f) //move to the right
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0)
        Matrix.multiplyMM(mMVPMatrix, 0, mDisplayProjectionMatrix, 0, mMVPMatrix, 0)
        //-------------------------
        //setting up the matrices for drawing objects in the framebuffer
        if (leftOrRight == 0) //left view :
        {
            Matrix.frustumM(
                mProjectionMatrix,
                0,
                frustumShift - aspect,
                frustumShift + aspect,
                -1f,
                1f,
                nearZ,
                farZ
            )
            modeltranslation = IOD / 2
            Matrix.setLookAtM(
                mFrameViewMatrix, 0,
                -IOD / 2.0f, 0f, 0.1f,
                0f, 0f, screenZ,  //looks at the screen
                0f, 1f, 0f
            ) //head is down (set to (0,1,0) to look from the top)
        } else { //right view
            Matrix.frustumM(
                mProjectionMatrix,
                0,
                -aspect - frustumShift,
                aspect - frustumShift,
                -1f,
                1f,
                nearZ,
                farZ
            )
            modeltranslation = -IOD / 2
            Matrix.setLookAtM(
                mFrameViewMatrix, 0,
                IOD / 2.0f, 0f, 0.1f,
                0f, 0f, screenZ,  //looks at the screen
                0f, 1f, 0f
            ) //head is down (set to (0,1,0) to look from the top)
        }
        Matrix.setIdentityM(mFrameModelMatrix, 0) //set the model matrix to an identity matrix
        Matrix.translateM(mFrameModelMatrix, 0, modeltranslation, 0.0f, depthZ)
        //-------------------------
        frameBuffer = IntArray(1)
        frameBufferTextureID = IntArray(2)
        renderBuffer = IntArray(1)
        createFrameBuffers(width, height)
    }

    fun draw() {
        GLES32.glUseProgram(mProgram) // Add program to OpenGL environment
        // Apply the projection and view transformation
        GLES32.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0)
        GLES32.glActiveTexture(GLES32.GL_TEXTURE1) //set the active texture to unit 0
        GLES32.glBindTexture(
            GLES32.GL_TEXTURE_2D,
            frameBufferTextureID[0]
        ) //bind the texture to this unit
        GLES32.glUniform1i(textureHandle, 1) //tell the uniform sampler to use this texture i
        GLES32.glVertexAttribPointer(
            mTextureCoordHandle,
            TEXTURE_PER_VERTEX,
            GLES32.GL_FLOAT,
            false,
            textureStride,
            textureBuffer
        )
        //set the attribute of the vertex to point to the vertex buffer
        GLES32.glVertexAttribPointer(
            mPositionHandle, COORDS_PER_VERTEX,
            GLES32.GL_FLOAT, false, vertexStride, vertexBuffer
        )
        // Draw the 2D plane
        GLES32.glDrawElements(
            GLES32.GL_TRIANGLES,
            Plane2DIndex.size,
            GLES32.GL_UNSIGNED_INT,
            indexBuffer
        )
    }

    fun getModelMatrix(
        rotateX: Float,
        rotateY: Float,
        rotateZ: Float
    ): FloatArray { //get the model matrix to draw the object onto the frame buffer
        val pModelMatrix = FloatArray(16) //model  matrix
        val mRotationMatrixX = FloatArray(16) //rotation//  matrix
        val mRotationMatrixY = FloatArray(16) //rotation//  matrix
        val mRotationMatrixZ = FloatArray(16) //rotation//  matrix
        Matrix.setIdentityM(pModelMatrix, 0) //set the model matrix to an identity matrix
        Matrix.setRotateM(mRotationMatrixY, 0, rotateY, 0f, 1.0f, 0f) //rotate around the y-axis
        Matrix.setRotateM(mRotationMatrixX, 0, rotateX, 1.0f, 0f, 0f) //rotate around the x-axis
        Matrix.setRotateM(mRotationMatrixZ, 0, rotateZ, 0f, 0f, 1f) //rotate around the x-axis
        Matrix.multiplyMM(pModelMatrix, 0, mFrameModelMatrix, 0, mRotationMatrixY, 0)
        Matrix.multiplyMM(pModelMatrix, 0, pModelMatrix, 0, mRotationMatrixX, 0)
        Matrix.multiplyMM(pModelMatrix, 0, pModelMatrix, 0, mRotationMatrixZ, 0)
        return pModelMatrix
    }

    private fun createFrameBuffers(width: Int, height: Int) {
        GLES32.glGenTextures(1, frameBufferTextureID, 0) //generate 2 texture objects
        GLES32.glGenFramebuffers(1, frameBuffer, 0) //generate a framebuffer object
        //bind the framebuffer for drawing
        GLES32.glBindFramebuffer(GLES32.GL_DRAW_FRAMEBUFFER, frameBuffer[0])
        //initialise texture (i.e. glActivateTextgure...glBindTexture...glTexImage2D....)
        initaliseTexture(
            GLES32.GL_TEXTURE1,
            frameBufferTextureID[0],
            width,
            height,
            GLES32.GL_RGBA,
            GLES32.GL_UNSIGNED_BYTE
        )
        GLES32.glFramebufferTexture2D(
            GLES32.GL_FRAMEBUFFER,
            GLES32.GL_COLOR_ATTACHMENT0,
            GLES32.GL_TEXTURE_2D,
            frameBufferTextureID[0],
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
            Log.d("framebuffer", "Error in creating framebuffer")
        }
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, 0) //unbind the texture
        GLES32.glBindFramebuffer(GLES32.GL_FRAMEBUFFER, 0) //unbind the framebuffer
    }

    private fun initaliseTexture(
        whichTexture: Int,
        textureID: Int,
        width: Int,
        height: Int,
        pixelFormat: Int,
        type: Int
    ) {
        GLES32.glActiveTexture(whichTexture) //activate the texture
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, textureID) //bind the texture with the ID
        GLES32.glTexParameterf(
            GLES32.GL_TEXTURE_2D,
            GLES32.GL_TEXTURE_MIN_FILTER,
            GLES32.GL_NEAREST.toFloat()
        ) //set the min filter
        GLES32.glTexParameterf(
            GLES32.GL_TEXTURE_2D,
            GLES32.GL_TEXTURE_MAG_FILTER,
            GLES32.GL_NEAREST.toFloat()
        ) //set the mag filter
        GLES32.glTexParameterf(
            GLES32.GL_TEXTURE_2D,
            GLES32.GL_TEXTURE_WRAP_S,
            GLES32.GL_CLAMP_TO_EDGE.toFloat()
        ) //set the wrap for the edge s
        GLES32.glTexParameterf(
            GLES32.GL_TEXTURE_2D,
            GLES32.GL_TEXTURE_WRAP_T,
            GLES32.GL_CLAMP_TO_EDGE.toFloat()
        ) //set the wrap for the edge t
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
        ) //set the format to be RGBA
    }

    companion object {
        //--------
        // number of coordinates per vertex in this array
        private const val COORDS_PER_VERTEX = 3
        private const val TEXTURE_PER_VERTEX = 2 //no of texture coordinates per vertex
        private var Plane2DVertex = floatArrayOf( //front face
            -1.0f, -1.0f, 1.0f,
            1.0f, -1.0f, 1.0f,
            1.0f, 1.0f, 1.0f,
            -1.0f, 1.0f, 1.0f
        )
        private var Plane2DIndex = intArrayOf(
            0, 1, 2, 0, 2, 3
        )
        private var Plane2DTextureCoords = floatArrayOf( //front face
            0f, 0f,
            1f, 0f,
            1f, 1f,
            0f, 1f
        )
        private const val IOD = 0.8f //intraocular distance

        private const val nearZ = 1f //near clipping plane
        private const val farZ = 8.0f //far clipping plane
        private const val screenZ = -10f //screen projection plane

        private const val frustumShift = -(IOD / 2) * nearZ / screenZ
    }

}