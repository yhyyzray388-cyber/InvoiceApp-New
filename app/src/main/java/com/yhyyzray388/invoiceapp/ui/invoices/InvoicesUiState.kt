package com.yhyyzray388.invoiceapp.ui.invoices

import com.yhyyzray388.invoiceapp.data.local.entity.InvoiceEntity

data class InvoicesUiState(
    val invoices: List<InvoiceEntity> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
