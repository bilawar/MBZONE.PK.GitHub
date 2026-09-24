package com.example.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class CustomerTab(val label: String) {
    HOME("Home"),
    EXPLORE("Explore"),
    ORDERS("Orders"),
    PROFILE("Profile")
}

@Composable
fun PakShopBottomBar(
    currentTab: CustomerTab,
    cartItemCount: Int,
    onTabSelected: (CustomerTab) -> Unit,
    onOpenCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            // Tab 1: Home
            NavigationBarItem(
                selected = currentTab == CustomerTab.HOME,
                onClick = { onTabSelected(CustomerTab.HOME) },
                icon = {
                    Icon(
                        imageVector = if (currentTab == CustomerTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                        contentDescription = "Home"
                    )
                },
                label = { Text("Home", fontSize = 11.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )

            // Tab 2: Explore
            NavigationBarItem(
                selected = currentTab == CustomerTab.EXPLORE,
                onClick = { onTabSelected(CustomerTab.EXPLORE) },
                icon = {
                    Icon(
                        imageVector = if (currentTab == CustomerTab.EXPLORE) Icons.Filled.Explore else Icons.Outlined.Explore,
                        contentDescription = "Explore"
                    )
                },
                label = { Text("Explore", fontSize = 11.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )

            // Tab 3: Orders
            NavigationBarItem(
                selected = currentTab == CustomerTab.ORDERS,
                onClick = { onTabSelected(CustomerTab.ORDERS) },
                icon = {
                    Icon(
                        imageVector = if (currentTab == CustomerTab.ORDERS) Icons.Filled.Assignment else Icons.Outlined.Assignment,
                        contentDescription = "Orders"
                    )
                },
                label = { Text("Orders", fontSize = 11.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )

            // Tab 4: Profile
            NavigationBarItem(
                selected = currentTab == CustomerTab.PROFILE,
                onClick = { onTabSelected(CustomerTab.PROFILE) },
                icon = {
                    Icon(
                        imageVector = if (currentTab == CustomerTab.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                        contentDescription = "Profile"
                    )
                },
                label = { Text("Profile", fontSize = 11.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }

        // Floating Cart FAB docked on bottom right (matches Screenshots 2 & 6)
        FloatingActionButton(
            onClick = onOpenCart,
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(6.dp),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-16).dp, y = (-20).dp)
                .size(52.dp)
                .testTag("floating_cart_fab")
        ) {
            BadgedBox(
                badge = {
                    if (cartItemCount > 0) {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = Color.White
                        ) {
                            Text(cartItemCount.toString(), fontSize = 10.sp)
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingBag,
                    contentDescription = "Shopping Bag",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
