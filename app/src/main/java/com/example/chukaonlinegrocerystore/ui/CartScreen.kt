package com.example.chukaonlinegrocerystore.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chukaonlinegrocerystore.viewmodel.CartViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavHostController,
    cartViewModel: CartViewModel = viewModel()
) {
    val cartItems by cartViewModel.cartItems.collectAsState()
    val totalPrice by cartViewModel.totalPrice.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Cart") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* Handle checkout action */ }) {
                Icon(Icons.Filled.Done, contentDescription = "Checkout")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (cartItems.isEmpty()) {
                Text("Your cart is empty.", style = MaterialTheme.typography.headlineSmall)
            } else {
                LazyColumn {
                    items(cartItems) { product ->
                        CartItem(product) {
                            cartViewModel.removeFromCart(product)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Total: Ksh %.2f".format(totalPrice),
                    style = MaterialTheme.typography.titleLarge
                )
                Button(
                    onClick = { cartViewModel.clearCart() },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Clear Cart")
                }
            }
        }
    }
}

@Composable
fun CartItem(product: com.example.chukaonlinegrocerystore.model.Product, onRemove: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Text(
                text = product.name,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Ksh %.2f".format(product.price),
                style = MaterialTheme.typography.bodyLarge
            )
            IconButton(onClick = onRemove) {
                Icon(imageVector = Icons.Filled.Delete, contentDescription = "Remove")
            }
        }
    }
}