package com.example.services

import kotlinx.coroutines.delay
import kotlin.random.Random

sealed class PaymentResult {
    data class Success(val transactionRef: String, val message: String) : PaymentResult()
    data class Failure(val errorMessage: String) : PaymentResult()
}

enum class PaymentGateway(val displayName: String, val badge: String, val colorHex: Long) {
    CASH_ON_DELIVERY("Cash on Delivery (COD)", "Pay upon doorstep delivery", 0xFF0A5C36),
    JAZZCASH("JazzCash Mobile Account", "Instant USSD / Mobile Wallet PIN", 0xFFEC1C24),
    EASYPAISA("EasyPaisa Mobile Account", "Instant Wallet App Push Approval", 0xFF00A651),
    SAFEPAY("Safepay / Debit Card", "Visa & MasterCard 3D Secure", 0xFF2563EB),
    BANK_TRANSFER("Direct IBFT / Bank Transfer", "Manual Receipt Upload", 0xFF475569)
}

class PaymentService {

    /**
     * Simulates processing payment through Pakistani Gateways
     */
    suspend fun processPayment(
        gateway: PaymentGateway,
        amountPkr: Long,
        customerPhone: String,
        customerCnicOrPin: String = ""
    ): PaymentResult {
        // Realistic simulated processing latency
        delay(1200)

        return when (gateway) {
            PaymentGateway.CASH_ON_DELIVERY -> {
                val codToken = "COD-${Random.nextInt(100000, 999999)}"
                PaymentResult.Success(
                    transactionRef = codToken,
                    message = "COD order verified. Cash will be collected by courier rider upon delivery."
                )
            }
            PaymentGateway.JAZZCASH -> {
                if (customerPhone.length < 10) {
                    return PaymentResult.Failure("Invalid JazzCash mobile account number. Please enter a valid 11-digit number.")
                }
                val jcRef = "JC-${System.currentTimeMillis().toString().takeLast(6)}${Random.nextInt(100, 999)}"
                PaymentResult.Success(
                    transactionRef = jcRef,
                    message = "Payment of Rs. $amountPkr approved via JazzCash Mobile Account ($customerPhone)."
                )
            }
            PaymentGateway.EASYPAISA -> {
                if (customerPhone.length < 10) {
                    return PaymentResult.Failure("Invalid EasyPaisa account number. Please enter a valid 11-digit number.")
                }
                val epRef = "EP-${System.currentTimeMillis().toString().takeLast(6)}${Random.nextInt(100, 999)}"
                PaymentResult.Success(
                    transactionRef = epRef,
                    message = "EasyPaisa transaction approved successfully. Ref #$epRef"
                )
            }
            PaymentGateway.SAFEPAY -> {
                val spRef = "SP-CARD-${Random.nextInt(1000000, 9999999)}"
                PaymentResult.Success(
                    transactionRef = spRef,
                    message = "Card payment processed securely via Safepay 3D-Secure."
                )
            }
            PaymentGateway.BANK_TRANSFER -> {
                val ibftRef = "IBFT-${Random.nextInt(100000, 999999)}"
                PaymentResult.Success(
                    transactionRef = ibftRef,
                    message = "IBFT order booked. Please transfer to HBL A/C # 0123-456789-01."
                )
            }
        }
    }
}
