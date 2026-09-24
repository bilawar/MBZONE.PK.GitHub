package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productId: Long,
    val productName: String,
    val category: String,
    val selectedVariantLabel: String, // e.g. "500g (Aadha Kilo)" or "Size: L, Color: Emerald Green"
    val unitPrice: Long,
    val quantity: Int = 1,
    val imageResName: String = ""
) {
    val totalPrice: Long get() = unitPrice * quantity
}
