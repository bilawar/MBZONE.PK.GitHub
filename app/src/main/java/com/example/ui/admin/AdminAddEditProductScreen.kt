package com.example.ui.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.ProductEntity
import com.example.model.PricingMode
import com.example.model.UnitType
import com.example.model.VariantConstants
import com.example.utils.Formatters
import com.example.utils.PriceCalculator

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AdminAddEditProductScreen(
    onBack: () -> Unit,
    onSaveProduct: (ProductEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Groceries & Grains") }
    var categoryExpanded by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf("") }

    var selectedUnitType by remember { mutableStateOf(UnitType.WEIGHT_GRAMS) }
    var basePriceText by remember { mutableStateOf("500") }
    var stockQuantityText by remember { mutableStateOf("100") }

    // Auto vs Manual Pricing Switch
    var isManualPricing by remember { mutableStateOf(false) }

    // Manual rates overrides
    var rate250 by remember { mutableStateOf("125") }
    var rate500 by remember { mutableStateOf("250") }
    var rate750 by remember { mutableStateOf("375") }
    var rate1000 by remember { mutableStateOf("500") }

    // Clothing Variants
    val colorList = remember { mutableStateListOf("Emerald Green", "Royal Maroon", "Jet Black", "Ivory White") }
    var newColorInput by remember { mutableStateOf("") }

    val sizeList = remember { mutableStateListOf("S", "M", "L", "XL") }

    val categories = listOf("Groceries & Grains", "Oils & Ghee", "Women's Fashion", "Men's Clothing", "Dry Fruits", "General Store")

    val basePriceLong = basePriceText.toLongOrNull() ?: 500L

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Add New Multi-Variant Product",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Basic Product Details Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "1. Basic Product Info",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_product_name_input"),
                        label = { Text("Product Title (e.g. Super Kernel Basmati)") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    ExposedDropdownMenuBox(
                        expanded = categoryExpanded,
                        onExpandedChange = { categoryExpanded = !categoryExpanded }
                    ) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .testTag("admin_category_dropdown"),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false }
                        ) {
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat) },
                                    onClick = {
                                        category = cat
                                        categoryExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_description_input"),
                        label = { Text("Product Description & Features") },
                        maxLines = 3,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // Unit Variant Type Selection Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "2. Unit Type Configuration",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Controls how customer selects variants (by grams, liters, or clothing sizes)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        UnitTypeChoiceButton(
                            title = "⚖️ Weight",
                            subtitle = "Grams / Kg",
                            isSelected = selectedUnitType == UnitType.WEIGHT_GRAMS,
                            onClick = { selectedUnitType = UnitType.WEIGHT_GRAMS },
                            modifier = Modifier.weight(1f)
                        )

                        UnitTypeChoiceButton(
                            title = "💧 Liquid",
                            subtitle = "Liters / ML",
                            isSelected = selectedUnitType == UnitType.LIQUID_LITERS,
                            onClick = { selectedUnitType = UnitType.LIQUID_LITERS },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        UnitTypeChoiceButton(
                            title = "👗 Clothing",
                            subtitle = "Colors & Sizes",
                            isSelected = selectedUnitType == UnitType.CLOTHING,
                            onClick = { selectedUnitType = UnitType.CLOTHING },
                            modifier = Modifier.weight(1f)
                        )

                        UnitTypeChoiceButton(
                            title = "📦 Standard",
                            subtitle = "Per Piece",
                            isSelected = selectedUnitType == UnitType.PIECE,
                            onClick = { selectedUnitType = UnitType.PIECE },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Pricing & Auto/Manual Formula Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "3. Pricing Strategy (Auto vs Manual Rates)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = basePriceText,
                        onValueChange = {
                            basePriceText = it
                            val p = it.toLongOrNull() ?: 0L
                            rate250 = (p * 0.25).toLong().toString()
                            rate500 = (p * 0.50).toLong().toString()
                            rate750 = (p * 0.75).toLong().toString()
                            rate1000 = p.toString()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_base_price_input"),
                        label = {
                            Text(
                                when (selectedUnitType) {
                                    UnitType.WEIGHT_GRAMS -> "Base Price per 1000g / 1 Kg (PKR)"
                                    UnitType.LIQUID_LITERS -> "Base Price per 1 Liter (PKR)"
                                    else -> "Base Price (PKR)"
                                }
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Auto vs Manual Switch (for Weight and Liquid)
                    if (selectedUnitType == UnitType.WEIGHT_GRAMS || selectedUnitType == UnitType.LIQUID_LITERS) {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (isManualPricing) "Manual Custom Rates Mode" else "Auto-Proportional Formula Mode",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (isManualPricing)
                                            "You can set independent custom prices for 250g, 500g, 750g, 1000g"
                                        else
                                            "System automatically computes 250g (25%), 500g (50%), 750g (75%), 1000g (100%)",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Switch(
                                    checked = isManualPricing,
                                    onCheckedChange = { isManualPricing = it },
                                    modifier = Modifier.testTag("manual_pricing_switch")
                                )
                            }
                        }

                        // Pricing preview / manual rate fields
                        if (isManualPricing) {
                            Text(
                                text = "Set Custom Variant Prices (PKR):",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = rate250,
                                    onValueChange = { rate250 = it },
                                    label = { Text("250g / 250ml") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = rate500,
                                    onValueChange = { rate500 = it },
                                    label = { Text("500g / 500ml") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = rate750,
                                    onValueChange = { rate750 = it },
                                    label = { Text("750g / 750ml") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = rate1000,
                                    onValueChange = { rate1000 = it },
                                    label = { Text("1000g / 1 Liter") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                            }
                        } else {
                            // Auto calculation preview
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("Auto-Calculated Rates Preview:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Text("• 250g (Pao / 25%): ${Formatters.formatPkr((basePriceLong * 0.25).toLong())}", style = MaterialTheme.typography.bodySmall)
                                    Text("• 500g (Aadha Kilo / 50%): ${Formatters.formatPkr((basePriceLong * 0.50).toLong())}", style = MaterialTheme.typography.bodySmall)
                                    Text("• 750g (Pone Kilo / 75%): ${Formatters.formatPkr((basePriceLong * 0.75).toLong())}", style = MaterialTheme.typography.bodySmall)
                                    Text("• 1000g (1 Kg / 100%): ${Formatters.formatPkr(basePriceLong)}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = stockQuantityText,
                        onValueChange = { stockQuantityText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_stock_input"),
                        label = { Text("Inventory Stock Count") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // Clothing Custom Colors & Sizes Configuration
            if (selectedUnitType == UnitType.CLOTHING) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "4. Clothing Colors & Sizes",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        // Color tags
                        Text("Configured Color Options:", style = MaterialTheme.typography.labelMedium)
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            colorList.forEach { color ->
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(color, style = MaterialTheme.typography.labelSmall)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Remove",
                                            modifier = Modifier
                                                .size(14.dp)
                                                .clickable { colorList.remove(color) }
                                        )
                                    }
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = newColorInput,
                                onValueChange = { newColorInput = it },
                                placeholder = { Text("Add new color name") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )
                            Button(
                                onClick = {
                                    if (newColorInput.isNotBlank()) {
                                        colorList.add(newColorInput.trim())
                                        newColorInput = ""
                                    }
                                },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Add")
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                        // Sizes
                        Text("Active Size Variants:", style = MaterialTheme.typography.labelMedium)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("XS", "S", "M", "L", "XL", "XXL").forEach { s ->
                                val isSelected = sizeList.contains(s)
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        if (isSelected) sizeList.remove(s) else sizeList.add(s)
                                    },
                                    label = { Text(s) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                        selectedLabelColor = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Save Product Button
            Button(
                onClick = {
                    val finalManualRates = if (isManualPricing) {
                        "250:${rate250.toLongOrNull() ?: 125},500:${rate500.toLongOrNull() ?: 250},750:${rate750.toLongOrNull() ?: 375},1000:${rate1000.toLongOrNull() ?: 500}"
                    } else ""

                    val newProduct = ProductEntity(
                        name = name.ifBlank { "New Pakistani Item" },
                        category = category,
                        description = description.ifBlank { "Top quality Pakistani product with multi-unit packaging." },
                        unitType = selectedUnitType.name,
                        basePrice = basePriceLong,
                        pricingMode = if (isManualPricing) PricingMode.MANUAL.name else PricingMode.AUTO.name,
                        manualRatesJson = finalManualRates,
                        availableColorsJson = colorList.joinToString(","),
                        availableSizesJson = sizeList.joinToString(","),
                        stockQuantity = stockQuantityText.toIntOrNull() ?: 100,
                        imageResName = when (selectedUnitType) {
                            UnitType.WEIGHT_GRAMS -> "prod_basmati_rice_1790233946106"
                            UnitType.LIQUID_LITERS -> "prod_desi_ghee_1790233960180"
                            UnitType.CLOTHING -> "prod_kurti_1790233973662"
                            UnitType.PIECE -> "prod_almonds_1790233986794"
                        },
                        isFeatured = true,
                        rating = 5.0
                    )
                    onSaveProduct(newProduct)
                },
                enabled = name.isNotBlank(),
                modifier = Modifier
                    .testTag("admin_save_product_button")
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Product to Catalog", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun UnitTypeChoiceButton(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .border(
                1.5.dp,
                if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
