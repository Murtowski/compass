package com.example.compassapplication

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.compassapplication.app.presentation.common.InputError
import com.example.compassapplication.app.presentation.main.MainViewModel
import com.example.compassapplication.core.data.SensorInterpreter
import com.example.compassapplication.core.domain.DomainLocation
import com.example.compassapplication.core.domain.SensorSample
import com.example.compassapplication.core.domain.SensorType
import com.example.compassapplication.core.usecases.LocationUsecase
import com.example.compassapplication.core.usecases.SensorUsecase
import com.example.compassapplication.util.MainCoroutineRule
import com.example.compassapplication.util.getOrAwaitValue
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {

    // Run tasks synchronously
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)

        val sensorUsecase = spyk<SensorUsecase>()
        every { sensorUsecase.getAndListenSensor() } returns flow {
            for (i in 1..10) {
                emit(floatArrayOf(i.toFloat()))
            }
        }.map { SensorSample(it, SensorType.ACCELEROMETER) }

        val locationUsecase = mockk<LocationUsecase>()
        every { locationUsecase.getAndListenLocation() } returns flow {
            emit(DomainLocation(0f, 0f))
        }

        val sensorInterpreter = mockk<SensorInterpreter> {
            every { calculateNorthAngle(any(), any()) } returns 0f
            every { calculateLocationAngle(any(), any()) } returns 0f
        }

        viewModel = MainViewModel(sensorUsecase, sensorInterpreter, locationUsecase)
    }

    @Test
    fun `test lat validation`() {
        viewModel.latitude.value = null // Biding adapter returns null if it cannot parse

        assert(viewModel.latitudeError.getOrAwaitValue() == InputError.INVALID_FORMAT)
        assert(viewModel.isLatitudeValid.getOrAwaitValue() == false)

        viewModel.latitude.value = -99f
        assert(viewModel.isLatitudeValid.getOrAwaitValue() == false)

        viewModel.latitude.value = 0f
        assert(viewModel.isLatitudeValid.getOrAwaitValue() == true)
        assert(viewModel.latitude.getOrAwaitValue() != null)
    }

    @Test
    fun `test lng validation`() {
        viewModel.longitude.value = null
        assert(viewModel.isLongitudeValid.getOrAwaitValue() == false)

        viewModel.longitude.value = 0f

        assert(viewModel.isLongitudeValid.getOrAwaitValue() == true)
        assert(viewModel.longitude.getOrAwaitValue() != null)
    }

    @Test
    fun `test update location`() {
        assert(viewModel.currentLocation.getOrAwaitValue() == null)

        viewModel.isLocationPermissionGranted.value = true
        viewModel.isLocationPermissionGranted.getOrAwaitValue()

        assert(viewModel.currentLocation.getOrAwaitValue() != null)
    }

    @Test
    fun `test update azimuth`() {
        assert(viewModel.isCustomAzimuthSet.getOrAwaitValue() == false)

        `test update location`()
        `test lat validation`()
        `test lng validation`()

        viewModel.azimuth.getOrAwaitValue()

        viewModel.setLosAngeles()

        assert(viewModel.isCustomAzimuthSet.getOrAwaitValue() == true) {
            println("latValid: ${viewModel.isLatitudeValid.getOrAwaitValue()}")
            println("lngValid: ${viewModel.isLongitudeValid.getOrAwaitValue()}")
            println("loc: ${viewModel.currentLocation.getOrAwaitValue() != null}")
            println("permision: ${viewModel.isLocationPermissionGranted.getOrAwaitValue()}")
        }

        viewModel.resetCustomAzimuth()

        assert(viewModel.isCustomAzimuthSet.getOrAwaitValue() == false)
    }
}
