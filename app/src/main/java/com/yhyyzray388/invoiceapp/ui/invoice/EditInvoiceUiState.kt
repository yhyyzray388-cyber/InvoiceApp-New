package com.yhyyzray388.invoiceapp.ui.invoice

import com.yhyyzray388.invoiceapp.domain.model.InvoiceItem

data class EditInvoiceUiState(
    val invoiceNumber: String = "",
    val customerName: String = "",
    val taxRatePercent: String = "0",
    val notes: String = "",
    val items: List<InvoiceItem> = emptyList(),
    val initialized: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null
)
