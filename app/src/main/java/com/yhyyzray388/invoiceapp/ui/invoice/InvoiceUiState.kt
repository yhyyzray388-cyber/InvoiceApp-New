package com.yhyyzray388.invoiceapp.ui.invoice

import com.yhyyzray388.invoiceapp.domain.model.InvoiceItemDraft

data class InvoiceUiState(
    val customerName: String = "",
    val items: List<InvoiceItemDraft> = emptyList()
) {
    val total: Double
        get() = items.sumOf { it.total }
}
