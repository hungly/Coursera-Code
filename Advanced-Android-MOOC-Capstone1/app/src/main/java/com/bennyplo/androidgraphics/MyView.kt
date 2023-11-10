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

    private var angle = 0.0

    private val room by lazy {
        Room(
            left = 0.0 - (roomSize / 2),
            top = 0.0 - (roomSize / 2),
            right = roomSize - (roomSize / 2),
            bottom = roomSize - (roomSize / 2),
            height = roomHeight,
            doorWidth = 80.0,
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
                        is Room -> drawBuffer[key] = value.copy()
                        is ECG -> drawBuffer[key] = value.copy().apply {
                            scale(0.1, 0.2, 1.0)
                            translate(0.0, roomHeight / 2, -roomSize / 2)
                            quaternionRotate(doubleArrayOf(0.0, 0.0, 1.0), 180.0)
                            quaternionRotate(doubleArrayOf(1.0, 0.0, 0.0), -90.0)
                        }
                    }
                }
                // Add your rotation functions here to spin the virtual objects

                // Final transformations
                drawBuffer.forEach { key, value ->
                    value.quaternionRotate(doubleArrayOf(1.0, 0.0, 0.0), 45.0)
                    value.quaternionRotate(doubleArrayOf(0.0, 1.0, 0.0), 20.0)

//                    value.quaternionRotate(doubleArrayOf(0.8, 0.5, 0.7), angle)

                    value.translate(
                        viewWidth / 2.0,
                        viewHeight / 2.0,
                        0.0
                    )

                    angle += 1
                    angle %= 360
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

    private val objects = SparseArray<Object>().apply {
        put(ROOM_INDEX, room)
        put(ECG_INDEX, ecg)
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

        private val ECG_DATA = intArrayOf(
            1539,
            1531,
            1547,
            1539,
            1543,
            1531,
            1575,
            1591,
            1543,
            1539,
            1523,
            1539,
            1543,
            1539,
            1859,
            2587,
            1455,
            1539,
            1523,
            1527,
            1543,
            1587,
            1619,
            1635,
            1655,
            1659,
            1639,
            1639,
            1579,
            1547,
            1527,
            1527,
            1547,
            1543,
            1551,
            1547,
            1547,
            1563,
            1539,
            1527,
            1523,
            1543,
            1539,
            1575,
            1599,
            1555,
            1531,
            1539,
            1551,
            1547,
            1487,
            1995,
            2331,
            1563,
            1539,
            1523,
            1563,
            1559,
            1591,
            1615,
            1635,
            1659,
            1651,
            1675,
            1631,
            1567,
            1531,
            1519,
            1527,
            1511,
            1531,
            1527,
            1539,
            1539,
            1527,
            1539,
            1543,
            1547,
            1547,
            1571,
            1603,
            1571,
            1539,
            1551,
            1547,
            1559,
            1487,
            1927,
            2475,
            1491,
            1531,
            1503,
            1551,
            1559,
            1571,
            1599,
            1623,
            1663,
            1659,
            1659,
            1615,
            1547,
            1519,
            1519,
            1511,
            1523,
            1539,
            1543,
            1551,
            1567,
            1563,
            1551,
            1555,
            1547,
            1587,
            1579,
            1567,
            1559,
            1539,
            1559,
            1555,
            1563
        )
    }

}