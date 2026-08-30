package com.yhyyzray388.invoiceapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
                val screenState = remember { mutableStateOf(Screen.List) }
                val selectedInvoiceIdState = remember { mutableStateOf<Long?>(null) }

                when (screenState.value) {
                    Screen.List -> {
                        InvoiceListScreen(
                            viewModel = listViewModel,
                            onCreateInvoice = { screenState.value = Screen.Create },
                            onInvoiceClick = { id ->
                                selectedInvoiceIdState.value = id
                                screenState.value = Screen.Edit
                            }
                        )
                    }

                    Screen.Create -> {
                        BackHandler {
                            screenState.value = Screen.List
                        }
                        CreateInvoiceScreen(
                            viewModel = invoiceViewModel,
                            onSaved = { screenState.value = Screen.List }
                        )
                    }

                    Screen.Edit -> {
                        BackHandler {
                            screenState.value = Screen.List
                            selectedInvoiceIdState.value = null
                        }

                        val id = selectedInvoiceIdState.value
                        if (id != null) {
                            EditInvoiceScreen(
                                invoiceId = id,
                                repository = app.appContainer.invoiceRepository,
                                onSaved = {
                                    screenState.value = Screen.List
                                    selectedInvoiceIdState.value = null
                                }
                            )
                        } else {
                            screenState.value = Screen.List
                        }
                    }
                }
            }
        }
    }

    private enum class Screen { List, Create, Edit }
}
