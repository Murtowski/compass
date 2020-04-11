package com.example.compassapplication

import android.content.Context
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels

class MainActivity : AppCompatActivity() {


    val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val sensorSource = SensorSourceImpl(sensorManager)
    val sensorInterpreter = SensorInterpreterImpl(sensorManager)
    val sensorUsecase = SensorUsecaseImpl(sensorSource, sensorInterpreter)
    val factory = DiscoverDivicesViewModelFactory(sensorUsecase)

    val viewModel by viewModels<MainViewModel> { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
