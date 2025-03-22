package com.example.chukaonlinegrocerystore.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.chukaonlinegrocerystore.model.Product
import com.example.chukaonlinegrocerystore.viewmodel.SellerViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerDashboard(
    navController: NavHostController,
    sellerViewModel: SellerViewModel = viewModel()
) {

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Inventory", "Add Product")

    val uiState by sellerViewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val productToDelete by sellerViewModel.productToDelete.collectAsState()
    val productToEdit by sellerViewModel.productToEdit.collectAsState()
    var showEditingDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Seller Dashboard",
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(text = title) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            //Display Products
            if (selectedTabIndex == 0) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Your Inventory", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn {
                    items(uiState.sellerProducts) { product ->
                        SellerProductItem(
                            product = product,
                            onDeleteClick = {
                                sellerViewModel.setProductToDelete(product)
                            },
                            onEditClick = {
                                sellerViewModel.setProductToEdit(product)
                                showEditingDialog = true
                            }
                        )
                    }
                }
            }

            //Add Product
            if (selectedTabIndex == 1) {
                Text(
                    text = "Add Product",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
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
                            productToEdit?.let { product ->
                                sellerViewModel.updateProduct(product, context)
                            } ?: sellerViewModel.addProduct(
                                null,
                                context
                            ) // Add new product if null
                        }
                    }) {
                        Text(text = if (productToEdit == null) "Add" else "Update")
                    }
                    Button(onClick = { sellerViewModel.clearFields() }) {
                        Text("Clear")
                    }
                }
            }

        }
    }
    //Delete Confirm Dialog
    productToDelete?.let { product ->
        BasicAlertDialog(
            onDismissRequest = { sellerViewModel.setProductToDelete(null) },
            modifier = Modifier.padding(16.dp)
        ) {
            Card(
                modifier = Modifier.padding(16.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Confirm Delete", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Are your sure you want to delete ${product.name}?")
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                sellerViewModel.setProductToDelete(null)
                                coroutineScope.launch {
                                    sellerViewModel.deleteProduct(product)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) { Text("Delete") }
                        OutlinedButton(onClick = { sellerViewModel.setProductToDelete(null) }) {
                            Text(
                                "Cancel"
                            )
                        }
                    }

                }
            }
        }

    }
    //Edit Confirm Dialog
    productToEdit?.let { product ->
        if (showEditingDialog) {
            BasicAlertDialog(
                onDismissRequest = { showEditingDialog = false },
                modifier = Modifier.padding(16.dp)
            ) {
                Card(
                    modifier = Modifier.padding(16.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Confirm Update", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Are your sure you want to Update ${product.name}?")
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    sellerViewModel.loadProductForEditing(product)
                                    selectedTabIndex = 1
                                    showEditingDialog = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                            ) { Text("Update") }
                            OutlinedButton(onClick = { sellerViewModel.setProductToEdit(null) }) {
                                Text(
                                    "Cancel"
                                )
                            }
                        }

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