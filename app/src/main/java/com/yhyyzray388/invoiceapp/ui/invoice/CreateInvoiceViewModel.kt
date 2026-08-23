package com.yhyyzray388.invoiceapp.ui.invoice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yhyyzray388.invoiceapp.data.local.entity.InvoiceEntity
import com.yhyyzray388.invoiceapp.data.local.entity.InvoiceItemEntity
import com.yhyyzray388.invoiceapp.domain.model.InvoiceItem
import com.yhyyzray388.invoiceapp.domain.usecase.CalculateInvoiceTotalsUseCase
import com.yhyyzray388.invoiceapp.domain.usecase.CreateInvoiceUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class CreateInvoiceViewModel(
    private val createInvoice: CreateInvoiceUseCase,
    private val calculateTotals: CalculateInvoiceTotalsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateInvoiceUiState())
    val uiState: StateFlow<CreateInvoiceUiState> = _uiState.asStateFlow()

    fun setInvoiceNumber(value: String) { _uiState.value = _uiState.value.copy(invoiceNumber = value, error = null) }
    fun setCustomerName(value: String) { _uiState.value = _uiState.value.copy(customerName = value, error = null) }
    fun setNotes(value: String) { _uiState.value = _uiState.value.copy(notes = value, error = null) }
    fun setTaxRate(value: String) { _uiState.value = _uiState.value.copy(taxRatePercent = value, error = null) }

    fun addItem() {
        _uiState.value = _uiState.value.copy(
            items = _uiState.value.items + InvoiceItem(description = "", quantity = 1.0, unitPrice = 0.0)
        )
    }

    fun updateItem(index: Int, item: InvoiceItem) {
        val items = _uiState.value.items.toMutableList()
        if (index in items.indices) {
            items[index] = item
            _uiState.value = _uiState.value.copy(items = items, error = null)
        }
    }

    fun removeItem(index: Int) {
        val items = _uiState.value.items.toMutableList()
        if (index in items.indices) {
            items.removeAt(index)
            _uiState.value = _uiState.value.copy(items = items)
        }
    }

    fun save() {
        val state = _uiState.value
        val invoiceNumber = state.invoiceNumber.trim()
        val customerName = state.customerName.trim()
        val taxRate = state.taxRatePercent.toDoubleOrNull()

        if (invoiceNumber.isBlank()) return setError("رقم الفاتورة مطلوب")
        if (customerName.isBlank()) return setError("اسم العميل مطلوب")
        if (taxRate == null || taxRate < 0) return setError("نسبة الضريبة غير صحيحة")
        if (state.items.isEmpty()) return setError("أضف بندًا واحدًا على الأقل")
        if (state.items.any { it.description.isBlank() || it.quantity <= 0 || it.unitPrice < 0 }) {
            return setError("تحقق من بيانات بنود الفاتورة")
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isSaving = true, error = null)
            runCatching {
                val totals = calculateTotals(state.items, taxRate)
                val invoice = InvoiceEntity(
                    invoiceNumber = invoiceNumber,
                    customerName = customerName,
                    issueDate = System.currentTimeMillis(),
                    subtotal = totals.subtotal,
                    tax = totals.tax,
                    total = totals.total,
                    notes = state.notes.trim()
                )
                val items = state.items.map {
                    InvoiceItemEntity(
                        invoiceId = 0,
                        description = it.description.trim(),
                        quantity = it.quantity,
                        unitPrice = it.unitPrice,
                        discount = it.discount
                    )
                }
                createInvoice(invoice, items)
            }.onSuccess { id ->
                _uiState.value = _uiState.value.copy(isSaving = false, savedInvoiceId = id)
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(isSaving = false, error = error.message ?: "تعذر حفظ الفاتورة")
            }
        }
    }

    private fun setError(message: String) {
        _uiState.value = _uiState.value.copy(error = message, isSaving = false)
    }

    class Factory(
        private val createInvoice: CreateInvoiceUseCase,
        private val calculateTotals: CalculateInvoiceTotalsUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            if (modelClass.isAssignableFrom(CreateInvoiceViewModel::class.java)) {
                CreateInvoiceViewModel(createInvoice, calculateTotals) as T
            } else error("Unknown ViewModel class: ${modelClass.name}")
    }
}
