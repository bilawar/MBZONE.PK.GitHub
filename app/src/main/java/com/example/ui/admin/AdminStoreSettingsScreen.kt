package com.example.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.local.entity.StoreSettingsEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminStoreSettingsScreen(
    currentSettings: StoreSettingsEntity?,
    onBack: () -> Unit,
    onSaveSettings: (StoreSettingsEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var storeName by remember { mutableStateOf(currentSettings?.storeName ?: "PakShop - Digital Bazaar") }
    var helpline by remember { mutableStateOf(currentSettings?.helpline ?: "0300-1234567") }
    var warehouseCity by remember { mutableStateOf(currentSettings?.warehouseCity ?: "Lahore") }
    var deliveryFeeText by remember { mutableStateOf((currentSettings?.standardDeliveryFee ?: 200L).toString()) }
    var freeThresholdText by remember { mutableStateOf((currentSettings?.freeDeliveryThreshold ?: 3000L).toString()) }
    var traxApiKey by remember { mutableStateOf(currentSettings?.traxApiKey ?: "TRX_LIVE_PK_94827103") }
    var postexApiKey by remember { mutableStateOf(currentSettings?.postexApiKey ?: "PSTX_SEC_PK_847192") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Store & Pakistan Courier Settings",
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
            // General Info Card
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
                    Text("Store Identity & Logistics", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                    OutlinedTextField(
                        value = storeName,
                        onValueChange = { storeName = it },
                        label = { Text("Store Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = helpline,
                        onValueChange = { helpline = it },
                        label = { Text("Customer Helpline / WhatsApp") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = warehouseCity,
                        onValueChange = { warehouseCity = it },
                        label = { Text("Fulfillment Warehouse City (e.g. Lahore / Karachi)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // Shipping Fee Settings
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
                    Text("Pakistan Shipping Rates (PKR)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                    OutlinedTextField(
                        value = deliveryFeeText,
                        onValueChange = { deliveryFeeText = it },
                        label = { Text("Standard Delivery Fee (Rs.)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = freeThresholdText,
                        onValueChange = { freeThresholdText = it },
                        label = { Text("Free Shipping Order Threshold (Rs.)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // Courier APIs Configuration
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
                    Text("Courier API Keys (Trax & PostEx)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                    OutlinedTextField(
                        value = traxApiKey,
                        onValueChange = { traxApiKey = it },
                        label = { Text("Trax Logistics API Secret") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = postexApiKey,
                        onValueChange = { postexApiKey = it },
                        label = { Text("PostEx Express Merchant Token") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Button(
                onClick = {
                    val updated = (currentSettings ?: StoreSettingsEntity()).copy(
                        storeName = storeName,
                        helpline = helpline,
                        warehouseCity = warehouseCity,
                        standardDeliveryFee = deliveryFeeText.toLongOrNull() ?: 200L,
                        freeDeliveryThreshold = freeThresholdText.toLongOrNull() ?: 3000L,
                        traxApiKey = traxApiKey,
                        postexApiKey = postexApiKey
                    )
                    onSaveSettings(updated)
                },
                modifier = Modifier
                    .testTag("save_settings_button")
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Store Settings", fontWeight = FontWeight.Bold)
            }
        }
    }
}
