package com.yhyyzray388.invoiceapp.domain.usecase

import com.yhyyzray388.invoiceapp.data.repository.InvoiceRepository

class GetInvoicesUseCase(
    private val repository: InvoiceRepository
) {
    operator fun invoke() = repository.allInvoices
}
