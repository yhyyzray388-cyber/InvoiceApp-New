package com.yhyyzray388.invoiceapp.ui.invoice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yhyyzray388.invoiceapp.data.local.entity.InvoiceEntity
import com.yhyyzray388.invoiceapp.data.local.entity.InvoiceItemEntity
import com.yhyyzray388.invoiceapp.data.repository.InvoiceRepository
import com.yhyyzray388.invoiceapp.domain.model.InvoiceDraft
import com.yhyyzray388.invoiceapp.domain.util.InvoiceNumberGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InvoiceViewModel(
    private val repository: InvoiceRepository
) : ViewModel() {
    private val _saving = MutableStateFlow(false)
    val saving: StateFlow<Boolean> = _saving.asStateFlow()

    private val _savedInvoiceId = MutableStateFlow<Long?>(null)
    val savedInvoiceId: StateFlow<Long?> = _savedInvoiceId.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun save(draft: InvoiceDraft) {
        if (_saving.value) return
        _error.value = null
        viewModelScope.launch {
            _saving.value = true
            runCatching {
                val invoice = InvoiceEntity(
                    invoiceNumber = draft.invoiceNumber.ifBlank { InvoiceNumberGenerator.generate() },
                    customerName = draft.customerName.trim(),
                    issueDate = draft.issueDate,
                    subtotal = draft.subtotal,
                    tax = draft.tax,
                    total = draft.total,
                    notes = draft.notes.trim()
                )
                val items = draft.items.map {
                    InvoiceItemEntity(
                        invoiceId = 0,
                        description = it.description.trim(),
                        quantity = it.quantity,
                        unitPrice = it.unitPrice,
                        total = it.total
                    )
                }
                repository.saveInvoiceWithItems(invoice, items)
            }.onSuccess { id ->
                _savedInvoiceId.value = id
            }.onFailure { throwable ->
                _error.value = throwable.message ?: "تعذر حفظ الفاتورة"
            }
            _saving.value = false
        }
    }

    fun clearSavedState() {
        _savedInvoiceId.value = null
    }

    class Factory(private val repository: InvoiceRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(InvoiceViewModel::class.java)) {
                return InvoiceViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
