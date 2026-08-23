package com.yhyyzray388.invoiceapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.yhyyzray388.invoiceapp.data.local.dao.InvoiceDao
import com.yhyyzray388.invoiceapp.data.local.dao.InvoiceItemDao
import com.yhyyzray388.invoiceapp.data.local.entity.InvoiceEntity
import com.yhyyzray388.invoiceapp.data.local.entity.InvoiceItemEntity

@Database(
    entities = [InvoiceEntity::class, InvoiceItemEntity::class],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun invoiceDao(): InvoiceDao
    abstract fun invoiceItemDao(): InvoiceItemDao
}
