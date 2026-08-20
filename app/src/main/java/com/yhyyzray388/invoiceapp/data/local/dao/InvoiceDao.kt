package com.yhyyzray388.invoiceapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.yhyyzray388.invoiceapp.data.local.entity.InvoiceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InvoiceDao {

    @Insert
    suspend fun insert(invoice: InvoiceEntity): Long

    @Update
    suspend fun update(invoice: InvoiceEntity)

    @Delete
    suspend fun delete(invoice: InvoiceEntity)

    @Query("SELECT * FROM invoices ORDER BY issueDate DESC")
    fun getAllInvoices(): Flow<List<InvoiceEntity>>

    @Query("SELECT * FROM invoices WHERE id = :id")
    suspend fun getInvoiceById(id: Long): InvoiceEntity?

    @Query("DELETE FROM invoices")
    suspend fun deleteAllInvoices()
}
