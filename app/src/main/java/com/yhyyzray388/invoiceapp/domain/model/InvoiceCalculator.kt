package com.yhyyzray388.invoiceapp.domain.model

object InvoiceCalculator {
    fun subtotal(items: List<InvoiceItemDraft>): Double = items.sumOf { it.total }

    fun tax(subtotal: Double, taxRate: Double): Double =
        subtotal * (taxRate.coerceAtLeast(0.0) / 100.0)

    fun total(items: List<InvoiceItemDraft>, taxRate: Double): Double {
        val subtotal = subtotal(items)
        return subtotal + tax(subtotal, taxRate)
    }
}
