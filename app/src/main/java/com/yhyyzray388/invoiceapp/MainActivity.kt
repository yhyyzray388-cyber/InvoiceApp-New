package com.yhyyzray388.invoiceapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.yhyyzray388.invoiceapp.ui.invoice.CreateInvoiceScreen
import com.yhyyzray388.invoiceapp.ui.invoice.EditInvoiceScreen
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
                var screen by androidx.compose.runtime.remember {
                    androidx.compose.runtime.mutableStateOf(Screen.List)
                }
                var selectedInvoiceId by androidx.compose.runtime.remember {
                    androidx.compose.runtime.mutableStateOf<Long?>(null)
                }

                when (screen) {
                    Screen.List -> InvoiceListScreen(
                        viewModel = listViewModel,
                        onCreateInvoice = { screen = Screen.Create },
                        onInvoiceClick = { id ->
                            selectedInvoiceId = id
                            screen = Screen.Edit
                        }
                    )
                    Screen.Create -> CreateInvoiceScreen(
                        viewModel = invoiceViewModel,
                        onSaved = { screen = Screen.List }
                    )
                    Screen.Edit -> selectedInvoiceId?.let { id ->
                        EditInvoiceScreen(
                            invoiceId = id,
                            repository = app.appContainer.invoiceRepository,
                            onSaved = { screen = Screen.List }
                        )
                    } ?: run { screen = Screen.List }
                }
            }
        }
    }

    private enum class Screen { List, Create, Edit }
}
