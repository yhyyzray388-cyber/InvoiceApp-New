package com.yhyyzray388.invoiceapp.data.repository

import android.content.Context
import com.yhyyzray388.invoiceapp.data.local.AppDatabaseProvider

object RepositoryProvider {
    fun invoiceRepository(context: Context): InvoiceRepository {
        val db = AppDatabaseProvider.get(context)
        return InvoiceRepository(db.invoiceDao())
    }
}
