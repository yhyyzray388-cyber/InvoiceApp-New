package com.yhyyzray388.invoiceapp.ui.invoice

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CreateInvoiceScreen(
    onSave: () -> Unit = {}
) {
    var customerName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("1") }
    var price by remember { mutableStateOf("0") }

    val total = (quantity.toDoubleOrNull() ?: 0.0) * (price.toDoubleOrNull() ?: 0.0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("فاتورة جديدة", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = customerName,
            onValueChange = { customerName = it },
            label = { Text("اسم العميل") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("وصف المنتج") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = quantity,
            onValueChange = { quantity = it },
            label = { Text("الكمية") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("السعر") },
            modifier = Modifier.fillMaxWidth()
        )

        Text("الإجمالي: $total")

        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("حفظ الفاتورة")
        }
    }
}
