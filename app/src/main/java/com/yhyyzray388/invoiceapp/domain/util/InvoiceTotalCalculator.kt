package com.yhyyzray388.invoiceapp.domain.util

import com.yhyyzray388.invoiceapp.domain.model.InvoiceCalculator
import com.yhyyzray388.invoiceapp.domain.model.InvoiceItemDraft

/**
 * Compatibility facade. All invoice arithmetic is owned by InvoiceCalculator.
 */
object InvoiceTotalCalculator {
    fun subtotal(items: List<InvoiceItemDraft>): Double =
        InvoiceCalculator.subtotal(items)

    fun tax(subtotal: Double, taxRate: Double): Double =
        InvoiceCalculator.tax(subtotal, taxRate)

    fun total(items: List<InvoiceItemDraft>, taxRate: Double): Double =
        InvoiceCalculator.total(items, taxRate)
}
