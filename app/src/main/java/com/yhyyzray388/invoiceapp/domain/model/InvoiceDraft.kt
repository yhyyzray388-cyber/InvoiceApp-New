package com.yhyyzray388.invoiceapp.domain.model

data class InvoiceItemDraft(
    val description: String = "",
    val quantity: Double = 1.0,
    val unitPrice: Double = 0.0
) {
    val total: Double get() = quantity * unitPrice
}

data class InvoiceDraft(
    val invoiceNumber: String = "",
    val customerName: String = "",
    val issueDate: Long = System.currentTimeMillis(),
    val items: List<InvoiceItemDraft> = emptyList(),
    val taxRate: Double = 0.0,
    val notes: String = ""
) {
    val subtotal: Double get() = InvoiceCalculator.subtotal(items)
    val tax: Double get() = InvoiceCalculator.tax(subtotal, taxRate)
    val total: Double get() = InvoiceCalculator.total(items, taxRate)
}
