package com.yhyyzray388.invoiceapp.data.repository

import com.yhyyzray388.invoiceapp.data.local.dao.InvoiceDao
import com.yhyyzray388.invoiceapp.data.local.entity.InvoiceEntity
import kotlinx.coroutines.flow.Flow

class InvoiceRepository(
    private val invoiceDao: InvoiceDao
) {

    val allInvoices: Flow<List<InvoiceEntity>> =
        invoiceDao.getAllInvoices()

    suspend fun getInvoiceById(id: Long): InvoiceEntity? =
        invoiceDao.getInvoiceById(id)

    suspend fun insert(invoice: InvoiceEntity): Long =
        invoiceDao.insert(invoice)

    suspend fun update(invoice: InvoiceEntity) =
        invoiceDao.update(invoice)

    suspend fun delete(invoice: InvoiceEntity) =
        invoiceDao.delete(invoice)

    suspend fun deleteAllInvoices() =
        invoiceDao.deleteAllInvoices()
}
