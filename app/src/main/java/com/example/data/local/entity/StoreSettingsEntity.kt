package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "store_settings")
data class StoreSettingsEntity(
    @PrimaryKey
    val id: Int = 1,
    val storeName: String = "PakShop - Digital Bazaar",
    val helpline: String = "0300-1234567",
    val warehouseCity: String = "Lahore",
    val standardDeliveryFee: Long = 200L,
    val freeDeliveryThreshold: Long = 3000L,
    val traxApiKey: String = "TRX_LIVE_PK_94827103",
    val postexApiKey: String = "PSTX_SEC_PK_847192",
    val jazzcashMerchantId: String = "MC_928172",
    val easypaisaStoreId: String = "EP_STORE_5541"
)
