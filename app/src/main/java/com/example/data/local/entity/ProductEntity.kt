package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.PricingMode
import com.example.model.UnitType

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String,
    val description: String,
    val unitType: String = UnitType.WEIGHT_GRAMS.name,
    val basePrice: Long, // Base PKR rate (e.g. per 1000g, per 1000ml, or piece)
    val pricingMode: String = PricingMode.AUTO.name,
    val manualRatesJson: String = "", // e.g. "250:350,500:680,750:1000,1000:1300"
    val availableColorsJson: String = "", // e.g. "Emerald Green,Royal Maroon,Navy Blue"
    val availableSizesJson: String = "", // e.g. "S,M,L,XL"
    val stockQuantity: Int = 100,
    val imageResName: String = "",
    val isFeatured: Boolean = false,
    val rating: Double = 4.8
)
