package com.example.utils

import com.example.model.PricingMode
import com.example.model.UnitType
import kotlin.math.roundToLong

object PriceCalculator {

    /**
     * Calculates the unit price for a given weight in grams.
     * If pricingMode is MANUAL and a rate exists in manualRatesMap, uses that;
     * otherwise computes using proportional formula (grams / 1000.0 * basePricePerKg).
     */
    fun calculateWeightPrice(
        basePricePerKg: Long,
        grams: Int,
        pricingMode: PricingMode = PricingMode.AUTO,
        manualRatesMap: Map<Int, Long> = emptyMap()
    ): Long {
        if (pricingMode == PricingMode.MANUAL && manualRatesMap.containsKey(grams)) {
            val manualRate = manualRatesMap[grams]
            if (manualRate != null && manualRate > 0) {
                return manualRate
            }
        }
        val ratio = grams.toDouble() / 1000.0
        return (basePricePerKg * ratio).roundToLong()
    }

    /**
     * Calculates the unit price for liquids in milliliters.
     * Uses manual override if specified, otherwise proportional formula (ml / 1000.0 * basePricePerLiter).
     */
    fun calculateLiquidPrice(
        basePricePerLiter: Long,
        ml: Int,
        pricingMode: PricingMode = PricingMode.AUTO,
        manualRatesMap: Map<Int, Long> = emptyMap()
    ): Long {
        if (pricingMode == PricingMode.MANUAL && manualRatesMap.containsKey(ml)) {
            val manualRate = manualRatesMap[ml]
            if (manualRate != null && manualRate > 0) {
                return manualRate
            }
        }
        val ratio = ml.toDouble() / 1000.0
        return (basePricePerLiter * ratio).roundToLong()
    }

    /**
     * Parse manual rates from key-value string (e.g., "250:350,500:680,750:1000,1000:1300")
     */
    fun parseManualRates(rawString: String): Map<Int, Long> {
        if (rawString.isBlank()) return emptyMap()
        return try {
            rawString.split(",")
                .mapNotNull { pair ->
                    val parts = pair.split(":")
                    if (parts.size == 2) {
                        val key = parts[0].trim().toIntOrNull()
                        val value = parts[1].trim().toLongOrNull()
                        if (key != null && value != null) key to value else null
                    } else null
                }
                .toMap()
        } catch (_: Exception) {
            emptyMap()
        }
    }

    /**
     * Serialize manual rates map back to string
     */
    fun serializeManualRates(map: Map<Int, Long>): String {
        return map.entries.joinToString(",") { "${it.key}:${it.value}" }
    }
}
