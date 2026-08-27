package com.yhyyzray388.invoiceapp

import android.app.Application
import com.yhyyzray388.invoiceapp.di.AppContainer

class InvoiceAppApplication : Application() {
    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(this)
    }
}
