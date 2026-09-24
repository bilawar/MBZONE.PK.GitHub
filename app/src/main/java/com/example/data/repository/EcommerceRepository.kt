package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entity.CartItemEntity
import com.example.data.local.entity.OrderEntity
import com.example.data.local.entity.ProductEntity
import com.example.data.local.entity.StoreSettingsEntity
import com.example.model.PricingMode
import com.example.model.UnitType
import com.example.services.CourierBookingResponse
import com.example.services.CourierPartner
import com.example.services.CourierService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class EcommerceRepository(
    private val database: AppDatabase,
    private val courierService: CourierService = CourierService()
) {
    private val productDao = database.productDao()
    private val cartDao = database.cartDao()
    private val orderDao = database.orderDao()
    private val storeSettingsDao = database.storeSettingsDao()

    val allProducts: Flow<List<ProductEntity>> = productDao.getAllProducts()
    val allCartItems: Flow<List<CartItemEntity>> = cartDao.getAllCartItems()
    val allOrders: Flow<List<OrderEntity>> = orderDao.getAllOrders()
    val storeSettings: Flow<StoreSettingsEntity?> = storeSettingsDao.getSettings()
    val totalRevenue: Flow<Long?> = orderDao.getTotalRevenue()

    fun getProductById(id: Long): Flow<ProductEntity?> = productDao.getProductById(id)
    suspend fun getProductDirect(id: Long): ProductEntity? = productDao.getProductDirect(id)
    fun getOrderById(id: Long): Flow<OrderEntity?> = orderDao.getOrderById(id)

    suspend fun initializeSeedDataIfNeeded() {
        val count = productDao.getProductCount()
        if (count == 0) {
            val initialProducts = listOf(
                ProductEntity(
                    id = 1,
                    name = "Premium Super Kernel Basmati Rice",
                    category = "Groceries & Grains",
                    description = "Aromatic aged long-grain basmati rice from the fields of Punjab, Pakistan. Perfect for Biryani, Pulao, and daily gourmet dining. Cleaned and stone-free.",
                    unitType = UnitType.WEIGHT_GRAMS.name,
                    basePrice = 480L, // Rs. 480 per 1000g (1 Kg)
                    pricingMode = PricingMode.AUTO.name,
                    manualRatesJson = "250:120,500:240,750:360,1000:480,2000:950,5000:2350",
                    availableColorsJson = "",
                    availableSizesJson = "",
                    stockQuantity = 250,
                    imageResName = "prod_basmati_rice_1790233946106",
                    isFeatured = true,
                    rating = 4.9
                ),
                ProductEntity(
                    id = 2,
                    name = "Pure Golden Desi Ghee (Organic)",
                    category = "Oils & Ghee",
                    description = "100% natural, grass-fed cow & buffalo cultured desi makhan ghee. Rich traditional aroma and golden granules (danedar) for wholesome Pakistani parathas & sweets.",
                    unitType = UnitType.LIQUID_LITERS.name,
                    basePrice = 2800L, // Rs. 2800 per Liter
                    pricingMode = PricingMode.AUTO.name,
                    manualRatesJson = "250:720,500:1420,750:2120,1000:2800",
                    availableColorsJson = "",
                    availableSizesJson = "",
                    stockQuantity = 80,
                    imageResName = "prod_desi_ghee_1790233960180",
                    isFeatured = true,
                    rating = 5.0
                ),
                ProductEntity(
                    id = 3,
                    name = "Designer Embroidered Lawn Kurti",
                    category = "Women's Fashion",
                    description = "Handcrafted festive Pakistani stitched Kurti with delicate gold zari embroidery on premium breathable lawn fabric. Features comfortable modern cuts.",
                    unitType = UnitType.CLOTHING.name,
                    basePrice = 3650L,
                    pricingMode = PricingMode.AUTO.name,
                    manualRatesJson = "",
                    availableColorsJson = "Emerald Green,Royal Maroon,Navy Blue,Jet Black,Ivory White",
                    availableSizesJson = "S,M,L,XL,XXL",
                    stockQuantity = 45,
                    imageResName = "prod_kurti_1790233973662",
                    isFeatured = true,
                    rating = 4.8
                ),
                ProductEntity(
                    id = 4,
                    name = "Afghan Mamra Premium Almonds",
                    category = "Dry Fruits",
                    description = "Top grade high-oil Mamra Badam imported from organic mountain groves. Nutritious, crunchy, rich in Vitamin E. Packed fresh in airtight zip pouches.",
                    unitType = UnitType.WEIGHT_GRAMS.name,
                    basePrice = 3400L,
                    pricingMode = PricingMode.MANUAL.name, // Specially demonstrating Manual Custom Rates!
                    manualRatesJson = "250:900,500:1750,750:2600,1000:3400",
                    availableColorsJson = "",
                    availableSizesJson = "",
                    stockQuantity = 120,
                    imageResName = "prod_almonds_1790233986794",
                    isFeatured = true,
                    rating = 4.9
                ),
                ProductEntity(
                    id = 5,
                    name = "Cold Pressed Mustard (Sarson) Oil",
                    category = "Oils & Ghee",
                    description = "Pure wooden-kohl pressing method ensures zero chemicals and retains all natural pungent flavors and omega-3 nutrients. Ideal for pickles and traditional cooking.",
                    unitType = UnitType.LIQUID_LITERS.name,
                    basePrice = 750L,
                    pricingMode = PricingMode.AUTO.name,
                    manualRatesJson = "250:200,500:390,750:570,1000:750",
                    availableColorsJson = "",
                    availableSizesJson = "",
                    stockQuantity = 95,
                    imageResName = "prod_desi_ghee_1790233960180",
                    isFeatured = false,
                    rating = 4.7
                )
            )
            productDao.insertProducts(initialProducts)
        }

        val settings = storeSettingsDao.getSettingsDirect()
        if (settings == null) {
            storeSettingsDao.insertOrUpdate(
                StoreSettingsEntity(
                    id = 1,
                    storeName = "PakShop - Digital Bazaar",
                    helpline = "0300-9876543",
                    warehouseCity = "Lahore",
                    standardDeliveryFee = 200L,
                    freeDeliveryThreshold = 3000L,
                    traxApiKey = "TRX_LIVE_PK_94827103",
                    postexApiKey = "PSTX_SEC_PK_847192"
                )
            )
        }
    }

    // Product actions
    suspend fun saveProduct(product: ProductEntity): Long = productDao.insertProduct(product)
    suspend fun updateProduct(product: ProductEntity) = productDao.updateProduct(product)
    suspend fun deleteProduct(productId: Long) = productDao.deleteProductById(productId)

    // Cart actions
    suspend fun addToCart(
        product: ProductEntity,
        selectedVariantLabel: String,
        unitPrice: Long,
        quantity: Int
    ) {
        val existing = cartDao.findExistingCartItem(product.id, selectedVariantLabel)
        if (existing != null) {
            cartDao.updateCartItem(existing.copy(quantity = existing.quantity + quantity))
        } else {
            cartDao.insertCartItem(
                CartItemEntity(
                    productId = product.id,
                    productName = product.name,
                    category = product.category,
                    selectedVariantLabel = selectedVariantLabel,
                    unitPrice = unitPrice,
                    quantity = quantity,
                    imageResName = product.imageResName
                )
            )
        }
    }

    suspend fun updateCartQuantity(cartItemId: Long, newQuantity: Int) {
        if (newQuantity <= 0) {
            cartDao.deleteCartItemById(cartItemId)
        } else {
            val all = cartDao.getAllCartItems().firstOrNull() ?: emptyList()
            val item = all.find { it.id == cartItemId }
            if (item != null) {
                cartDao.updateCartItem(item.copy(quantity = newQuantity))
            }
        }
    }

    suspend fun removeCartItem(cartItemId: Long) = cartDao.deleteCartItemById(cartItemId)
    suspend fun clearCart() = cartDao.clearCart()

    // Order actions
    suspend fun placeOrder(order: OrderEntity): Long {
        val orderId = orderDao.insertOrder(order)
        cartDao.clearCart()
        return orderId
    }

    suspend fun updateOrder(order: OrderEntity) = orderDao.updateOrder(order)

    suspend fun autoBookCourier(orderId: Long, partner: CourierPartner): CourierBookingResponse {
        val all = orderDao.getAllOrders().firstOrNull() ?: emptyList()
        val order = all.find { it.id == orderId } ?: throw IllegalArgumentException("Order not found")

        val response = courierService.bookConsignment(
            courier = partner,
            orderNumber = order.orderNumber,
            destinationCity = order.city,
            pieces = 1,
            amountToCollect = if (order.paymentMethod.contains("COD", ignoreCase = true)) order.grandTotal else 0L
        )

        val updatedOrder = order.copy(
            courierPartner = partner.code,
            consignmentNumber = response.consignmentNumber,
            orderStatus = "APPROVED"
        )
        orderDao.updateOrder(updatedOrder)
        return response
    }

    suspend fun updateOrderStatus(orderId: Long, newStatus: String) {
        val all = orderDao.getAllOrders().firstOrNull() ?: emptyList()
        val order = all.find { it.id == orderId } ?: return
        orderDao.updateOrder(order.copy(orderStatus = newStatus))
    }

    // Store settings
    suspend fun updateStoreSettings(settings: StoreSettingsEntity) =
        storeSettingsDao.insertOrUpdate(settings)
}
