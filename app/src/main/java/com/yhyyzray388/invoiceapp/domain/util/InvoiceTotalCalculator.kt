package com.yhyyzray388.invoiceapp.domain.util

import com.yhyyzray388.invoiceapp.domain.model.InvoiceItemDraft

object InvoiceTotalCalculator {
    fun subtotal(items: List<InvoiceItemDraft>): Double =
        items.sumOf { it.quantity * it.unitPrice }

    fun tax(subtotal: Double, taxRate: Double): Double =
        subtotal * (taxRate / 100.0)

    fun total(items: List<InvoiceItemDraft>, taxRate: Double): Double {
        val subtotal = subtotal(items)
        return subtotal + tax(subtotal, taxRate)
    }
}
