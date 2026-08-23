package com.yhyyzray388.invoiceapp.domain.model

data class InvoiceItem(
    val id: Long = 0,
    val invoiceId: Long = 0,
    val description: String,
    val quantity: Double,
    val unitPrice: Double,
    val discount: Double = 0.0
) {
    val lineTotal: Double
        get() = (quantity * unitPrice - discount).coerceAtLeast(0.0)
}
