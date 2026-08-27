package com.yhyyzray388.invoiceapp.data.local

import android.content.Context
import androidx.room.Room

object AppDatabaseProvider {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun get(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "invoice_database"
            ).build().also { INSTANCE = it }
        }
    }
}
