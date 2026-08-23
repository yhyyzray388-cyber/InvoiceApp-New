package com.yhyyzray388.invoiceapp.domain.model

data class Invoice(
    val id: Long = 0,
    val invoiceNumber: String,
    val customerName: String,
    val issueDate: Long,
    val subtotal: Double,
    val tax: Double,
    val total: Double,
    val notes: String = ""
)
