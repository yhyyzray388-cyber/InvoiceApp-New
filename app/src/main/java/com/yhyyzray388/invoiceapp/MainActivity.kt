package com.yhyyzray388.invoiceapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.yhyyzray388.invoiceapp.ui.invoice.CreateInvoiceScreen
import com.yhyyzray388.invoiceapp.ui.invoice.InvoiceViewModel
import com.yhyyzray388.invoiceapp.ui.invoices.InvoiceListScreen
import com.yhyyzray388.invoiceapp.ui.invoices.InvoiceListViewModel
import com.yhyyzray388.invoiceapp.ui.theme.InvoiceAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as InvoiceAppApplication
        val listViewModel = ViewModelProvider(
            this,
            InvoiceListViewModel.Factory(app.appContainer.invoiceRepository)
        )[InvoiceListViewModel::class.java]
        val invoiceViewModel = ViewModelProvider(
            this,
            InvoiceViewModel.Factory(app.appContainer.invoiceRepository)
        )[InvoiceViewModel::class.java]

        setContent {
            InvoiceAppTheme {
                var showCreate = androidx.compose.runtime.remember {
                    androidx.compose.runtime.mutableStateOf(false)
                }

                if (showCreate.value) {
                    CreateInvoiceScreen(
                        viewModel = invoiceViewModel,
                        onSaved = { showCreate.value = false }
                    )
                } else {
                    InvoiceListScreen(
                        viewModel = listViewModel,
                        onCreateInvoice = { showCreate.value = true }
                    )
                }
            }
        }
    }
}
