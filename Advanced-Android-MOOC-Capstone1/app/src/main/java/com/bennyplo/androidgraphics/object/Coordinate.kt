package com.bennyplo.androidgraphics.`object`

class Coordinate(
    var x: Double = 0.0,
    var y: Double = 0.0,
    var z: Double = 0.0,
    var w: Double = 0.0,
) {
    fun normalise() {
        if (w != 0.0) {
            x /= w
            y /= w
            z /= w
            w = 1.0
        } else w = 1.0
    }
}