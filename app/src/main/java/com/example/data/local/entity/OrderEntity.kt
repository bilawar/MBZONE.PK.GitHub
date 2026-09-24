package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderNumber: String,
    val customerName: String,
    val phone: String,
    val address: String,
    val city: String,
    val paymentMethod: String, // CASH_ON_DELIVERY, JAZZCASH, EASYPAISA, BANK_TRANSFER
    val paymentStatus: String, // Unpaid (COD), Paid, Verified
    val transactionRef: String = "",
    val courierPartner: String = "TRAX", // TRAX, POSTEX, LEOPARDS, TCS
    val consignmentNumber: String = "", // e.g. TRX-9382104 or PSTX-847291
    val orderStatus: String = "PENDING", // PENDING, APPROVED, DISPATCHED, DELIVERED, CANCELLED
    val itemsSummary: String, // Readable text list of items with selected variants
    val subtotal: Long,
    val deliveryFee: Long,
    val grandTotal: Long,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
