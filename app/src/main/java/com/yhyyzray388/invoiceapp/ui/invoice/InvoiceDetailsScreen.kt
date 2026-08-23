package com.yhyyzray388.invoiceapp.ui.invoice

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun InvoiceDetailsScreen(
    viewModel: InvoiceDetailsViewModel,
    onBack: () -> Unit
) {
    val invoice by viewModel.invoice.collectAsStateWithLifecycle()
    val items by viewModel.items.collectAsStateWithLifecycle()

    Scaffold(topBar = { TopAppBar(title = { Text("تفاصيل الفاتورة") }) }) { padding ->
        invoice?.let { data ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item { Text("رقم الفاتورة: ${data.invoiceNumber}", style = MaterialTheme.typography.titleLarge) }
                item { Text("العميل: ${data.customerName}") }
                item { Text("المجموع الفرعي: %.2f".format(data.subtotal)) }
                item { Text("الضريبة: %.2f".format(data.tax)) }
                item { Text("الإجمالي: %.2f".format(data.total), style = MaterialTheme.typography.titleMedium) }
                item { Text("البنود", style = MaterialTheme.typography.titleMedium) }
                items(items, key = { it.id }) { item ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text(item.description)
                                Text("${item.quantity} × %.2f".format(item.unitPrice))
                            }
                            Text("%.2f".format(item.lineTotal))
                        }
                    }
                }
                item { Text(data.notes) }
            }
        } ?: Text("الفاتورة غير موجودة", modifier = Modifier.padding(padding).padding(24.dp))
    }
}
