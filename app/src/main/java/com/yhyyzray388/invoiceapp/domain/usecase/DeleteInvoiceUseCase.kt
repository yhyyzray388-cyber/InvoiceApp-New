package com.yhyyzray388.invoiceapp.domain.usecase

import com.yhyyzray388.invoiceapp.data.local.entity.InvoiceEntity
import com.yhyyzray388.invoiceapp.data.repository.InvoiceRepository

class DeleteInvoiceUseCase(
    private val repository: InvoiceRepository
) {
    suspend operator fun invoke(invoice: InvoiceEntity) {
        repository.delete(invoice)
    }
}
