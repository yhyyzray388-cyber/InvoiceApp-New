package com.yhyyzray388.invoiceapp.ui.invoice

import com.yhyyzray388.invoiceapp.domain.model.InvoiceItem

 data class CreateInvoiceUiState(
    val invoiceNumber: String = "",
    val customerName: String = "",
    val notes: String = "",
    val taxRatePercent: String = "0",
    val items: List<InvoiceItem> = emptyList(),
    val isSaving: Boolean = false,
    val savedInvoiceId: Long? = null,
    val error: String? = null
)
