package com.yhyyzray388.invoiceapp.domain.util

import java.text.NumberFormat
import java.util.Locale

private fun groupedNumber(value: Double, maxFractionDigits: Int): String {
    return NumberFormat.getNumberInstance(Locale.US).apply {
        isGroupingUsed = true
        minimumFractionDigits = 0
        maximumFractionDigits = maxFractionDigits
    }.format(value)
}

/** Whole Rial amounts: 6000.0 -> 6,000 and 60000.0 -> 60,000. */
fun formatAmount(value: Double): String = groupedNumber(value, 0)

/** Quantities: 1.0 -> 1, while preserving meaningful fractional quantities. */
fun formatQuantity(value: Double): String = groupedNumber(value, 2)
