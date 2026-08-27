package com.yhyyzray388.invoiceapp.domain.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object InvoiceNumberGenerator {
    fun generate(): String {
        val format = SimpleDateFormat("yyyyMMdd-HHmmss-SSS", Locale.US)
        return "INV-${format.format(Date())}"
    }
}
