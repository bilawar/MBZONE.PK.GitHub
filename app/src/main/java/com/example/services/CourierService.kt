package com.example.services

import kotlinx.coroutines.delay
import kotlin.random.Random

enum class CourierPartner(val code: String, val displayName: String, val prefix: String) {
    TRAX("TRAX", "Trax Logistics (Auto CN)", "TRX"),
    POSTEX("POSTEX", "PostEx Express (Auto CN)", "PSTX"),
    LEOPARDS("LEOPARDS", "Leopards Courier", "LCS"),
    TCS("TCS", "TCS Express", "TCS")
}

data class CourierBookingResponse(
    val success: Boolean,
    val courier: CourierPartner,
    val consignmentNumber: String,
    val trackingUrl: String,
    val estimatedDays: Int = 2,
    val message: String
)

data class TrackingCheckpoint(
    val status: String,
    val location: String,
    val time: String,
    val isCompleted: Boolean,
    val isCurrent: Boolean
)

class CourierService {

    /**
     * Books a consignment with Pakistani Courier APIs (Trax / PostEx / etc.)
     */
    suspend fun bookConsignment(
        courier: CourierPartner,
        orderNumber: String,
        destinationCity: String,
        pieces: Int,
        amountToCollect: Long
    ): CourierBookingResponse {
        delay(900) // Simulated API network call
        val cnNumber = "${courier.prefix}-${Random.nextInt(10000000, 99999999)}"
        val trackingUrl = when (courier) {
            CourierPartner.TRAX -> "https://sonic.trax.pk/tracking?tracking_number=$cnNumber"
            CourierPartner.POSTEX -> "https://postex.pk/tracking?cn=$cnNumber"
            CourierPartner.LEOPARDS -> "https://leopardscourier.com/track/$cnNumber"
            CourierPartner.TCS -> "https://tcsexpress.com/track/$cnNumber"
        }

        return CourierBookingResponse(
            success = true,
            courier = courier,
            consignmentNumber = cnNumber,
            trackingUrl = trackingUrl,
            estimatedDays = if (destinationCity.equals("Lahore", ignoreCase = true) || destinationCity.equals("Karachi", ignoreCase = true)) 1 else 2,
            message = "Booking created with ${courier.displayName}. CN: $cnNumber generated for $destinationCity."
        )
    }

    /**
     * Generates a realistic tracking checkpoint timeline for an order
     */
    fun getTrackingTimeline(
        consignmentNumber: String,
        destinationCity: String,
        orderStatus: String
    ): List<TrackingCheckpoint> {
        val isDelivered = orderStatus.equals("DELIVERED", ignoreCase = true)
        val isDispatched = orderStatus.equals("DISPATCHED", ignoreCase = true) || isDelivered
        val isApproved = orderStatus.equals("APPROVED", ignoreCase = true) || isDispatched

        return listOf(
            TrackingCheckpoint(
                status = "Electronic Booking Confirmed (CN Assigned)",
                location = "Lahore Central Warehouse",
                time = "Step 1",
                isCompleted = true,
                isCurrent = !isApproved
            ),
            TrackingCheckpoint(
                status = "Parcel Picked Up by Courier Fleet",
                location = "Lahore Dispatch Facility",
                time = "Step 2",
                isCompleted = isApproved,
                isCurrent = isApproved && !isDispatched
            ),
            TrackingCheckpoint(
                status = "In Transit: Arrived at Main Transit Hub",
                location = "Air/Linehaul Hub -> $destinationCity Hub",
                time = "Step 3",
                isCompleted = isDispatched,
                isCurrent = isDispatched && !isDelivered
            ),
            TrackingCheckpoint(
                status = "Out for Doorstep Delivery with Rider",
                location = "$destinationCity Delivery Station",
                time = "Step 4",
                isCompleted = isDelivered,
                isCurrent = false
            ),
            TrackingCheckpoint(
                status = "Successfully Delivered & Cash Handed Over",
                location = "$destinationCity (Customer Received)",
                time = "Final Step",
                isCompleted = isDelivered,
                isCurrent = isDelivered
            )
        )
    }
}
