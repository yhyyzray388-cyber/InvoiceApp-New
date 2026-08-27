package com.yhyyzray388.invoiceapp.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.yhyyzray388.invoiceapp.data.local.InvoiceWithItems
import kotlinx.coroutines.flow.Flow

@Dao
interface InvoiceWithItemsDao {
    @Transaction
    @Query("SELECT * FROM invoices WHERE id = :invoiceId")
    fun observeInvoiceWithItems(invoiceId: Long): Flow<InvoiceWithItems?>
}
