package com.example.model

/**
 * Standard unit types supported by PakShop:
 * - WEIGHT_GRAMS: 250g, 500g, 750g, 1000g, 2000g, etc.
 * - LIQUID_LITERS: 250ml, 500ml, 750ml, 1 Liter, 2 Liters, etc.
 * - CLOTHING: Colors & Sizes (S, M, L, XL, XXL)
 * - PIECE: Individual standard items (Per Piece)
 */
enum class UnitType(val displayName: String) {
    WEIGHT_GRAMS("Weight (Grams/Kg)"),
    LIQUID_LITERS("Liquid (Liters/ML)"),
    CLOTHING("Clothing (Colors/Sizes)"),
    PIECE("Standard Item (Per Piece)")
}

enum class PricingMode(val displayName: String) {
    AUTO("Auto Calculation"),
    MANUAL("Manual Custom Rates")
}

data class WeightOption(
    val grams: Int,
    val label: String,
    val subtitle: String,
    val ratio: Double // 250g = 0.25, 500g = 0.5, 750g = 0.75, 1000g = 1.0
)

data class LiquidOption(
    val ml: Int,
    val label: String,
    val subtitle: String,
    val ratio: Double // 250ml = 0.25, 500ml = 0.5, 750ml = 0.75, 1000ml = 1.0
)

data class ClothingColor(
    val name: String,
    val hexCode: Long
)

object VariantConstants {
    val DEFAULT_WEIGHT_OPTIONS = listOf(
        WeightOption(250, "250g", "Pao / Quarter", 0.25),
        WeightOption(500, "500g", "Aadha Kilo / Half", 0.50),
        WeightOption(750, "750g", "Pone Kilo / 3/4", 0.75),
        WeightOption(1000, "1 Kg", "1000 Grams", 1.0),
        WeightOption(2000, "2 Kg", "Family Pack", 2.0),
        WeightOption(5000, "5 Kg", "Bumper Sack", 5.0)
    )

    val DEFAULT_LIQUID_OPTIONS = listOf(
        LiquidOption(250, "250 ml", "Small Bottle", 0.25),
        LiquidOption(500, "500 ml", "Half Liter", 0.50),
        LiquidOption(750, "750 ml", "Medium Bottle", 0.75),
        LiquidOption(1000, "1 Liter", "Full Liter", 1.0),
        LiquidOption(2000, "2 Liters", "Family Can", 2.0),
        LiquidOption(5000, "5 Liters", "Bulk Tin", 5.0)
    )

    val DEFAULT_SIZES = listOf("XS", "S", "M", "L", "XL", "XXL")

    val DEFAULT_CLOTHING_COLORS = listOf(
        ClothingColor("Emerald Green", 0xFF0A5C36),
        ClothingColor("Royal Maroon", 0xFF800020),
        ClothingColor("Navy Blue", 0xFF002244),
        ClothingColor("Jet Black", 0xFF1C1917),
        ClothingColor("Ivory White", 0xFFF8FAFC),
        ClothingColor("Mustard Yellow", 0xFFEAB308)
    )
}
