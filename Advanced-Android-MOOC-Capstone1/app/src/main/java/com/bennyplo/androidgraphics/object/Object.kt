package com.bennyplo.androidgraphics.`object`

import android.graphics.Canvas
import android.graphics.Path
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

interface Object {

    val components: Array<Pair<Int, Array<Coordinate>>>

    fun draw(canvas: Canvas, path: Path)

    fun translate(
        xTranslateAmount: Double,
        yTranslateAmount: Double,
        zTranslateAmount: Double
    ) {
        val matrix = getIdentityMatrix()

        matrix[3] = xTranslateAmount
        matrix[7] = yTranslateAmount
        matrix[11] = zTranslateAmount

        components.forEachIndexed { index, pair ->
            components[index] = pair.first to transformation(pair.second, matrix)
        }
    }

    fun orderVertices(partVertices: Array<Array<Coordinate>>): Array<Array<Coordinate>> =
        partVertices.sortedByDescending {
            it.minOf { coordinate -> coordinate.z }
        }.toTypedArray()

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

    fun quaternionRotate(
        rotateAxis: IntArray,
        rotateDegree: Double
    ) {
        components.forEach { pair ->
            pair.second.let { coordinates ->
                coordinates.forEachIndexed { index, coordinate ->
                    coordinate.let {
                        components[pair.first].second[index] =
                            quaternionCalculate(it, rotateAxis, rotateDegree)
                    }
                }
            }
        }
    }

    //***********************************************************
    // Affine rotation
    fun rotate(
        vertices: Array<Coordinate>,
        xRotateDegree: Double? = null,
        yRotateDegree: Double? = null,
        zRotateDegree: Double? = null
    ): Array<Coordinate> {
        var result = vertices
        var rotateMatrix: DoubleArray
        var rotateRadians: Double

        xRotateDegree?.let {
            rotateMatrix = getIdentityMatrix()
            rotateRadians = Math.toRadians(it)
            rotateMatrix[5] = cos(rotateRadians)
            rotateMatrix[6] = -sin(rotateRadians)
            rotateMatrix[9] = sin(rotateRadians)
            rotateMatrix[10] = cos(rotateRadians)
            result = transformation(result, rotateMatrix)
        }
        yRotateDegree?.let {
            rotateMatrix = getIdentityMatrix()
            rotateRadians = Math.toRadians(it)
            rotateMatrix[0] = cos(rotateRadians)
            rotateMatrix[2] = sin(rotateRadians)
            rotateMatrix[8] = -sin(rotateRadians)
            rotateMatrix[10] = cos(rotateRadians)
            result = transformation(result, rotateMatrix)
        }
        zRotateDegree?.let {
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

    // Affine transformation with homogeneous coordinates
    // i.e. a vector (vertex) multiply with the transformation matrix
    fun transformation(
        vertex: Coordinate,
        matrix: DoubleArray
    ): Coordinate {
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

    // Affine transform a 3D object with vertices
    fun transformation(
        vertices: Array<Coordinate>,
        matrix: DoubleArray
    ): Array<Coordinate> {
        // vertices - vertices of the 3D object.
        // matrix - transformation matrix
        val result = arrayOf(*vertices)

        for (i in vertices.indices) {
            result[i] = transformation(vertices[i], matrix)
            result[i].normalise()
        }

        return result
    }

    //***********************************************************
    // Affine transformation
    fun translate(
        vertices: Array<Coordinate>,
        xTranslateAmount: Double,
        yTranslateAmount: Double,
        zTranslateAmount: Double
    ): Array<Coordinate> {
        val matrix = getIdentityMatrix()

        matrix[3] = xTranslateAmount
        matrix[7] = yTranslateAmount
        matrix[11] = zTranslateAmount

        return transformation(vertices, matrix)
    }

    private fun getIdentityMatrix(): DoubleArray {
        return doubleArrayOf(
            1.0, 0.0, 0.0, 0.0,
            0.0, 1.0, 0.0, 0.0,
            0.0, 0.0, 1.0, 0.0,
            0.0, 0.0, 0.0, 1.0
        )
    }

    private fun quaternionCalculate(
        vertex: Coordinate,
        rotateAxis: IntArray,
        rotateDegree: Double
    ): Coordinate {
        val (cos, sin) = (Math.toRadians(rotateDegree) / 2).let {
            cos(it) to sin(it)
        }
        val m: DoubleArray = Coordinate(
            w = cos,
            x = sin * rotateAxis[0],
            y = sin * rotateAxis[1],
            z = sin * rotateAxis[2],
        ).let {
            doubleArrayOf(
                it.w.pow(2) + it.x.pow(2) - it.y.pow(2) - it.z.pow(2),
                2 * it.x * it.y - 2 * it.w * it.z,
                2 * it.x * it.z + 2 * it.w * it.y,
                0.0,
                2 * it.x * it.y + 2 * it.w * it.z,
                it.w.pow(2) + it.y.pow(2) - it.x.pow(2) - it.z.pow(2),
                2 * it.y * it.z - 2 * it.w * it.x,
                0.0,
                2 * it.x * it.z - 2 * it.w * it.y,
                2 * it.y * it.z + 2 * it.w * it.x,
                it.w.pow(2) + it.z.pow(2) - it.x.pow(2) - it.y.pow(2),
                0.0,
                0.0,
                0.0,
                0.0,
                1.0
            )
        }

        return transformation(vertex, m)
    }

}