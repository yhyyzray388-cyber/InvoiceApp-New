package com.yhyyzray388.invoiceapp

import android.app.Application
import com.yhyyzray388.invoiceapp.di.AppContainer

class InvoiceApplication : Application() {
    val appContainer: AppContainer by lazy {
        AppContainer(this)
    }
}
