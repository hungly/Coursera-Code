package com.bennyplo.androidgraphics

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import java.util.Timer
import java.util.TimerTask

/**
 * Created by benlo on 26/08/2019.
 */
class MyView(context: Context?) : View(context, null) {

    private val redPaint: Paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE // Stroke only no fill
            color = Color.RED // Color red
            strokeWidth = 5f // Set the line stroke width to 5
        }
    }

    private val task: TimerTask by lazy {
        object : TimerTask() {
            override fun run() {
                // Add your rotation functions here to spin the virtual objects
                this@MyView.invalidate() // Update the view
            }
        }
    }

    private val timer by lazy { Timer() }

    // Screen dimension
    private val viewHeight: Int = resources.displayMetrics.heightPixels - 70

    private val viewWidth: Int = resources.displayMetrics.widthPixels

    init {
        timer.scheduleAtFixedRate(task, 1000, 100)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Add your drawing code here
        canvas.drawRect(0f, 0f, 600f, 600f, redPaint)
    }

    companion object {

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