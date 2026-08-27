package com.yhyyzray388.invoiceapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.yhyyzray388.invoiceapp.ui.invoices.InvoiceListScreen
import com.yhyyzray388.invoiceapp.ui.invoices.InvoiceListViewModel
import com.yhyyzray388.invoiceapp.ui.theme.InvoiceAppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val application = application as InvoiceAppApplication
        val viewModel = ViewModelProvider(
            this,
            InvoiceListViewModel.Factory(application.appContainer.invoiceRepository)
        )[InvoiceListViewModel::class.java]

        setContent {
            InvoiceAppTheme {
                InvoiceListScreen(viewModel = viewModel)
            }
        }
    }
}
