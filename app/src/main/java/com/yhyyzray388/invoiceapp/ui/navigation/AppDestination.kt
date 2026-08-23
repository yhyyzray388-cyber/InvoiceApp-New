package com.yhyyzray388.invoiceapp.ui.navigation

sealed class AppDestination(val route: String) {
    data object Invoices : AppDestination("invoices")
    data object CreateInvoice : AppDestination("invoices/create")
    data object InvoiceDetails : AppDestination("invoices/{invoiceId}") {
        fun createRoute(invoiceId: Long) = "invoices/$invoiceId"
    }
    data object EditInvoice : AppDestination("invoices/{invoiceId}/edit") {
        fun createRoute(invoiceId: Long) = "invoices/$invoiceId/edit"
    }
}
