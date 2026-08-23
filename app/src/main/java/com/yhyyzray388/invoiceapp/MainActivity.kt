package com.yhyyzray388.invoiceapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yhyyzray388.invoiceapp.ui.invoice.InvoiceViewModel
import com.yhyyzray388.invoiceapp.ui.theme.InvoiceAppTheme

class MainActivity : ComponentActivity() {

    private val invoiceViewModel: InvoiceViewModel by viewModels {
        InvoiceViewModel.Factory(
            (application as InvoiceApplication).appContainer.invoiceRepository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            InvoiceAppTheme {
                val invoices by invoiceViewModel.invoices.collectAsStateWithLifecycle()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WelcomeScreen(invoiceCount = invoices.size)
                }
            }
        }
    }
}

@Composable
private fun WelcomeScreen(invoiceCount: Int) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "InvoiceApp",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "الفواتير المحفوظة: $invoiceCount",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
