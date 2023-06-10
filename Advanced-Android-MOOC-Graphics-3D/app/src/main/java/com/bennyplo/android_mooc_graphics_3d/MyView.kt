package com.bennyplo.android_mooc_graphics_3d

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import kotlin.math.cos
import kotlin.math.sin

class MyView(context: Context?) : View(context, null) {

    private var drawCubeVertices //the vertices for drawing a 3D cube
            : Array<Coordinate?>

    private val cubeVertices //the vertices of a 3D cube
            : Array<Coordinate?>

    private val redPaint //paint object for drawing the lines
            : Paint

    init {
        val thisView = this
        //create the paint object
        redPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        redPaint.style = Paint.Style.STROKE //Stroke
        redPaint.color = Color.RED
        redPaint.strokeWidth = 2f
        //create a 3D cube
        cubeVertices = arrayOfNulls(8)
        cubeVertices[0] = Coordinate(-1.0, -1.0, -1.0, 1.0)
        cubeVertices[1] = Coordinate(-1.0, -1.0, 1.0, 1.0)
        cubeVertices[2] = Coordinate(-1.0, 1.0, -1.0, 1.0)
        cubeVertices[3] = Coordinate(-1.0, 1.0, 1.0, 1.0)
        cubeVertices[4] = Coordinate(1.0, -1.0, -1.0, 1.0)
        cubeVertices[5] = Coordinate(1.0, -1.0, 1.0, 1.0)
        cubeVertices[6] = Coordinate(1.0, 1.0, -1.0, 1.0)
        cubeVertices[7] = Coordinate(1.0, 1.0, 1.0, 1.0)
        drawCubeVertices = translate(cubeVertices, 2.0, 2.0, 2.0)
        drawCubeVertices = scale(drawCubeVertices, 40.0, 40.0, 40.0)
//        drawCubeVertices = rotate(drawCubeVertices, null, 45.0, null)
//        drawCubeVertices = rotate(drawCubeVertices, 45.0, null, null)
//        drawCubeVertices = rotate(drawCubeVertices, null, null, 80.0)
//        drawCubeVertices = rotate(drawCubeVertices, null, 30.0, null)
        thisView.invalidate() //update the view
    }

    override fun onDraw(canvas: Canvas) {
        //draw objects on the screen
        super.onDraw(canvas)
        drawCube(canvas) //draw a cube onto the screen
    }

    //*********************************
//matrix and transformation functions
    fun getIdentityMatrix(): DoubleArray { //return an 4x4 identity matrix
        val matrix = DoubleArray(16)
        matrix[0] = 1.0
        matrix[1] = 0.0
        matrix[2] = 0.0
        matrix[3] = 0.0
        matrix[4] = 0.0
        matrix[5] = 1.0
        matrix[6] = 0.0
        matrix[7] = 0.0
        matrix[8] = 0.0
        matrix[9] = 0.0
        matrix[10] = 1.0
        matrix[11] = 0.0
        matrix[12] = 0.0
        matrix[13] = 0.0
        matrix[14] = 0.0
        matrix[15] = 1.0
        return matrix
    }

    fun transformation(
        vertices: Array<Coordinate?>,
        matrix: DoubleArray
    ): Array<Coordinate?> {   //Affine transform a 3D object with vertices
        // vertices - vertices of the 3D object.
        // matrix - transformation matrix
        val result = arrayOfNulls<Coordinate>(vertices.size)
        for (i in vertices.indices) {
            result[i] = transformation(vertices[i], matrix)
            result[i]!!.Normalise()
        }
        return result
    }

    fun transformation(
        vertex: Coordinate?,
        matrix: DoubleArray
    ): Coordinate { //affine transformation with homogeneous coordinates
        //i.e. a vector (vertex) multiply with the transformation matrix
        // vertex - vector in 3D
        // matrix - transformation matrix
        val result = Coordinate()
        result.x = matrix[0] * vertex!!.x + matrix[1] * vertex.y + matrix[2] * vertex.z + matrix[3]
        result.y = matrix[4] * vertex.x + matrix[5] * vertex.y + matrix[6] * vertex.z + matrix[7]
        result.z = matrix[8] * vertex.x + matrix[9] * vertex.y + matrix[10] * vertex.z + matrix[11]
        result.w =
            matrix[12] * vertex.x + matrix[13] * vertex.y + matrix[14] * vertex.z + matrix[15]
        return result
    }

    //***********************************************************
    // Affine transformation
    fun translate(
        vertices: Array<Coordinate?>,
        tx: Double,
        ty: Double,
        tz: Double
    ): Array<Coordinate?> {
        val matrix = getIdentityMatrix()
        matrix[3] = tx
        matrix[7] = ty
        matrix[11] = tz
        return transformation(vertices, matrix)
    }

    //***********************************************************
    // Affine rotation
    fun rotate(
        vertices: Array<Coordinate?>,
        rx: Double? = null,
        ry: Double? = null,
        rz: Double? = null
    ): Array<Coordinate?> {
        var result = vertices
        var rotateMatrix: DoubleArray
        var rotateRadians: Double
        rx?.let {
            rotateMatrix = getIdentityMatrix()
            rotateRadians = Math.toRadians(it)
            rotateMatrix[5]= cos(rotateRadians)
            rotateMatrix[6] = -sin(rotateRadians)
            rotateMatrix[9] = sin(rotateRadians)
            rotateMatrix[10] = cos(rotateRadians)
            result = transformation(result, rotateMatrix)
        }
        ry?.let {
            rotateMatrix = getIdentityMatrix()
            rotateRadians = Math.toRadians(it)
            rotateMatrix[0]= cos(rotateRadians)
            rotateMatrix[2] = sin(rotateRadians)
            rotateMatrix[8] = -sin(rotateRadians)
            rotateMatrix[10] = cos(rotateRadians)
            result = transformation(result, rotateMatrix)
        }
        rz?.let {
            rotateMatrix = getIdentityMatrix()
            rotateRadians = Math.toRadians(it)
            rotateMatrix[0]= cos(rotateRadians)
            rotateMatrix[1] = -sin(rotateRadians)
            rotateMatrix[4] = sin(rotateRadians)
            rotateMatrix[5] = cos(rotateRadians)
            result = transformation(result, rotateMatrix)
        }
        return result
    }

    private fun drawCube(canvas: Canvas) { //draw a cube on the screen
        drawLinePairs(canvas, drawCubeVertices, 0, 1, redPaint)
        drawLinePairs(canvas, drawCubeVertices, 1, 3, redPaint)
        drawLinePairs(canvas, drawCubeVertices, 3, 2, redPaint)
        drawLinePairs(canvas, drawCubeVertices, 2, 0, redPaint)
        drawLinePairs(canvas, drawCubeVertices, 4, 5, redPaint)
        drawLinePairs(canvas, drawCubeVertices, 5, 7, redPaint)
        drawLinePairs(canvas, drawCubeVertices, 7, 6, redPaint)
        drawLinePairs(canvas, drawCubeVertices, 6, 4, redPaint)
        drawLinePairs(canvas, drawCubeVertices, 0, 4, redPaint)
        drawLinePairs(canvas, drawCubeVertices, 1, 5, redPaint)
        drawLinePairs(canvas, drawCubeVertices, 2, 6, redPaint)
        drawLinePairs(canvas, drawCubeVertices, 3, 7, redPaint)
    }

    private fun drawLinePairs(
        canvas: Canvas,
        vertices: Array<Coordinate?>,
        start: Int,
        end: Int,
        paint: Paint
    ) { //draw a line connecting 2 points
        //canvas - canvas of the view
        //points - array of points
        //start - index of the starting point
        //end - index of the ending point
        //paint - the paint of the line
        canvas.drawLine(
            vertices[start]!!.x.toInt().toFloat(),
            vertices[start]!!.y.toInt().toFloat(),
            vertices[end]!!.x.toInt().toFloat(),
            vertices[end]!!.y.toInt().toFloat(),
            paint
        )
    }

    private fun scale(
        vertices: Array<Coordinate?>,
        sx: Double,
        sy: Double,
        sz: Double
    ): Array<Coordinate?> {
        val matrix = getIdentityMatrix()
        matrix[0] = sx
        matrix[5] = sy
        matrix[10] = sz
        return transformation(vertices, matrix)
    }

}
