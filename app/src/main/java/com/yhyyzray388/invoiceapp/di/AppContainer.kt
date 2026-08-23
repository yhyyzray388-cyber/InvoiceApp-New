package com.yhyyzray388.invoiceapp.di

import android.content.Context
import androidx.room.Room
import com.yhyyzray388.invoiceapp.data.local.database.AppDatabase
import com.yhyyzray388.invoiceapp.data.repository.InvoiceItemRepository
import com.yhyyzray388.invoiceapp.data.repository.InvoiceRepository

class AppContainer(context: Context) {
    private val database: AppDatabase =
        Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "invoice_app.db"
        )
            .fallbackToDestructiveMigration()
            .build()

    val invoiceRepository = InvoiceRepository(database.invoiceDao())
    val invoiceItemRepository = InvoiceItemRepository(database.invoiceItemDao())
}
