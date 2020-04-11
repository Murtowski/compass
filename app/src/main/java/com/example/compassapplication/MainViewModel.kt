package com.example.compassapplication

import android.app.Application
import android.content.Context
import android.hardware.SensorEvent
import android.hardware.SensorManager
import androidx.lifecycle.*
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Created by
 * @author Piotr Piskorski
 * @date on 02.04.2020.
 */

class MainViewModelFactory(
    private val sensorUsecase: SensorUsecase,
    private val context: Context
): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = MainViewModel(
        sensorUsecase,context
    ) as T
}

class MainViewModel(
    private val sensor: SensorUsecase,
    private val context: Context
): ViewModel(){

    private var previousAzimuth = 0f
    lateinit var ld : LiveData<Float>

    init {
        sensor.updateSensorManager(context.getSystemService(Context.SENSOR_SERVICE) as SensorManager)
        ld = sensor.getAndRegister().asLiveData()
    }


    val azimuth = MutableLiveData<Pair<Float,Float>>()

//    val azimuth: LiveData<Pair<Float,Float>?>  = Transformations.map(ld) {
//            Timber.d("Receiver new: $it")
//            val rotation = Pair(previousAzimuth, it)
//            previousAzimuth = it
//            rotation
//        }

    override fun onCleared() {
        sensor.stop()
        super.onCleared()
    }
}