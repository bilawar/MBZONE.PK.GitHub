package com.example.ui.customer

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.OrderEntity
import com.example.utils.Formatters

@Composable
fun OrderSuccessScreen(
    order: OrderEntity,
    onTrackOrder: (Long) -> Unit,
    onContinueShopping: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
            .testTag("order_success_screen"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Blue Rounded Square with White Checkmark (matches Screenshot 2)
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Success",
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Order Placed",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Your order has been placed successfully",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Clean Details Container (Matches Screenshot 2)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                OrderDetailRow(label = "Order ID", value = order.orderNumber, isBold = true)
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

                OrderDetailRow(label = "Payment", value = order.paymentMethod)
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

                OrderDetailRow(label = "Total", value = Formatters.formatPkr(order.grandTotal), isBold = true, valueColor = MaterialTheme.colorScheme.primary)
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

                OrderDetailRow(label = "Est. Delivery", value = "2 - 3 Days (Express)")
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

                OrderDetailRow(
                    label = "Courier CN",
                    value = "${order.courierPartner}: ${order.consignmentNumber.ifEmpty { "Pending" }}",
                    isHighlight = true
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Action Buttons
        Button(
            onClick = onContinueShopping,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("continue_shopping_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Continue Shopping", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = { onTrackOrder(order.id) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("track_courier_button"),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Track Courier Delivery", fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
private fun OrderDetailRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    valueColor: Color = Color.Unspecified,
    isHighlight: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (isHighlight) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        } else {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
                color = if (valueColor != Color.Unspecified) valueColor else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
