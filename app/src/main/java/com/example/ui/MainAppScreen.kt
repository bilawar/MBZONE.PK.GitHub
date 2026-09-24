package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.PricingMode
import com.example.model.UnitType
import com.example.ui.admin.AdminAddEditProductScreen
import com.example.ui.admin.AdminDashboardScreen
import com.example.ui.admin.AdminOrdersScreen
import com.example.ui.admin.AdminStoreSettingsScreen
import com.example.ui.components.CustomerTab
import com.example.ui.components.PakShopBottomBar
import com.example.ui.components.PakShopTopBar
import com.example.ui.customer.CartBottomSheet
import com.example.ui.customer.CheckoutScreen
import com.example.ui.customer.ExploreScreen
import com.example.ui.customer.HomeScreen
import com.example.ui.customer.OrderSuccessScreen
import com.example.ui.customer.OrderTrackingScreen
import com.example.ui.customer.ProductDetailScreen
import com.example.ui.customer.ProfileScreen
import com.example.utils.Formatters
import com.example.utils.PriceCalculator
import kotlinx.coroutines.launch

@Composable
fun MainAppScreen(
    viewModel: PakShopViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val products by viewModel.products.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val storeSettings by viewModel.storeSettings.collectAsState()

    val isAdminMode by viewModel.isAdminMode.collectAsState()
    val selectedProduct by viewModel.selectedProduct.collectAsState()
    val isCartOpen by viewModel.isCartOpen.collectAsState()
    val isCheckoutOpen by viewModel.isCheckoutOpen.collectAsState()
    val placedOrder by viewModel.placedOrder.collectAsState()
    val isOrdersTrackingOpen by viewModel.isOrdersTrackingOpen.collectAsState()
    val selectedTrackingOrderId by viewModel.selectedTrackingOrderId.collectAsState()
    val adminCurrentScreen by viewModel.adminCurrentScreen.collectAsState()
    val isProcessingOrder by viewModel.isProcessingOrder.collectAsState()

    var customerTab by remember { mutableStateOf(CustomerTab.HOME) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val totalRevenue = orders.filter { it.orderStatus != "CANCELLED" }.sumOf { it.grandTotal }

    // Check if bottom bar should be visible
    val isBottomBarVisible = !isAdminMode &&
            !isCheckoutOpen &&
            selectedProduct == null &&
            placedOrder == null &&
            !isOrdersTrackingOpen

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (isAdminMode) {
                PakShopTopBar(
                    isAdminMode = true,
                    cartItemCount = 0,
                    onToggleAdminMode = { viewModel.toggleAdminMode() },
                    onOpenCart = {},
                    onOpenOrders = {}
                )
            }
        },
        bottomBar = {
            if (isBottomBarVisible) {
                PakShopBottomBar(
                    currentTab = customerTab,
                    cartItemCount = cartItems.sumOf { it.quantity },
                    onTabSelected = { tab -> customerTab = tab },
                    onOpenCart = { viewModel.openCart() }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = isAdminMode,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "AdminCustomerMode"
            ) { inAdmin ->
                if (inAdmin) {
                    // ADMIN PORTAL
                    when (adminCurrentScreen) {
                        "ADD_PRODUCT" -> {
                            AdminAddEditProductScreen(
                                onBack = { viewModel.setAdminScreen("DASHBOARD") },
                                onSaveProduct = { newProduct ->
                                    viewModel.saveProduct(newProduct)
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Product '${newProduct.name}' added successfully!")
                                    }
                                }
                            )
                        }
                        "ORDERS" -> {
                            AdminOrdersScreen(
                                orders = orders,
                                onBack = { viewModel.setAdminScreen("DASHBOARD") },
                                onBookCourierCn = { orderId, partner ->
                                    viewModel.bookCourierCn(orderId, partner)
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Auto CN generated with ${partner.name}!")
                                    }
                                },
                                onUpdateOrderStatus = { orderId, status ->
                                    viewModel.updateOrderStatus(orderId, status)
                                }
                            )
                        }
                        "SETTINGS" -> {
                            AdminStoreSettingsScreen(
                                currentSettings = storeSettings,
                                onBack = { viewModel.setAdminScreen("DASHBOARD") },
                                onSaveSettings = { updated ->
                                    viewModel.saveStoreSettings(updated)
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Store settings saved successfully!")
                                    }
                                }
                            )
                        }
                        else -> {
                            AdminDashboardScreen(
                                products = products,
                                orders = orders,
                                totalRevenue = totalRevenue,
                                onNavigateToAddProduct = { viewModel.setAdminScreen("ADD_PRODUCT") },
                                onNavigateToOrders = { viewModel.setAdminScreen("ORDERS") },
                                onNavigateToSettings = { viewModel.setAdminScreen("SETTINGS") }
                            )
                        }
                    }
                } else {
                    // CUSTOMER BAZAAR
                    when {
                        placedOrder != null -> {
                            OrderSuccessScreen(
                                order = placedOrder!!,
                                onTrackOrder = { orderId ->
                                    viewModel.dismissOrderSuccess()
                                    viewModel.openOrdersTracking(orderId)
                                },
                                onContinueShopping = {
                                    viewModel.dismissOrderSuccess()
                                    customerTab = CustomerTab.HOME
                                }
                            )
                        }
                        isCheckoutOpen -> {
                            CheckoutScreen(
                                cartItems = cartItems,
                                onBack = { viewModel.closeCheckout() },
                                onConfirmOrder = { customerName, phone, address, city, gateway, courier, accountNumber, notes ->
                                    viewModel.confirmOrder(
                                        customerName, phone, address, city, gateway, courier, accountNumber, notes
                                    )
                                },
                                isProcessing = isProcessingOrder
                            )
                        }
                        isOrdersTrackingOpen -> {
                            OrderTrackingScreen(
                                orders = orders,
                                selectedOrderId = selectedTrackingOrderId,
                                onBack = { viewModel.closeOrdersTracking() },
                                onSelectOrder = { orderId -> viewModel.openOrdersTracking(orderId) }
                            )
                        }
                        selectedProduct != null -> {
                            ProductDetailScreen(
                                product = selectedProduct!!,
                                onBack = { viewModel.selectProduct(null) },
                                onAddToCart = { variantLabel, unitPrice, qty ->
                                    viewModel.addToCart(selectedProduct!!, variantLabel, unitPrice, qty)
                                    viewModel.selectProduct(null)
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Added $qty item(s) to Cart")
                                    }
                                },
                                onBuyNow = { variantLabel, unitPrice, qty ->
                                    viewModel.addToCart(selectedProduct!!, variantLabel, unitPrice, qty)
                                    viewModel.selectProduct(null)
                                    viewModel.openCheckout()
                                }
                            )
                        }
                        else -> {
                            // 4-Tab Customer Experience
                            when (customerTab) {
                                CustomerTab.HOME -> {
                                    HomeScreen(
                                        products = products,
                                        onProductClick = { product -> viewModel.selectProduct(product) },
                                        onQuickAddClick = { product ->
                                            quickAddProductToCart(product, viewModel) {
                                                coroutineScope.launch {
                                                    snackbarHostState.showSnackbar("Added 1x ${product.name} to Cart")
                                                }
                                            }
                                        },
                                        onSeeAllClick = {
                                            customerTab = CustomerTab.EXPLORE
                                        }
                                    )
                                }
                                CustomerTab.EXPLORE -> {
                                    ExploreScreen(
                                        products = products,
                                        onProductClick = { product -> viewModel.selectProduct(product) },
                                        onQuickAddToCart = { product ->
                                            quickAddProductToCart(product, viewModel) {
                                                coroutineScope.launch {
                                                    snackbarHostState.showSnackbar("Added 1x ${product.name} to Cart")
                                                }
                                            }
                                        }
                                    )
                                }
                                CustomerTab.ORDERS -> {
                                    OrderTrackingScreen(
                                        orders = orders,
                                        selectedOrderId = selectedTrackingOrderId,
                                        onBack = { customerTab = CustomerTab.HOME },
                                        onSelectOrder = { orderId -> viewModel.openOrdersTracking(orderId) }
                                    )
                                }
                                CustomerTab.PROFILE -> {
                                    ProfileScreen(
                                        userName = "Mr nevo",
                                        userEmail = "nevooff1@gmail.com",
                                        onNavigateToOrders = {
                                            customerTab = CustomerTab.ORDERS
                                        },
                                        onNavigateToAdmin = {
                                            viewModel.toggleAdminMode()
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Slide-up Cart Modal Bottom Sheet
            if (isCartOpen) {
                CartBottomSheet(
                    cartItems = cartItems,
                    onDismiss = { viewModel.closeCart() },
                    onUpdateQuantity = { cartItemId, newQty ->
                        viewModel.updateCartQuantity(cartItemId, newQty)
                    },
                    onRemoveItem = { cartItemId ->
                        viewModel.removeCartItem(cartItemId)
                    },
                    onProceedToCheckout = {
                        viewModel.openCheckout()
                    }
                )
            }
        }
    }
}

private fun quickAddProductToCart(
    product: com.example.data.local.entity.ProductEntity,
    viewModel: PakShopViewModel,
    onSuccess: () -> Unit
) {
    val pricingMode = if (product.pricingMode == PricingMode.MANUAL.name) PricingMode.MANUAL else PricingMode.AUTO
    val manualRates = PriceCalculator.parseManualRates(product.manualRatesJson)
    val (variantLabel, unitPrice) = when (product.unitType) {
        UnitType.WEIGHT_GRAMS.name -> {
            "1000g / 1 Kg (Standard Packaging)" to PriceCalculator.calculateWeightPrice(product.basePrice, 1000, pricingMode, manualRates)
        }
        UnitType.LIQUID_LITERS.name -> {
            "1000ml / 1 Liter Bottle" to PriceCalculator.calculateLiquidPrice(product.basePrice, 1000, pricingMode, manualRates)
        }
        UnitType.CLOTHING.name -> {
            "Size: M, Color: Emerald Green" to product.basePrice
        }
        else -> "Standard Unit (1 Piece)" to product.basePrice
    }
    viewModel.addToCart(product, variantLabel, unitPrice, 1)
    onSuccess()
}
