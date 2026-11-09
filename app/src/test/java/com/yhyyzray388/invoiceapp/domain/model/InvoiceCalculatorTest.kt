package com.yhyyzray388.invoiceapp.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class InvoiceCalculatorTest {

    @Test
    fun subtotalSumsAllItemTotals() {
        val items = listOf(
            InvoiceItemDraft(description = "A", quantity = 2.0, unitPrice = 1500.0),
            InvoiceItemDraft(description = "B", quantity = 3.0, unitPrice = 2000.0)
        )

        assertEquals(9000.0, InvoiceCalculator.subtotal(items), 0.000001)
    }

    @Test
    fun emptyInvoiceHasZeroAmounts() {
        assertEquals(0.0, InvoiceCalculator.subtotal(emptyList()), 0.0)
        assertEquals(0.0, InvoiceCalculator.tax(0.0, 15.0), 0.0)
        assertEquals(0.0, InvoiceCalculator.total(emptyList(), 15.0), 0.0)
    }

    @Test
    fun calculatesTaxAndTotalForMultipleItems() {
        val items = listOf(
            InvoiceItemDraft(description = "A", quantity = 2.0, unitPrice = 1000.0),
            InvoiceItemDraft(description = "B", quantity = 1.0, unitPrice = 500.0)
        )

        assertEquals(2500.0, InvoiceCalculator.subtotal(items), 0.000001)
        assertEquals(375.0, InvoiceCalculator.tax(2500.0, 15.0), 0.000001)
        assertEquals(2875.0, InvoiceCalculator.total(items, 15.0), 0.000001)
    }

    @Test
    fun negativeTaxRateIsTreatedAsZero() {
        val items = listOf(InvoiceItemDraft(quantity = 2.0, unitPrice = 750.0))

        assertEquals(0.0, InvoiceCalculator.tax(1500.0, -5.0), 0.0)
        assertEquals(1500.0, InvoiceCalculator.total(items, -5.0), 0.000001)
    }

    @Test
    fun nonFiniteTaxRateIsTreatedAsZero() {
        val items = listOf(InvoiceItemDraft(quantity = 1.0, unitPrice = 1200.0))

        assertEquals(0.0, InvoiceCalculator.tax(1200.0, Double.NaN), 0.0)
        assertEquals(1200.0, InvoiceCalculator.total(items, Double.POSITIVE_INFINITY), 0.000001)
    }

    @Test
    fun draftAndCompatibilityCalculatorUseSameArithmetic() {
        val items = listOf(
            InvoiceItemDraft(description = "A", quantity = 4.0, unitPrice = 1250.0)
        )
        val draft = InvoiceDraft(items = items, taxRate = 5.0)

        assertEquals(5000.0, draft.subtotal, 0.000001)
        assertEquals(250.0, draft.tax, 0.000001)
        assertEquals(5250.0, draft.total, 0.000001)
        assertEquals(draft.subtotal, com.yhyyzray388.invoiceapp.domain.util.InvoiceTotalCalculator.subtotal(items), 0.000001)
        assertEquals(draft.tax, com.yhyyzray388.invoiceapp.domain.util.InvoiceTotalCalculator.tax(draft.subtotal, 5.0), 0.000001)
        assertEquals(draft.total, com.yhyyzray388.invoiceapp.domain.util.InvoiceTotalCalculator.total(items, 5.0), 0.000001)
    }
}
