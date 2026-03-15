package dev.shoheiyamagiwa.constell

import android.app.Application
import dev.shoheiyamagiwa.constell.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class ConstellApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@ConstellApplication)

            modules(networkModule)
        }
    }
}