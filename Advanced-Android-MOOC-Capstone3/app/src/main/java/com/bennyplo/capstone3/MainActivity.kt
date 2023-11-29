package com.bennyplo.capstone3

import android.content.res.Configuration
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.view.Surface
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class MainActivity : AppCompatActivity(), SensorEventListener {

    private val _glView: MyView by lazy {
        MyView(this)
    }

    private val _rotationSensor: Sensor? by lazy {
        _sensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    }

    private val _sensorManager: SensorManager? by lazy {
        getSystemService(SENSOR_SERVICE) as SensorManager
    }

    override fun onConfigurationChanged(newConfig: Configuration) { // Ensure that no matter which orientation, the app will use full screen!
        super.onConfigurationChanged(newConfig)

        setupFullscreen()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(_glView)

        setupFullscreen()
    }

    override fun onPause() {
        super.onPause()

//        _sensorManager?.unregisterListener(this)
        _glView.onPause()
    }

    override fun onResume() {
        super.onResume()

//        _rotationSensor?.let {
//            _sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_FASTEST)
//        }
        _glView.onResume()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor == _rotationSensor) {
            event?.values?.let {
                updateOrientation(it)
            }
        }
    }

    private fun setupFullscreen() {
        // Set full screen
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, _glView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    @Suppress("DEPRECATION")
    private fun updateOrientation(rotationVector: FloatArray) {
        val rotationMatrix = FloatArray(9)
        val deviceRelativeX: Int
        val deviceRelativeY: Int

        val adjustedRotationMatrix = FloatArray(9)
        val orientation = FloatArray(3)

        SensorManager.getRotationMatrixFromVector(rotationMatrix, rotationVector)

        resources.configuration.orientation
        when (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            display?.rotation
        } else {
            windowManager.defaultDisplay.rotation
        }) {
            Surface.ROTATION_0 -> {
                deviceRelativeX = SensorManager.AXIS_X
                deviceRelativeY = SensorManager.AXIS_Z
            }

            Surface.ROTATION_90 -> {
                deviceRelativeX = SensorManager.AXIS_Z
                deviceRelativeY = SensorManager.AXIS_MINUS_X
            }

            Surface.ROTATION_180 -> {
                deviceRelativeX = SensorManager.AXIS_MINUS_X
                deviceRelativeY = SensorManager.AXIS_MINUS_Z
            }

            Surface.ROTATION_270 -> {
                deviceRelativeX = SensorManager.AXIS_MINUS_Z
                deviceRelativeY = SensorManager.AXIS_X
            }

            else -> {
                deviceRelativeX = SensorManager.AXIS_X
                deviceRelativeY = SensorManager.AXIS_Z
            }
        }

        SensorManager.remapCoordinateSystem(
            rotationMatrix,
            deviceRelativeX,
            deviceRelativeY,
            adjustedRotationMatrix
        )

        SensorManager.getOrientation(adjustedRotationMatrix, orientation)
        val yaw = Math.toDegrees(orientation[0].toDouble())
        val pitch = Math.toDegrees(orientation[1].toDouble())
        val roll = Math.toDegrees(orientation[2].toDouble())

        _glView.sensorRotates(pitch, yaw, roll)
    }

}