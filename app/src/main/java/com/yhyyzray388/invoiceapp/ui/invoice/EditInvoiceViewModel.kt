package com.yhyyzray388.invoiceapp.ui.invoice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yhyyzray388.invoiceapp.data.local.entity.InvoiceEntity
import com.yhyyzray388.invoiceapp.data.local.entity.InvoiceItemEntity
import com.yhyyzray388.invoiceapp.data.repository.InvoiceItemRepository
import com.yhyyzray388.invoiceapp.data.repository.InvoiceRepository
import com.yhyyzray388.invoiceapp.domain.model.InvoiceItem
import com.yhyyzray388.invoiceapp.domain.usecase.CalculateInvoiceTotalsUseCase
import com.yhyyzray388.invoiceapp.domain.usecase.UpdateInvoiceUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditInvoiceViewModel(
    private val invoiceRepository: InvoiceRepository,
    private val itemRepository: InvoiceItemRepository,
    private val updateInvoice: UpdateInvoiceUseCase,
    private val calculateTotals: CalculateInvoiceTotalsUseCase,
    private val invoiceId: Long
) : ViewModel() {
    private val _uiState = MutableStateFlow(EditInvoiceUiState())
    val uiState: StateFlow<EditInvoiceUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val invoice = invoiceRepository.getInvoiceById(invoiceId)
            val items = itemRepository.getItemsForInvoice(invoiceId)
            items.collect { list ->
                if (invoice != null && !_uiState.value.initialized) {
                    val taxRate = if (invoice.subtotal > 0) invoice.tax / invoice.subtotal * 100 else 0.0
                    _uiState.value = EditInvoiceUiState(
                        invoiceNumber = invoice.invoiceNumber,
                        customerName = invoice.customerName,
                        notes = invoice.notes,
                        taxRatePercent = taxRate.toString(),
                        items = list.map { InvoiceItem(it.id, it.invoiceId, it.description, it.quantity, it.unitPrice, it.discount) },
                        initialized = true
                    )
                }
            }
        }
    }

    fun setInvoiceNumber(v: String) { _uiState.value = _uiState.value.copy(invoiceNumber = v, error = null) }
    fun setCustomerName(v: String) { _uiState.value = _uiState.value.copy(customerName = v, error = null) }
    fun setNotes(v: String) { _uiState.value = _uiState.value.copy(notes = v, error = null) }
    fun setTaxRate(v: String) { _uiState.value = _uiState.value.copy(taxRatePercent = v, error = null) }
    fun updateItem(index: Int, item: InvoiceItem) { _uiState.value = _uiState.value.copy(items = _uiState.value.items.toMutableList().also { if (index in it.indices) it[index] = item }) }
    fun addItem() { _uiState.value = _uiState.value.copy(items = _uiState.value.items + InvoiceItem(description = "", quantity = 1.0, unitPrice = 0.0)) }
    fun removeItem(index: Int) { _uiState.value = _uiState.value.copy(items = _uiState.value.items.toMutableList().also { if (index in it.indices) it.removeAt(index) }) }

    fun save(onSaved: () -> Unit) {
        val state = _uiState.value
        val taxRate = state.taxRatePercent.toDoubleOrNull()
        if (state.invoiceNumber.isBlank() || state.customerName.isBlank()) return setError("رقم الفاتورة واسم العميل مطلوبان")
        if (taxRate == null || taxRate < 0) return setError("نسبة الضريبة غير صحيحة")
        if (state.items.isEmpty()) return setError("أضف بندًا واحدًا على الأقل")
        if (state.items.any { it.description.isBlank() || it.quantity <= 0 || it.unitPrice < 0 }) return setError("تحقق من بيانات البنود")
        viewModelScope.launch {
            _uiState.value = state.copy(isSaving = true, error = null)
            runCatching {
                val old = invoiceRepository.getInvoiceById(invoiceId) ?: error("الفاتورة غير موجودة")
                val totals = calculateTotals(state.items, taxRate)
                val invoice = InvoiceEntity(invoiceId, state.invoiceNumber.trim(), state.customerName.trim(), old.issueDate, totals.subtotal, totals.tax, totals.total, state.notes.trim())
                val items = state.items.map { InvoiceItemEntity(0, invoiceId, it.description.trim(), it.quantity, it.unitPrice, it.discount) }
                updateInvoice(invoice, items)
            }.onSuccess { _uiState.value = _uiState.value.copy(isSaving = false); onSaved() }
             .onFailure { _uiState.value = _uiState.value.copy(isSaving = false, error = it.message ?: "تعذر تحديث الفاتورة") }
        }
    }
    private fun setError(message: String) { _uiState.value = _uiState.value.copy(error = message, isSaving = false) }

    class Factory(private val invoiceRepository: InvoiceRepository, private val itemRepository: InvoiceItemRepository, private val updateInvoice: UpdateInvoiceUseCase, private val calculateTotals: CalculateInvoiceTotalsUseCase, private val invoiceId: Long) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = if (modelClass.isAssignableFrom(EditInvoiceViewModel::class.java)) EditInvoiceViewModel(invoiceRepository, itemRepository, updateInvoice, calculateTotals, invoiceId) as T else error("Unknown ViewModel class: ${modelClass.name}")
    }
}
