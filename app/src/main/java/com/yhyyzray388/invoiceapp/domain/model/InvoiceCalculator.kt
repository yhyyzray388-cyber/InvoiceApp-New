package com.yhyyzray388.invoiceapp.domain.model

object InvoiceCalculator {
    fun subtotal(items: List<InvoiceItemDraft>): Double =
        items.sumOf { it.total }

    /**
     * Negative or non-finite tax rates are treated as zero.
     * This keeps every invoice entry point consistent and prevents NaN tax values.
     */
    fun tax(subtotal: Double, taxRate: Double): Double {
        val normalizedRate = taxRate
            .takeIf { it.isFinite() }
            ?.coerceAtLeast(0.0)
            ?: 0.0
        return subtotal * (normalizedRate / 100.0)
    }

    fun total(items: List<InvoiceItemDraft>, taxRate: Double): Double {
        val subtotal = subtotal(items)
        return subtotal + tax(subtotal, taxRate)
    }
}
