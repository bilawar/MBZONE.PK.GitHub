package com.example.ui.customer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.ProductEntity
import com.example.model.PricingMode
import com.example.model.UnitType
import com.example.model.VariantConstants
import com.example.ui.components.ColorPickerChips
import com.example.ui.components.LiquidSelectorChips
import com.example.ui.components.SizePickerChips
import com.example.ui.components.WeightSelectorChips
import com.example.utils.Formatters
import com.example.utils.PriceCalculator

@Composable
fun ProductDetailScreen(
    product: ProductEntity,
    onBack: () -> Unit,
    onAddToCart: (selectedVariant: String, unitPrice: Long, quantity: Int) -> Unit,
    onBuyNow: (selectedVariant: String, unitPrice: Long, quantity: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val pricingMode = remember(product.pricingMode) {
        if (product.pricingMode == PricingMode.MANUAL.name) PricingMode.MANUAL else PricingMode.AUTO
    }
    val manualRatesMap = remember(product.manualRatesJson) {
        PriceCalculator.parseManualRates(product.manualRatesJson)
    }

    val availableColors = remember(product.availableColorsJson) {
        if (product.availableColorsJson.isNotBlank()) {
            product.availableColorsJson.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        } else {
            VariantConstants.DEFAULT_CLOTHING_COLORS.map { it.name }
        }
    }

    val availableSizes = remember(product.availableSizesJson) {
        if (product.availableSizesJson.isNotBlank()) {
            product.availableSizesJson.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        } else {
            VariantConstants.DEFAULT_SIZES
        }
    }

    // Selected state
    var selectedGrams by remember { mutableIntStateOf(1000) }
    var selectedWeightPrice by remember {
        mutableLongStateOf(
            PriceCalculator.calculateWeightPrice(product.basePrice, 1000, pricingMode, manualRatesMap)
        )
    }

    var selectedMl by remember { mutableIntStateOf(1000) }
    var selectedLiquidPrice by remember {
        mutableLongStateOf(
            PriceCalculator.calculateLiquidPrice(product.basePrice, 1000, pricingMode, manualRatesMap)
        )
    }

    var selectedColor by remember { mutableStateOf(availableColors.firstOrNull() ?: "Emerald Green") }
    var selectedSize by remember { mutableStateOf(availableSizes.firstOrNull() ?: "M") }
    var quantity by remember { mutableIntStateOf(1) }

    val currentUnitPrice by remember {
        derivedStateOf {
            when (product.unitType) {
                UnitType.WEIGHT_GRAMS.name -> selectedWeightPrice
                UnitType.LIQUID_LITERS.name -> selectedLiquidPrice
                else -> product.basePrice
            }
        }
    }

    val selectedVariantLabel by remember {
        derivedStateOf {
            when (product.unitType) {
                UnitType.WEIGHT_GRAMS.name -> {
                    val opt = VariantConstants.DEFAULT_WEIGHT_OPTIONS.find { it.grams == selectedGrams }
                    "${opt?.label ?: "${selectedGrams}g"} (${opt?.subtitle ?: ""})"
                }
                UnitType.LIQUID_LITERS.name -> {
                    val opt = VariantConstants.DEFAULT_LIQUID_OPTIONS.find { it.ml == selectedMl }
                    "${opt?.label ?: "${selectedMl}ml"} (${opt?.subtitle ?: ""})"
                }
                UnitType.CLOTHING.name -> {
                    "Size: $selectedSize, Color: $selectedColor"
                }
                else -> "Standard Unit (1 Piece)"
            }
        }
    }

    val totalLinePrice = currentUnitPrice * quantity

    val imageResId = remember(product.imageResName) {
        if (product.imageResName.isNotEmpty()) {
            context.resources.getIdentifier(product.imageResName, "drawable", context.packageName)
        } else 0
    }

    Scaffold(
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("product_detail_bottom_bar"),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Total Amount (${quantity}x)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = Formatters.formatPkr(totalLinePrice),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Quantity Counter
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedIconButton(
                                onClick = { if (quantity > 1) quantity-- },
                                modifier = Modifier
                                    .testTag("qty_minus_button")
                                    .size(36.dp),
                                enabled = quantity > 1
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrease Quantity")
                            }

                            Text(
                                text = quantity.toString(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.testTag("qty_text")
                            )

                            FilledIconButton(
                                onClick = { quantity++ },
                                modifier = Modifier
                                    .testTag("qty_plus_button")
                                    .size(36.dp),
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Increase Quantity")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FilledTonalButton(
                            onClick = { onAddToCart(selectedVariantLabel, currentUnitPrice, quantity) },
                            modifier = Modifier
                                .testTag("add_to_cart_detail_button")
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.ShoppingBag, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add to Cart", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { onBuyNow(selectedVariantLabel, currentUnitPrice, quantity) },
                            modifier = Modifier
                                .testTag("buy_now_button")
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(Icons.Default.FlashOn, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Buy Now", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Product Hero Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                if (imageResId != 0) {
                    Image(
                        painter = painterResource(id = imageResId),
                        contentDescription = product.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                // Top Back Button
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .testTag("detail_back_button")
                        .padding(16.dp)
                        .align(Alignment.TopStart)
                        .background(Color.White.copy(alpha = 0.85f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                // In-Stock & Category Chip Bottom
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color.White.copy(alpha = 0.95f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "In Stock (${product.stockQuantity} available)",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = product.category,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Product Details Block
            Column(modifier = Modifier.padding(16.dp)) {
                // Name & Rating
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )

                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${product.rating}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Price display card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Selected Variant Price",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = Formatters.formatPkr(currentUnitPrice),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = selectedVariantLabel,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // DYNAMIC VARIANT SELECTORS
                when (product.unitType) {
                    UnitType.WEIGHT_GRAMS.name -> {
                        WeightSelectorChips(
                            basePricePerKg = product.basePrice,
                            pricingMode = pricingMode,
                            manualRatesMap = manualRatesMap,
                            selectedGrams = selectedGrams,
                            onWeightSelected = { option, price ->
                                selectedGrams = option.grams
                                selectedWeightPrice = price
                            }
                        )
                    }
                    UnitType.LIQUID_LITERS.name -> {
                        LiquidSelectorChips(
                            basePricePerLiter = product.basePrice,
                            pricingMode = pricingMode,
                            manualRatesMap = manualRatesMap,
                            selectedMl = selectedMl,
                            onLiquidSelected = { option, price ->
                                selectedMl = option.ml
                                selectedLiquidPrice = price
                            }
                        )
                    }
                    UnitType.CLOTHING.name -> {
                        ColorPickerChips(
                            availableColors = availableColors,
                            selectedColor = selectedColor,
                            onColorSelected = { selectedColor = it }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        SizePickerChips(
                            availableSizes = availableSizes,
                            selectedSize = selectedSize,
                            onSizeSelected = { selectedSize = it }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Description
                Text(
                    text = "Description & Details",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Delivery assurance banner
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🚚 Fast Delivery across Pakistan with Trax & PostEx. Cash on Delivery (COD) & JazzCash / EasyPaisa available.",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }
}
