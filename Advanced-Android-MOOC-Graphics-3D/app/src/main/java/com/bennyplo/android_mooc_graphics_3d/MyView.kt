package com.bennyplo.android_mooc_graphics_3d

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.tan

class MyView(context: Context?) : View(context, null) {

    private val cubeVertices: Array<Coordinate?> by lazy {
        arrayOf(
            Coordinate(-1.0, -1.0, -1.0, 1.0),
            Coordinate(-1.0, -1.0, 1.0, 1.0),
            Coordinate(-1.0, 1.0, -1.0, 1.0),
            Coordinate(-1.0, 1.0, 1.0, 1.0),
            Coordinate(1.0, -1.0, -1.0, 1.0),
            Coordinate(1.0, -1.0, 1.0, 1.0),
            Coordinate(1.0, 1.0, -1.0, 1.0),
            Coordinate(1.0, 1.0, 1.0, 1.0),
        )
    }

    private val headVertices: Array<Coordinate?> by lazy {
        scale(cubeVertices, 90.0, 90.0, 90.0)
    }

    private val neckVertices: Array<Coordinate?> by lazy {
        scale(cubeVertices, 45.0, 20.0, 45.0)
    }

    private val headPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            color = Color.BLACK
            strokeWidth = 2f
        }
    }

    private val neckPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            color = Color.BLUE
            strokeWidth = 2f
        }
    }

    private val chestPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            color = Color.CYAN
            strokeWidth = 2f
        }
    }

    private val hipPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            color = Color.DKGRAY
            strokeWidth = 2f
        }
    }

    private val uArmPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            color = Color.GRAY
            strokeWidth = 2f
        }
    }

    private val lArmPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            color = Color.GREEN
            strokeWidth = 2f
        }
    }

    private val handPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            color = Color.LTGRAY
            strokeWidth = 2f
        }
    }

    private val uLegPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            color = Color.MAGENTA
            strokeWidth = 2f
        }
    }

    private val lLegPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            color = Color.RED
            strokeWidth = 2f
        }
    }

    private val footPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            color = Color.YELLOW
            strokeWidth = 2f
        }
    }

    private val paints = arrayOf(
        headPaint,
        neckPaint,
        chestPaint,
        hipPaint,
        uArmPaint,
        lArmPaint,
        handPaint,
        uArmPaint,
        lArmPaint,
        handPaint,
        uLegPaint,
        lLegPaint,
        footPaint,
        uLegPaint,
        lLegPaint,
        footPaint,
    )

    private val baseAndroid by lazy {
        arrayOf(
            headVertices.map { it?.copy() }.toTypedArray(),
            neckVertices.map { it?.copy() }.toTypedArray()
        )
    }

    private val drawAndroid = baseAndroid

    init {
        val frameTime = 1000L / 120L
        CoroutineScope((Dispatchers.Default)).launch {
            do {
                delay(2000)
                resetParts()
                positionParts()
                invalidate()
            }while (true)
        }
    }

    override fun onDraw(canvas: Canvas) {
        //draw objects on the screen
        super.onDraw(canvas)
        //draw a cube onto the screen
        drawAndroid.forEachIndexed { index, coordinates ->
            drawCube(canvas, coordinates, paints[index])
        }
    }

    private fun resetParts() {
        drawAndroid.forEachIndexed { index, _ ->
            drawAndroid[index] = baseAndroid[index]
        }
    }

    private fun positionParts() {
        drawAndroid[HEAD] = translate(drawAndroid[HEAD], (width / 2).toDouble(), 250.0,0.0)
        val headBottomCenter = drawAndroid[HEAD].filterNotNull().let {
            Coordinate(
                (it[2].x + it[3].x + it[6].x + it[7].x) / 4,
                (it[2].y + it[3].y + it[6].y + it[7].y) / 4,
                (it[2].z + it[3].z + it[6].z + it[7].z) / 4,
                1.0
            )
        }
        val neckTopCenter = drawAndroid[NECK].filterNotNull().let {
            Coordinate(
                (it[0].x + it[1].x + it[4].x + it[5].x) / 4,
                (it[0].y + it[1].y + it[4].y + it[5].y) / 4,
                (it[0].z + it[1].z + it[4].z + it[5].z) / 4,
                1.0
            )
        }
        val neckBottomCenter = drawAndroid[NECK].filterNotNull().let {
            Coordinate(
                (it[2].x + it[3].x + it[6].x + it[7].x) / 4,
                (it[2].y + it[3].y + it[6].y + it[7].y) / 4,
                (it[2].z + it[3].z + it[6].z + it[7].z) / 4,
                1.0
            )
        }
        drawAndroid[NECK] = translate(drawAndroid[NECK], neckTopCenter.x + headBottomCenter.x, neckTopCenter.y + headBottomCenter.y + (neckBottomCenter.y - neckTopCenter.y), neckTopCenter.z + headBottomCenter.z)
    }

    //*********************************
    // Matrix and transformation functions
    private fun getIdentityMatrix(): DoubleArray { //return an 4x4 identity matrix
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

    private fun transformation(
        vertex: Coordinate?,
        matrix: DoubleArray
    ): Coordinate { //affine transformation with homogeneous coordinates
        //i.e. a vector (vertex) multiply with the transformation matrix
        // vertex - vector in 3D
        // matrix - transformation matrix
        val result = Coordinate()
        result.x =
            matrix[0] * vertex!!.x + matrix[1] * vertex.y + matrix[2] * vertex.z + matrix[3] * vertex.w
        result.y =
            matrix[4] * vertex.x + matrix[5] * vertex.y + matrix[6] * vertex.z + matrix[7] * vertex.w
        result.z =
            matrix[8] * vertex.x + matrix[9] * vertex.y + matrix[10] * vertex.z + matrix[11] * vertex.w
        result.w =
            matrix[12] * vertex.x + matrix[13] * vertex.y + matrix[14] * vertex.z + matrix[15] * vertex.w
        return result
    }

    //***********************************************************
    // Affine transformation
    private fun translate(
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
            rotateMatrix[5] = cos(rotateRadians)
            rotateMatrix[6] = -sin(rotateRadians)
            rotateMatrix[9] = sin(rotateRadians)
            rotateMatrix[10] = cos(rotateRadians)
            result = transformation(result, rotateMatrix)
        }
        ry?.let {
            rotateMatrix = getIdentityMatrix()
            rotateRadians = Math.toRadians(it)
            rotateMatrix[0] = cos(rotateRadians)
            rotateMatrix[2] = sin(rotateRadians)
            rotateMatrix[8] = -sin(rotateRadians)
            rotateMatrix[10] = cos(rotateRadians)
            result = transformation(result, rotateMatrix)
        }
        rz?.let {
            rotateMatrix = getIdentityMatrix()
            rotateRadians = Math.toRadians(it)
            rotateMatrix[0] = cos(rotateRadians)
            rotateMatrix[1] = -sin(rotateRadians)
            rotateMatrix[4] = sin(rotateRadians)
            rotateMatrix[5] = cos(rotateRadians)
            result = transformation(result, rotateMatrix)
        }
        return result
    }

    //***********************************************************
    // Quaternion rotation
    fun quaternionRotate(
        vertices: Array<Coordinate?>,
        rotateAxis: IntArray,
        rotateDegree: Double
    ): Array<Coordinate?> {
        val result = arrayOfNulls<Coordinate>(vertices.size)

        vertices.forEachIndexed { index, coordinate ->
            coordinate?.let {
                result[index] = quaternionCalculate(it, rotateAxis, rotateDegree)
            }
        }

        return result
    }

    private fun quaternionCalculate(
        vertex: Coordinate,
        rotateAxis: IntArray,
        rotateDegree: Double
    ): Coordinate {
        val (cos, sin) = (Math.toRadians(rotateDegree) / 2).let {
            cos(it) to sin(it)
        }
        var m: DoubleArray
        Coordinate(
            w = cos,
            x = sin * rotateAxis[0],
            y = sin * rotateAxis[1],
            z = sin * rotateAxis[2],
        ).apply {
            m = doubleArrayOf(
                w.pow(2) + x.pow(2) - y.pow(2) - z.pow(2),
                2 * x * y - 2 * w * z,
                2 * x * z + 2 * w * y,
                0.0,
                2 * x * y + 2 * w * z,
                w.pow(2) + y.pow(2) - x.pow(2) - z.pow(2),
                2 * y * z - 2 * w * x,
                0.0,
                2 * x * z - 2 * w * y,
                2 * y * z + 2 * w * x,
                w.pow(2) + z.pow(2) - x.pow(2) - y.pow(2),
                0.0,
                0.0,
                0.0,
                0.0,
                1.0
            )
        }
        return transformation(vertex, m)
    }

    //***********************************************************
    // Perspective projection
    private fun perspectiveProjection(
        vertices: Array<Coordinate?>,
        fovInDegree: Double,
        plane: DoubleArray
    ): Array<Coordinate?> {
        val aspectRatio = (plane[2] - plane[0]) / (plane[1] - plane[3])
        val tanA = tan(Math.toRadians(fovInDegree) / 2)
        val m = doubleArrayOf(
            1 / (aspectRatio * tanA),
            0.0,
            0.0,
            0.0,
            0.0,
            1 / tanA,
            0.0,
            0.0,
            0.0,
            0.0,
            -(plane[5] + plane[4]) / (plane[5] - plane[4]),
            -(2 * plane[5] * plane[4]) / (plane[5] - plane[4]),
            0.0,
            0.0,
            -1.0,
            0.0
        )

        return transformation(vertices, m)
    }

    private fun drawCube(
        canvas: Canvas,
        cubeVertices: Array<Coordinate?>,
        paint: Paint
    ) { //draw a cube on the screen
        drawLinePairs(canvas, cubeVertices, 0, 1, paint)
        drawLinePairs(canvas, cubeVertices, 1, 3, paint)
        drawLinePairs(canvas, cubeVertices, 3, 2, paint)
        drawLinePairs(canvas, cubeVertices, 2, 0, paint)
        drawLinePairs(canvas, cubeVertices, 4, 5, paint)
        drawLinePairs(canvas, cubeVertices, 5, 7, paint)
        drawLinePairs(canvas, cubeVertices, 7, 6, paint)
        drawLinePairs(canvas, cubeVertices, 6, 4, paint)
        drawLinePairs(canvas, cubeVertices, 0, 4, paint)
        drawLinePairs(canvas, cubeVertices, 1, 5, paint)
        drawLinePairs(canvas, cubeVertices, 2, 6, paint)
        drawLinePairs(canvas, cubeVertices, 3, 7, paint)
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

    companion object {
        const val HEAD = 0
        const val NECK = 1
        const val CHEST = 2
        const val HIP = 3
        const val UPPER_LEFT_ARM = 4
        const val LOWER_LEFT_ARM = 5
        const val LEFT_HAND = 6
        const val UPPER_RIGHT_ARM = 7
        const val LOWER_RIGHT_ARM = 8
        const val RIGHT_HAND = 9
        const val UPPER_LEFT_LEG = 10
        const val LOWER_LEFT_LEG = 11
        const val LEFT_FOOT = 12
        const val UPPER_RIGHT_LEG = 13
        const val LOWER_RIGHT_LEG = 14
        const val RIGHT_FOOT = 15
    }
}
