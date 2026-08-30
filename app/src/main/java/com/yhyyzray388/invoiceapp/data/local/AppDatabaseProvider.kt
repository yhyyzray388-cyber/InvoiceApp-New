package com.yhyyzray388.invoiceapp.data.local

import android.content.Context
import androidx.room.Room
import com.yhyyzray388.invoiceapp.data.local.database.AppDatabase

object AppDatabaseProvider {
    @Volatile
    private var instance: AppDatabase? = null

    fun get(context: Context): AppDatabase {
        return instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "invoice_app.db"
            )
                .addMigrations(
                    AppDatabase.MIGRATION_1_2,
                    AppDatabase.MIGRATION_2_3
                )
                .build()
                .also { instance = it }
        }
    }
}
