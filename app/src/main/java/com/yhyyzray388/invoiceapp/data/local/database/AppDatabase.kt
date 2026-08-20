package com.yhyyzray388.invoiceapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.yhyyzray388.invoiceapp.data.local.dao.InvoiceDao
import com.yhyyzray388.invoiceapp.data.local.entity.InvoiceEntity

@Database(
    entities = [InvoiceEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun invoiceDao(): InvoiceDao
}
