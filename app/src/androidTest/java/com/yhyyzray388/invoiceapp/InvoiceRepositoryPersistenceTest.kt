package com.yhyyzray388.invoiceapp.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yhyyzray388.invoiceapp.data.local.database.AppDatabase
import com.yhyyzray388.invoiceapp.data.local.entity.InvoiceEntity
import com.yhyyzray388.invoiceapp.data.local.entity.InvoiceItemEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InvoiceRepositoryPersistenceTest {
    private lateinit var database: AppDatabase
    private lateinit var repository: InvoiceRepository

    @Before
    fun createDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = InvoiceRepository(database)
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun saveInvoiceWithItemsPersistsHeaderAndEveryItem() = runBlocking {
        val invoiceId = repository.saveInvoiceWithItems(
            invoice(),
            listOf(
                item("دفتر", quantity = 2.0, unitPrice = 1500.0, total = 3000.0),
                item("قلم", quantity = 3.0, unitPrice = 500.0, total = 1500.0)
            )
        )

        val saved = repository.getInvoiceById(invoiceId)
        assertNotNull(saved)
        assertEquals("عميل الاختبار", saved?.customerName)
        assertEquals(4500.0, saved?.subtotal ?: 0.0, 0.000001)

        val savedItems = repository.observeItems(invoiceId).first()
        assertEquals(2, savedItems.size)
        assertEquals(listOf("دفتر", "قلم"), savedItems.map { it.description })
        assertEquals(4500.0, savedItems.sumOf { it.total }, 0.000001)
    }

    @Test
    fun updateInvoiceWithItemsReplacesOldItemsAndUpdatesHeader() = runBlocking {
        val invoiceId = repository.saveInvoiceWithItems(
            invoice(),
            listOf(item("قديم", quantity = 1.0, unitPrice = 100.0, total = 100.0))
        )
        val existing = repository.getInvoiceById(invoiceId)!!
        repository.updateInvoiceWithItems(
            existing.copy(
                customerName = "عميل معدل",
                subtotal = 2500.0,
                taxRate = 10.0,
                tax = 250.0,
                total = 2750.0
            ),
            listOf(
                item("بند معدل", quantity = 2.0, unitPrice = 1000.0, total = 2000.0),
                item("بند جديد", quantity = 1.0, unitPrice = 500.0, total = 500.0)
            )
        )

        val updated = repository.getInvoiceById(invoiceId)!!
        assertEquals("عميل معدل", updated.customerName)
        assertEquals(2500.0, updated.subtotal, 0.000001)
        assertEquals(10.0, updated.taxRate, 0.000001)
        assertEquals(250.0, updated.tax, 0.000001)
        assertEquals(2750.0, updated.total, 0.000001)

        val updatedItems = repository.observeItems(invoiceId).first()
        assertEquals(2, updatedItems.size)
        assertEquals(listOf("بند معدل", "بند جديد"), updatedItems.map { it.description })
        assertEquals(2500.0, updatedItems.sumOf { it.total }, 0.000001)
    }

    @Test
    fun deletingInvoiceCascadesToItsItems() = runBlocking {
        val invoiceId = repository.saveInvoiceWithItems(
            invoice(),
            listOf(
                item("بند 1", quantity = 1.0, unitPrice = 200.0, total = 200.0),
                item("بند 2", quantity = 2.0, unitPrice = 300.0, total = 600.0)
            )
        )
        val savedInvoice = repository.getInvoiceById(invoiceId)!!
        assertEquals(2, repository.observeItems(invoiceId).first().size)

        repository.delete(savedInvoice)

        assertNull(repository.getInvoiceById(invoiceId))
        assertEquals(0, repository.observeItems(invoiceId).first().size)
    }

    private fun invoice() = InvoiceEntity(
        invoiceNumber = "ROOM-TEST-001",
        customerName = "عميل الاختبار",
        issueDate = 1_700_000_000_000,
        subtotal = 4500.0,
        taxRate = 0.0,
        tax = 0.0,
        total = 4500.0
    )

    private fun item(
        description: String,
        quantity: Double,
        unitPrice: Double,
        total: Double
    ) = InvoiceItemEntity(
        invoiceId = 0,
        description = description,
        quantity = quantity,
        unitPrice = unitPrice,
        total = total
    )
}
