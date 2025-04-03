package com.example.chukaonlinegrocerystore.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.chukaonlinegrocerystore.model.Product
import com.example.chukaonlinegrocerystore.viewmodel.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavHostController,
    cartViewModel: CartViewModel = viewModel()
) {
    val cartItems by cartViewModel.cartItems.collectAsState()
    val totalPrice by cartViewModel.totalPrice.collectAsState()
    val checkoutState by cartViewModel.checkoutState.collectAsState()

    var showConfirmCheckout by remember { mutableStateOf(false) }
    val context = LocalContext.current

    var showBottomSheet by remember { mutableStateOf(false) }
    var phoneNumber by remember { mutableStateOf("") }
    var isPhoneNumberValid by remember { mutableStateOf(true) }
    val phonePattern = remember { Regex("^[0-9]{10}$") } // Simple 10-digit validation

    // Helper function for formatting double with specific decimal places
    fun Double.format(digits: Int) = "%.${digits}f".format(this)

    // Observe checkout state
    LaunchedEffect(checkoutState) {
        when (checkoutState) {
            is CartViewModel.CheckoutState.Success -> {
                Toast.makeText(context, "Checkout successful!", Toast.LENGTH_SHORT).show()
                navController.navigate("buyer_dashboard") {
                    popUpTo("buyer_dashboard") { inclusive = true }
                }
            }

            is CartViewModel.CheckoutState.Error -> {
                val error = (checkoutState as CartViewModel.CheckoutState.Error).message
                Toast.makeText(context, "Error: $error", Toast.LENGTH_LONG).show()
            }

            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Cart") })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (cartItems.isNotEmpty()) {
                        showConfirmCheckout = true
                    } else {
                        Toast.makeText(context, "Cart is empty", Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
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
                        CartItem(
                            product = product,
                            onRemove = { cartViewModel.removeFromCart(product) },
                            cartViewModel = cartViewModel
                        )
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

           /* if (showConfirmCheckout) {
                AlertDialog(
                    onDismissRequest = { showConfirmCheckout = false },
                    title = { Text("Confirm Checkout") },
                    text = {
                        Text("This will update inventory quantities. Continue with checkout?")
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                cartViewModel.checkout()
                                showConfirmCheckout = false
                            }
                        ) {
                            Text("Confirm")
                        }
                    },
                    dismissButton = {
                        OutlinedButton(onClick = { showConfirmCheckout = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }*/
            if (showConfirmCheckout) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showConfirmCheckout = false
                        phoneNumber = ""
                        isPhoneNumberValid = true
                    },
                    sheetState = rememberModalBottomSheetState(),
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp)
                            .padding(bottom = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Payment Information",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = {
                                phoneNumber = it
                                isPhoneNumberValid = it.isEmpty() || it.matches(phonePattern)
                            },
                            label = { Text("Phone Number for Payment") },
                            placeholder = { Text("Enter 10-digit phone number") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            isError = !isPhoneNumberValid,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            supportingText = {
                                if (!isPhoneNumberValid) {
                                    Text("Please enter valid 10-digit phone number")
                                }
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = "Phone"
                                )
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Total Amount: Ksh ${totalPrice.format(2)}",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = "Inventory quantities will be updated after payment",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    showConfirmCheckout = false
                                    phoneNumber = ""
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancel")
                            }

                            Button(
                                onClick = {
                                    if (phoneNumber.matches(phonePattern)) {
                                        Toast.makeText(context, "Processing payment on $phoneNumber", Toast.LENGTH_SHORT).show()
                                        cartViewModel.checkout()
                                        showConfirmCheckout = false
                                        phoneNumber = ""
                                    } else {
                                        isPhoneNumberValid = false
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                enabled = phoneNumber.matches(phonePattern)
                            ) {
                                Text("Pay Now")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CartItem(product: Product, onRemove: () -> Unit, cartViewModel: CartViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(
                width = 1.dp,
                color = Color.Black.copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Product info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Ksh %.2f".format(product.price),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Quantity controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Card(
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    IconButton(
                        onClick = { cartViewModel.decrementQuantity(product.id) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowDown,
                            contentDescription = "Decrease Quantity",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${cartViewModel.getQuantity(product.id)}",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.width(8.dp))
                Card(
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    IconButton(
                        onClick = { cartViewModel.incrementQuantity(product.id) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Increase Quantity",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(20.dp))

                // Delete button
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            color = Color.Red.copy(alpha = 0.1f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Remove",
                        tint = Color.Red,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun CartItemPreview() {
    val product = Product(
        id = "1",
        name = "Sample Product",
        price = 100.0,
        imageUrl = "",
        imageResId = 0
    )
    val cartViewModel: CartViewModel = viewModel()
    CartItem(product, onRemove = {}, cartViewModel)

}