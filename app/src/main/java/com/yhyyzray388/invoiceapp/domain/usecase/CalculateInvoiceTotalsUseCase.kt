package com.yhyyzray388.invoiceapp.domain.usecase

import com.yhyyzray388.invoiceapp.domain.model.InvoiceItem

class CalculateInvoiceTotalsUseCase {
    operator fun invoke(items: List<InvoiceItem>, taxRatePercent: Double): Totals {
        val subtotal = items.sumOf { it.lineTotal }
        val tax = subtotal * (taxRatePercent.coerceAtLeast(0.0) / 100.0)
        return Totals(subtotal = subtotal, tax = tax, total = subtotal + tax)
    }

    data class Totals(
        val subtotal: Double,
        val tax: Double,
        val total: Double
    )
}
