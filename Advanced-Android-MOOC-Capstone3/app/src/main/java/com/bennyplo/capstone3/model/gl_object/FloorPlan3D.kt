package com.bennyplo.capstone3.model.gl_object

import android.content.Context
import android.opengl.GLES32
import com.bennyplo.capstone3.MyRenderer.Companion.checkGlError
import com.bennyplo.capstone3.R
import com.bennyplo.capstone3.model.Constant
import com.bennyplo.capstone3.model.GLObject
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer

class FloorPlan3D(context: Context?, val doorWidth: Float, val wallThickness: Float) :
    GLObject() {

    private val _exteriorWallsColorBuffer: FloatBuffer by lazy {
        ByteBuffer.allocateDirect(exteriorWallColors.size * COLORS_PER_VERTEX).apply {
            order(ByteOrder.nativeOrder())
        }.asFloatBuffer().apply {
            put(exteriorWallColors)
            position(0)
        }
    }

    private val _exteriorWallsIndexBuffer: IntBuffer by lazy {
        IntBuffer.allocate(EXTERIOR_WALLS_INDEXES.size).apply {
            put(EXTERIOR_WALLS_INDEXES)
            position(0)
        }
    }

    private val _exteriorWallsNormal: FloatArray by lazy {
        calculateDefaultNormalMap(EXTERIOR_WALLS_VERTICES)
    }

    private val _exteriorWallsNormalBuffer: FloatBuffer by lazy {
        ByteBuffer.allocateDirect(_exteriorWallsNormal.size * Float.SIZE_BYTES)
            .apply {
                order(ByteOrder.nativeOrder())
            }.asFloatBuffer().apply {
                put(_exteriorWallsNormal)
                position(0)
            }
    }

    private val _exteriorWallsTextureBuffer: FloatBuffer by lazy {
        ByteBuffer.allocateDirect(_exteriorWallsTextureCoordinateData.size * Float.SIZE_BYTES)
            .apply {
                order(ByteOrder.nativeOrder())
            }.asFloatBuffer().apply {
                put(_exteriorWallsTextureCoordinateData)
                position(0)
            }
    }

    private val _exteriorWallsTextureCoordinateData: FloatArray by lazy {
        val data = arrayListOf<Float>()
        repeat((0 until EXTERIOR_WALLS_VERTICES.size / COORDINATES_PER_VERTEX).count()) {
            data.add(0.0F)
            data.add(1.0F)
            data.add(0.0F)
            data.add(0.0F)
            data.add(1.0F)
            data.add(0.0F)
            data.add(1.0F)
            data.add(1.0F)
        }
        data.toFloatArray()
    }

    private val _exteriorWallsVertexBuffer: FloatBuffer by lazy {
        // (# of coordinate values * 4 bytes per float)
        ByteBuffer.allocateDirect(EXTERIOR_WALLS_VERTICES.size * Float.SIZE_BYTES).apply {
            order(ByteOrder.nativeOrder())
        }.asFloatBuffer().apply {
            put(EXTERIOR_WALLS_VERTICES)
            position(0)
        }
    }

    private val _floorColorBuffer: FloatBuffer by lazy {
        ByteBuffer.allocateDirect(floorColors.size * COLORS_PER_VERTEX).apply {
            order(ByteOrder.nativeOrder())
        }.asFloatBuffer().apply {
            put(floorColors)
            position(0)
        }
    }

    private val _floorIndexBuffer: IntBuffer by lazy {
        IntBuffer.allocate(FLOOR_INDEXES.size).apply {
            put(FLOOR_INDEXES)
            position(0)
        }
    }

    private val _floorTextureBuffer: FloatBuffer by lazy {
        ByteBuffer.allocateDirect(FLOOR_TEXTURE_COORDINATE_DATA.size * Float.SIZE_BYTES).apply {
            order(ByteOrder.nativeOrder())
        }.asFloatBuffer().apply {
            put(FLOOR_TEXTURE_COORDINATE_DATA)
            position(0)
        }
    }

    private val _floorVertexBuffer: FloatBuffer by lazy {
        // (# of coordinate values * 4 bytes per float)
        ByteBuffer.allocateDirect(FLOOR_VERTICES.size * Float.SIZE_BYTES).apply {
            order(ByteOrder.nativeOrder())
        }.asFloatBuffer().apply {
            put(FLOOR_VERTICES)
            position(0)
        }
    }

    private val _floorWallsNormal: FloatArray by lazy {
        calculateDefaultNormalMap(FLOOR_VERTICES)
    }

    private val _floorWallsNormalBuffer: FloatBuffer by lazy {
        ByteBuffer.allocateDirect(_floorWallsNormal.size * Float.SIZE_BYTES)
            .apply {
                order(ByteOrder.nativeOrder())
            }.asFloatBuffer().apply {
                put(_floorWallsNormal)
                position(0)
            }
    }

    private val _interiorWallVertexBuffer: FloatBuffer by lazy {
        // (# of coordinate values * 4 bytes per float)
        ByteBuffer.allocateDirect(_interiorWallsVertices.size * Float.SIZE_BYTES).apply {
            order(ByteOrder.nativeOrder())
        }.asFloatBuffer().apply {
            put(_interiorWallsVertices)
            position(0)
        }
    }

    private val _interiorWallsColorBuffer: FloatBuffer by lazy {
        ByteBuffer.allocateDirect(interiorWallColors.size * COLORS_PER_VERTEX).apply {
            order(ByteOrder.nativeOrder())
        }.asFloatBuffer().apply {
            put(interiorWallColors)
            position(0)
        }
    }

    private val _interiorWallsIndexBuffer: IntBuffer by lazy {
        IntBuffer.allocate(INTERIOR_WALLS_INDEXES_BASE.size).apply {
            put(INTERIOR_WALLS_INDEXES_BASE)
            position(0)
        }
    }

    private val _interiorWallsNormal: FloatArray by lazy {
        calculateDefaultNormalMap(_interiorWallsVertices)
    }

    private val _interiorWallsNormalBuffer: FloatBuffer by lazy {
        ByteBuffer.allocateDirect(_interiorWallsNormal.size * Float.SIZE_BYTES)
            .apply {
                order(ByteOrder.nativeOrder())
            }.asFloatBuffer().apply {
                put(_interiorWallsNormal)
                position(0)
            }
    }

    private val _interiorWallsTextureBuffer: FloatBuffer by lazy {
        ByteBuffer.allocateDirect(_interiorWallsTextureCoordinateData.size * Float.SIZE_BYTES)
            .apply {
                order(ByteOrder.nativeOrder())
            }.asFloatBuffer().apply {
                put(_interiorWallsTextureCoordinateData)
                position(0)
            }
    }

    private val _interiorWallsTextureCoordinateData: FloatArray by lazy {
        val data = arrayListOf<Float>()
        repeat((0 until _interiorWallsVertices.size / COORDINATES_PER_VERTEX).count()) {
            data.add(0.0F)
            data.add(4.0F)
            data.add(0.0F)
            data.add(0.0F)
            data.add(2.0F)
            data.add(0.0F)
            data.add(2.0F)
            data.add(4.0F)
        }
        data.toFloatArray()
    }

    private val _interiorWallsVertices by lazy {
        val offsetAmount = wallThickness / 2.0F
        floatArrayOf(
            // Vertical - Left - Bottom
            // Left
            -1.0F - offsetAmount, -3.0F, -1.0F, // 0
            -1.0F - offsetAmount, -3.0F, 1.0F, // 1
            -1.0F - offsetAmount, -1.0F - doorWidth, 1.0F, // 2
            -1.0F - offsetAmount, -1.0F - doorWidth, -1.0F, // 3
            // Right
            -1.0F + offsetAmount, -3.0F, -1.0F, // 4
            -1.0F + offsetAmount, -3.0F, 1.0F, // 5
            -1.0F + offsetAmount, -1.0F - doorWidth, 1.0F, // 6
            -1.0F + offsetAmount, -1.0F - doorWidth, -1.0F, // 7
            // Top
            -1.0F - offsetAmount, -1.0F - doorWidth, -1.0F, // 8
            -1.0F - offsetAmount, -1.0F - doorWidth, 1.0F, // 9
            -1.0F + offsetAmount, -1.0F - doorWidth, 1.0F, // 10
            -1.0F + offsetAmount, -1.0F - doorWidth, -1.0F, // 11
            // Bottom
            -1.0F - offsetAmount, -3.0F, -1.0F, // 12
            -1.0F - offsetAmount, -3.0F, 1.0F, // 13
            -1.0F + offsetAmount, -3.0F, 1.0F, // 14
            -1.0F + offsetAmount, -3.0F, -1.0F, // 15
            // Front
            -1.0F - offsetAmount, -1.0F - doorWidth, 1.0F, // 16
            -1.0F - offsetAmount, -3.0F, 1.0F, // 17
            -1.0F + offsetAmount, -3.0F, 1.0F, // 18
            -1.0F + offsetAmount, -1.0F - doorWidth, 1.0F, // 19
            // Back
            -1.0F - offsetAmount, -1.0F - doorWidth, -1.0F, // 20
            -1.0F - offsetAmount, -3.0F, -1.0F, // 21
            -1.0F + offsetAmount, -3.0F, -1.0F, // 22
            -1.0F + offsetAmount, -1.0F - doorWidth, -1.0F, // 23

            // Horizontal - Left - Bottom
            // Left
            -3.0F, -1.0F - offsetAmount, -1.0F, // 24
            -3.0F, -1.0F - offsetAmount, 1.0F, // 25
            -3.0F, -1.0F + offsetAmount, 1.0F, // 26
            -3.0F, -1.0F + offsetAmount, -1.0F, // 27
            // Right
            -1.0F + offsetAmount, -1.0F - offsetAmount, -1.0F, // 28
            -1.0F + offsetAmount, -1.0F - offsetAmount, 1.0F, // 29
            -1.0F + offsetAmount, -1.0F + offsetAmount, 1.0F, // 30
            -1.0F + offsetAmount, -1.0F + offsetAmount, -1.0F, // 31
            // Top
            -3.0F, -1.0F + offsetAmount, -1.0F, // 32
            -3.0F, -1.0F + offsetAmount, 1.0F, // 33
            -1.0F + offsetAmount, -1.0F + offsetAmount, 1.0F, // 34
            -1.0F + offsetAmount, -1.0F + offsetAmount, -1.0F, // 35
            // Bottom
            -3.0F, -1.0F - offsetAmount, -1.0F, // 36
            -3.0F, -1.0F - offsetAmount, 1.0F, // 37
            -1.0F + offsetAmount, -1.0F - offsetAmount, 1.0F, // 38
            -1.0F + offsetAmount, -1.0F - offsetAmount, -1.0F, // 39
            // Front
            -3.0F, -1.0F + offsetAmount, 1.0F, // 40
            -3.0F, -1.0F - offsetAmount, 1.0F, // 41
            -1.0F + offsetAmount, -1.0F - offsetAmount, 1.0F, // 42
            -1.0F + offsetAmount, -1.0F + offsetAmount, 1.0F, // 43
            // Back
            -3.0F, -1.0F + offsetAmount, -1.0F, // 44
            -3.0F, -1.0F - offsetAmount, -1.0F, // 45
            -1.0F + offsetAmount, -1.0F - offsetAmount, -1.0F, // 46
            -1.0F + offsetAmount, -1.0F + offsetAmount, -1.0F, // 47

            // Vertical - Left - Middle
            // Left
            -1.0F - offsetAmount, -1.0F + doorWidth, -1.0F, // 48
            -1.0F - offsetAmount, -1.0F + doorWidth, 1.0F, // 49
            -1.0F - offsetAmount, 1.0F - doorWidth, 1.0F, // 50
            -1.0F - offsetAmount, 1.0F - doorWidth, -1.0F, // 51
            // Right
            -1.0F + offsetAmount, -1.0F + doorWidth, -1.0F, // 52
            -1.0F + offsetAmount, -1.0F + doorWidth, 1.0F, // 53
            -1.0F + offsetAmount, 1.0F - doorWidth, 1.0F, // 54
            -1.0F + offsetAmount, 1.0F - doorWidth, -1.0F, // 55
            // Top
            -1.0F - offsetAmount, 1.0F - doorWidth, -1.0F, // 56
            -1.0F - offsetAmount, 1.0F - doorWidth, 1.0F, // 57
            -1.0F + offsetAmount, 1.0F - doorWidth, 1.0F, // 58
            -1.0F + offsetAmount, 1.0F - doorWidth, -1.0F, // 59
            // Bottom
            -1.0F - offsetAmount, -1.0F + doorWidth, -1.0F, // 60
            -1.0F - offsetAmount, -1.0F + doorWidth, 1.0F, // 61
            -1.0F + offsetAmount, -1.0F + doorWidth, 1.0F, // 62
            -1.0F + offsetAmount, -1.0F + doorWidth, -1.0F, // 63
            // Front
            -1.0F - offsetAmount, 1.0F - doorWidth, 1.0F, // 64
            -1.0F - offsetAmount, -1.0F + doorWidth, 1.0F, // 65
            -1.0F + offsetAmount, -1.0F + doorWidth, 1.0F, // 66
            -1.0F + offsetAmount, 1.0F - doorWidth, 1.0F, // 67
            // Back
            -1.0F - offsetAmount, 1.0F - doorWidth, -1.0F, // 68
            -1.0F - offsetAmount, -1.0F + doorWidth, -1.0F, // 69
            -1.0F + offsetAmount, -1.0F + doorWidth, -1.0F, // 70
            -1.0F + offsetAmount, 1.0F - doorWidth, -1.0F, // 71

            // Horizontal - Left - Top
            // Left
            -3.0F, 1.0F - offsetAmount, -1.0F, // 72
            -3.0F, 1.0F - offsetAmount, 1.0F, // 73
            -3.0F, 1.0F + offsetAmount, 1.0F, // 74
            -3.0F, 1.0F + offsetAmount, -1.0F, // 75
            // Right
            -1.0F + offsetAmount, 1.0F - offsetAmount, -1.0F, // 76
            -1.0F + offsetAmount, 1.0F - offsetAmount, 1.0F, // 77
            -1.0F + offsetAmount, 1.0F + offsetAmount, 1.0F, // 78
            -1.0F + offsetAmount, 1.0F + offsetAmount, -1.0F, // 79
            // Top
            -3.0F, 1.0F + offsetAmount, -1.0F, // 80
            -3.0F, 1.0F + offsetAmount, 1.0F, // 81
            -1.0F + offsetAmount, 1.0F + offsetAmount, 1.0F, // 82
            -1.0F + offsetAmount, 1.0F + offsetAmount, -1.0F, // 83
            // Bottom
            -3.0F, 1.0F - offsetAmount, -1.0F, // 84
            -3.0F, 1.0F - offsetAmount, 1.0F, // 85
            -1.0F + offsetAmount, 1.0F - offsetAmount, 1.0F, // 86
            -1.0F + offsetAmount, 1.0F - offsetAmount, -1.0F, // 87
            // Front
            -3.0F, 1.0F + offsetAmount, 1.0F, // 88
            -3.0F, 1.0F - offsetAmount, 1.0F, // 89
            -1.0F + offsetAmount, 1.0F - offsetAmount, 1.0F, // 90
            -1.0F + offsetAmount, 1.0F + offsetAmount, 1.0F, // 91
            // Back
            -3.0F, 1.0F + offsetAmount, -1.0F, // 92
            -3.0F, 1.0F - offsetAmount, -1.0F, // 93
            -1.0F + offsetAmount, 1.0F - offsetAmount, -1.0F, // 94
            -1.0F + offsetAmount, 1.0F + offsetAmount, -1.0F, // 95

            // Vertical - Left - Top
            // Left
            -1.0F - offsetAmount, 1.0F + doorWidth, -1.0F, // 96
            -1.0F - offsetAmount, 1.0F + doorWidth, 1.0F, // 97
            -1.0F - offsetAmount, 3.0F, 1.0F, // 98
            -1.0F - offsetAmount, 3.0F, -1.0F, // 99
            // Right
            -1.0F + offsetAmount, 1.0F + doorWidth, -1.0F, // 100
            -1.0F + offsetAmount, 1.0F + doorWidth, 1.0F, // 101
            -1.0F + offsetAmount, 3.0F, 1.0F, // 102
            -1.0F + offsetAmount, 3.0F, -1.0F, // 103
            // Top
            -1.0F - offsetAmount, 3.0F, -1.0F, // 104
            -1.0F - offsetAmount, 3.0F, 1.0F, // 105
            -1.0F + offsetAmount, 3.0F, 1.0F, // 106
            -1.0F + offsetAmount, 3.0F, -1.0F, // 107
            // Bottom
            -1.0F - offsetAmount, 1.0F + doorWidth, -1.0F, // 108
            -1.0F - offsetAmount, 1.0F + doorWidth, 1.0F, // 109
            -1.0F + offsetAmount, 1.0F + doorWidth, 1.0F, // 110
            -1.0F + offsetAmount, 1.0F + doorWidth, -1.0F, // 111
            // Front
            -1.0F - offsetAmount, 3.0F, 1.0F, // 112
            -1.0F - offsetAmount, 1.0F + doorWidth, 1.0F, // 113
            -1.0F + offsetAmount, 1.0F + doorWidth, 1.0F, // 114
            -1.0F + offsetAmount, 3.0F, 1.0F, // 115
            // Back
            -1.0F - offsetAmount, 3.0F, -1.0F, // 116
            -1.0F - offsetAmount, 1.0F + doorWidth, -1.0F, // 117
            -1.0F + offsetAmount, 1.0F + doorWidth, -1.0F, // 118
            -1.0F + offsetAmount, 3.0F, -1.0F, // 119

            // Vertical - Right - Top
            // Left
            1.0F - offsetAmount, 1.0F + doorWidth, -1.0F, // 120
            1.0F - offsetAmount, 1.0F + doorWidth, 1.0F, // 121
            1.0F - offsetAmount, 3.0F, 1.0F, // 122
            1.0F - offsetAmount, 3.0F, -1.0F, // 123
            // Right
            1.0F + offsetAmount, 1.0F + doorWidth, -1.0F, // 124
            1.0F + offsetAmount, 1.0F + doorWidth, 1.0F, // 125
            1.0F + offsetAmount, 3.0F, 1.0F, // 126
            1.0F + offsetAmount, 3.0F, -1.0F, // 127
            // Top
            1.0F - offsetAmount, 3.0F, -1.0F, // 128
            1.0F - offsetAmount, 3.0F, 1.0F, // 129
            1.0F + offsetAmount, 3.0F, 1.0F, // 130
            1.0F + offsetAmount, 3.0F, -1.0F, // 131
            // Bottom
            1.0F - offsetAmount, 1.0F + doorWidth, -1.0F, // 132
            1.0F - offsetAmount, 1.0F + doorWidth, 1.0F, // 133
            1.0F + offsetAmount, 1.0F + doorWidth, 1.0F, // 134
            1.0F + offsetAmount, 1.0F + doorWidth, -1.0F, // 135
            // Front
            1.0F - offsetAmount, 3.0F, 1.0F, // 136
            1.0F - offsetAmount, 1.0F + doorWidth, 1.0F, // 137
            1.0F + offsetAmount, 1.0F + doorWidth, 1.0F, // 138
            1.0F + offsetAmount, 3.0F, 1.0F, // 139
            // Back
            1.0F - offsetAmount, 3.0F, -1.0F, // 140
            1.0F - offsetAmount, 1.0F + doorWidth, -1.0F, // 141
            1.0F + offsetAmount, 1.0F + doorWidth, -1.0F, // 142
            1.0F + offsetAmount, 3.0F, -1.0F, // 143

            // Horizontal - Right - Top
            // Left
            1.0F - offsetAmount, 1.0F - offsetAmount, -1.0F, // 144
            1.0F - offsetAmount, 1.0F - offsetAmount, 1.0F, // 145
            1.0F - offsetAmount, 1.0F + offsetAmount, 1.0F, // 146
            1.0F - offsetAmount, 1.0F + offsetAmount, -1.0F, // 147
            // Right
            3.0F, 1.0F - offsetAmount, -1.0F, // 148
            3.0F, 1.0F - offsetAmount, 1.0F, // 149
            3.0F, 1.0F + offsetAmount, 1.0F, // 150
            3.0F, 1.0F + offsetAmount, -1.0F, // 151
            // Top
            1.0F - offsetAmount, 1.0F + offsetAmount, -1.0F, // 152
            1.0F - offsetAmount, 1.0F + offsetAmount, 1.0F, // 153
            3.0F, 1.0F + offsetAmount, 1.0F, // 154
            3.0F, 1.0F + offsetAmount, -1.0F, // 155
            // Bottom
            1.0F - offsetAmount, 1.0F - offsetAmount, -1.0F, // 156
            1.0F - offsetAmount, 1.0F - offsetAmount, 1.0F, // 157
            3.0F, 1.0F - offsetAmount, 1.0F, // 158
            3.0F, 1.0F - offsetAmount, -1.0F, // 159
            // Front
            1.0F - offsetAmount, 1.0F + offsetAmount, 1.0F, // 160
            1.0F - offsetAmount, 1.0F - offsetAmount, 1.0F, // 161
            3.0F, 1.0F - offsetAmount, 1.0F, // 162
            3.0F, 1.0F + offsetAmount, 1.0F, // 163
            // Back
            1.0F - offsetAmount, 1.0F + offsetAmount, -1.0F, // 164
            1.0F - offsetAmount, 1.0F - offsetAmount, -1.0F, // 165
            3.0F, 1.0F - offsetAmount, -1.0F, // 166
            3.0F, 1.0F + offsetAmount, -1.0F, // 167

            // Vertical - Right - Middle
            // Left
            1.0F - offsetAmount, -1.0F + doorWidth, -1.0F, // 168
            1.0F - offsetAmount, -1.0F + doorWidth, 1.0F, // 169
            1.0F - offsetAmount, 1.0F - doorWidth, 1.0F, // 170
            1.0F - offsetAmount, 1.0F - doorWidth, -1.0F, // 171
            // Right
            1.0F + offsetAmount, -1.0F + doorWidth, -1.0F, // 172
            1.0F + offsetAmount, -1.0F + doorWidth, 1.0F, // 173
            1.0F + offsetAmount, 1.0F - doorWidth, 1.0F, // 174
            1.0F + offsetAmount, 1.0F - doorWidth, -1.0F, // 175
            // Top
            1.0F - offsetAmount, 1.0F - doorWidth, -1.0F, // 176
            1.0F - offsetAmount, 1.0F - doorWidth, 1.0F, // 177
            1.0F + offsetAmount, 1.0F - doorWidth, 1.0F, // 178
            1.0F + offsetAmount, 1.0F - doorWidth, -1.0F, // 179
            // Bottom
            1.0F - offsetAmount, -1.0F + doorWidth, -1.0F, // 180
            1.0F - offsetAmount, -1.0F + doorWidth, 1.0F, // 181
            1.0F + offsetAmount, -1.0F + doorWidth, 1.0F, // 182
            1.0F + offsetAmount, -1.0F + doorWidth, -1.0F, // 183
            // Front
            1.0F - offsetAmount, 1.0F - doorWidth, 1.0F, // 184
            1.0F - offsetAmount, -1.0F + doorWidth, 1.0F, // 185
            1.0F + offsetAmount, -1.0F + doorWidth, 1.0F, // 186
            1.0F + offsetAmount, 1.0F - doorWidth, 1.0F, // 187
            // Back
            1.0F - offsetAmount, 1.0F - doorWidth, -1.0F, // 188
            1.0F - offsetAmount, -1.0F + doorWidth, -1.0F, // 189
            1.0F + offsetAmount, -1.0F + doorWidth, -1.0F, // 190
            1.0F + offsetAmount, 1.0F - doorWidth, -1.0F, // 191

            // Horizontal - Right - Bottom
            // Left
            1.0F - offsetAmount, -1.0F - offsetAmount, -1.0F, // 192
            1.0F - offsetAmount, -1.0F - offsetAmount, 1.0F, // 193
            1.0F - offsetAmount, -1.0F + offsetAmount, 1.0F, // 194
            1.0F - offsetAmount, -1.0F + offsetAmount, -1.0F, // 195
            // Right
            3.0F, -1.0F - offsetAmount, -1.0F, // 196
            3.0F, -1.0F - offsetAmount, 1.0F, // 197
            3.0F, -1.0F + offsetAmount, 1.0F, // 198
            3.0F, -1.0F + offsetAmount, -1.0F, // 199
            // Top
            1.0F - offsetAmount, -1.0F + offsetAmount, -1.0F, // 200
            1.0F - offsetAmount, -1.0F + offsetAmount, 1.0F, // 201
            3.0F, -1.0F + offsetAmount, 1.0F, // 202
            3.0F, -1.0F + offsetAmount, -1.0F, // 203
            // Bottom
            1.0F - offsetAmount, -1.0F - offsetAmount, -1.0F, // 204
            1.0F - offsetAmount, -1.0F - offsetAmount, 1.0F, // 205
            3.0F, -1.0F - offsetAmount, 1.0F, // 206
            3.0F, -1.0F - offsetAmount, -1.0F, // 207
            // Front
            1.0F - offsetAmount, -1.0F + offsetAmount, 1.0F, // 208
            1.0F - offsetAmount, -1.0F - offsetAmount, 1.0F, // 209
            3.0F, -1.0F - offsetAmount, 1.0F, // 210
            3.0F, -1.0F + offsetAmount, 1.0F, // 211
            // Back
            1.0F - offsetAmount, -1.0F + offsetAmount, -1.0F, // 212
            1.0F - offsetAmount, -1.0F - offsetAmount, -1.0F, // 213
            3.0F, -1.0F - offsetAmount, -1.0F, // 214
            3.0F, -1.0F + offsetAmount, -1.0F, // 215

            // Vertical - Right - Bottom
            // Left
            1.0F - offsetAmount, -3.0F, -1.0F, // 216
            1.0F - offsetAmount, -3.0F, 1.0F, // 217
            1.0F - offsetAmount, -1.0F - doorWidth, 1.0F, // 218
            1.0F - offsetAmount, -1.0F - doorWidth, -1.0F, // 219
            // Right
            1.0F + offsetAmount, -3.0F, -1.0F, // 220
            1.0F + offsetAmount, -3.0F, 1.0F, // 221
            1.0F + offsetAmount, -1.0F - doorWidth, 1.0F, // 222
            1.0F + offsetAmount, -1.0F - doorWidth, -1.0F, // 223
            // Top
            1.0F - offsetAmount, -1.0F - doorWidth, -1.0F, // 224
            1.0F - offsetAmount, -1.0F - doorWidth, 1.0F, // 225
            1.0F + offsetAmount, -1.0F - doorWidth, 1.0F, // 226
            1.0F + offsetAmount, -1.0F - doorWidth, -1.0F, // 227
            // Bottom
            1.0F - offsetAmount, -3.0F, -1.0F, // 228
            1.0F - offsetAmount, -3.0F, 1.0F, // 229
            1.0F + offsetAmount, -3.0F, 1.0F, // 230
            1.0F + offsetAmount, -3.0F, -1.0F, // 231
            // Front
            1.0F - offsetAmount, -1.0F - doorWidth, 1.0F, // 232
            1.0F - offsetAmount, -3.0F, 1.0F, // 233
            1.0F + offsetAmount, -3.0F, 1.0F, // 234
            1.0F + offsetAmount, -1.0F - doorWidth, 1.0F, // 235
            // Back
            1.0F - offsetAmount, -1.0F - doorWidth, -1.0F, // 236
            1.0F - offsetAmount, -3.0F, -1.0F, // 237
            1.0F + offsetAmount, -3.0F, -1.0F, // 238
            1.0F + offsetAmount, -1.0F - doorWidth, -1.0F, // 239
        )
    }

    private val exteriorWallColors by lazy {
        val numberOfVertices = EXTERIOR_WALLS_VERTICES.size / COORDINATES_PER_VERTEX
        val colors = arrayListOf<Float>()
        repeat((0 until numberOfVertices).count()) {
            colors.add(0.4F)
            colors.add(0.4F)
            colors.add(0.4F)
            colors.add(1.0F)
        }
        colors.toFloatArray()
    }

    private val exteriorWallsTextureImageHandler by lazy {
        loadTextureFromResource(R.drawable.exterior_walls, context)
    }

    private val floorColors by lazy {
        val numberOfVertices = FLOOR_VERTICES.size / COORDINATES_PER_VERTEX
        val colors = arrayListOf<Float>()
        repeat((0 until numberOfVertices).count()) {
            colors.add(0.2F)
            colors.add(0.2F)
            colors.add(0.2F)
            colors.add(1.0F)
        }
        colors.toFloatArray()
    }

    private val floorTextureImageHandler by lazy {
        loadTextureFromResource(R.drawable.floor, context)
    }

    private val interiorWallColors by lazy {
        val numberOfVertices = _interiorWallsVertices.size / COORDINATES_PER_VERTEX
        val colors = arrayListOf<Float>()
        repeat((0 until numberOfVertices).count()) {
            colors.add(0.6F)
            colors.add(0.6F)
            colors.add(0.6F)
            colors.add(1.0F)
        }
        colors.toFloatArray()
    }

    private val interiorWallsTextureImageHandler by lazy {
        loadTextureFromResource(R.drawable.interior_walls, context)
    }

    override var textureRatio: Float = 0.0F

    override val textureImageHandler: Int? = null

    override fun draw(mvpMatrix: FloatArray?, mLightModelMatrix: FloatArray?) {
        super.draw(mvpMatrix, mLightModelMatrix)

        GLES32.glUniform3fv(attenuateHandle, 1, ATTENUATION, 0)
        GLES32.glUniform3fv(ambientColorHandle, 1, AMBIENT_COLOR, 0)
        GLES32.glUniform4fv(diffuseColorHandle, 1, DIFFUSE_COLOR, 0)
        GLES32.glUniform3fv(diffuseLightLocationHandle, 1, DIFFUSE_LIGHT_LOCATION, 0)

        draw(
            vertexBuffer = _floorVertexBuffer,
            colorBuffer = _floorColorBuffer,
            normalBuffer = _floorWallsNormalBuffer,
            indexBuffer = _floorIndexBuffer,
            indexSize = FLOOR_INDEXES.size,
            textureBuffer = _floorTextureBuffer,
            textureImageHandler = floorTextureImageHandler,
            useTexture = 1
        )
        draw(
            vertexBuffer = _exteriorWallsVertexBuffer,
            colorBuffer = _exteriorWallsColorBuffer,
            normalBuffer = _exteriorWallsNormalBuffer,
            indexBuffer = _exteriorWallsIndexBuffer,
            indexSize = EXTERIOR_WALLS_INDEXES.size,
            textureBuffer = _exteriorWallsTextureBuffer,
            textureImageHandler = exteriorWallsTextureImageHandler,
            useTexture = 1
        )

        GLES32.glEnable(GLES32.GL_CULL_FACE)
        GLES32.glCullFace(GLES32.GL_FRONT)
        GLES32.glFrontFace(GLES32.GL_CW)

        draw(
            vertexBuffer = _interiorWallVertexBuffer,
            colorBuffer = _interiorWallsColorBuffer,
            normalBuffer = _interiorWallsNormalBuffer,
            indexBuffer = _interiorWallsIndexBuffer,
            indexSize = INTERIOR_WALLS_INDEXES_BASE.size,
            textureBuffer = _interiorWallsTextureBuffer,
            textureImageHandler = interiorWallsTextureImageHandler,
            useTexture = 1
        )
    }

    private fun draw(
        vertexBuffer: FloatBuffer,
        colorBuffer: FloatBuffer,
        normalBuffer: FloatBuffer,
        indexBuffer: IntBuffer,
        indexSize: Int,
        textureBuffer: FloatBuffer,
        textureImageHandler: Int,
        useTexture: Int
    ) {
        // Set the attribute of the vertex to point to the vertex buffer
        GLES32.glVertexAttribPointer(
            positionHandle,
            COORDINATES_PER_VERTEX,
            GLES32.GL_FLOAT,
            false,
            VERTEX_STRIDE,
            vertexBuffer
        )
        GLES32.glVertexAttribPointer(
            colorHandle,
            COORDINATES_PER_VERTEX,
            GLES32.GL_FLOAT,
            false,
            COLOR_STRIDE,
            colorBuffer
        )
        GLES32.glVertexAttribPointer(
            normalHandle,
            COORDINATES_PER_VERTEX,
            GLES32.GL_FLOAT,
            false,
            VERTEX_STRIDE,
            normalBuffer
        )

        // Set up texture
        GLES32.glVertexAttribPointer(
            textureCoordinateHandle,
            TEXTURE_COORDINATES_PER_VERTEX,
            GLES32.GL_FLOAT,
            false,
            TEXTURE_STRIDE,
            textureBuffer
        )
        GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, textureImageHandler)
        GLES32.glUniform1i(textureSamplerHandle, 0)
        GLES32.glUniform1i(useTextureHandle, useTexture)

        GLES32.glDrawElements(
            GLES32.GL_TRIANGLES,
            indexSize,
            GLES32.GL_UNSIGNED_INT,
            indexBuffer
        )
    }

    companion object {

        private val FLOOR_VERTICES = floatArrayOf(
            -3.0F, -3.0F, -1.0F, // 0
            -3.0F, 3.0F, -1.0F, // 1
            3.0F, 3.0F, -1.0F, // 2
            3.0F, -3.0F, -1.0F, // 3
        )

        private val FLOOR_INDEXES = intArrayOf(
            0, 1, 2, 0, 2, 3
        )

        private val FLOOR_TEXTURE_COORDINATE_DATA = floatArrayOf(
            0.0F, 4.0F,
            0.0F, 0.0F,
            4.0F, 0.0F,
            4.0F, 4.0F,
        )

        private val EXTERIOR_WALLS_VERTICES = floatArrayOf(
            // Left
            -3.0F, -3.0F, -1.0F, // 0
            -3.0F, -3.0F, 1.0F, // 1
            -3.0F, 3.0F, 1.0F, // 2
            -3.0F, 3.0F, -1.0F, // 3
            // Top
            -3.0F, 3.0F, -1.0F, // 4
            -3.0F, 3.0F, 1.0F, // 5
            3.0F, 3.0F, 1.0F, // 6
            3.0F, 3.0F, -1.0F, // 7
            // Right
            3.0F, 3.0F, -1.0F, // 8
            3.0F, 3.0F, 1.0F, // 9
            3.0F, -3.0F, 1.0F, // 10
            3.0F, -3.0F, -1.0F, // 11
            // Bottom
            3.0F, -3.0F, -1.0F, // 12
            3.0F, -3.0F, 1.0F, // 13
            -3.0F, -3.0F, 1.0F, // 14
            -3.0F, -3.0F, -1.0F, // 15
        )

        private val EXTERIOR_WALLS_INDEXES = intArrayOf(
            0, 1, 2, 0, 2, 3, // Left
            4, 5, 6, 4, 6, 7, // Top
            8, 9, 10, 8, 10, 11, // Right
            12, 13, 14, 12, 14, 15, // Bottom
        )

        private val INTERIOR_WALLS_INDEXES_BASE = intArrayOf(
            // Vertical - Left - Bottom
            0, 1, 2, 0, 2, 3, // Left
            4, 6, 5, 4, 7, 6, // Right
            8, 9, 10, 8, 10, 11, // Top
//            12, 14, 13, 12, 15, 14, // Bottom
            16, 17, 18, 16, 18, 19, // Front
//            20, 22, 21, 20, 23, 22, // Back

            // Horizontal - Left - Bottom
//            24, 25, 26, 24, 26, 27, // Left
            28, 30, 29, 28, 31, 30, // Right
            32, 33, 34, 32, 34, 35, // Top
            36, 38, 37, 36, 39, 38, // Bottom
            40, 41, 42, 40, 42, 43, // Front
//            44, 46, 45, 44, 47, 46, // Back

            // Vertical - Left - Middle
            48, 49, 50, 48, 50, 51, // Left
            52, 54, 53, 52, 55, 54, // Right
            56, 57, 58, 56, 58, 59, // Top
            60, 62, 61, 60, 63, 62, // Bottom
            64, 65, 66, 64, 66, 67, // Front
//            68, 70, 69, 68, 71, 70, // Back

            // Horizontal - Left - Top
//            72, 73, 74, 72, 74, 75, // Left
            76, 78, 77, 76, 79, 78, // Right
            80, 81, 82, 80, 82, 83, // Top
            84, 86, 85, 84, 87, 86, // Bottom
            88, 89, 90, 88, 90, 91, // Front
//            92, 94, 93, 92, 95, 94, // Back

            // Vertical - Left - Top
            96, 97, 98, 96, 98, 99, // Left
            100, 102, 101, 100, 103, 102, // Right
//            104, 105, 106, 104, 106, 107, // Top
            108, 110, 109, 108, 111, 110, // Bottom
            112, 113, 114, 112, 114, 115, // Front
//            116, 118, 117, 116, 119, 118, // Back

            // Vertical - Right - Top
            120, 121, 122, 120, 122, 123, // Left
            124, 126, 125, 124, 127, 126, // Right
//            128, 129, 130, 128, 130, 131, // Top
            132, 134, 133, 132, 135, 134, // Bottom
            136, 137, 138, 136, 138, 139, // Front
//            140, 142, 141, 140, 143, 142, // Back

            // Horizontal - Right - Top
            144, 145, 146, 144, 146, 147, // Left
//            148, 150, 149, 148, 151, 150, // Right
            152, 153, 154, 152, 154, 155, // Top
            156, 158, 157, 156, 159, 158, // Bottom
            160, 161, 162, 160, 162, 163, // Front
//            164, 166, 165, 164, 167, 166, // Back

            // Vertical - Right - Middle
            168, 169, 170, 168, 170, 171, // Left
            172, 174, 173, 172, 175, 174, // Right
            176, 177, 178, 176, 178, 179, // Top
            180, 182, 181, 180, 183, 182, // Bottom
            184, 185, 186, 184, 186, 187, // Front
//            188, 190, 189, 188, 191, 190, // Back

            // Horizontal - Right - Bottom
            192, 193, 194, 192, 194, 195, // Left
//            196, 198, 197, 196, 199, 198, // Right
            200, 201, 202, 200, 202, 203, // Top
            204, 206, 205, 204, 207, 206, // Bottom
            208, 209, 210, 208, 210, 211, // Front
//            212, 214, 213, 212, 215, 214, // Back

            // Vertical - Right - Bottom
            216, 217, 218, 216, 218, 219, // Left
            220, 222, 221, 220, 223, 222, // Right
            224, 225, 226, 224, 226, 227, // Top
//            228, 230, 229, 228, 231, 230, // Bottom
            232, 233, 234, 232, 234, 235, // Front
//            236, 238, 237, 236, 239, 238, // Back
        )
    }

}