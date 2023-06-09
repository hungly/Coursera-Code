package com.bennyplo.android_mooc_graphics_3d

//*********************************************
//* Homogeneous coordinate in 3D space
data class Coordinate(
    var w: Double = 0.0,
    var x: Double = 0.0,
    var y: Double = 0.0,
    var z: Double = 0.0,
) {

    fun Normalise() { //to keep it as a homogeneous coordinate -> divide the coordinate with w and set w=1
        if (w != 0.0) { //ensure that w!=0
            x /= w
            y /= w
            z /= w
            w = 1.0
        } else w = 1.0
    }

}
