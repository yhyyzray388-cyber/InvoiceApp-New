package com.yhyyzray388.invoiceapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.yhyyzray388.invoiceapp.ui.navigation.AppNavHost
import com.yhyyzray388.invoiceapp.ui.theme.InvoiceAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InvoiceAppTheme {
                AppNavHost(application as InvoiceApplication)
            }
        }
    }
}
