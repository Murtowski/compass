package com.example.compassapplication

import android.location.Location
import androidx.lifecycle.*
import timber.log.Timber

/**
 * Created by
 * @author Piotr Piskorski
 * @date on 02.04.2020.
 */

class MainViewModelFactory(
    private val sensorUsecase: SensorUsecase,
    private val locationUsecase: LocationUsecase
): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = MainViewModel(
        sensorUsecase, locationUsecase
    ) as T
}

class MainViewModel(
    private val sensorUsecase: SensorUsecase,
    private val locationUsecase: LocationUsecase
): ViewModel(){

    /*
    * Count Azimuth, by default to NORTH
    * */
    private var previousAzimuth = 0f
    val azimuth: LiveData<Pair<Float,Float>>  = sensorUsecase.getAndRegister().asLiveData().map {
//            Timber.d("Receiver new: $it")
            val rotation = Pair(previousAzimuth, it)
            previousAzimuth = it
            rotation
        }

    /*
    * LONGITUDE
    * */
    val longitude = MutableLiveData<Double?>(0.0)
    val longitudeError : LiveData<InputError> = longitude.map {
        when{
            it == null -> InputError.INVALID_FORMAT
            it !in (-180f .. 180f) ->  InputError.OUT_OF_RANGE
            else -> InputError.NONE
        }
    }
    val isLongitudeValid: LiveData<Boolean> = longitudeError.map {
        it == InputError.NONE
    }

    /*
    * LATITUDE
    * */
    val latitude = MutableLiveData<Double?>(0.0)
    val latitudeError : LiveData<InputError> = latitude.map {
        when{
            it == null -> InputError.INVALID_FORMAT
            it !in (-90f .. 90f) ->  InputError.OUT_OF_RANGE
            else -> InputError.NONE
        }
    }
    val isLatitudeValid: LiveData<Boolean> = latitudeError.map {
        it == InputError.NONE
    }

    /*
    * LATITUDE & LONGITUDE validation > automatic update destination
    * */
    init {
        MediatorLiveData<Location>().apply {
            addSource(longitude){
                updateDestination()
            }
            addSource(latitude){
                updateDestination()
            }
        }
    }

    private fun updateDestination(){
        if(isLocationPermissionGranted.value == true
            && isLatitudeValid.value == true
            && isLongitudeValid.value == true){
            val newDestination = Location("").also {
                it.latitude = latitude.value!!
                it.longitude = longitude.value!!
            }
            sensorUsecase.setLocationOffset(newDestination)
        }
    }

    /*
    * Location
    * */
    val isLocationPermissionGranted = MutableLiveData(false)

    override fun onCleared() {
        sensorUsecase.stop()
        super.onCleared()
    }
}

enum class InputError(val msg: String?){ // Here we may use @StringRes
    INVALID_FORMAT("Invalid Format"),
    OUT_OF_RANGE("Given location out of range"),
    NONE(null)
}