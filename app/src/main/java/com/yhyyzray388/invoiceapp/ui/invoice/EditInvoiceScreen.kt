package com.yhyyzray388.invoiceapp.ui.invoice

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.yhyyzray388.invoiceapp.data.local.entity.InvoiceEntity
import com.yhyyzray388.invoiceapp.data.local.entity.InvoiceItemEntity
import com.yhyyzray388.invoiceapp.data.repository.InvoiceRepository
import com.yhyyzray388.invoiceapp.domain.model.InvoiceCalculator
import com.yhyyzray388.invoiceapp.domain.util.formatAmount
import kotlinx.coroutines.flow.first
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
    var taxRateInput by remember { mutableStateOf("0") }
    var draftItems by remember { mutableStateOf<List<InvoiceItemEntity>>(emptyList()) }
    var quantityInputs by remember { mutableStateOf<Map<Long, String>>(emptyMap()) }
    var priceInputs by remember { mutableStateOf<Map<Long, String>>(emptyMap()) }
    var loading by remember { mutableStateOf(true) }
    var saving by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(invoiceId) {
        loading = true
        error = null
        val loadedInvoice = repository.getInvoiceById(invoiceId)
        if (loadedInvoice == null) {
            error = "تعذر العثور على الفاتورة"
        } else {
            invoice = loadedInvoice
            customerName = loadedInvoice.customerName
            notes = loadedInvoice.notes
            taxRateInput = loadedInvoice.taxRate.toString().removeSuffix(".0")
            draftItems = repository.observeItems(invoiceId).first()
            quantityInputs = emptyMap()
            priceInputs = emptyMap()
        }
        loading = false
    }

    if (loading) {
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val current = invoice
    if (current == null) {
        Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center) {
            Text(error ?: "تعذر تحميل الفاتورة")
        }
        return
    }

    val subtotal = draftItems.sumOf { it.quantity * it.unitPrice }
    val taxRate = taxRateInput.replace(",", ".").toDoubleOrNull()?.coerceAtLeast(0.0) ?: 0.0
    val tax = InvoiceCalculator.tax(subtotal, taxRate)
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

        Text("البنود", style = MaterialTheme.typography.titleMedium)

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(
                draftItems,
                key = { index, item -> if (item.id != 0L) item.id else "new-$index" }
            ) { index, item ->
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(
                        value = item.description,
                        onValueChange = { value ->
                            draftItems = draftItems.toMutableList().also {
                                it[index] = item.copy(description = value)
                            }
                        },
                        label = { Text("وصف البند") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val quantityKey = if (item.id != 0L) item.id else index.toLong().unaryMinus()
                        val quantityText = quantityInputs[quantityKey]
                            ?: item.quantity.toInt().toString()
                        OutlinedTextField(
                            value = quantityText,
                            onValueChange = { value ->
                                val digitsOnly = value.filter(Char::isDigit)
                                quantityInputs = quantityInputs + (quantityKey to digitsOnly)
                                digitsOnly.toIntOrNull()?.takeIf { it >= 0 }?.let { quantity ->
                                    draftItems = draftItems.toMutableList().also {
                                        it[index] = item.copy(
                                            quantity = quantity.toDouble(),
                                            total = quantity * item.unitPrice
                                        )
                                    }
                                }
                            },
                            label = { Text("الكمية") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        val priceKey = quantityKey
                        val priceText = priceInputs[priceKey] ?: formatAmount(item.unitPrice)
                        OutlinedTextField(
                            value = priceText,
                            onValueChange = { value ->
                                val normalized = value.filter { it.isDigit() || it == '.' || it == ',' }
                                priceInputs = priceInputs + (priceKey to normalized)
                                normalized.replace(",", "").toDoubleOrNull()
                                    ?.takeIf { it >= 0.0 }
                                    ?.let { price ->
                                        draftItems = draftItems.toMutableList().also {
                                            it[index] = item.copy(
                                                unitPrice = price,
                                                total = item.quantity * price
                                            )
                                        }
                                    }
                            },
                            label = { Text("سعر الوحدة") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                    }
                    Text("الإجمالي: ${formatAmount(item.quantity * item.unitPrice)}")
                    OutlinedButton(
                        onClick = {
                            draftItems = draftItems.filterIndexed { itemIndex, _ -> itemIndex != index }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("حذف البند")
                    }
                }
            }

            item {
                Button(
                    onClick = {
                        draftItems = draftItems + InvoiceItemEntity(
                            invoiceId = invoiceId,
                            description = "",
                            quantity = 1.0,
                            unitPrice = 0.0,
                            total = 0.0
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("إضافة بند")
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
        Text("الإجمالي: ${formatAmount(total)}", style = MaterialTheme.typography.titleLarge)
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        Button(
            enabled = !saving && customerName.isNotBlank() &&
                draftItems.isNotEmpty() && draftItems.all {
                    it.description.isNotBlank() && it.quantity > 0 && it.quantity % 1.0 == 0.0 && it.unitPrice >= 0
                },
            onClick = {
                saving = true
                error = null
                val normalizedItems = draftItems.map {
                    it.copy(
                        invoiceId = invoiceId,
                        description = it.description.trim(),
                        total = it.quantity * it.unitPrice
                    )
                }
                scope.launch {
                    try {
                        repository.updateInvoiceWithItems(
                            current.copy(
                                customerName = customerName.trim(),
                                subtotal = subtotal,
                                taxRate = taxRate,
                                tax = tax,
                                total = total,
                                notes = notes.trim()
                            ),
                            normalizedItems
                        )
                        onSaved()
                    } catch (t: Throwable) {
                        error = t.message ?: "تعذر حفظ التعديلات"
                    } finally {
                        saving = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            if (saving) CircularProgressIndicator() else Text("حفظ التعديلات")
        }
    }
}
