package com.xooloo.r8bug

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

// Important: Using lazy injection to make sure nothing is initialized before the app.
@HiltAndroidApp
class Messenger : Application(), Configuration.Provider {

    // Work Manager Injection Support
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        // Init core library
        AndroidThreeTen.init(this)

        // Finaly, init Crashlytics and Timber
        Timber.plant(Timber.DebugTree())
    }


}
