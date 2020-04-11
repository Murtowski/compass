package com.example.compassapplication

import android.app.Application
import android.content.Context
import android.hardware.SensorManager
import android.location.LocationManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import timber.log.Timber

class CompassApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        startKoin {
            // declare used Android context
            androidContext(this@CompassApplication)
            // declare modules
            modules(myModule)
        }
    }


    val myModule : Module = module {

        factory{ applicationContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
        factory<SensorSource> { SensorSourceImpl(get()) }
        factory<SensorInterpreter>{SensorInterpreterImpl(get())}
        single<SensorUsecase> { (SensorUsecaseImpl(get(), get())) }

        factory { applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager }
        factory { LocationServices.getFusedLocationProviderClient(applicationContext) as FusedLocationProviderClient }
        factory<LocationSource>{ LocationSourceImpl(get(), get()) }
        single<LocationUsecase>{ LocationUsecaseImpl(get()) }

        factory { MainViewModelFactory(get(),get()) }
        viewModel { MainViewModel(get(),get()) }
    }
}