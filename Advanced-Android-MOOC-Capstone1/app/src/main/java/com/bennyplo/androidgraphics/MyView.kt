package com.bennyplo.androidgraphics

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.SparseArray
import android.view.View
import androidx.core.util.forEach
import com.bennyplo.androidgraphics.`object`.ECG
import com.bennyplo.androidgraphics.`object`.Object
import com.bennyplo.androidgraphics.`object`.Picture
import com.bennyplo.androidgraphics.`object`.Room
import java.util.Timer
import java.util.TimerTask

/**
 * Created by benlo on 26/08/2019.
 */
class MyView(context: Context?) : View(context, null) {

    private val path by lazy {
        Path()
    }

    private val roomSize by lazy {
        viewWidth - (viewWidth / 2.0)
    }

    private val graphWidth by lazy {
        viewWidth - (viewWidth / 4.0)
    }

    private val roomHeight by lazy {
        250.0
    }

    private val picHeight by lazy {
        roomHeight - (roomHeight / 1.5)
    }

    private val picWidth by lazy {
        (roomSize / 20)
    }

    private var angle = 0.0

    private val room by lazy {
        Room(
            left = 0.0 - (roomSize / 2),
            top = 0.0 - (roomSize / 2),
            right = roomSize - (roomSize / 2),
            bottom = roomSize - (roomSize / 2),
            height = roomHeight,
            doorWidth = 50.0,
            floorColor = Color.BLACK,
            wallColor = Color.RED,
            paintStyle = Paint.Style.STROKE
        )
    }
    private val ecg by lazy {
        ECG(
            left = 0.0 - (graphWidth / 2),
            top = 0.0 - (1000 / 2),
            right = graphWidth - (graphWidth / 2),
            bottom = 1000 - (1000 / 2.0),
            maxValue = 2400.0,
            minValue = 1700.0
        )
    }

    private val task: TimerTask by lazy {
        object : TimerTask() {
            override fun run() {
                objects.forEach { key, value ->
                    when (value) {
                        is Room -> buffer[key] = value.copy()
                        is ECG -> buffer[key] = value.copy().apply {
                            scale(0.1, 0.2, 1.0)
                            translate(0.0, roomHeight / 2, -roomSize / 2)
                            quaternionRotate(doubleArrayOf(0.0, 0.0, 1.0), 180.0)
                            quaternionRotate(doubleArrayOf(1.0, 0.0, 0.0), -90.0)
                        }

                        is Picture -> buffer[key] = value.copy(
                            pattern = value.pattern,
                            bgColor = value.bgColor,
                            patternColor = value.patternColor
                        ).apply {
                            when (key) {
                                PIC_1 -> {
                                    translate(
                                        (roomSize / 3),
                                        roomHeight / 2,
                                        -roomSize / 2
                                    )
                                }
                                PIC_2 -> {
                                    translate(
                                        -(roomSize / 3),
                                        roomHeight / 2,
                                        -roomSize / 2
                                    )
                                }
                                PIC_3 -> {
                                    translate(
                                        (roomSize / 3),
                                        roomHeight / 2,
                                        -roomSize / 2 + roomSize / 3
                                    )
                                }
                                PIC_4 -> {
                                    translate(
                                        -(roomSize / 3),
                                        roomHeight / 2,
                                        -roomSize / 2 + roomSize / 3
                                    )
                                }
                                PIC_5 -> {
                                    translate(
                                        (roomSize / 3),
                                        roomHeight / 2,
                                        roomSize / 2 - roomSize / 3
                                    )
                                }
                                PIC_6 -> {
                                    translate(
                                        -(roomSize / 3),
                                        roomHeight / 2,
                                        roomSize / 2 - roomSize / 3
                                    )
                                }
                                PIC_7 -> {
                                    translate(
                                        (roomSize / 3),
                                        roomHeight / 2,
                                        roomSize / 2
                                    )
                                }
                                PIC_8 -> {
                                    translate(
                                        -(roomSize / 3),
                                        roomHeight / 2,
                                        roomSize / 2
                                    )
                                }
                                PIC_9 -> {
                                    translate(0.0, roomHeight / 2, roomSize / 2)
                                }
                                PIC_10 -> {
                                    quaternionRotate(doubleArrayOf(0.0, 1.0, 0.0), 90.0)
                                    translate(
                                        (roomSize / 2),
                                        roomHeight / 2,
                                        -(roomSize / 3)
                                    )
                                }
                                PIC_11 -> {
                                    quaternionRotate(doubleArrayOf(0.0, 1.0, 0.0), 90.0)
                                    translate(
                                        (roomSize / 2) - (roomSize / 3),
                                        roomHeight / 2,
                                        -(roomSize / 3)
                                    )
                                }
                                PIC_12 -> {
                                    quaternionRotate(doubleArrayOf(0.0, 1.0, 0.0), 90.0)
                                    translate(
                                        -(roomSize / 2) + (roomSize / 3),
                                        roomHeight / 2,
                                        -(roomSize / 3)
                                    )
                                }
                                PIC_13 -> {
                                    quaternionRotate(doubleArrayOf(0.0, 1.0, 0.0), 90.0)
                                    translate(
                                        -(roomSize / 2),
                                        roomHeight / 2,
                                        -(roomSize / 3)
                                    )
                                }

                                PIC_14 -> {
                                    quaternionRotate(doubleArrayOf(0.0, 1.0, 0.0), 90.0)
                                    translate(
                                        (roomSize / 2),
                                        roomHeight / 2,
                                        0.0
                                    )
                                }
                                PIC_15 -> {
                                    quaternionRotate(doubleArrayOf(0.0, 1.0, 0.0), 90.0)
                                    translate(
                                        (roomSize / 2) - (roomSize / 3),
                                        roomHeight / 2,
                                        0.0
                                    )
                                }
                                PIC_16 -> {
                                    quaternionRotate(doubleArrayOf(0.0, 1.0, 0.0), 90.0)
                                    translate(
                                        -(roomSize / 2) + (roomSize / 3),
                                        roomHeight / 2,
                                        0.0
                                    )
                                }
                                PIC_17 -> {
                                    quaternionRotate(doubleArrayOf(0.0, 1.0, 0.0), 90.0)
                                    translate(
                                        -(roomSize / 2),
                                        roomHeight / 2,
                                        0.0
                                    )
                                }

                                PIC_18 -> {
                                    quaternionRotate(doubleArrayOf(0.0, 1.0, 0.0), 90.0)
                                    translate(
                                        (roomSize / 2),
                                        roomHeight / 2,
                                        (roomSize / 3)
                                    )
                                }
                                PIC_19 -> {
                                    quaternionRotate(doubleArrayOf(0.0, 1.0, 0.0), 90.0)
                                    translate(
                                        (roomSize / 2) - (roomSize / 3),
                                        roomHeight / 2,
                                        (roomSize / 3)
                                    )
                                }
                                PIC_20 -> {
                                    quaternionRotate(doubleArrayOf(0.0, 1.0, 0.0), 90.0)
                                    translate(
                                        -(roomSize / 2) + (roomSize / 3),
                                        roomHeight / 2,
                                        (roomSize / 3)
                                    )
                                }
                                PIC_21 -> {
                                    quaternionRotate(doubleArrayOf(0.0, 1.0, 0.0), 90.0)
                                    translate(
                                        -(roomSize / 2),
                                        roomHeight / 2,
                                        (roomSize / 3)
                                    )
                                }
                            }
                            quaternionRotate(doubleArrayOf(0.0, 0.0, 1.0), 180.0)
                            quaternionRotate(doubleArrayOf(1.0, 0.0, 0.0), -90.0)
                        }
                    }
                }
                // Add your rotation functions here to spin the virtual objects

                // Final transformations
                buffer.forEach { key, value ->
//                    value.quaternionRotate(doubleArrayOf(1.0, 0.0, 0.0), 45.0)
//                    value.quaternionRotate(doubleArrayOf(0.0, 1.0, 0.0), 20.0)

//                    value.quaternionRotate(doubleArrayOf(1.0, 0.0, 0.0), 90.0)

                    value.quaternionRotate(doubleArrayOf(0.8, 0.5, 0.7), angle)

                    value.translate(
                        viewWidth / 2.0,
                        viewHeight / 2.0,
                        0.0
                    )

                    angle += 0.1
                    angle %= 360
                }

                buffer.forEach { key, value ->
                    drawBuffer[key] = value
                }

                this@MyView.invalidate() // Update the view
            }
        }
    }

    private val timer by lazy { Timer() }

    // Screen dimension
    private val viewHeight: Int by lazy { resources.displayMetrics.heightPixels - 70 }

    private val viewWidth: Int by lazy { resources.displayMetrics.widthPixels }

    private val drawBuffer = SparseArray<Object>()
    private val buffer = SparseArray<Object>()

    private val objects = SparseArray<Object>().apply {
        put(ROOM_INDEX, room)
        put(ECG_INDEX, ecg)
        Picture.getColor().let { bgColor ->
            put(
                PIC_1, Picture(
                    left = 0.0 - picWidth,
                    top = 0.0 - picHeight,
                    right = picWidth,
                    bottom = roomHeight - picHeight * 2,
                    pattern = Picture.getPattern(),
                    bgColor = bgColor,
                    patternColor = Picture.getColor(bgColor)
                )
            )
        }
        Picture.getColor().let { bgColor ->
            put(
                PIC_2, Picture(
                    left = 0.0 - picWidth,
                    top = 0.0 - picHeight,
                    right = picWidth,
                    bottom = roomHeight - picHeight * 2,
                    pattern = Picture.getPattern(),
                    bgColor = bgColor,
                    patternColor = Picture.getColor(bgColor)
                )
            )
        }
        Picture.getColor().let { bgColor ->
            put(
                PIC_3, Picture(
                    left = 0.0 - picWidth,
                    top = 0.0 - picHeight,
                    right = picWidth,
                    bottom = roomHeight - picHeight * 2,
                    pattern = Picture.getPattern(),
                    bgColor = bgColor,
                    patternColor = Picture.getColor(bgColor)
                )
            )
        }
        Picture.getColor().let { bgColor ->
            put(
                PIC_4, Picture(
                    left = 0.0 - picWidth,
                    top = 0.0 - picHeight,
                    right = picWidth,
                    bottom = roomHeight - picHeight * 2,
                    pattern = Picture.getPattern(),
                    bgColor = bgColor,
                    patternColor = Picture.getColor(bgColor)
                )
            )
        }
        Picture.getColor().let { bgColor ->
            put(
                PIC_5, Picture(
                    left = 0.0 - picWidth,
                    top = 0.0 - picHeight,
                    right = picWidth,
                    bottom = roomHeight - picHeight * 2,
                    pattern = Picture.getPattern(),
                    bgColor = bgColor,
                    patternColor = Picture.getColor(bgColor)
                )
            )
        }
        Picture.getColor().let { bgColor ->
            put(
                PIC_6, Picture(
                    left = 0.0 - picWidth,
                    top = 0.0 - picHeight,
                    right = picWidth,
                    bottom = roomHeight - picHeight * 2,
                    pattern = Picture.getPattern(),
                    bgColor = bgColor,
                    patternColor = Picture.getColor(bgColor)
                )
            )
        }
        Picture.getColor().let { bgColor ->
            put(
                PIC_7, Picture(
                    left = 0.0 - picWidth,
                    top = 0.0 - picHeight,
                    right = picWidth,
                    bottom = roomHeight - picHeight * 2,
                    pattern = Picture.getPattern(),
                    bgColor = bgColor,
                    patternColor = Picture.getColor(bgColor)
                )
            )
        }
        Picture.getColor().let { bgColor ->
            put(
                PIC_8, Picture(
                    left = 0.0 - picWidth,
                    top = 0.0 - picHeight,
                    right = picWidth,
                    bottom = roomHeight - picHeight * 2,
                    pattern = Picture.getPattern(),
                    bgColor = bgColor,
                    patternColor = Picture.getColor(bgColor)
                )
            )
        }
        Picture.getColor().let { bgColor ->
            put(
                PIC_9, Picture(
                    left = 0.0 - picWidth,
                    top = 0.0 - picHeight,
                    right = picWidth,
                    bottom = roomHeight - picHeight * 2,
                    pattern = Picture.getPattern(),
                    bgColor = bgColor,
                    patternColor = Picture.getColor(bgColor)
                )
            )
        }
        Picture.getColor().let { bgColor ->
            put(
                PIC_10, Picture(
                    left = 0.0 - picWidth,
                    top = 0.0 - picHeight,
                    right = picWidth,
                    bottom = roomHeight - picHeight * 2,
                    pattern = Picture.getPattern(),
                    bgColor = bgColor,
                    patternColor = Picture.getColor(bgColor)
                )
            )
        }
        Picture.getColor().let { bgColor ->
            put(
                PIC_11, Picture(
                    left = 0.0 - picWidth,
                    top = 0.0 - picHeight,
                    right = picWidth,
                    bottom = roomHeight - picHeight * 2,
                    pattern = Picture.getPattern(),
                    bgColor = bgColor,
                    patternColor = Picture.getColor(bgColor)
                )
            )
        }
        Picture.getColor().let { bgColor ->
            put(
                PIC_12, Picture(
                    left = 0.0 - picWidth,
                    top = 0.0 - picHeight,
                    right = picWidth,
                    bottom = roomHeight - picHeight * 2,
                    pattern = Picture.getPattern(),
                    bgColor = bgColor,
                    patternColor = Picture.getColor(bgColor)
                )
            )
        }
        Picture.getColor().let { bgColor ->
            put(
                PIC_13, Picture(
                    left = 0.0 - picWidth,
                    top = 0.0 - picHeight,
                    right = picWidth,
                    bottom = roomHeight - picHeight * 2,
                    pattern = Picture.getPattern(),
                    bgColor = bgColor,
                    patternColor = Picture.getColor(bgColor)
                )
            )
        }
        Picture.getColor().let { bgColor ->
            put(
                PIC_14, Picture(
                    left = 0.0 - picWidth,
                    top = 0.0 - picHeight,
                    right = picWidth,
                    bottom = roomHeight - picHeight * 2,
                    pattern = Picture.getPattern(),
                    bgColor = bgColor,
                    patternColor = Picture.getColor(bgColor)
                )
            )
        }
        Picture.getColor().let { bgColor ->
            put(
                PIC_15, Picture(
                    left = 0.0 - picWidth,
                    top = 0.0 - picHeight,
                    right = picWidth,
                    bottom = roomHeight - picHeight * 2,
                    pattern = Picture.getPattern(),
                    bgColor = bgColor,
                    patternColor = Picture.getColor(bgColor)
                )
            )
        }
        Picture.getColor().let { bgColor ->
            put(
                PIC_16, Picture(
                    left = 0.0 - picWidth,
                    top = 0.0 - picHeight,
                    right = picWidth,
                    bottom = roomHeight - picHeight * 2,
                    pattern = Picture.getPattern(),
                    bgColor = bgColor,
                    patternColor = Picture.getColor(bgColor)
                )
            )
        }
        Picture.getColor().let { bgColor ->
            put(
                PIC_17, Picture(
                    left = 0.0 - picWidth,
                    top = 0.0 - picHeight,
                    right = picWidth,
                    bottom = roomHeight - picHeight * 2,
                    pattern = Picture.getPattern(),
                    bgColor = bgColor,
                    patternColor = Picture.getColor(bgColor)
                )
            )
        }
        Picture.getColor().let { bgColor ->
            put(
                PIC_18, Picture(
                    left = 0.0 - picWidth,
                    top = 0.0 - picHeight,
                    right = picWidth,
                    bottom = roomHeight - picHeight * 2,
                    pattern = Picture.getPattern(),
                    bgColor = bgColor,
                    patternColor = Picture.getColor(bgColor)
                )
            )
        }
        Picture.getColor().let { bgColor ->
            put(
                PIC_19, Picture(
                    left = 0.0 - picWidth,
                    top = 0.0 - picHeight,
                    right = picWidth,
                    bottom = roomHeight - picHeight * 2,
                    pattern = Picture.getPattern(),
                    bgColor = bgColor,
                    patternColor = Picture.getColor(bgColor)
                )
            )
        }
        Picture.getColor().let { bgColor ->
            put(
                PIC_20, Picture(
                    left = 0.0 - picWidth,
                    top = 0.0 - picHeight,
                    right = picWidth,
                    bottom = roomHeight - picHeight * 2,
                    pattern = Picture.getPattern(),
                    bgColor = bgColor,
                    patternColor = Picture.getColor(bgColor)
                )
            )
        }
        Picture.getColor().let { bgColor ->
            put(
                PIC_21, Picture(
                    left = 0.0 - picWidth,
                    top = 0.0 - picHeight,
                    right = picWidth,
                    bottom = roomHeight - picHeight * 2,
                    pattern = Picture.getPattern(),
                    bgColor = bgColor,
                    patternColor = Picture.getColor(bgColor)
                )
            )
        }
    }

    init {
        timer.scheduleAtFixedRate(task, 1000, 100)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Add your drawing code here
        drawBuffer.forEach { _, value ->
            value.draw(canvas, path)
        }
    }

    companion object {

        private const val ROOM_INDEX = 0
        private const val ECG_INDEX = 1
        private const val PIC_1 = 2
        private const val PIC_2 = 3
        private const val PIC_3 = 4
        private const val PIC_4 = 5
        private const val PIC_5 = 6
        private const val PIC_6 = 7
        private const val PIC_7 = 8
        private const val PIC_8 = 9
        private const val PIC_9 = 10
        private const val PIC_10 = 11
        private const val PIC_11 = 12
        private const val PIC_12 = 13
        private const val PIC_13 = 14
        private const val PIC_14 = 15
        private const val PIC_15 = 16
        private const val PIC_16 = 17
        private const val PIC_17 = 18
        private const val PIC_18= 19
        private const val PIC_19= 20
        private const val PIC_20= 21
        private const val PIC_21= 22
    }

}