package com.bennyplo.androidgraphics.`object`

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path

data class ECG(
    val bottom: Double = 0.0,
    val left: Double = 0.0,
    val maxValue: Double = 0.0,
    val minValue: Double = 0.0,
    val right: Double = 0.0,
    val top: Double = 0.0,
) : Object {

    private val paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLUE
            strokeWidth = 5F
            style = Paint.Style.STROKE
        }
    }

    override val components: Array<Pair<Int, Array<Coordinate>>>

    init {
        if (left >= right) throw IllegalArgumentException("left must be less than right")
        if (top >= bottom) throw IllegalArgumentException("top must be less than bottom")
        if (maxValue <= minValue) throw IllegalArgumentException("maxValue must be greater than minValue")

        val parts = arrayListOf<Coordinate>()
        val newComponents = arrayListOf<Pair<Int, Array<Coordinate>>>()
        val step = (right - left) / ECG_DATA.size
        val valueScaleFactor = (bottom - top) / (maxValue - minValue)

        parts.add(Coordinate(left, top, 0.0, 1.0))
        parts.add(Coordinate(right, top, 0.0, 1.0))
        parts.add(Coordinate(right, bottom, 0.0, 1.0))
        parts.add(Coordinate(left, bottom, 0.0, 1.0))

        newComponents.add(BORDER_INDEX to parts.toTypedArray())

        parts.clear()
        ECG_DATA.forEachIndexed { index, i ->
            parts.add(Coordinate(right - (index * step), ((i - top - minValue) / valueScaleFactor) + top, 0.0, 1.0))
        }
        newComponents.add(GRAPH_INDEX to parts.toTypedArray())

        components = newComponents.toTypedArray()
    }

    override fun draw(canvas: Canvas, path: Path) {
        orderVertices(components).let { components ->
            components.forEach {
                path.reset()
                it.second.let { coordinates ->
                    when (it.first) {
                        BORDER_INDEX -> {
                            path.moveTo(coordinates[0].x.toFloat(), coordinates[0].y.toFloat())
                            path.lineTo(coordinates[1].x.toFloat(), coordinates[1].y.toFloat())
                            path.lineTo(coordinates[2].x.toFloat(), coordinates[2].y.toFloat())
                            path.lineTo(coordinates[3].x.toFloat(), coordinates[3].y.toFloat())
                            path.close()

                        }

                        GRAPH_INDEX -> {
                            coordinates.foldRightIndexed(arrayListOf<Float>()) { index, coordinate, list ->
                                list.add(coordinate.x.toFloat())
                                list.add(coordinate.y.toFloat())
                                if (index > 0 && index < coordinates.lastIndex){
                                    list.add(coordinate.x.toFloat())
                                    list.add(coordinate.y.toFloat())
                                }
                                list
                            }.toFloatArray().let {pts->
                                canvas.drawLines(pts,paint)
                            }
                        }
                    }
                }

                canvas.drawPath(path, paint)
            }
        }
    }

    companion object {

        private const val BORDER_INDEX = 0
        private const val GRAPH_INDEX = 1

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