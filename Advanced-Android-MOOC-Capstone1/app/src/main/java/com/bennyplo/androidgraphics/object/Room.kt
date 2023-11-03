package com.bennyplo.androidgraphics.`object`

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path

data class Room(
    val bottom: Double = 0.0,
    val doorWidth: Double = 0.0,
    val floorColor: Int = 0,
    val height: Double = 0.0,
    val left: Double = 0.0,
    val paintStyle: Paint.Style = Paint.Style.STROKE,
    val right: Double = 0.0,
    val top: Double = 0.0,
    val wallColor: Int = 0,
) : Object {

    private val floorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = floorColor
        strokeWidth = 5F
        style = paintStyle
    }

    private val wallPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = wallColor
        strokeWidth = 5F
        style = paintStyle
    }

    override val components: Array<Pair<Int, Array<Coordinate>>>

    init {
        if (left >= right) throw IllegalArgumentException("left must be less than right")
        if (top >= bottom) throw IllegalArgumentException("top must be less than bottom")
        if (height <= 0) throw IllegalArgumentException("height must be greater than 0")

        val parts = arrayListOf<Coordinate>()
        val newComponents = arrayListOf<Pair<Int, Array<Coordinate>>>()

        // Floor
        parts.clear()
        parts.add(Coordinate(left, top, 0.0, 1.0))
        parts.add(Coordinate(right, top, 0.0, 1.0))
        parts.add(Coordinate(right, bottom, 0.0, 1.0))
        parts.add(Coordinate(left, bottom, 0.0, 1.0))
        newComponents.add(FLOOR_INDEX to parts.toTypedArray())

        // Walls
        // Left wall
        parts.clear()
        parts.add(Coordinate(left, top, 0.0, 1.0))
        parts.add(Coordinate(left, top, height, 1.0))
        parts.add(Coordinate(left, bottom, height, 1.0))
        parts.add(Coordinate(left, bottom, 0.0, 1.0))
        newComponents.add(LEFT_WALL_INDEX to parts.toTypedArray())

        // Top wall
        parts.clear()
        parts.add(Coordinate(left, top, 0.0, 1.0))
        parts.add(Coordinate(left, top, height, 1.0))
        parts.add(Coordinate(right, top, height, 1.0))
        parts.add(Coordinate(right, top, 0.0, 1.0))
        newComponents.add(TOP_WALL_INDEX to parts.toTypedArray())

        // Right wall
        parts.clear()
        parts.add(Coordinate(right, top, 0.0, 1.0))
        parts.add(Coordinate(right, top, height, 1.0))
        parts.add(Coordinate(right, bottom, height, 1.0))
        parts.add(Coordinate(right, bottom, 0.0, 1.0))
        newComponents.add(RIGHT_WALL_INDEX to parts.toTypedArray())

        // Bottom wall
        parts.clear()
        parts.add(Coordinate(left, bottom, 0.0, 1.0))
        parts.add(Coordinate(left, bottom, height, 1.0))
        parts.add(Coordinate(right, bottom, height, 1.0))
        parts.add(Coordinate(right, bottom, 0.0, 1.0))
        newComponents.add(BOTTOM_WALL_INDEX to parts.toTypedArray())

        val aThirdWidth = (right - left) / 3
        val aThirdHeight = (bottom - top) / 3
        // Inner walls
        parts.clear()
        parts.add(Coordinate(left + aThirdWidth, top, 0.0, 1.0))
        parts.add(Coordinate(left + aThirdWidth, top, height, 1.0))
        parts.add(Coordinate(left + aThirdWidth, top + aThirdHeight - doorWidth, height, 1.0))
        parts.add(Coordinate(left + aThirdWidth, top + aThirdHeight - doorWidth, 0.0, 1.0))
        newComponents.add(INNER_WALL_1_INDEX to parts.toTypedArray())

        parts.clear()
        parts.add(Coordinate(right - aThirdWidth, top, 0.0, 1.0))
        parts.add(Coordinate(right - aThirdWidth, top, height, 1.0))
        parts.add(Coordinate(right - aThirdWidth, top + aThirdHeight - doorWidth, height, 1.0))
        parts.add(Coordinate(right - aThirdWidth, top + aThirdHeight - doorWidth, 0.0, 1.0))
        newComponents.add(INNER_WALL_2_INDEX to parts.toTypedArray())

        parts.clear()
        parts.add(Coordinate(left, top + aThirdHeight, 0.0, 1.0))
        parts.add(Coordinate(left, top + aThirdHeight, height, 1.0))
        parts.add(Coordinate(left + aThirdWidth, top + aThirdHeight, height, 1.0))
        parts.add(Coordinate(left + aThirdWidth, top + aThirdHeight, 0.0, 1.0))
        newComponents.add(INNER_WALL_3_INDEX to parts.toTypedArray())

        parts.clear()
        parts.add(Coordinate(right - aThirdWidth, top + aThirdHeight, 0.0, 1.0))
        parts.add(Coordinate(right - aThirdWidth, top + aThirdHeight, height, 1.0))
        parts.add(Coordinate(right, top + aThirdHeight, height, 1.0))
        parts.add(Coordinate(right, top + aThirdHeight, 0.0, 1.0))
        newComponents.add(INNER_WALL_4_INDEX to parts.toTypedArray())

        parts.clear()
        parts.add(Coordinate(left + aThirdWidth, top + aThirdHeight + doorWidth, 0.0, 1.0))
        parts.add(Coordinate(left + aThirdWidth, top + aThirdHeight + doorWidth, height, 1.0))
        parts.add(Coordinate(left + aThirdWidth, bottom - aThirdHeight - doorWidth, height, 1.0))
        parts.add(Coordinate(left + aThirdWidth, bottom - aThirdHeight - doorWidth, 0.0, 1.0))
        newComponents.add(INNER_WALL_5_INDEX to parts.toTypedArray())

        parts.clear()
        parts.add(Coordinate(right - aThirdWidth, top + aThirdHeight + doorWidth, 0.0, 1.0))
        parts.add(Coordinate(right - aThirdWidth, top + aThirdHeight + doorWidth, height, 1.0))
        parts.add(Coordinate(right - aThirdWidth, bottom - aThirdHeight - doorWidth, height, 1.0))
        parts.add(Coordinate(right - aThirdWidth, bottom - aThirdHeight - doorWidth, 0.0, 1.0))
        newComponents.add(INNER_WALL_6_INDEX to parts.toTypedArray())

        parts.clear()
        parts.add(Coordinate(left, bottom - aThirdHeight, 0.0, 1.0))
        parts.add(Coordinate(left, bottom - aThirdHeight, height, 1.0))
        parts.add(Coordinate(left + aThirdWidth, bottom - aThirdHeight, height, 1.0))
        parts.add(Coordinate(left + aThirdWidth, bottom - aThirdHeight, 0.0, 1.0))
        newComponents.add(INNER_WALL_7_INDEX to parts.toTypedArray())

        parts.clear()
        parts.add(Coordinate(right - aThirdWidth, bottom - aThirdHeight, 0.0, 1.0))
        parts.add(Coordinate(right - aThirdWidth, bottom - aThirdHeight, height, 1.0))
        parts.add(Coordinate(right, bottom - aThirdHeight, height, 1.0))
        parts.add(Coordinate(right, bottom - aThirdHeight, 0.0, 1.0))
        newComponents.add(INNER_WALL_8_INDEX to parts.toTypedArray())

        parts.clear()
        parts.add(Coordinate(left + aThirdWidth, bottom - aThirdHeight + doorWidth, 0.0, 1.0))
        parts.add(Coordinate(left + aThirdWidth, bottom - aThirdHeight + doorWidth, height, 1.0))
        parts.add(Coordinate(left + aThirdWidth, bottom, height, 1.0))
        parts.add(Coordinate(left + aThirdWidth, bottom, 0.0, 1.0))
        newComponents.add(INNER_WALL_9_INDEX to parts.toTypedArray())

        parts.clear()
        parts.add(Coordinate(right - aThirdWidth, bottom - aThirdHeight + doorWidth, 0.0, 1.0))
        parts.add(Coordinate(right - aThirdWidth, bottom - aThirdHeight + doorWidth, height, 1.0))
        parts.add(Coordinate(right - aThirdWidth, bottom, height, 1.0))
        parts.add(Coordinate(right - aThirdWidth, bottom, 0.0, 1.0))
        newComponents.add(INNER_WALL_10_INDEX to parts.toTypedArray())

        components = newComponents.toTypedArray()
    }

    override fun draw(canvas: Canvas, path: Path) {
        orderVertices(components).forEach {
            path.reset()
            it.second.let { coordinates ->
                when (it.first) {
                    FLOOR_INDEX -> {
                        path.moveTo(coordinates[0].x.toFloat(), coordinates[0].y.toFloat())
                        path.lineTo(coordinates[1].x.toFloat(), coordinates[1].y.toFloat())
                        path.lineTo(coordinates[2].x.toFloat(), coordinates[2].y.toFloat())
                        path.lineTo(coordinates[3].x.toFloat(), coordinates[3].y.toFloat())
                        path.close()
                    }

                    INNER_WALL_1_INDEX, INNER_WALL_2_INDEX, INNER_WALL_3_INDEX, INNER_WALL_4_INDEX,
                    INNER_WALL_7_INDEX, INNER_WALL_8_INDEX, INNER_WALL_9_INDEX, INNER_WALL_10_INDEX,
                    -> {
                    }

                    else -> {
                        path.moveTo(coordinates[0].x.toFloat(), coordinates[0].y.toFloat())
                        path.lineTo(coordinates[1].x.toFloat(), coordinates[1].y.toFloat())
                        path.lineTo(coordinates[2].x.toFloat(), coordinates[2].y.toFloat())
                        path.lineTo(coordinates[3].x.toFloat(), coordinates[3].y.toFloat())
                        path.close()
                    }
                }
            }

            canvas.drawPath(path, if (it.first == FLOOR_INDEX) floorPaint else wallPaint)
        }
    }

    private fun orderVertices(partVertices: Array<Pair<Int, Array<Coordinate>>>): Array<Pair<Int, Array<Coordinate>>> =
        partVertices.sortedByDescending {
            it.second.minOf { coordinate -> coordinate.z }
        }.toTypedArray()

    companion object {
        private const val FLOOR_INDEX = 0
        private const val LEFT_WALL_INDEX = 1
        private const val TOP_WALL_INDEX = 2
        private const val RIGHT_WALL_INDEX = 3
        private const val BOTTOM_WALL_INDEX = 4
        private const val INNER_WALL_1_INDEX = 5
        private const val INNER_WALL_2_INDEX = 6
        private const val INNER_WALL_3_INDEX = 7
        private const val INNER_WALL_4_INDEX = 8
        private const val INNER_WALL_5_INDEX = 9
        private const val INNER_WALL_6_INDEX = 10
        private const val INNER_WALL_7_INDEX = 11
        private const val INNER_WALL_8_INDEX = 12
        private const val INNER_WALL_9_INDEX = 13
        private const val INNER_WALL_10_INDEX = 14
    }

}