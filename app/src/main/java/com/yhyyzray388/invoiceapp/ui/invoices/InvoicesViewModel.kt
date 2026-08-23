package com.yhyyzray388.invoiceapp.ui.invoices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yhyyzray388.invoiceapp.domain.usecase.GetInvoicesUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class InvoicesViewModel(
    getInvoices: GetInvoicesUseCase
) : ViewModel() {
    val uiState = getInvoices()
        .map { InvoicesUiState(invoices = it, isLoading = false) }
        .catch { emit(InvoicesUiState(isLoading = false, errorMessage = it.message)) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            InvoicesUiState()
        )
}
