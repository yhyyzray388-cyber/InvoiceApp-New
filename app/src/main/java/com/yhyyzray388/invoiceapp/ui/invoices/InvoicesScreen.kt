package com.yhyyzray388.invoiceapp.ui.invoices

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yhyyzray388.invoiceapp.data.local.entity.InvoiceEntity

@Composable
fun InvoicesScreen(
    viewModel: InvoicesViewModel,
    onCreateInvoice: () -> Unit,
    onInvoiceClick: (Long) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("الفواتير") }) }
    ) { padding ->
        when {
            state.isLoading -> LoadingContent(Modifier.padding(padding))
            state.errorMessage != null -> ErrorContent(
                state.errorMessage!!,
                Modifier.padding(padding)
            )
            state.invoices.isEmpty() -> EmptyInvoicesContent(
                onCreateInvoice,
                Modifier.padding(padding)
            )
            else -> InvoiceList(
                invoices = state.invoices,
                onInvoiceClick = onInvoiceClick,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
private fun InvoiceList(
    invoices: List<InvoiceEntity>,
    onInvoiceClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(invoices, key = { it.id }) { invoice ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onInvoiceClick(invoice.id) }
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(invoice.invoiceNumber, style = MaterialTheme.typography.titleMedium)
                    Text(invoice.customerName)
                    Text("الإجمالي: ${"%.2f".format(invoice.total)}")
                }
            }
        }
    }
}

@Composable
private fun EmptyInvoicesContent(onCreateInvoice: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("لا توجد فواتير بعد", style = MaterialTheme.typography.headlineSmall)
        Button(onClick = onCreateInvoice, modifier = Modifier.padding(top = 16.dp)) {
            Text("إنشاء فاتورة")
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) { CircularProgressIndicator() }
}

@Composable
private fun ErrorContent(message: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) { Text(message.ifBlank { "حدث خطأ أثناء تحميل الفواتير" }) }
}
