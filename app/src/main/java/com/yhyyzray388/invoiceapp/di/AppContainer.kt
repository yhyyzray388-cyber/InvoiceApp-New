package com.yhyyzray388.invoiceapp.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
            .addMigrations(MIGRATION_1_2)
            .build()

    val invoiceRepository = InvoiceRepository(database.invoiceDao())
    val invoiceItemRepository = InvoiceItemRepository(database.invoiceItemDao())

    companion object {
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS invoice_items (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        invoiceId INTEGER NOT NULL,
                        description TEXT NOT NULL,
                        quantity REAL NOT NULL,
                        unitPrice REAL NOT NULL,
                        discount REAL NOT NULL,
                        FOREIGN KEY(invoiceId) REFERENCES invoices(id) ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_invoice_items_invoiceId ON invoice_items(invoiceId)")
            }
        }
    }
}
