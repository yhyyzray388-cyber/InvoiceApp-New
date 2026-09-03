package com.yhyyzray388.invoiceapp.ui.invoices

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.yhyyzray388.invoiceapp.data.local.entity.InvoiceEntity
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceListScreen(
    viewModel: InvoiceListViewModel,
    onCreateInvoice: () -> Unit,
    onInvoiceClick: (Long) -> Unit = {}
) {
    val invoices by viewModel.invoices.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("الفواتير") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateInvoice) { Text("+") }
        }
    ) { paddingValues ->
        if (invoices.isEmpty()) {
            EmptyInvoicesState(modifier = Modifier.padding(paddingValues))
        } else {
            InvoiceList(
                invoices = invoices,
                modifier = Modifier.padding(paddingValues),
                onInvoiceClick = onInvoiceClick,
                onDeleteInvoice = viewModel::deleteInvoice
            )
        }
    }
}

@Composable
private fun EmptyInvoicesState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("لا توجد فواتير بعد", style = MaterialTheme.typography.headlineSmall)
        Text(
            "اضغط + لإنشاء أول فاتورة.",
            modifier = Modifier.padding(top = 8.dp),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun InvoiceList(
    invoices: List<InvoiceEntity>,
    modifier: Modifier = Modifier,
    onInvoiceClick: (Long) -> Unit,
    onDeleteInvoice: (InvoiceEntity) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(invoices, key = { it.id }) { invoice ->
            InvoiceCard(
                invoice = invoice,
                onClick = { onInvoiceClick(invoice.id) },
                onDelete = { onDeleteInvoice(invoice) }
            )
        }
    }
}

@Composable
private fun InvoiceCard(
    invoice: InvoiceEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val currency = NumberFormat.getCurrencyInstance(Locale.getDefault())
    val date = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date(invoice.issueDate))

    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(invoice.invoiceNumber, style = MaterialTheme.typography.titleLarge)
            Text(invoice.customerName, modifier = Modifier.padding(top = 4.dp))
            Text("التاريخ: $date", modifier = Modifier.padding(top = 8.dp))
            Text(
                "الإجمالي: ${currency.format(invoice.total)}",
                modifier = Modifier.padding(top = 4.dp),
                style = MaterialTheme.typography.titleMedium
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = onClick, modifier = Modifier.weight(1f)) {
                    Text("فتح وتعديل")
                }
                OutlinedButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("حذف")
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("حذف الفاتورة") },
            text = { Text("هل أنت متأكد من حذف الفاتورة ${invoice.invoiceNumber}؟") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDelete()
                    }
                ) {
                    Text("حذف")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}
