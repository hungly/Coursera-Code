package com.bennyplo.android_mooc_graphics_3d

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.tan

class MyView(context: Context?) : View(context, null) {

    private val cubeVertices: Array<Coordinate> by lazy {
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

    private val headVertices: Array<Coordinate> by lazy {
        scale(cubeVertices, 100.0, 100.0, 100.0)
    }

    private val neckVertices: Array<Coordinate> by lazy {
        scale(cubeVertices, 50.0, 25.0, 50.0)
    }

    private val chestVertices: Array<Coordinate> by lazy {
        scale(cubeVertices, 200.0, 175.0, 100.0)
    }

    private val hipVertices: Array<Coordinate> by lazy {
        scale(cubeVertices, 175.0, 100.0, 100.0)
    }

    private val uLeftArmVertices: Array<Coordinate> by lazy {
        scale(cubeVertices, 50.0, 150.0, 50.0)
    }

    private val lLeftArmVertices: Array<Coordinate> by lazy {
        scale(cubeVertices, 50.0, 125.0, 50.0)
    }

    private val leftHandVertices: Array<Coordinate> by lazy {
        scale(cubeVertices, 40.0, 40.0, 60.0)
    }

    private val uRightArmVertices: Array<Coordinate> by lazy {
        scale(cubeVertices, 50.0, 150.0, 50.0)
    }

    private val lRightArmVertices: Array<Coordinate> by lazy {
        scale(cubeVertices, 50.0, 125.0, 50.0)
    }

    private val rightHandVertices: Array<Coordinate> by lazy {
        scale(cubeVertices, 40.0, 40.0, 60.0)
    }

    private val uLeftLegVertices: Array<Coordinate> by lazy {
        scale(cubeVertices, 75.0, 175.0, 75.0)
    }

    private val lLeftLegVertices: Array<Coordinate> by lazy {
        scale(cubeVertices, 75.0, 175.0, 75.0)
    }

    private val leftFootVertices: Array<Coordinate> by lazy {
        scale(cubeVertices, 75.0, 50.0, 125.0)
    }

    private val uRightLegVertices: Array<Coordinate> by lazy {
        scale(cubeVertices, 75.0, 175.0, 75.0)
    }

    private val lRightLegVertices: Array<Coordinate> by lazy {
        scale(cubeVertices, 75.0, 175.0, 75.0)
    }

    private val rightFootVertices: Array<Coordinate> by lazy {
        scale(cubeVertices, 75.0, 50.0, 125.0)
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
            color = Color.MAGENTA
            strokeWidth = 2f
        }
    }

    private val chestPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            color = Color.DKGRAY
            strokeWidth = 2f
        }
    }

    private val hipPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            color = Color.CYAN
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
            color = Color.BLUE
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
            color = Color.GREEN
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
            color = Color.BLACK
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
            headVertices.map { it.copy() }.toTypedArray(),
            neckVertices.map { it.copy() }.toTypedArray(),
            chestVertices.map { it.copy() }.toTypedArray(),
            hipVertices.map { it.copy() }.toTypedArray(),
            uLeftArmVertices.map { it.copy() }.toTypedArray(),
            lLeftArmVertices.map { it.copy() }.toTypedArray(),
            leftHandVertices.map { it.copy() }.toTypedArray(),
            uRightArmVertices.map { it.copy() }.toTypedArray(),
            lRightArmVertices.map { it.copy() }.toTypedArray(),
            rightHandVertices.map { it.copy() }.toTypedArray(),
            uLeftLegVertices.map { it.copy() }.toTypedArray(),
            lLeftLegVertices.map { it.copy() }.toTypedArray(),
            leftFootVertices.map { it.copy() }.toTypedArray(),
            uRightLegVertices.map { it.copy() }.toTypedArray(),
            lRightLegVertices.map { it.copy() }.toTypedArray(),
            rightFootVertices.map { it.copy() }.toTypedArray(),
        )
    }

    private val drawAndroid = arrayOf(*baseAndroid)

    private var isCalculating = false
    private var isDrawing = false

    private var angle = 0.0
    private var angleDirection = Y_ROTATION_CHANGE
    private var androidHeight = 0.0
    private var yTranslationValue = 0.0
    private var normalLowestPointOfFoot = 0.0

    init {
        val frameTime = 1000L / 120L

        // Render loop
        CoroutineScope((Dispatchers.Default)).launch {
            do {
                delay(frameTime)
                // Skip frame?
                if (isCalculating.not() && isDrawing.not()) {
                    withContext(Dispatchers.Default) {
                        isDrawing = true
                        invalidate()
                        isDrawing = false
                    }
                }
            } while (true)
        }

        // Animation loop
        CoroutineScope((Dispatchers.Default)).launch {
            do {
                delay(frameTime)
                if (isCalculating.not()) {
                    withContext(Dispatchers.Default) {
                        isCalculating = true

                        // Reset part position
                        var temp = resetParts()

                        temp = positionParts(temp)

                        temp = rotateAndroid(temp)

                        temp = positionAndroid(temp)

                        updateDrawBuffer(temp)

                        isCalculating = false
                    }
                }
            } while (true)
        }
    }

    private fun positionAndroid(partVertices: Array<Array<Coordinate>>): Array<Array<Coordinate>> {
        val result = partVertices.map { coordinates ->
            coordinates.map { coordinate ->
                coordinate.copy()
            }.toTypedArray()
        }.toTypedArray()

        if (yTranslationValue == 0.0) {
            yTranslationValue = (height.toDouble() + androidHeight) / 2
        }

        val x = (width / 2).toDouble()
        val lowestPointOfFoots =
            max(
                calculateBottomCenter(result[LEFT_FOOT]).y,
                calculateBottomCenter(result[RIGHT_FOOT]).y
            )
        val yNormalizer = normalLowestPointOfFoot - lowestPointOfFoots
        result.forEachIndexed { index, coordinates ->
            Log.d("HUNG", "$x,$yTranslationValue")
            result[index] = translate(coordinates, x, yTranslationValue - yNormalizer, 0.0)
        }

        return result
    }

    private fun updateDrawBuffer(partVertices: Array<Array<Coordinate>>) {
        partVertices.forEachIndexed { index, coordinates ->
            drawAndroid[index] = coordinates
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

    private fun rotateAndroid(partVertices: Array<Array<Coordinate>>): Array<Array<Coordinate>> {
        val result = partVertices.map { coordinates ->
            coordinates.map { coordinate ->
                coordinate.copy()
            }.toTypedArray()
        }.toTypedArray()

        result.forEachIndexed { index, coordinates ->
            result[index] = quaternionRotate(coordinates, intArrayOf(0, 1, 0), angle)
            result[index] = quaternionRotate(result[index], intArrayOf(1, 0, 0), 10.0)
        }
        angle += angleDirection
        angleDirection = if (angle > Y_ROTATION_LIMIT) {
            -Y_ROTATION_CHANGE
        } else if (angle < -Y_ROTATION_LIMIT) {
            Y_ROTATION_CHANGE
        } else {
            angleDirection
        }

        return result
    }

    private fun resetParts() = baseAndroid.map { coordinates ->
        coordinates.map { coordinate ->
            coordinate.copy()
        }.toTypedArray()
    }.toTypedArray()

    private fun positionParts(partVertices: Array<Array<Coordinate>>): Array<Array<Coordinate>> {
        val result = partVertices.map { coordinates ->
            coordinates.map { coordinate ->
                coordinate.copy()
            }.toTypedArray()
        }.toTypedArray()

        result[NECK] = alignTopBottom(result[HEAD], result[NECK])
        result[CHEST] = alignTopBottom(result[NECK], result[CHEST])
        result[HIP] = alignTopBottom(result[CHEST], result[HIP])

        result[UPPER_LEFT_ARM] =
            alignArmToBody(result[CHEST], result[UPPER_LEFT_ARM], true)
        result[LOWER_LEFT_ARM] =
            alignTopBottom(result[UPPER_LEFT_ARM], result[LOWER_LEFT_ARM])
        result[LEFT_HAND] =
            alignTopBottom(result[LOWER_LEFT_ARM], result[LEFT_HAND])

        result[UPPER_RIGHT_ARM] =
            alignArmToBody(result[CHEST], result[UPPER_RIGHT_ARM], false)
        result[LOWER_RIGHT_ARM] =
            alignTopBottom(result[UPPER_RIGHT_ARM], result[LOWER_RIGHT_ARM])
        result[RIGHT_HAND] =
            alignTopBottom(result[LOWER_RIGHT_ARM], result[RIGHT_HAND])

        result[UPPER_LEFT_LEG] =
            alignTopBottom(result[HIP], result[UPPER_LEFT_LEG], -100.0)
        result[LOWER_LEFT_LEG] =
            alignTopBottom(result[UPPER_LEFT_LEG], result[LOWER_LEFT_LEG])
        result[LEFT_FOOT] =
            alignTopBottom(result[LOWER_LEFT_LEG], result[LEFT_FOOT], zOffset = -25.0)

        result[UPPER_RIGHT_LEG] =
            alignTopBottom(result[HIP], result[UPPER_RIGHT_LEG], 100.0)
        result[LOWER_RIGHT_LEG] =
            alignTopBottom(result[UPPER_RIGHT_LEG], result[LOWER_RIGHT_LEG])
        result[RIGHT_FOOT] =
            alignTopBottom(result[LOWER_RIGHT_LEG], result[RIGHT_FOOT], zOffset = -25.0)

        if (normalLowestPointOfFoot == 0.0) {
            normalLowestPointOfFoot = max(
                calculateBottomCenter(result[LEFT_FOOT]).y,
                calculateBottomCenter(result[RIGHT_FOOT]).y
            )
        }
        if (androidHeight == 0.0) {
            androidHeight = normalLowestPointOfFoot - calculateTopCenter(result[HEAD]).y
        }

        return result
    }

    private fun alignTopBottom(
        parent: Array<Coordinate>,
        child: Array<Coordinate>,
        xOffset: Double = 0.0,
        zOffset: Double = 0.0
    ): Array<Coordinate> {
        val parentBottomCenter = calculateBottomCenter(parent)
        val childTopCenter = calculateTopCenter(child)
        val childBottomCenter = calculateBottomCenter(child)
        return translate(
            child,
            childTopCenter.x + parentBottomCenter.x + xOffset,
            childTopCenter.y + parentBottomCenter.y + (childBottomCenter.y - childTopCenter.y),
            childTopCenter.z + parentBottomCenter.z + zOffset
        )
    }

    private fun alignArmToBody(
        body: Array<Coordinate>,
        arm: Array<Coordinate>,
        leftSide: Boolean
    ): Array<Coordinate> {
        val bodyTopCenter = calculateTopCenter(body)
        val armTopCenter = calculateTopCenter(arm)
        val armWidth = calculateRightCenter(arm).x - calculateLeftCenter(arm).x
        val bodySideCenter: Coordinate
        val armSideCenter: Coordinate
        val x = if (leftSide) {
            bodySideCenter = calculateLeftCenter(body)
            armSideCenter = calculateRightCenter(arm)
            armSideCenter.x + bodySideCenter.x - armWidth
        } else {
            bodySideCenter = calculateRightCenter(body)
            armSideCenter = calculateLeftCenter(arm)
            armSideCenter.x + bodySideCenter.x + armWidth
        }
        return translate(
            arm,
            x,
            bodyTopCenter.y - armTopCenter.y,
            bodyTopCenter.z + bodyTopCenter.z
        )
    }

    private fun calculateBottomCenter(vertices: Array<Coordinate>): Coordinate = Coordinate(
        (vertices[BOTTOM_RIGHT_BACK].x + vertices[BOTTOM_RIGHT_FRONT].x + vertices[BOTTOM_LEFT_BACK].x + vertices[BOTTOM_LEFT_FRONT].x) / 4,
        (vertices[BOTTOM_RIGHT_BACK].y + vertices[BOTTOM_RIGHT_FRONT].y + vertices[BOTTOM_LEFT_BACK].y + vertices[BOTTOM_LEFT_FRONT].y) / 4,
        (vertices[BOTTOM_RIGHT_BACK].z + vertices[BOTTOM_RIGHT_FRONT].z + vertices[BOTTOM_LEFT_BACK].z + vertices[BOTTOM_LEFT_FRONT].z) / 4,
        1.0
    )

    private fun calculateTopCenter(vertices: Array<Coordinate>): Coordinate = Coordinate(
        (vertices[TOP_RIGHT_BACK].x + vertices[TOP_RIGHT_FRONT].x + vertices[TOP_LEFT_BACK].x + vertices[TOP_LEFT_FRONT].x) / 4,
        (vertices[TOP_RIGHT_BACK].y + vertices[TOP_RIGHT_FRONT].y + vertices[TOP_LEFT_BACK].y + vertices[TOP_LEFT_FRONT].y) / 4,
        (vertices[TOP_RIGHT_BACK].z + vertices[TOP_RIGHT_FRONT].z + vertices[TOP_LEFT_BACK].z + vertices[TOP_LEFT_FRONT].z) / 4,
        1.0
    )

    private fun calculateLeftCenter(vertices: Array<Coordinate>): Coordinate = Coordinate(
        (vertices[TOP_LEFT_FRONT].x + vertices[TOP_LEFT_BACK].x + vertices[BOTTOM_LEFT_FRONT].x + vertices[BOTTOM_LEFT_BACK].x) / 4,
        (vertices[TOP_LEFT_FRONT].y + vertices[TOP_LEFT_BACK].y + vertices[BOTTOM_LEFT_FRONT].y + vertices[BOTTOM_LEFT_BACK].y) / 4,
        (vertices[TOP_LEFT_FRONT].z + vertices[TOP_LEFT_BACK].z + vertices[BOTTOM_LEFT_FRONT].z + vertices[BOTTOM_LEFT_BACK].z) / 4,
        1.0
    )

    private fun calculateRightCenter(vertices: Array<Coordinate>): Coordinate = Coordinate(
        (vertices[TOP_RIGHT_FRONT].x + vertices[TOP_RIGHT_BACK].x + vertices[BOTTOM_RIGHT_FRONT].x + vertices[BOTTOM_RIGHT_BACK].x) / 4,
        (vertices[TOP_RIGHT_FRONT].y + vertices[TOP_RIGHT_BACK].y + vertices[BOTTOM_RIGHT_FRONT].y + vertices[BOTTOM_RIGHT_BACK].y) / 4,
        (vertices[TOP_RIGHT_FRONT].z + vertices[TOP_RIGHT_BACK].z + vertices[BOTTOM_RIGHT_FRONT].z + vertices[BOTTOM_RIGHT_BACK].z) / 4,
        1.0
    )

    private fun calculateFrontCenter(vertices: Array<Coordinate>): Coordinate = Coordinate(
        (vertices[TOP_RIGHT_FRONT].x + vertices[TOP_LEFT_FRONT].x + vertices[BOTTOM_RIGHT_FRONT].x + vertices[BOTTOM_LEFT_FRONT].x) / 4,
        (vertices[TOP_RIGHT_FRONT].y + vertices[TOP_LEFT_FRONT].y + vertices[BOTTOM_RIGHT_FRONT].y + vertices[BOTTOM_LEFT_FRONT].y) / 4,
        (vertices[TOP_RIGHT_FRONT].z + vertices[TOP_LEFT_FRONT].z + vertices[BOTTOM_RIGHT_FRONT].z + vertices[BOTTOM_LEFT_FRONT].z) / 4,
        1.0
    )

    private fun calculateBackCenter(vertices: Array<Coordinate>): Coordinate = Coordinate(
        (vertices[TOP_RIGHT_BACK].x + vertices[TOP_LEFT_BACK].x + vertices[BOTTOM_RIGHT_BACK].x + vertices[BOTTOM_LEFT_BACK].x) / 4,
        (vertices[TOP_RIGHT_BACK].y + vertices[TOP_LEFT_BACK].y + vertices[BOTTOM_RIGHT_BACK].y + vertices[BOTTOM_LEFT_BACK].y) / 4,
        (vertices[TOP_RIGHT_BACK].z + vertices[TOP_LEFT_BACK].z + vertices[BOTTOM_RIGHT_BACK].z + vertices[BOTTOM_LEFT_BACK].z) / 4,
        1.0
    )

    //*********************************
// Matrix and transformation functions
    private fun getIdentityMatrix(): DoubleArray { //return an 4x4 identity matrix
        return doubleArrayOf(
            1.0, 0.0, 0.0, 0.0,
            0.0, 1.0, 0.0, 0.0,
            0.0, 0.0, 1.0, 0.0,
            0.0, 0.0, 0.0, 1.0
        )
    }

    private fun transformation(
        vertices: Array<Coordinate>,
        matrix: DoubleArray
    ): Array<Coordinate> {   //Affine transform a 3D object with vertices
        // vertices - vertices of the 3D object.
        // matrix - transformation matrix
        val result = arrayOf(*vertices)
        for (i in vertices.indices) {
            result[i] = transformation(vertices[i], matrix)
            result[i].Normalise()
        }
        return result
    }

    private fun transformation(
        vertex: Coordinate,
        matrix: DoubleArray
    ): Coordinate { //affine transformation with homogeneous coordinates
        //i.e. a vector (vertex) multiply with the transformation matrix
        // vertex - vector in 3D
        // matrix - transformation matrix
        val result = Coordinate()
        result.x =
            matrix[0] * vertex.x + matrix[1] * vertex.y + matrix[2] * vertex.z + matrix[3] * vertex.w
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
        vertices: Array<Coordinate>,
        tx: Double,
        ty: Double,
        tz: Double
    ): Array<Coordinate> {
        val matrix = getIdentityMatrix()
        matrix[3] = tx
        matrix[7] = ty
        matrix[11] = tz
        return transformation(vertices, matrix)
    }

    //***********************************************************
// Affine rotation
    fun rotate(
        vertices: Array<Coordinate>,
        rx: Double? = null,
        ry: Double? = null,
        rz: Double? = null
    ): Array<Coordinate> {
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
        vertices: Array<Coordinate>,
        rotateAxis: IntArray,
        rotateDegree: Double
    ): Array<Coordinate> {
        val result = arrayOf(*vertices)

        vertices.forEachIndexed { index, coordinate ->
            coordinate.let {
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
        vertices: Array<Coordinate>,
        fovInDegree: Double,
        plane: DoubleArray
    ): Array<Coordinate> {
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
        cubeVertices: Array<Coordinate>,
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
        vertices: Array<Coordinate>,
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
            vertices[start].x.toInt().toFloat(),
            vertices[start].y.toInt().toFloat(),
            vertices[end].x.toInt().toFloat(),
            vertices[end].y.toInt().toFloat(),
            paint
        )
    }

    private fun scale(
        vertices: Array<Coordinate>,
        sx: Double,
        sy: Double,
        sz: Double
    ): Array<Coordinate> {
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

        const val TOP_LEFT_BACK = 0
        const val TOP_LEFT_FRONT = 1
        const val BOTTOM_LEFT_BACK = 2
        const val BOTTOM_LEFT_FRONT = 3
        const val TOP_RIGHT_BACK = 4
        const val TOP_RIGHT_FRONT = 5
        const val BOTTOM_RIGHT_BACK = 6
        const val BOTTOM_RIGHT_FRONT = 7

        const val Y_ROTATION_LIMIT = 50.0
        const val Y_ROTATION_CHANGE = 1.0
    }
}
