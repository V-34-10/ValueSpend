package com.finance.valuespend

import android.app.Application
import com.finance.valuespend.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ValueSpendApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@ValueSpendApp)
            modules(appModule)
        }
    }
}

