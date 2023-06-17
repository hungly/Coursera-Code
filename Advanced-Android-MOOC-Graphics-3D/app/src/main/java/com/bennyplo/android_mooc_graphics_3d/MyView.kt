package com.bennyplo.android_mooc_graphics_3d

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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
        scale(cubeVertices, 75.0, 50.0, 150.0)
    }

    private val uRightLegVertices: Array<Coordinate> by lazy {
        scale(cubeVertices, 75.0, 175.0, 75.0)
    }

    private val lRightLegVertices: Array<Coordinate> by lazy {
        scale(cubeVertices, 75.0, 175.0, 75.0)
    }

    private val rightFootVertices: Array<Coordinate> by lazy {
        scale(cubeVertices, 75.0, 50.0, 150.0)
    }

    private val fpsPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 25F
        }
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

    private var frameCount = 0
    private var lastFrameCount = 0L
    private var currentTime = System.currentTimeMillis()
    private var fps = ""

    private val drawAndroid = arrayOf(*baseAndroid)

    private var isCalculating = false
    private var isDrawing = false

    private var angle = 0.0
    private var angleDirection = Y_ROTATION_CHANGE
    private var androidHeight = 0.0
    private var xTranslationValue = 0.0
    private var yTranslationValue = 0.0
    private var normalLowestPointOfFoot = 0.0

    private var headAngle = 0.0
    private var headNodDirection = HEAD_NOD_CHANGE

    private var armAngle = 0.0

    private var bodyAngle = 0.0
    private var bodyRockDirection = BODY_ROCK_RATE

    private var bodyTwistAngle = BODY_TWIST_LIMIT
    private var bodyTwistDirection = BODY_TWIST_RATE

    private var bodyXMovement = 0.0
    private var bodyXDirection = BODY_X_MOVEMENT_RATE

    private var upperLegBendAngle = UPPER_LEG_BEND_LOWER_LIMIT
    private var upperLegBendDirection = UPPER_LEG_BEND_RATE

    private var lowerLegBendAngle = LOWER_LEG_BEND_LOWER_LIMIT
    private var lowerLegBendDirection = LOWER_LEG_BEND_RATE

    private var renderJob: Job? = null

    init {

        viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)

                // Animation loop
                CoroutineScope((Dispatchers.Default)).launch {
                    do {
                        delay(5)
                        if (isCalculating.not()) {
                            withContext(Dispatchers.Default) {
                                isCalculating = true

                                currentTime = System.currentTimeMillis()
                                if (currentTime - lastFrameCount > 1000) {
                                    val t =
                                        frameCount.toDouble() / (currentTime - lastFrameCount) * 1000
                                    fps = String.format("FPS: %.1f", t)
                                    frameCount = 0
                                    lastFrameCount = currentTime
                                }

                                // Reset part position
                                var temp = resetParts()

                                temp = headNod(temp)

                                temp[UPPER_LEFT_ARM] = quaternionRotate(
                                    temp[UPPER_LEFT_ARM],
                                    intArrayOf(1, 0, 0),
                                    -80.0
                                )
                                temp[LOWER_LEFT_ARM] = quaternionRotate(
                                    temp[LOWER_LEFT_ARM],
                                    intArrayOf(0, 0, 1),
                                    -110.0
                                )
                                temp[LEFT_HAND] =
                                    quaternionRotate(temp[LEFT_HAND], intArrayOf(0, 0, 1), -110.0)

                                temp[UPPER_RIGHT_ARM] = quaternionRotate(
                                    temp[UPPER_RIGHT_ARM],
                                    intArrayOf(1, 0, 0),
                                    -80.0
                                )
                                temp[LOWER_RIGHT_ARM] = quaternionRotate(
                                    temp[LOWER_RIGHT_ARM],
                                    intArrayOf(0, 0, 1),
                                    110.0
                                )
                                temp[RIGHT_HAND] =
                                    quaternionRotate(temp[RIGHT_HAND], intArrayOf(0, 0, 1), 110.0)

                                temp[LOWER_LEFT_ARM] = rotateArm(temp[LOWER_LEFT_ARM], 0.0)
                                temp[LEFT_HAND] = rotateArm(temp[LEFT_HAND], 0.0)

                                temp[LOWER_RIGHT_ARM] = rotateArm(temp[LOWER_RIGHT_ARM], 180.0)
                                temp[RIGHT_HAND] = rotateArm(temp[RIGHT_HAND], 180.0)

                                temp[CHEST] = bodyRock(temp[CHEST])
                                temp[CHEST] = bodyTwist(temp[CHEST])

                                temp[UPPER_LEFT_LEG] = upperLegMovement(temp[UPPER_LEFT_LEG])
                                temp[LOWER_LEFT_LEG] = lowerLegMovement(temp[LOWER_LEFT_LEG])

                                temp[UPPER_RIGHT_LEG] = upperLegMovement(temp[UPPER_RIGHT_LEG])
                                temp[LOWER_RIGHT_LEG] = lowerLegMovement(temp[LOWER_RIGHT_LEG])

                                temp[UPPER_LEFT_LEG] = quaternionRotate(
                                    temp[UPPER_LEFT_LEG],
                                    intArrayOf(0, 1, 0),
                                    50.0
                                )
                                temp[LOWER_LEFT_LEG] = quaternionRotate(
                                    temp[LOWER_LEFT_LEG],
                                    intArrayOf(0, 1, 0),
                                    50.0
                                )
                                temp[LEFT_FOOT] = quaternionRotate(
                                    temp[LEFT_FOOT],
                                    intArrayOf(0, 1, 0),
                                    50.0
                                )

                                temp[UPPER_RIGHT_LEG] = quaternionRotate(
                                    temp[UPPER_RIGHT_LEG],
                                    intArrayOf(0, 1, 0),
                                    -50.0
                                )
                                temp[LOWER_RIGHT_LEG] = quaternionRotate(
                                    temp[LOWER_RIGHT_LEG],
                                    intArrayOf(0, 1, 0),
                                    -50.0
                                )
                                temp[RIGHT_FOOT] = quaternionRotate(
                                    temp[RIGHT_FOOT],
                                    intArrayOf(0, 1, 0),
                                    -50.0
                                )

                                temp = positionParts(temp)

                                temp = upperBodyXMove(temp)

                                temp = rotateAndroid(temp)
//                                temp.forEachIndexed { index, coordinates ->
//                                    temp[index] =
//                                        quaternionRotate(coordinates, intArrayOf(0, 1, 0), 30.0)
//                                    temp[index] =
//                                        quaternionRotate(temp[index], intArrayOf(1, 0, 0), 10.0)
//                                }

                                temp = positionAndroid(temp)

                                updateDrawBuffer(temp)

                                isCalculating = false
                            }
                        }
                    } while (true)
                }
            }
        })
    }

    fun stopRender() {
        renderJob?.cancel()
    }

    fun startRender() {
        // Render loop
        renderJob?.cancel()
        renderJob = CoroutineScope((Dispatchers.Default)).launch {
            do {
                delay(FRAME_TIME)
                // Skip frame?
                if (isCalculating.not() && isDrawing.not()) {
                    withContext(Dispatchers.Default) {
                        isDrawing = true
                        invalidate()
                        frameCount++
                        isDrawing = false
                    }
                }
            } while (true)
        }
    }

    private fun positionAndroid(partVertices: Array<Array<Coordinate>>): Array<Array<Coordinate>> {
        if (yTranslationValue == 0.0) {
            yTranslationValue = (height.toDouble() - androidHeight) / 2
        }

        if (xTranslationValue == 0.0) {
            xTranslationValue = (width / 2).toDouble()
        }

        val lowestPointOfFoots =
            max(
                calculateBottomCenter(partVertices[LEFT_FOOT]).y,
                calculateBottomCenter(partVertices[RIGHT_FOOT]).y
            )
        val yNormalizer = normalLowestPointOfFoot - lowestPointOfFoots
        partVertices.forEachIndexed { index, coordinates ->
            partVertices[index] =
                translate(coordinates, xTranslationValue, yTranslationValue + yNormalizer, 0.0)
        }

        return partVertices
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
        canvas.drawText(fps, 25F, height - 25F, fpsPaint)
    }

    private fun rotateAndroid(partVertices: Array<Array<Coordinate>>): Array<Array<Coordinate>> {
        partVertices.forEachIndexed { index, coordinates ->
            partVertices[index] = quaternionRotate(coordinates, intArrayOf(0, 1, 0), angle)
            partVertices[index] = quaternionRotate(partVertices[index], intArrayOf(1, 0, 0), 10.0)
        }
        angle += angleDirection
        angleDirection = if (angle > Y_ROTATION_LIMIT) {
            -Y_ROTATION_CHANGE
        } else if (angle < -Y_ROTATION_LIMIT) {
            Y_ROTATION_CHANGE
        } else {
            angleDirection
        }

        return partVertices
    }


    private fun headNod(partVertices: Array<Array<Coordinate>>): Array<Array<Coordinate>> {
        partVertices[HEAD] = quaternionRotate(partVertices[HEAD], intArrayOf(1, 0, 0), headAngle)
        headAngle += headNodDirection
        headNodDirection = if (headAngle > HEAD_NOD_UPPER_LIMIT) {
            -HEAD_NOD_CHANGE
        } else if (headAngle < HEAD_NOD_LOWER_LIMIT) {
            HEAD_NOD_CHANGE
        } else {
            headNodDirection
        }

        return partVertices
    }

    private fun rotateArm(partVertices: Array<Coordinate>, offset: Double): Array<Coordinate> {
        val result = quaternionRotate(partVertices, intArrayOf(1, 0, 0), armAngle + offset)
        armAngle += ARM_ANGLE_CHANGE
        if (armAngle > 360) {
            armAngle = 0.0
        }
        return result
    }

    private fun bodyRock(partVertices: Array<Coordinate>): Array<Coordinate> {
        val result = quaternionRotate(partVertices, intArrayOf(0, 1, 0), bodyAngle)
        bodyAngle += bodyRockDirection
        bodyRockDirection = if (bodyAngle > BODY_ROCK_LIMIT) {
            -BODY_ROCK_RATE
        } else if (bodyAngle < -BODY_ROCK_LIMIT) {
            BODY_ROCK_RATE
        } else {
            bodyRockDirection
        }
        return result
    }

    private fun bodyTwist(partVertices: Array<Coordinate>): Array<Coordinate> {
        val result = quaternionRotate(partVertices, intArrayOf(0, 0, 1), bodyTwistAngle)
        bodyTwistAngle += bodyTwistDirection
        bodyTwistDirection = if (bodyTwistAngle > BODY_TWIST_LIMIT) {
            -BODY_TWIST_RATE
        } else if (bodyTwistAngle < -BODY_TWIST_LIMIT) {
            BODY_TWIST_RATE
        } else {
            bodyTwistDirection
        }
        return result
    }

    private fun upperBodyXMove(partVertices: Array<Array<Coordinate>>): Array<Array<Coordinate>> {
        partVertices[HEAD] = translate(partVertices[HEAD], bodyXMovement, 0.0, 0.0)
        partVertices[NECK] = translate(partVertices[NECK], bodyXMovement, 0.0, 0.0)
        partVertices[CHEST] = translate(partVertices[CHEST], bodyXMovement, 0.0, 0.0)

        partVertices[UPPER_LEFT_ARM] = translate(partVertices[UPPER_LEFT_ARM], bodyXMovement, 0.0, 0.0)
        partVertices[LOWER_LEFT_ARM] = translate(partVertices[LOWER_LEFT_ARM], bodyXMovement, 0.0, 0.0)
        partVertices[LEFT_HAND] = translate(partVertices[LEFT_HAND], bodyXMovement, 0.0, 0.0)

        partVertices[UPPER_RIGHT_ARM] = translate(partVertices[UPPER_RIGHT_ARM], bodyXMovement, 0.0, 0.0)
        partVertices[LOWER_RIGHT_ARM] = translate(partVertices[LOWER_RIGHT_ARM], bodyXMovement, 0.0, 0.0)
        partVertices[RIGHT_HAND] = translate(partVertices[RIGHT_HAND], bodyXMovement, 0.0, 0.0)

        bodyXMovement += bodyXDirection
        bodyXDirection = if (bodyXMovement > BODY_X_MOVEMENT_LIMIT) {
            -BODY_X_MOVEMENT_RATE
        } else if (bodyXMovement < -BODY_X_MOVEMENT_LIMIT) {
            BODY_X_MOVEMENT_RATE
        } else {
            bodyXDirection
        }
        return partVertices
    }

    private fun upperLegMovement(partVertices: Array<Coordinate>) : Array<Coordinate>{
        val result = quaternionRotate(partVertices, intArrayOf(1, 0, 0), -upperLegBendAngle)
        upperLegBendAngle += upperLegBendDirection
        upperLegBendDirection = if (upperLegBendAngle > UPPER_LEG_BEND_UPPER_LIMIT) {
            -UPPER_LEG_BEND_RATE
        } else if (upperLegBendAngle < UPPER_LEG_BEND_LOWER_LIMIT) {
            UPPER_LEG_BEND_RATE
        } else {
            upperLegBendDirection
        }
        return result
    }

    private fun lowerLegMovement(partVertices: Array<Coordinate>) : Array<Coordinate>{
        val result = quaternionRotate(partVertices, intArrayOf(1, 0, 0), lowerLegBendAngle)
        lowerLegBendAngle += lowerLegBendDirection
        lowerLegBendDirection = if (lowerLegBendAngle > LOWER_LEG_BEND_UPPER_LIMIT) {
            -LOWER_LEG_BEND_RATE
        } else if (lowerLegBendAngle < LOWER_LEG_BEND_LOWER_LIMIT) {
            LOWER_LEG_BEND_RATE
        } else {
            lowerLegBendDirection
        }
        return result
    }

    private fun resetParts() = baseAndroid.map { coordinates ->
        coordinates.map { coordinate ->
            coordinate.copy()
        }.toTypedArray()
    }.toTypedArray()

    private fun positionParts(partVertices: Array<Array<Coordinate>>): Array<Array<Coordinate>> {
        val headBase = calculateBottomCenter(partVertices[HEAD])
        partVertices[HEAD] = translate(partVertices[HEAD], -headBase.x, -headBase.y, -headBase.z)
        partVertices[NECK] = alignTopBottom(partVertices[HEAD], partVertices[NECK])
        partVertices[CHEST] = alignTopBottom(partVertices[NECK], partVertices[CHEST])
        partVertices[HIP] = alignTopBottom(partVertices[CHEST], partVertices[HIP])

        partVertices[UPPER_LEFT_ARM] =
            alignArmToBody(partVertices[CHEST], partVertices[UPPER_LEFT_ARM], true)
        partVertices[LOWER_LEFT_ARM] =
            alignTopBottom(partVertices[UPPER_LEFT_ARM], partVertices[LOWER_LEFT_ARM])
        partVertices[LEFT_HAND] =
            alignTopBottom(partVertices[LOWER_LEFT_ARM], partVertices[LEFT_HAND])

        partVertices[UPPER_RIGHT_ARM] =
            alignArmToBody(partVertices[CHEST], partVertices[UPPER_RIGHT_ARM], false)
        partVertices[LOWER_RIGHT_ARM] =
            alignTopBottom(partVertices[UPPER_RIGHT_ARM], partVertices[LOWER_RIGHT_ARM])
        partVertices[RIGHT_HAND] =
            alignTopBottom(partVertices[LOWER_RIGHT_ARM], partVertices[RIGHT_HAND])

        partVertices[UPPER_LEFT_LEG] =
            alignTopBottom(partVertices[HIP], partVertices[UPPER_LEFT_LEG], -100.0)
        partVertices[LOWER_LEFT_LEG] =
            alignTopBottom(partVertices[UPPER_LEFT_LEG], partVertices[LOWER_LEFT_LEG])
        partVertices[LEFT_FOOT] =
            alignTopBottom(
                partVertices[LOWER_LEFT_LEG],
                partVertices[LEFT_FOOT],
                zOffset = -FOOT_Z_OFFSET
            )

        partVertices[UPPER_RIGHT_LEG] =
            alignTopBottom(partVertices[HIP], partVertices[UPPER_RIGHT_LEG], 100.0)
        partVertices[LOWER_RIGHT_LEG] =
            alignTopBottom(partVertices[UPPER_RIGHT_LEG], partVertices[LOWER_RIGHT_LEG])
        partVertices[RIGHT_FOOT] =
            alignTopBottom(
                partVertices[LOWER_RIGHT_LEG],
                partVertices[RIGHT_FOOT],
                zOffset = -FOOT_Z_OFFSET
            )

        if (normalLowestPointOfFoot == 0.0) {
            normalLowestPointOfFoot = max(
                calculateBottomCenter(partVertices[LEFT_FOOT]).y,
                calculateBottomCenter(partVertices[RIGHT_FOOT]).y
            )
        }
        if (androidHeight == 0.0) {
            androidHeight = normalLowestPointOfFoot - calculateTopCenter(partVertices[HEAD]).y
        }

        return partVertices
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
            parentBottomCenter.x - childTopCenter.x + xOffset,
            parentBottomCenter.y + childTopCenter.y + (childBottomCenter.y - childTopCenter.y),
            parentBottomCenter.z - childTopCenter.z + zOffset
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
            bodyTopCenter.z - armTopCenter.z
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
            vertices[start].x.toFloat(),
            vertices[start].y.toFloat(),
            vertices[end].x.toFloat(),
            vertices[end].y.toFloat(),
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
        const val FRAME_TIME = 1000L / 120L

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

        const val FOOT_Z_OFFSET = 50.0

        const val Y_ROTATION_LIMIT = 50.0
        const val Y_ROTATION_CHANGE = 0.5

        const val HEAD_NOD_LOWER_LIMIT = -20.0
        const val HEAD_NOD_UPPER_LIMIT = 30.0
        const val HEAD_NOD_CHANGE = 1.0

        const val ARM_ANGLE_CHANGE = 0.75

        const val BODY_ROCK_LIMIT = 10.0
        const val BODY_ROCK_RATE = 0.25

        const val BODY_TWIST_LIMIT = 5.0
        const val BODY_TWIST_RATE = 0.1

        const val BODY_X_MOVEMENT_LIMIT = 25
        const val BODY_X_MOVEMENT_RATE = 0.5

        const val UPPER_LEG_BEND_UPPER_LIMIT = 75.0
        const val UPPER_LEG_BEND_LOWER_LIMIT = 60.0
        const val UPPER_LEG_BEND_RATE = 0.1

        const val LOWER_LEG_BEND_UPPER_LIMIT = 25.0
        const val LOWER_LEG_BEND_LOWER_LIMIT = 10.0
        const val LOWER_LEG_BEND_RATE = 0.1
    }
}
