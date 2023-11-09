package com.bennyplo.androidgraphics

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.SparseArray
import android.view.View
import androidx.core.util.forEach
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
        viewWidth - (viewWidth / 2.5)
    }

    private val room by lazy {
        Room(
            left = 0.0,
            top = 0.0,
            right = roomSize,
            bottom = roomSize,
            height = 250.0,
            doorWidth = 80.0,
            floorColor = Color.BLACK,
            wallColor = Color.RED,
            paintStyle = Paint.Style.STROKE
        )
    }

    private val task: TimerTask by lazy {
        object : TimerTask() {
            override fun run() {
                objects.forEach { key, value ->
                    when (value) {
                        is Room -> drawBuffer[key] = value.copy()
                    }
                }
                // Add your rotation functions here to spin the virtual objects

                // Final transformations
                drawBuffer.forEach { _, value ->
                    value.quaternionRotate(intArrayOf(1, 0, 0), 45.0)
                    value.quaternionRotate(intArrayOf(0, 1, 0), 20.0)
                    value.translate(
                        (viewWidth - roomSize) / 2.0,
                        (viewHeight - roomSize) / 2.0,
                        0.0
                    )
                }

                this@MyView.invalidate() // Update the view
            }
        }
    }

    private val timer by lazy { Timer() }

    // Screen dimension
    private val viewHeight: Int by lazy { resources.displayMetrics.heightPixels - 70 }

    private val viewWidth: Int by lazy { resources.displayMetrics.widthPixels }

    private val drawBuffer = SparseArray<Object>().apply {
        put(ROOM_INDEX, room)
    }

    private val objects = SparseArray<Object>().apply {
        put(ROOM_INDEX, room)
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