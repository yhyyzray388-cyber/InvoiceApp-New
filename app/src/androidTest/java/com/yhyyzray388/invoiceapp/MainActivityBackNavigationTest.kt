package com.yhyyzray388.invoiceapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yhyyzray388.invoiceapp.data.local.entity.InvoiceEntity
import kotlinx.coroutines.flow.first
import org.junit.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityBackNavigationTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val repository
        get() = (composeTestRule.activity.application as InvoiceAppApplication)
            .appContainer.invoiceRepository

    @Before
    fun clearInvoices() {
        runBlocking {
            repository.deleteAllInvoices()
        }
    }


    @Test
    fun createInvoicePersistsLineItemAndEditUpdatesIt() {
        composeTestRule.onNodeWithText("+").performClick()
        composeTestRule.onNodeWithText("اسم العميل").performTextInput("عميل التدفق")
        composeTestRule.onNodeWithText("وصف المنتج").performTextInput("دفتر")
        composeTestRule.onNodeWithText("الكمية").performTextClearance()
        composeTestRule.onNodeWithText("الكمية").performTextInput("2")
        composeTestRule.onNodeWithText("السعر").performTextInput("1500")
        composeTestRule.onNodeWithText("إضافة بند").performClick()

        composeTestRule.onNodeWithText("دفتر").assertExists()
        composeTestRule.onNodeWithText("حفظ الفاتورة").performClick()
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText("عميل التدفق")
                .fetchSemanticsNodes().isNotEmpty()
        }

        val invoice = runBlocking { repository.allInvoices.first().single() }
        val savedItems = runBlocking { repository.observeItems(invoice.id).first() }
        assertEquals(1, savedItems.size)
        assertEquals("دفتر", savedItems.single().description)
        assertEquals(2.0, savedItems.single().quantity, 0.000001)
        assertEquals(1500.0, savedItems.single().unitPrice, 0.000001)

        composeTestRule.onNodeWithText("عميل التدفق").performClick()
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText("تعديل الفاتورة")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("وصف البند").performTextClearance()
        composeTestRule.onNodeWithText("وصف البند").performTextInput("دفتر معدل")
        composeTestRule.onNodeWithText("الكمية").performTextClearance()
        composeTestRule.onNodeWithText("الكمية").performTextInput("3")
        composeTestRule.onNodeWithText("سعر الوحدة").performTextClearance()
        composeTestRule.onNodeWithText("سعر الوحدة").performTextInput("2000")
        composeTestRule.onNodeWithText("حفظ التعديلات").performClick()

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText("عميل التدفق")
                .fetchSemanticsNodes().isNotEmpty()
        }
        val updatedInvoice = runBlocking { repository.getInvoiceById(invoice.id)!! }
        val updatedItems = runBlocking { repository.observeItems(invoice.id).first() }
        assertEquals(6000.0, updatedInvoice.total, 0.000001)
        assertEquals(1, updatedItems.size)
        assertEquals("دفتر معدل", updatedItems.single().description)
        assertEquals(3.0, updatedItems.single().quantity, 0.000001)
        assertEquals(2000.0, updatedItems.single().unitPrice, 0.000001)
    }

    @Test
    fun backFromCreateReturnsToInvoiceList() {
        composeTestRule.onNodeWithText("+").performClick()
        composeTestRule.onNodeWithText("فاتورة جديدة").assertIsDisplayed()

        composeTestRule.activity.runOnUiThread {
            composeTestRule.activity.onBackPressedDispatcher.onBackPressed()
        }

        composeTestRule.onNodeWithText("الفواتير").assertIsDisplayed()
    }

    @Test
    fun backFromEditReturnsToInvoiceListAndCanReopenSameInvoice() {
        runBlocking {
            repository.insert(
                InvoiceEntity(
                    invoiceNumber = "TEST-BACK-001",
                    customerName = "عميل الاختبار",
                    issueDate = 1_700_000_000_000,
                    subtotal = 1000.0,
                    taxRate = 0.0,
                    tax = 0.0,
                    total = 1000.0
                )
            )
        }

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText("TEST-BACK-001")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("TEST-BACK-001").performClick()

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText("تعديل الفاتورة")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("تعديل الفاتورة").assertIsDisplayed()

        composeTestRule.activity.runOnUiThread {
            composeTestRule.activity.onBackPressedDispatcher.onBackPressed()
        }
        composeTestRule.onNodeWithText("الفواتير").assertIsDisplayed()

        composeTestRule.onNodeWithText("TEST-BACK-001").performClick()
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText("تعديل الفاتورة")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("تعديل الفاتورة").assertIsDisplayed()
    }

    @Test
    fun deleteInvoiceRemovesItFromListAndRoom() {
        val invoiceId = runBlocking {
            repository.insert(
                InvoiceEntity(
                    invoiceNumber = "TEST-DELETE-001",
                    customerName = "عميل الحذف",
                    issueDate = 1_700_000_000_000,
                    subtotal = 2500.0,
                    taxRate = 0.0,
                    tax = 0.0,
                    total = 2500.0
                )
            )
        }

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText("TEST-DELETE-001")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Delete is an action on the invoice card in the list; do not open the editor.
        composeTestRule.onNodeWithText("حذف").performClick()
        composeTestRule.onNodeWithText("حذف الفاتورة").assertIsDisplayed()
        composeTestRule.onNodeWithText("إلغاء").assertIsDisplayed()

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText("حذف").fetchSemanticsNodes().size >= 2
        }
        composeTestRule.onAllNodesWithText("حذف")[1].performClick()

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText("TEST-DELETE-001")
                .fetchSemanticsNodes().isEmpty()
        }

        assert(runBlocking { repository.getInvoiceById(invoiceId) } == null)
        composeTestRule.onNodeWithText("لا توجد فواتير بعد").assertIsDisplayed()
    }
}
