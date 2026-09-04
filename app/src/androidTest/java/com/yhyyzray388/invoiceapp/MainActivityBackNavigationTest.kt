package com.yhyyzray388.invoiceapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yhyyzray388.invoiceapp.data.local.entity.InvoiceEntity
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityBackNavigationTest {
    @get:Rule
    @JvmField
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val repository
        get() = (composeTestRule.activity.application as InvoiceAppApplication)
            .appContainer.invoiceRepository

    @Before
    fun clearInvoices() = runBlocking { repository.deleteAllInvoices() }

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
    fun backFromEditReturnsToInvoiceListAndCanReopenSameInvoice() = runBlocking {
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
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText("TEST-BACK-001").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("TEST-BACK-001").performClick()
        composeTestRule.onNodeWithText("تعديل الفاتورة").assertIsDisplayed()
        composeTestRule.activity.runOnUiThread {
            composeTestRule.activity.onBackPressedDispatcher.onBackPressed()
        }
        composeTestRule.onNodeWithText("الفواتير").assertIsDisplayed()
        composeTestRule.onNodeWithText("TEST-BACK-001").performClick()
        composeTestRule.onNodeWithText("تعديل الفاتورة").assertIsDisplayed()
    }

    @Test
    fun deleteInvoiceRemovesItFromListAndRoom() = runBlocking {
        val invoiceId = repository.insert(
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
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText("TEST-DELETE-001").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("TEST-DELETE-001").performClick()
        composeTestRule.onNodeWithText("حذف").performClick()
        composeTestRule.onNodeWithText("حذف الفاتورة").assertIsDisplayed()
        composeTestRule.onNodeWithText("إلغاء").assertIsDisplayed()
        composeTestRule.onAllNodesWithText("حذف")[1].performClick()
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText("TEST-DELETE-001").fetchSemanticsNodes().isEmpty()
        }
        assert(repository.getInvoiceById(invoiceId) == null)
        composeTestRule.onNodeWithText("لا توجد فواتير بعد").assertIsDisplayed()
    }
}
