package com.yhyyzray388.invoiceapp.data.repository

import androidx.room.withTransaction
import com.yhyyzray388.invoiceapp.data.local.database.AppDatabase
import com.yhyyzray388.invoiceapp.data.local.entity.InvoiceEntity
import com.yhyyzray388.invoiceapp.data.local.entity.InvoiceItemEntity
import kotlinx.coroutines.flow.Flow

class InvoiceRepository(
    private val database: AppDatabase
) {
    private val invoiceDao = database.invoiceDao()
    private val itemDao = database.invoiceItemDao()

    val allInvoices: Flow<List<InvoiceEntity>> = invoiceDao.getAllInvoices()

    suspend fun getInvoiceById(id: Long): InvoiceEntity? = invoiceDao.getInvoiceById(id)

    suspend fun insert(invoice: InvoiceEntity): Long = invoiceDao.insert(invoice)

    suspend fun update(invoice: InvoiceEntity) = invoiceDao.update(invoice)

    suspend fun delete(invoice: InvoiceEntity) = invoiceDao.delete(invoice)

    suspend fun deleteItem(item: InvoiceItemEntity) = itemDao.delete(item)

    suspend fun deleteAllInvoices() = invoiceDao.deleteAllInvoices()

    suspend fun saveInvoiceWithItems(
        invoice: InvoiceEntity,
        items: List<InvoiceItemEntity>
    ): Long = database.withTransaction {
        val invoiceId = invoiceDao.insert(invoice)
        items.forEach { itemDao.insert(it.copy(id = 0, invoiceId = invoiceId)) }
        invoiceId
    }

    suspend fun updateInvoiceWithItems(
        invoice: InvoiceEntity,
        items: List<InvoiceItemEntity>
    ) = database.withTransaction {
        invoiceDao.update(invoice)
        itemDao.deleteItemsForInvoice(invoice.id)
        items.forEach { itemDao.insert(it.copy(id = 0, invoiceId = invoice.id)) }
    }

    fun observeItems(invoiceId: Long): Flow<List<InvoiceItemEntity>> =
        itemDao.getItemsForInvoice(invoiceId)
}
