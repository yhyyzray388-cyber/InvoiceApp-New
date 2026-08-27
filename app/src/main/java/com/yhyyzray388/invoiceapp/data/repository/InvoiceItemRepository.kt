package com.yhyyzray388.invoiceapp.data.repository

import com.yhyyzray388.invoiceapp.data.local.dao.InvoiceItemDao
import com.yhyyzray388.invoiceapp.data.local.entity.InvoiceItemEntity
import kotlinx.coroutines.flow.Flow

class InvoiceItemRepository(
    private val dao: InvoiceItemDao
) {
    fun observeForInvoice(invoiceId: Long): Flow<List<InvoiceItemEntity>> =
        dao.getItemsForInvoice(invoiceId)

    suspend fun insert(item: InvoiceItemEntity): Long = dao.insert(item)

    suspend fun update(item: InvoiceItemEntity) = dao.update(item)

    suspend fun delete(item: InvoiceItemEntity) = dao.delete(item)

    suspend fun deleteForInvoice(invoiceId: Long) = dao.deleteItemsForInvoice(invoiceId)
}
