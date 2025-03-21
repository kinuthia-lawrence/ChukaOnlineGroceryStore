package com.example.chukaonlinegrocerystore.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.chukaonlinegrocerystore.model.Product
import com.example.chukaonlinegrocerystore.viewmodel.SellerViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerDashboard(
    navController: NavHostController,
    sellerViewModel: SellerViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var showAddProduct by remember { mutableStateOf(false) }
    var showInventory by remember { mutableStateOf(false) }

    val uiState by sellerViewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Seller Dashboard") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    showAddProduct = true
                    showInventory = false
                }) {
                    Text("Add Item")
                }
                Button(onClick = {
                    showInventory = true
                    showAddProduct = false
                }) {
                    Text("View Items")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (showAddProduct) {
                Text("Add / Update Product", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = uiState.productName,
                    onValueChange = { sellerViewModel.onProductNameChanged(it) },
                    label = { Text("Product Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.productPrice,
                    onValueChange = { sellerViewModel.onProductPriceChanged(it) },
                    label = { Text("Price") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.productCategory,
                    onValueChange = { sellerViewModel.onProductCategoryChanged(it) },
                    label = { Text("Category") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.productQuantity,
                    onValueChange = { sellerViewModel.onProductQuantityChanged(it) },
                    label = { Text("Quantity") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {
                        coroutineScope.launch {
                            sellerViewModel.addOrUpdateProduct(null) // No Image
                        }
                    }) {
                        Text("Save Product")
                    }
                    Button(onClick = { sellerViewModel.clearFields() }) {
                        Text("Clear")
                    }
                }
            }

            if (showInventory) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Your Inventory", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn {
                    items(uiState.sellerProducts) { product ->
                        SellerProductItem(
                            product = product,
                            onDeleteClick = {
                                coroutineScope.launch {
                                    sellerViewModel.deleteProduct(product)
                                }
                            },
                            onEditClick = { sellerViewModel.loadProductForEditing(product) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SellerProductItem(
    product: Product,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Price: Ksh ${product.price} | Qty: ${product.quantity}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Category: ${product.category}",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onEditClick) {
                    Text("Edit")
                }
                OutlinedButton(onClick = onDeleteClick) {
                    Text("Delete")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SellerDashboardPreview() {
    MaterialTheme {
        SellerDashboard(navController = rememberNavController())
    }
}