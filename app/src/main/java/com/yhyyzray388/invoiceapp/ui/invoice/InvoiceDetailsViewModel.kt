package com.yhyyzray388.invoiceapp.ui.invoice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yhyyzray388.invoiceapp.data.local.entity.InvoiceEntity
import com.yhyyzray388.invoiceapp.data.local.entity.InvoiceItemEntity
import com.yhyyzray388.invoiceapp.data.repository.InvoiceItemRepository
import com.yhyyzray388.invoiceapp.data.repository.InvoiceRepository
import com.yhyyzray388.invoiceapp.domain.usecase.DeleteInvoiceUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class InvoiceDetailsViewModel(
    private val invoiceRepository: InvoiceRepository,
    private val itemRepository: InvoiceItemRepository,
    private val deleteInvoice: DeleteInvoiceUseCase,
    invoiceId: Long
) : ViewModel() {
    private val _invoice = MutableStateFlow<InvoiceEntity?>(null)
    val invoice: StateFlow<InvoiceEntity?> = _invoice.asStateFlow()

    val items: StateFlow<List<InvoiceItemEntity>> =
        itemRepository.getItemsForInvoice(invoiceId).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    init {
        viewModelScope.launch { _invoice.value = invoiceRepository.getInvoiceById(invoiceId) }
    }

    fun delete(onDeleted: () -> Unit) {
        val current = _invoice.value ?: return
        viewModelScope.launch {
            deleteInvoice(current)
            onDeleted()
        }
    }

    class Factory(
        private val invoiceRepository: InvoiceRepository,
        private val itemRepository: InvoiceItemRepository,
        private val deleteInvoice: DeleteInvoiceUseCase,
        private val invoiceId: Long
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            if (modelClass.isAssignableFrom(InvoiceDetailsViewModel::class.java)) {
                InvoiceDetailsViewModel(invoiceRepository, itemRepository, deleteInvoice, invoiceId) as T
            } else error("Unknown ViewModel class: ${modelClass.name}")
    }
}
