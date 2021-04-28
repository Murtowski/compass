package com.example.compassapplication.app.presentation.main

import androidx.lifecycle.*
import com.example.compassapplication.app.presentation.common.InputError
import com.example.compassapplication.core.data.SensorInterpreter
import com.example.compassapplication.core.domain.DomainLocation
import com.example.compassapplication.core.usecases.LocationUsecase
import com.example.compassapplication.core.usecases.SensorUsecase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Created by
 * @author Piotr Piskorski
 * @date on 02.04.2020.
 */
class MainViewModelFactory(
    private val sensorUsecase: SensorUsecase,
    private val sensorInterpreter: SensorInterpreter,
    private val locationUsecase: LocationUsecase
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = MainViewModel(
        sensorUsecase, sensorInterpreter, locationUsecase
    ) as T
}

class MainViewModel(
    private val sensorUsecase: SensorUsecase,
    private val sensorInterpreter: SensorInterpreter,
    private val locationUsecase: LocationUsecase
) : ViewModel() {
    private val cancellationHandler = CoroutineExceptionHandler { _, exception ->
        Timber.d("CoroutineExceptionHandler got $exception")
    }

    /*
    * LONGITUDE
    * */
    val longitude = MutableLiveData<Float>()
    val longitudeError: LiveData<InputError> = longitude.map {
        when (it) {
            null -> InputError.INVALID_FORMAT
            !in (-180f..180f) -> InputError.OUT_OF_RANGE
            else -> InputError.NONE
        }
    }
    val isLongitudeValid: LiveData<Boolean> = longitudeError.map {
        it == InputError.NONE
    }

    /*
    * LATITUDE
    * */
    val latitude = MutableLiveData<Float>()
    val latitudeError: LiveData<InputError> = latitude.map {
        when (it) {
            null -> InputError.INVALID_FORMAT
            !in (-90f..90f) -> InputError.OUT_OF_RANGE
            else -> InputError.NONE
        }
    }
    val isLatitudeValid: LiveData<Boolean> = latitudeError.map { it == InputError.NONE }

    /*
    * Update DESTINATION based on chosen LATITUDE & LONGITUDE updates
    * */
    private val destinationMediatorLiveData = MediatorLiveData<Pair<Float, Float>>().apply {
        addSource(latitude) { createNewDestinationFromLatLng() }
        addSource(longitude) { createNewDestinationFromLatLng() }
    }

    private fun createNewDestinationFromLatLng() {
        if (isLatitudeValid.value == true && isLongitudeValid.value == true) {
            DestinationLocation.of(latitude.value, longitude.value)?.let { destination ->
                setNewDestination(destination)
            }
        }
    }

    private fun setNewDestination(destination: DestinationLocation) {
        destinationMediatorLiveData.value = destination.location
        _isCustomAzimuthSet.postValue(true)
        if (destination !is DestinationLocation.Custom) {
            latitude.value = destination.location.first!!
            longitude.value = destination.location.second!!
        }
    }

    /*
    * Track current Location
    * */
    private val _currentLocation = MutableLiveData<DomainLocation>(null)
    val currentLocation: LiveData<DomainLocation> = _currentLocation

    val isLocationPermissionGranted = object : MutableLiveData<Boolean?>(null) {
        override fun setValue(permissionGranted: Boolean?) {
            super.setValue(permissionGranted)
            Timber.d("Location permission changed: $permissionGranted")
            if (permissionGranted == true) {
                runCompass()
            }
        }
    }

    /*
    * Calculate location angle
    * */
    private val _isCustomAzimuthSet = MutableLiveData(false)
    val isCustomAzimuthSet: LiveData<Boolean> = _isCustomAzimuthSet

    fun resetCustomAzimuth() {
        viewModelScope.launch { locationAngle.emit(0f) }
        _isCustomAzimuthSet.value = false
    }

    private val locationAngle: MutableStateFlow<Float> by lazy {
        MutableStateFlow(0f).also { locationAngle ->
            combine(
                locationUsecase.getAndListenLocation()
                    .onEach { _currentLocation.postValue(it) },
                destinationMediatorLiveData.asFlow()
                    .debounce(DEBOUNCE_TIME_MILLIS)
                    .map { pair -> DomainLocation(pair.first, pair.second) }
            ) { location, destination ->
                sensorInterpreter.calculateLocationAngle(location, destination)
            }.onEach { newLocationAngle ->
                locationAngle.emit(newLocationAngle)
            }.launchIn(viewModelScope)
        }
    }

    /*
    * Count Azimuth, by default is NORTH with 0 rotation angle
    * */
    private var previousAzimuth = 0f
    private val _azimuth: MutableLiveData<Pair<Float, Float>> = MutableLiveData()
    val azimuth: LiveData<Pair<Float, Float>> = _azimuth

    private fun runCompass() = viewModelScope.launch(cancellationHandler) {
        combine(
            locationAngle,
            sensorUsecase.getAndListenSensor()
        ) { locationAngle, sensorData ->
            sensorInterpreter.calculateNorthAngle(sensorData, locationAngle)
        }.filterNotNull()
            .collect { newAzimuth ->
                val rotation = Pair(previousAzimuth, newAzimuth)
                previousAzimuth = newAzimuth
                _azimuth.postValue(rotation)
            }
    }

    fun setWroclaw() {
        setNewDestination(DestinationLocation.Wroclaw)
    }

    fun setMountEverest() {
        setNewDestination(DestinationLocation.MountEverest)
    }

    fun setPrague() {
        setNewDestination(DestinationLocation.Prague)
    }

    fun setLosAngeles() {
        setNewDestination(DestinationLocation.LosAngeles)
    }

    companion object {
        const val DEBOUNCE_TIME_MILLIS = 1000L
    }

    sealed class DestinationLocation(val location: Pair<Float, Float>) {
        object Wroclaw : DestinationLocation(Pair(51.107883f, 17.038538f))
        object MountEverest : DestinationLocation(Pair(27.986065f, 86.922623f))
        object Prague : DestinationLocation(Pair(50.073658f, 14.418540f))
        object LosAngeles : DestinationLocation(Pair(34.052235f, -118.243683f))
        class Custom(lat: Float, lng: Float) : DestinationLocation(Pair(lat, lng))

        companion object {
            fun of(lat: Float?, lng: Float?): Custom? =
                if (lat != null && lng != null) Custom(lat, lng)
                else null
        }
    }
}
