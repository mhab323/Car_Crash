package utilities

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class TiltDetector(context: Context, private val onTiltCallback: (Int) -> Unit) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private lateinit var sensorEventListener: SensorEventListener

    private var lastTimestamp: Long = 0

    init {
        initEventListener()
    }

    private fun initEventListener() {
        sensorEventListener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

            override fun onSensorChanged(event: SensorEvent) {
                val x = event.values[0]
                calculateTilt(x)
            }
        }
    }

    private fun calculateTilt(x: Float) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastTimestamp < 300) return

        val targetLane = when {
            x > 3.0 -> 0
            x > 1.0 -> 1
            x < -3.0 -> 4
            x < -1.0 -> 3
            else -> 2
        }

        lastTimestamp = currentTime
        onTiltCallback(targetLane)
    }

    fun start() {
        sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_GAME)
    }

    fun stop() {
        sensorManager.unregisterListener(sensorEventListener)
    }
}