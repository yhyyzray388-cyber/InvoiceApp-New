package com.yhyyzray388.invoiceapp.ui.invoice

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yhyyzray388.invoiceapp.data.local.entity.InvoiceEntity

@Composable
fun InvoicesScreen(
    viewModel: InvoiceViewModel,
    onCreateInvoice: () -> Unit,
    onInvoiceClick: (Long) -> Unit
) {
    val invoices by viewModel.invoices.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("الفواتير") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateInvoice) { Text("+") }
        }
    ) { padding ->
        if (invoices.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text("لا توجد فواتير بعد", style = MaterialTheme.typography.headlineSmall)
                Text("أنشئ أول فاتورة باستخدام زر +")
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
                items(invoices, key = { it.id }) { invoice ->
                    InvoiceRow(invoice, onClick = { onInvoiceClick(invoice.id) })
                }
            }
        }
    }
}

@Composable
private fun InvoiceRow(invoice: InvoiceEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp).clickable(onClick = onClick)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(invoice.invoiceNumber, style = MaterialTheme.typography.titleMedium)
                Text(invoice.customerName)
            }
            Text("%.2f".format(invoice.total), style = MaterialTheme.typography.titleMedium)
        }
    }
}
