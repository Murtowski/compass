package com.example.compassapplication

import android.app.Application
import android.content.Context
import android.hardware.SensorManager
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

class CompassApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            // declare used Android context
            androidContext(this@CompassApplication)
            // declare modules
            modules(myModule)
        }
    }


    val myModule : Module = module {

        factory<SensorManager>{applicationContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager}
        factory<SensorSource> { SensorSourceImpl(get()) }
        factory<SensorInterpreter>{SensorInterpreterImpl(get())}
        single<SensorUsecase> { (SensorUsecaseImpl(get(), get())) }

        factory { MainViewModelFactory(get(),get()) }
        viewModel { MainViewModel(get(),get()) }
    }
}