package com.bennyplo.capstone2_opengles.gl_object

abstract class GLObject {

    var initialRotation = Triple(0.0F, 0.0F, 0.0F)
    var initialScale = Triple(1.0F, 1.0F, 1.0F)
    var initialTranslation = Triple(0.0F, 0.0F, 0.0F)

    abstract fun draw(mvpMatrix: FloatArray?)
}