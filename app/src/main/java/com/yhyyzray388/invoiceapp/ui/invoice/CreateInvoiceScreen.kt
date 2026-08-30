package com.yhyyzray388.invoiceapp.ui.invoice

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.yhyyzray388.invoiceapp.domain.model.InvoiceCalculator
import com.yhyyzray388.invoiceapp.domain.model.InvoiceDraft
import com.yhyyzray388.invoiceapp.domain.model.InvoiceItemDraft
import com.yhyyzray388.invoiceapp.domain.util.formatAmount

@Composable
fun CreateInvoiceScreen(
    viewModel: InvoiceViewModel,
    onSaved: () -> Unit
) {
    var customerName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("1") }
    var price by remember { mutableStateOf("") }
    var taxRateInput by remember { mutableStateOf("0") }
    var notes by remember { mutableStateOf("") }
    val items = remember { mutableStateListOf<InvoiceItemDraft>() }
    val saving by viewModel.saving.collectAsState()
    val savedInvoiceId by viewModel.savedInvoiceId.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(savedInvoiceId) {
        if (savedInvoiceId != null) {
            viewModel.clearSavedState()
            onSaved()
        }
    }

    val taxRate = taxRateInput.replace(",", ".").toDoubleOrNull()?.coerceAtLeast(0.0) ?: 0.0
    val subtotal = items.sumOf { it.total }
    val tax = InvoiceCalculator.tax(subtotal, taxRate)
    val currentTotal = subtotal + tax

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("فاتورة جديدة", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = customerName,
            onValueChange = { customerName = it },
            label = { Text("اسم العميل") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("وصف المنتج") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = quantity,
                onValueChange = { value -> quantity = value.filter(Char::isDigit) },
                label = { Text("الكمية") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = price,
                onValueChange = { value ->
                    price = value.filter { it.isDigit() || it == '.' || it == ',' }
                },
                label = { Text("السعر") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
        }

        Button(
            onClick = {
                val q = quantity.toIntOrNull()?.toDouble()
                val p = price.replace(",", "").toDoubleOrNull()
                if (description.isNotBlank() && q != null && q > 0 && p != null && p >= 0) {
                    items.add(InvoiceItemDraft(description.trim(), q, p))
                    description = ""
                    quantity = "1"
                    price = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("إضافة بند") }

        OutlinedTextField(
            value = taxRateInput,
            onValueChange = { value ->
                taxRateInput = value.filter { it.isDigit() || it == '.' || it == ',' }
            },
            label = { Text("نسبة الضريبة %") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        HorizontalDivider()

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            itemsIndexed(items) { index, item ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.description, style = MaterialTheme.typography.titleMedium)
                            Text("${item.quantity.toInt()} × ${formatAmount(item.unitPrice)}")
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(formatAmount(item.total))
                            Button(onClick = { items.removeAt(index) }) { Text("حذف") }
                        }
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

        Text("المجموع الفرعي: ${formatAmount(subtotal)}")
        Text("الضريبة: ${formatAmount(tax)}")
        Text("الإجمالي: ${formatAmount(currentTotal)}", style = MaterialTheme.typography.titleLarge)
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        Button(
            enabled = !saving && customerName.isNotBlank() && items.isNotEmpty(),
            onClick = {
                viewModel.save(
                    InvoiceDraft(
                        customerName = customerName.trim(),
                        items = items.toList(),
                        taxRate = taxRate,
                        notes = notes.trim()
                    )
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            if (saving) CircularProgressIndicator() else Text("حفظ الفاتورة")
        }

        Spacer(Modifier.height(4.dp))
    }
}
