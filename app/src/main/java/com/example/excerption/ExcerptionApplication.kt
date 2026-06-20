package com.example.excerption

import android.app.Application
import com.example.excerption.data.AppContainer

class ExcerptionApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppContainer.init(this)
    }
}
