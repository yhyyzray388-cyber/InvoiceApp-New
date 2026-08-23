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
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yhyyzray388.invoiceapp.domain.model.InvoiceItem

@Composable
fun EditInvoiceScreen(viewModel: EditInvoiceViewModel, onSaved: () -> Unit, onBack: () -> Unit) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(topBar = { TopAppBar(title = { Text("تعديل الفاتورة") }) }) { padding ->
        if (!state.initialized) {
            Column(Modifier.fillMaxSize().padding(padding), verticalArrangement = Arrangement.Center) { CircularProgressIndicator() }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                item { OutlinedTextField(state.invoiceNumber, viewModel::setInvoiceNumber, label = { Text("رقم الفاتورة") }, modifier = Modifier.fillMaxWidth(), singleLine = true) }
                item { OutlinedTextField(state.customerName, viewModel::setCustomerName, label = { Text("اسم العميل") }, modifier = Modifier.fillMaxWidth(), singleLine = true) }
                item { OutlinedTextField(state.taxRatePercent, viewModel::setTaxRate, label = { Text("الضريبة %") }, modifier = Modifier.fillMaxWidth(), singleLine = true) }
                item { Text("بنود الفاتورة") }
                itemsIndexed(state.items) { index, item -> EditItemEditor(item, { viewModel.updateItem(index, it) }, { viewModel.removeItem(index) }) }
                item { Button(onClick = viewModel::addItem, modifier = Modifier.fillMaxWidth()) { Text("إضافة بند") } }
                item { OutlinedTextField(state.notes, viewModel::setNotes, label = { Text("ملاحظات") }, modifier = Modifier.fillMaxWidth()) }
                state.error?.let { item { Text(it) } }
                item { Button(onClick = { viewModel.save(onSaved) }, enabled = !state.isSaving, modifier = Modifier.fillMaxWidth()) { if (state.isSaving) CircularProgressIndicator() else Text("حفظ التعديلات") } }
                item { Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("إلغاء") } }
            }
        }
    }
}

@Composable
private fun EditItemEditor(item: InvoiceItem, onChange: (InvoiceItem) -> Unit, onRemove: () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(item.description, { onChange(item.copy(description = it)) }, label = { Text("الوصف") }, modifier = Modifier.fillMaxWidth())
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(item.quantity.toString(), { onChange(item.copy(quantity = it.toDoubleOrNull() ?: 0.0)) }, label = { Text("الكمية") }, modifier = Modifier.weight(1f), singleLine = true)
                OutlinedTextField(item.unitPrice.toString(), { onChange(item.copy(unitPrice = it.toDoubleOrNull() ?: 0.0)) }, label = { Text("سعر الوحدة") }, modifier = Modifier.weight(1f), singleLine = true)
            }
            Text("الإجمالي: %.2f".format(item.lineTotal))
            Button(onClick = onRemove) { Text("حذف البند") }
        }
    }
}
