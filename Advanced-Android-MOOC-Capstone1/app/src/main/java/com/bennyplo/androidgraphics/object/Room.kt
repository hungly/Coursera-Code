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

    private val floorPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = floorColor
            strokeWidth = 5F
            style = paintStyle
        }
    }

    private val wallPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = wallColor
            strokeWidth = 5F
            style = paintStyle
        }
    }

    override val components: Array<Pair<Int, Array<Coordinate>>>

    init {
        if (left >= right) throw IllegalArgumentException("left must be less than right")
        if (top >= bottom) throw IllegalArgumentException("top must be less than bottom")
        if (height <= 0) throw IllegalArgumentException("height must be greater than 0")

        val aThirdWidth = (right - left) / 3
        val aThirdHeight = (bottom - top) / 3

        val floor = arrayListOf<Coordinate>()
        val ceiling = arrayListOf<Coordinate>()
        val newComponents = arrayListOf<Pair<Int, Array<Coordinate>>>()

        // Floor
        floor.add(Coordinate(left, top, 0.0, 1.0))
        floor.add(Coordinate(right, top, 0.0, 1.0))
        floor.add(Coordinate(right, bottom, 0.0, 1.0))
        floor.add(Coordinate(left, bottom, 0.0, 1.0))

        floor.add(Coordinate(left + aThirdWidth, top, 0.0, 1.0))
        floor.add(Coordinate(left + aThirdWidth, top + aThirdHeight - doorWidth, 0.0, 1.0))

        floor.add(Coordinate(right - aThirdWidth, top, 0.0, 1.0))
        floor.add(Coordinate(right - aThirdWidth, top + aThirdHeight - doorWidth, 0.0, 1.0))

        floor.add(Coordinate(left, top + aThirdHeight, 0.0, 1.0))
        floor.add(Coordinate(left + aThirdWidth, top + aThirdHeight, 0.0, 1.0))

        floor.add(Coordinate(right - aThirdWidth, top + aThirdHeight, 0.0, 1.0))
        floor.add(Coordinate(right, top + aThirdHeight, 0.0, 1.0))

        floor.add(Coordinate(left + aThirdWidth, top + aThirdHeight + doorWidth, 0.0, 1.0))
        floor.add(Coordinate(left + aThirdWidth, bottom - aThirdHeight - doorWidth, 0.0, 1.0))

        floor.add(Coordinate(right - aThirdWidth, top + aThirdHeight + doorWidth, 0.0, 1.0))
        floor.add(Coordinate(right - aThirdWidth, bottom - aThirdHeight - doorWidth, 0.0, 1.0))

        floor.add(Coordinate(left, bottom - aThirdHeight, 0.0, 1.0))
        floor.add(Coordinate(left + aThirdWidth, bottom - aThirdHeight, 0.0, 1.0))

        floor.add(Coordinate(right - aThirdWidth, bottom - aThirdHeight, 0.0, 1.0))
        floor.add(Coordinate(right, bottom - aThirdHeight, 0.0, 1.0))

        floor.add(Coordinate(left + aThirdWidth, bottom - aThirdHeight + doorWidth, 0.0, 1.0))
        floor.add(Coordinate(left + aThirdWidth, bottom, 0.0, 1.0))

        floor.add(Coordinate(right - aThirdWidth, bottom - aThirdHeight + doorWidth, 0.0, 1.0))
        floor.add(Coordinate(right - aThirdWidth, bottom, 0.0, 1.0))

        newComponents.add(FLOOR_INDEX to floor.toTypedArray())

        floor.forEach { coordinate ->
            ceiling.add(Coordinate(coordinate.x, coordinate.y, coordinate.z + height, coordinate.w))
        }

        newComponents.add(CEILING_INDEX to ceiling.toTypedArray())

        components = newComponents.toTypedArray()
    }

    override fun draw(canvas: Canvas, path: Path) {
        orderVertices(components).let { components ->
            components.forEach {
                path.reset()
                it.second.let { coordinates ->
                    when (it.first) {
                        FLOOR_INDEX -> {
                            path.moveTo(coordinates[0].x.toFloat(), coordinates[0].y.toFloat())
                            path.lineTo(coordinates[1].x.toFloat(), coordinates[1].y.toFloat())
                            path.lineTo(coordinates[2].x.toFloat(), coordinates[2].y.toFloat())
                            path.lineTo(coordinates[3].x.toFloat(), coordinates[3].y.toFloat())
                            path.close()

                            canvas.drawLine(
                                coordinates[4].x.toFloat(),
                                coordinates[4].y.toFloat(),
                                coordinates[5].x.toFloat(),
                                coordinates[5].y.toFloat(),
                                floorPaint
                            )
                            canvas.drawLine(
                                coordinates[6].x.toFloat(),
                                coordinates[6].y.toFloat(),
                                coordinates[7].x.toFloat(),
                                coordinates[7].y.toFloat(),
                                floorPaint
                            )
                            canvas.drawLine(
                                coordinates[8].x.toFloat(),
                                coordinates[8].y.toFloat(),
                                coordinates[9].x.toFloat(),
                                coordinates[9].y.toFloat(),
                                floorPaint
                            )
                            canvas.drawLine(
                                coordinates[10].x.toFloat(),
                                coordinates[10].y.toFloat(),
                                coordinates[11].x.toFloat(),
                                coordinates[11].y.toFloat(),
                                floorPaint
                            )
                            canvas.drawLine(
                                coordinates[12].x.toFloat(),
                                coordinates[12].y.toFloat(),
                                coordinates[13].x.toFloat(),
                                coordinates[13].y.toFloat(),
                                floorPaint
                            )
                            canvas.drawLine(
                                coordinates[14].x.toFloat(),
                                coordinates[14].y.toFloat(),
                                coordinates[15].x.toFloat(),
                                coordinates[15].y.toFloat(),
                                floorPaint
                            )
                            canvas.drawLine(
                                coordinates[16].x.toFloat(),
                                coordinates[16].y.toFloat(),
                                coordinates[17].x.toFloat(),
                                coordinates[17].y.toFloat(),
                                floorPaint
                            )
                            canvas.drawLine(
                                coordinates[18].x.toFloat(),
                                coordinates[18].y.toFloat(),
                                coordinates[19].x.toFloat(),
                                coordinates[19].y.toFloat(),
                                floorPaint
                            )
                            canvas.drawLine(
                                coordinates[20].x.toFloat(),
                                coordinates[20].y.toFloat(),
                                coordinates[21].x.toFloat(),
                                coordinates[21].y.toFloat(),
                                floorPaint
                            )
                            canvas.drawLine(
                                coordinates[22].x.toFloat(),
                                coordinates[22].y.toFloat(),
                                coordinates[23].x.toFloat(),
                                coordinates[23].y.toFloat(),
                                floorPaint
                            )
                        }

                        else -> {
                            components.find { components ->
                                components.first == FLOOR_INDEX
                            }?.second?.let { floorCoordinates ->
                                path.moveTo(coordinates[0].x.toFloat(), coordinates[0].y.toFloat())
                                path.lineTo(coordinates[1].x.toFloat(), coordinates[1].y.toFloat())
                                path.lineTo(coordinates[2].x.toFloat(), coordinates[2].y.toFloat())
                                path.lineTo(coordinates[3].x.toFloat(), coordinates[3].y.toFloat())
                                path.close()

                                canvas.drawLine(
                                    coordinates[0].x.toFloat(),
                                    coordinates[0].y.toFloat(),
                                    floorCoordinates[0].x.toFloat(),
                                    floorCoordinates[0].y.toFloat(),
                                    wallPaint
                                )
                                canvas.drawLine(
                                    coordinates[1].x.toFloat(),
                                    coordinates[1].y.toFloat(),
                                    floorCoordinates[1].x.toFloat(),
                                    floorCoordinates[1].y.toFloat(),
                                    wallPaint
                                )

                                canvas.drawLine(
                                    coordinates[2].x.toFloat(),
                                    coordinates[2].y.toFloat(),
                                    floorCoordinates[2].x.toFloat(),
                                    floorCoordinates[2].y.toFloat(),
                                    wallPaint
                                )

                                canvas.drawLine(
                                    coordinates[3].x.toFloat(),
                                    coordinates[3].y.toFloat(),
                                    floorCoordinates[3].x.toFloat(),
                                    floorCoordinates[3].y.toFloat(),
                                    wallPaint
                                )


                                canvas.drawLine(
                                    coordinates[4].x.toFloat(),
                                    coordinates[4].y.toFloat(),
                                    coordinates[5].x.toFloat(),
                                    coordinates[5].y.toFloat(),
                                    wallPaint
                                )
                                canvas.drawLine(
                                    floorCoordinates[5].x.toFloat(),
                                    floorCoordinates[5].y.toFloat(),
                                    coordinates[5].x.toFloat(),
                                    coordinates[5].y.toFloat(),
                                    wallPaint
                                )

                                canvas.drawLine(
                                    coordinates[6].x.toFloat(),
                                    coordinates[6].y.toFloat(),
                                    coordinates[7].x.toFloat(),
                                    coordinates[7].y.toFloat(),
                                    wallPaint
                                )
                                canvas.drawLine(
                                    floorCoordinates[7].x.toFloat(),
                                    floorCoordinates[7].y.toFloat(),
                                    coordinates[7].x.toFloat(),
                                    coordinates[7].y.toFloat(),
                                    wallPaint
                                )

                                canvas.drawLine(
                                    coordinates[8].x.toFloat(),
                                    coordinates[8].y.toFloat(),
                                    coordinates[9].x.toFloat(),
                                    coordinates[9].y.toFloat(),
                                    wallPaint
                                )
                                canvas.drawLine(
                                    floorCoordinates[9].x.toFloat(),
                                    floorCoordinates[9].y.toFloat(),
                                    coordinates[9].x.toFloat(),
                                    coordinates[9].y.toFloat(),
                                    wallPaint
                                )

                                canvas.drawLine(
                                    coordinates[10].x.toFloat(),
                                    coordinates[10].y.toFloat(),
                                    coordinates[11].x.toFloat(),
                                    coordinates[11].y.toFloat(),
                                    wallPaint
                                )
                                canvas.drawLine(
                                    floorCoordinates[10].x.toFloat(),
                                    floorCoordinates[10].y.toFloat(),
                                    coordinates[10].x.toFloat(),
                                    coordinates[10].y.toFloat(),
                                    wallPaint
                                )

                                canvas.drawLine(
                                    coordinates[12].x.toFloat(),
                                    coordinates[12].y.toFloat(),
                                    coordinates[13].x.toFloat(),
                                    coordinates[13].y.toFloat(),
                                    wallPaint
                                )
                                canvas.drawLine(
                                    floorCoordinates[12].x.toFloat(),
                                    floorCoordinates[12].y.toFloat(),
                                    coordinates[12].x.toFloat(),
                                    coordinates[12].y.toFloat(),
                                    wallPaint
                                )
                                canvas.drawLine(
                                    floorCoordinates[13].x.toFloat(),
                                    floorCoordinates[13].y.toFloat(),
                                    coordinates[13].x.toFloat(),
                                    coordinates[13].y.toFloat(),
                                    wallPaint
                                )

                                canvas.drawLine(
                                    coordinates[14].x.toFloat(),
                                    coordinates[14].y.toFloat(),
                                    coordinates[15].x.toFloat(),
                                    coordinates[15].y.toFloat(),
                                    wallPaint
                                )
                                canvas.drawLine(
                                    floorCoordinates[14].x.toFloat(),
                                    floorCoordinates[14].y.toFloat(),
                                    coordinates[14].x.toFloat(),
                                    coordinates[14].y.toFloat(),
                                    wallPaint
                                )
                                canvas.drawLine(
                                    floorCoordinates[15].x.toFloat(),
                                    floorCoordinates[15].y.toFloat(),
                                    coordinates[15].x.toFloat(),
                                    coordinates[15].y.toFloat(),
                                    wallPaint
                                )

                                canvas.drawLine(
                                    coordinates[16].x.toFloat(),
                                    coordinates[16].y.toFloat(),
                                    coordinates[17].x.toFloat(),
                                    coordinates[17].y.toFloat(),
                                    wallPaint
                                )
                                canvas.drawLine(
                                    floorCoordinates[17].x.toFloat(),
                                    floorCoordinates[17].y.toFloat(),
                                    coordinates[17].x.toFloat(),
                                    coordinates[17].y.toFloat(),
                                    wallPaint
                                )

                                canvas.drawLine(
                                    coordinates[18].x.toFloat(),
                                    coordinates[18].y.toFloat(),
                                    coordinates[19].x.toFloat(),
                                    coordinates[19].y.toFloat(),
                                    wallPaint
                                )
                                canvas.drawLine(
                                    floorCoordinates[18].x.toFloat(),
                                    floorCoordinates[18].y.toFloat(),
                                    coordinates[18].x.toFloat(),
                                    coordinates[18].y.toFloat(),
                                    wallPaint
                                )

                                canvas.drawLine(
                                    coordinates[20].x.toFloat(),
                                    coordinates[20].y.toFloat(),
                                    coordinates[21].x.toFloat(),
                                    coordinates[21].y.toFloat(),
                                    wallPaint
                                )
                                canvas.drawLine(
                                    floorCoordinates[20].x.toFloat(),
                                    floorCoordinates[20].y.toFloat(),
                                    coordinates[20].x.toFloat(),
                                    coordinates[20].y.toFloat(),
                                    wallPaint
                                )

                                canvas.drawLine(
                                    coordinates[22].x.toFloat(),
                                    coordinates[22].y.toFloat(),
                                    coordinates[23].x.toFloat(),
                                    coordinates[23].y.toFloat(),
                                    wallPaint
                                )
                                canvas.drawLine(
                                    floorCoordinates[22].x.toFloat(),
                                    floorCoordinates[22].y.toFloat(),
                                    coordinates[22].x.toFloat(),
                                    coordinates[22].y.toFloat(),
                                    wallPaint
                                )
                            }
                        }
                    }
                }

                canvas.drawPath(path, if (it.first == FLOOR_INDEX) floorPaint else wallPaint)
            }
        }
    }

    companion object {
        private const val FLOOR_INDEX = 0
        private const val CEILING_INDEX = 1
    }

}