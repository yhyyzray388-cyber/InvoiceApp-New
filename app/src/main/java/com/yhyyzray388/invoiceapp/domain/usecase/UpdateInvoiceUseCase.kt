package com.yhyyzray388.invoiceapp.domain.usecase

import com.yhyyzray388.invoiceapp.data.local.entity.InvoiceEntity
import com.yhyyzray388.invoiceapp.data.local.entity.InvoiceItemEntity
import com.yhyyzray388.invoiceapp.data.repository.InvoiceItemRepository
import com.yhyyzray388.invoiceapp.data.repository.InvoiceRepository

class UpdateInvoiceUseCase(
    private val invoiceRepository: InvoiceRepository,
    private val itemRepository: InvoiceItemRepository
) {
    suspend operator fun invoke(invoice: InvoiceEntity, items: List<InvoiceItemEntity>) {
        invoiceRepository.update(invoice)
        itemRepository.deleteItemsForInvoice(invoice.id)
        items.forEach { itemRepository.insert(it.copy(id = 0, invoiceId = invoice.id)) }
    }
}
