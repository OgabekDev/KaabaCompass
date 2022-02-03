package dev.ogabek.kotlin

import android.R.attr
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.view.animation.Animation
import android.view.animation.RotateAnimation


class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var compass: ImageView
    private val mGravity = FloatArray(3)
    private val mGeomagnetic = FloatArray(3)
    private var azimuth = 0f
    private var currentAzimuth = 0f
    private lateinit var sensorManager: SensorManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()

    }

    private fun initViews() {
        compass = findViewById(R.id.iv_compass)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_GAME)
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME)
    }

    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        val alpha = 0.97f
        synchronized(this) {
            if (sensorEvent!!.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                mGravity[0] = alpha * mGravity[0] + (1 - alpha) * sensorEvent.values[0]
                mGravity[1] = alpha * mGravity[1] + (1 - alpha) * sensorEvent.values[1]
                mGravity[2] = alpha * mGravity[2] + (1 - alpha) * sensorEvent.values[2]
            }
            if (sensorEvent.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                mGeomagnetic[0] = alpha * mGeomagnetic[0] + (1 - alpha) * sensorEvent.values[0]
                mGeomagnetic[1] = alpha * mGeomagnetic[1] + (1 - alpha) * sensorEvent.values[1]
                mGeomagnetic[2] = alpha * mGeomagnetic[2] + (1 - alpha) * sensorEvent.values[2]
            }

            val R = FloatArray(9)
            val I = FloatArray(9)

            val success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic)

            if (success) {
                val orientation = FloatArray(3)
                SensorManager.getOrientation(R, orientation)
                azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
                azimuth = (azimuth + 360) % 360

                val anim: Animation = RotateAnimation(-currentAzimuth, -azimuth, Animation.RELATIVE_TO_SELF , 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
                Log.d("TAG", "onSensorChanged: galdi")
                currentAzimuth = azimuth

                anim.duration = 500
                anim.repeatCount = 0
                anim.fillAfter = true

                compass.startAnimation(anim )
            }

        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }


}