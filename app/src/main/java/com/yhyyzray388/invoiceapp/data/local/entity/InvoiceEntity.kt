package com.yhyyzray388.invoiceapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "invoices")
data class InvoiceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val invoiceNumber: String,
    val customerName: String,
    val issueDate: Long,
    val subtotal: Double,
    val taxRate: Double = 0.0,
    val tax: Double,
    val total: Double,
    val notes: String = ""
)
