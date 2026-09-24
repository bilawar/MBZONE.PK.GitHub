package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.CartItemEntity
import com.example.data.local.entity.OrderEntity
import com.example.data.local.entity.ProductEntity
import com.example.data.local.entity.StoreSettingsEntity
import com.example.data.repository.EcommerceRepository
import com.example.services.CourierPartner
import com.example.services.CourierService
import com.example.services.PaymentGateway
import com.example.services.PaymentResult
import com.example.services.PaymentService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

class PakShopViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: EcommerceRepository
    private val paymentService = PaymentService()
    private val courierService = CourierService()

    init {
        val database = AppDatabase.getInstance(application)
        repository = EcommerceRepository(database = database, courierService = courierService)
        viewModelScope.launch {
            repository.initializeSeedDataIfNeeded()
        }
    }

    val products: StateFlow<List<ProductEntity>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartItems: StateFlow<List<CartItemEntity>> = repository.allCartItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val orders: StateFlow<List<OrderEntity>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val storeSettings: StateFlow<StoreSettingsEntity?> = repository.storeSettings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // UI Navigation & Dialog states
    private val _isAdminMode = MutableStateFlow(false)
    val isAdminMode: StateFlow<Boolean> = _isAdminMode.asStateFlow()

    private val _selectedProduct = MutableStateFlow<ProductEntity?>(null)
    val selectedProduct: StateFlow<ProductEntity?> = _selectedProduct.asStateFlow()

    private val _isCartOpen = MutableStateFlow(false)
    val isCartOpen: StateFlow<Boolean> = _isCartOpen.asStateFlow()

    private val _isCheckoutOpen = MutableStateFlow(false)
    val isCheckoutOpen: StateFlow<Boolean> = _isCheckoutOpen.asStateFlow()

    private val _placedOrder = MutableStateFlow<OrderEntity?>(null)
    val placedOrder: StateFlow<OrderEntity?> = _placedOrder.asStateFlow()

    private val _selectedTrackingOrderId = MutableStateFlow<Long?>(null)
    val selectedTrackingOrderId: StateFlow<Long?> = _selectedTrackingOrderId.asStateFlow()

    private val _isOrdersTrackingOpen = MutableStateFlow(false)
    val isOrdersTrackingOpen: StateFlow<Boolean> = _isOrdersTrackingOpen.asStateFlow()

    // Admin Navigation
    private val _adminCurrentScreen = MutableStateFlow("DASHBOARD") // "DASHBOARD", "ADD_PRODUCT", "ORDERS", "SETTINGS"
    val adminCurrentScreen: StateFlow<String> = _adminCurrentScreen.asStateFlow()

    private val _isProcessingOrder = MutableStateFlow(false)
    val isProcessingOrder: StateFlow<Boolean> = _isProcessingOrder.asStateFlow()

    fun toggleAdminMode() {
        _isAdminMode.value = !_isAdminMode.value
        _adminCurrentScreen.value = "DASHBOARD"
    }

    fun selectProduct(product: ProductEntity?) {
        _selectedProduct.value = product
    }

    fun openCart() {
        _isCartOpen.value = true
    }

    fun closeCart() {
        _isCartOpen.value = false
    }

    fun openCheckout() {
        _isCartOpen.value = false
        _isCheckoutOpen.value = true
    }

    fun closeCheckout() {
        _isCheckoutOpen.value = false
    }

    fun openOrdersTracking(orderId: Long? = null) {
        _selectedTrackingOrderId.value = orderId
        _isOrdersTrackingOpen.value = true
    }

    fun closeOrdersTracking() {
        _isOrdersTrackingOpen.value = false
    }

    fun dismissOrderSuccess() {
        _placedOrder.value = null
    }

    fun setAdminScreen(screen: String) {
        _adminCurrentScreen.value = screen
    }

    fun addToCart(product: ProductEntity, selectedVariantLabel: String, unitPrice: Long, quantity: Int) {
        viewModelScope.launch {
            repository.addToCart(
                product = product,
                selectedVariantLabel = selectedVariantLabel,
                unitPrice = unitPrice,
                quantity = quantity
            )
        }
    }

    fun updateCartQuantity(cartItemId: Long, newQuantity: Int) {
        viewModelScope.launch {
            repository.updateCartQuantity(cartItemId, newQuantity)
        }
    }

    fun removeCartItem(cartItemId: Long) {
        viewModelScope.launch {
            repository.removeCartItem(cartItemId)
        }
    }

    fun confirmOrder(
        customerName: String,
        phone: String,
        address: String,
        city: String,
        gateway: PaymentGateway,
        courier: CourierPartner,
        accountNumber: String,
        notes: String
    ) {
        viewModelScope.launch {
            _isProcessingOrder.value = true
            val items = cartItems.value
            val subtotal = items.sumOf { it.totalPrice }
            val deliveryFee = if (subtotal >= 3000L) 0L else 200L
            val grandTotal = subtotal + deliveryFee
            val orderNumber = "PK-${Random.nextInt(10000, 99999)}"

            // Process payment transaction
            val paymentResult = paymentService.processPayment(
                gateway = gateway,
                amountPkr = grandTotal,
                customerPhone = phone,
                customerCnicOrPin = accountNumber
            )

            // Auto-book Courier CN with Trax/PostEx API
            val courierBooking = courierService.bookConsignment(
                courier = courier,
                orderNumber = orderNumber,
                destinationCity = city,
                pieces = items.sumOf { it.quantity },
                amountToCollect = if (gateway == PaymentGateway.CASH_ON_DELIVERY) grandTotal else 0L
            )

            val summary = items.joinToString("; ") {
                "${it.quantity}x ${it.productName} (${it.selectedVariantLabel})"
            }

            val isPaid = paymentResult is PaymentResult.Success && gateway != PaymentGateway.CASH_ON_DELIVERY

            val newOrder = OrderEntity(
                orderNumber = orderNumber,
                customerName = customerName,
                phone = phone,
                address = address,
                city = city,
                subtotal = subtotal,
                deliveryFee = deliveryFee,
                grandTotal = grandTotal,
                paymentMethod = gateway.displayName,
                paymentStatus = if (isPaid) "PAID" else "PENDING_COD",
                courierPartner = courier.code,
                consignmentNumber = courierBooking.consignmentNumber,
                orderStatus = "APPROVED",
                itemsSummary = summary,
                notes = notes
            )

            val generatedId = repository.placeOrder(newOrder)
            val finalOrder = newOrder.copy(id = generatedId)

            _isProcessingOrder.value = false
            _isCheckoutOpen.value = false
            _placedOrder.value = finalOrder
        }
    }

    fun bookCourierCn(orderId: Long, partner: CourierPartner) {
        viewModelScope.launch {
            repository.autoBookCourier(orderId, partner)
        }
    }

    fun updateOrderStatus(orderId: Long, status: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, status)
        }
    }

    fun saveProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.saveProduct(product)
            _adminCurrentScreen.value = "DASHBOARD"
        }
    }

    fun saveStoreSettings(settings: StoreSettingsEntity) {
        viewModelScope.launch {
            repository.updateStoreSettings(settings)
            _adminCurrentScreen.value = "DASHBOARD"
        }
    }
}
