package com.yhyyzray388.invoiceapp.ui.invoice

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yhyyzray388.invoiceapp.data.local.entity.InvoiceEntity
import com.yhyyzray388.invoiceapp.data.repository.InvoiceRepository
import kotlinx.coroutines.launch

@Composable
fun EditInvoiceScreen(
    invoiceId: Long,
    repository: InvoiceRepository,
    onSaved: () -> Unit
) {
    var invoice by remember { mutableStateOf<InvoiceEntity?>(null) }
    var customerName by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var saving by remember { mutableStateOf(false) }
    val items by repository.observeItems(invoiceId).collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    LaunchedEffect(invoiceId) {
        repository.getInvoiceById(invoiceId)?.let {
            invoice = it
            customerName = it.customerName
            notes = it.notes
        }
    }

    val current = invoice
    if (current == null) {
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val subtotal = items.sumOf { it.total }
    val tax = current.tax
    val total = subtotal + tax

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("تعديل الفاتورة", style = MaterialTheme.typography.headlineSmall)
        Text(current.invoiceNumber, style = MaterialTheme.typography.titleMedium)

        OutlinedTextField(
            value = customerName,
            onValueChange = { customerName = it },
            label = { Text("اسم العميل") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            itemsIndexed(items, key = { _, item -> item.id }) { _, item ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.description)
                        Text("${item.quantity} × ${item.unitPrice} = ${item.total}")
                    }
                    Button(onClick = { scope.launch { repository.deleteItem(item) } }) {
                        Text("حذف")
                    }
                }
            }
        }

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("ملاحظات") },
            modifier = Modifier.fillMaxWidth()
        )

        Text("الإجمالي: $total", style = MaterialTheme.typography.titleLarge)

        Button(
            enabled = !saving && customerName.isNotBlank(),
            onClick = {
                saving = true
                scope.launch {
                    repository.updateInvoiceWithItems(
                        current.copy(
                            customerName = customerName.trim(),
                            subtotal = subtotal,
                            total = total,
                            notes = notes
                        ),
                        items
                    )
                    saving = false
                    onSaved()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            if (saving) CircularProgressIndicator() else Text("حفظ التعديلات")
        }
    }
}
