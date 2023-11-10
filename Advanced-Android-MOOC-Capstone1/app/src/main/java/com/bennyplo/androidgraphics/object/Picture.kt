package com.bennyplo.androidgraphics.`object`

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import kotlin.math.abs
import kotlin.random.Random
import kotlin.random.nextInt

data class Picture(
    val bottom: Double = 0.0,
    val left: Double = 0.0,
    val right: Double = 0.0,
    val top: Double = 0.0,
    val pattern: Int,
    val bgColor: Int,
    val patternColor: Int
) : Object {

    private val bgPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = bgColor
            strokeWidth = 1F
            style = Paint.Style.FILL
        }
    }

    private val patternPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = patternColor
            strokeWidth = 1F
            style = Paint.Style.FILL
        }
    }



    override val components: Array<Pair<Int, Array<Coordinate>>>

    override fun draw(canvas: Canvas, path: Path) {
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

                        canvas.drawPath(path, bgPaint)
                    }

                    PATTERN_INDEX -> {
                        when (pattern) {
                            PATTERN_1 -> {
                                path.moveTo(
                                    coordinates[0].x.toFloat(),
                                    coordinates[0].y.toFloat()
                                )
                                path.lineTo(
                                    coordinates[1].x.toFloat(),
                                    coordinates[1].y.toFloat()
                                )
                                path.lineTo(
                                    coordinates[2].x.toFloat(),
                                    coordinates[2].y.toFloat()
                                )
                                path.lineTo(
                                    coordinates[3].x.toFloat(),
                                    coordinates[3].y.toFloat()
                                )
                                path.close()
                            }

                            PATTERN_2 -> {
                                path.addCircle(
                                    coordinates[0].x.toFloat(),
                                    coordinates[0].y.toFloat(),
                                    abs((coordinates[2].x.toFloat() - coordinates[1].x.toFloat()) / 2) - 5,
                                    Path.Direction.CW
                                )
                            }

                            PATTERN_3, PATTERN_5 -> {
                                path.moveTo(
                                    coordinates[0].x.toFloat(),
                                    coordinates[0].y.toFloat()
                                )
                                path.lineTo(
                                    coordinates[1].x.toFloat(),
                                    coordinates[1].y.toFloat()
                                )
                                path.lineTo(
                                    coordinates[2].x.toFloat(),
                                    coordinates[2].y.toFloat()
                                )
                                path.lineTo(
                                    coordinates[3].x.toFloat(),
                                    coordinates[3].y.toFloat()
                                )
                                path.close()
                            }

                            PATTERN_4  -> {
                                path.moveTo(
                                    coordinates[0].x.toFloat(),
                                    coordinates[0].y.toFloat()
                                )
                                path.lineTo(
                                    coordinates[1].x.toFloat(),
                                    coordinates[1].y.toFloat()
                                )
                                path.lineTo(
                                    coordinates[2].x.toFloat(),
                                    coordinates[2].y.toFloat()
                                )
                                path.lineTo(
                                    coordinates[3].x.toFloat(),
                                    coordinates[3].y.toFloat()
                                )
                                path.close()
                            }
                            PATTERN_6 -> {
                                path.moveTo(
                                    coordinates[0].x.toFloat(),
                                    coordinates[0].y.toFloat()
                                )
                                path.lineTo(
                                    coordinates[1].x.toFloat(),
                                    coordinates[1].y.toFloat()
                                )
                                path.lineTo(
                                    coordinates[2].x.toFloat(),
                                    coordinates[2].y.toFloat()
                                )
                                path.close()
                            }
                        }

                        canvas.drawPath(path, patternPaint)
                    }
                }
            }
        }
    }

    init {
        if (left >= right) throw IllegalArgumentException("left must be less than right")
        if (top >= bottom) throw IllegalArgumentException("top must be less than bottom")

        val parts = arrayListOf<Coordinate>()
        val newComponents = arrayListOf<Pair<Int, Array<Coordinate>>>()

        parts.add(Coordinate(left, top, 0.0, 1.0))
        parts.add(Coordinate(right, top, 0.0, 1.0))
        parts.add(Coordinate(right, bottom, 0.0, 1.0))
        parts.add(Coordinate(left, bottom, 0.0, 1.0))

        newComponents.add(BORDER_INDEX to parts.toTypedArray())

        parts.clear()
        when (pattern) {
            PATTERN_1 -> {
                parts.add(Coordinate(left,  top + (bottom - top) / 2, 0.0, 1.0))
                parts.add(Coordinate(left + (right - left) / 2, top, 0.0, 1.0))
                parts.add(Coordinate(right, top + (bottom - top) / 2, 0.0, 1.0))
                parts.add(Coordinate(left + (right - left) / 2, bottom, 0.0, 1.0))
            }

            PATTERN_2 -> {
                parts.add(Coordinate(left + (right - left) / 2, top + (bottom - top) / 2, 0.0, 1.0))
                parts.add(Coordinate(left, (bottom - top) / 2, 0.0, 1.0))
                parts.add(Coordinate(right, (bottom - top) / 2, 0.0, 1.0))
            }

            PATTERN_3 -> {
                val temp = (bottom - top) / 3
                parts.add(Coordinate(left, top + temp, 0.0, 1.0))
                parts.add(Coordinate(left, bottom - temp, 0.0, 1.0))
                parts.add(Coordinate(right, top + temp, 0.0, 1.0))
                parts.add(Coordinate(right, bottom - temp, 0.0, 1.0))
            }

            PATTERN_4 -> {
                val temp = (right - left) / 3
                parts.add(Coordinate(left + temp, top, 0.0, 1.0))
                parts.add(Coordinate(right - temp, top, 0.0, 1.0))
                parts.add(Coordinate(right - temp, bottom, 0.0, 1.0))
                parts.add(Coordinate(left + temp, bottom, 0.0, 1.0))
            }

            PATTERN_5 -> {
                val temp = (right - left) / 1.5
                parts.add(Coordinate(left, top + temp, 0.0, 1.0))
                parts.add(Coordinate(right, top + temp, 0.0, 1.0))
                parts.add(Coordinate(right, bottom - temp, 0.0, 1.0))
                parts.add(Coordinate(left, bottom - temp, 0.0, 1.0))
            }

            PATTERN_6 -> {
                parts.add(Coordinate(left, top, 0.0, 1.0))
                parts.add(Coordinate(right, top, 0.0, 1.0))
                parts.add(Coordinate(right, bottom, 0.0, 1.0))
            }
        }
        newComponents.add(PATTERN_INDEX to parts.toTypedArray())

        components = newComponents.toTypedArray()
    }

    companion object {
        private const val BORDER_INDEX = 0
        private const val PATTERN_INDEX = 1

        private const val PATTERN_1 = 0
        private const val PATTERN_2 = 1
        private const val PATTERN_3 = 2
        private const val PATTERN_4 = 3
        private const val PATTERN_5 = 4
        private const val PATTERN_6 = 5

        private val PATTERNS = intArrayOf(
            PATTERN_1,
            PATTERN_2,
            PATTERN_3,
            PATTERN_4,
            PATTERN_5,
            PATTERN_6
        )

        private val COLORS = intArrayOf(
            Color.RED,
            Color.GREEN,
            Color.BLUE,
            Color.YELLOW,
            Color.CYAN,
            Color.MAGENTA
        )

        fun getPattern(): Int = Random.nextInt(0..PATTERNS.lastIndex)

        fun getColor(excludeColor: Int? = null): Int {
            var color = COLORS[Random.nextInt(0..COLORS.lastIndex)]
            while (color == excludeColor) {
                color = COLORS[Random.nextInt(0..COLORS.lastIndex)]
            }
            return color
        }
    }

}