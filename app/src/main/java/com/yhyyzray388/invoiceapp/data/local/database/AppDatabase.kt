package com.yhyyzray388.invoiceapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.yhyyzray388.invoiceapp.data.local.dao.InvoiceDao
import com.yhyyzray388.invoiceapp.data.local.dao.InvoiceItemDao
import com.yhyyzray388.invoiceapp.data.local.entity.InvoiceEntity
import com.yhyyzray388.invoiceapp.data.local.entity.InvoiceItemEntity

@Database(
    entities = [InvoiceEntity::class, InvoiceItemEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun invoiceDao(): InvoiceDao
    abstract fun invoiceItemDao(): InvoiceItemDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """CREATE TABLE IF NOT EXISTS invoice_items (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        invoiceId INTEGER NOT NULL,
                        description TEXT NOT NULL,
                        quantity REAL NOT NULL,
                        unitPrice REAL NOT NULL,
                        total REAL NOT NULL,
                        FOREIGN KEY(invoiceId) REFERENCES invoices(id) ON DELETE CASCADE
                    )"""
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_invoice_items_invoiceId ON invoice_items(invoiceId)")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE invoices ADD COLUMN taxRate REAL NOT NULL DEFAULT 0.0")
                db.execSQL(
                    "UPDATE invoices SET taxRate = CASE WHEN subtotal > 0 THEN (tax * 100.0 / subtotal) ELSE 0.0 END"
                )
            }
        }
    }
}
