package com.yhyyzray388.invoiceapp.ui.invoice

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun InvoiceDetailsScreen(
    viewModel: InvoiceDetailsViewModel,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit
) {
    val invoice by viewModel.invoice.collectAsStateWithLifecycle()
    val items by viewModel.items.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) }

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
                item { if (data.notes.isNotBlank()) Text(data.notes) }
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { onEdit(data.id) }, modifier = Modifier.weight(1f)) { Text("تعديل") }
                        Button(onClick = { showDeleteDialog = true }, modifier = Modifier.weight(1f)) { Text("حذف") }
                    }
                }
            }
        } ?: Text("الفاتورة غير موجودة", modifier = Modifier.padding(padding).padding(24.dp))
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("حذف الفاتورة") },
            text = { Text("هل أنت متأكد من حذف هذه الفاتورة؟ لا يمكن التراجع عن العملية.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    viewModel.delete(onBack)
                }) { Text("حذف") }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("إلغاء") } }
        )
    }
}
