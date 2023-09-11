package com.bennyplo.virtualreality

import android.content.res.Configuration
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Surface
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.bennyplo.virtualreality.ref.MyView

class MainActivity : AppCompatActivity(), SensorEventListener {

    private var glView: com.bennyplo.virtualreality.ref.MyView? = null
    private var rotationSensor: Sensor? = null
    private var sensorManager: SensorManager? = null

    override fun onConfigurationChanged(newConfig: Configuration) { //ensure that no matter which orientation, the app will use full screen!
        super.onConfigurationChanged(newConfig)
        setupFullscreen()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glView = MyView(this)
        setContentView(glView)
        // set full screen
        setupFullscreen()

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        rotationSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    }

//    override fun onPause() {
//        super.onPause()
//        sensorManager?.unregisterListener(this)
//    }

//    override fun onResume() {
//        super.onResume()
//
//        rotationSensor?.let {
//            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_FASTEST)
//        }
//    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor == rotationSensor) {
            event?.values?.let {
                updateOrientation(it)
            }
        }
    }

    private fun setupFullscreen() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        glView?.let {
            WindowInsetsControllerCompat(window, it)
        }?.let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun updateOrientation(rotationVector: FloatArray) {
        val rotationMatrix = FloatArray(9)
        val deviceRelativeX: Int
        val deviceRelativeY: Int

        val adjustedRotationMatrix = FloatArray(9)
        val orientation = FloatArray(3)

        SensorManager.getRotationMatrixFromVector(rotationMatrix, rotationVector)

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

//        glView?.sensorRotates(pitch, yaw, roll)
    }

}
