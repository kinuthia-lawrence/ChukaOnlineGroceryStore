package com.example.chukaonlinegrocerystore.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.chukaonlinegrocerystore.model.Product
import com.example.chukaonlinegrocerystore.viewmodel.CartViewModel
import com.example.chukaonlinegrocerystore.R
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import com.example.chukaonlinegrocerystore.ui.ProductItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyerDashboard(
    navController: NavHostController,
    cartViewModel: CartViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }

    // Sample product list
    val products = listOf(
        Product("1", "Apple", 1.99, "Fruits", 5, R.drawable.apple),
        Product("2", "Banana", 0.99, "Fruits", 5, R.drawable.banana),
        Product("3", "Carrot", 1.49, "Vegetables", 5, R.drawable.carrot),
        Product("4", "Milk", 2.49, "Dairy", 5, R.drawable.milk),
        Product("5", "Juice", 3.99, "Beverages", 5, R.drawable.juice)
    )

    // Filter logic
    val filteredProducts = products.filter { product ->
        (selectedCategory.isEmpty() || product.category.equals(
            selectedCategory,
            ignoreCase = true
        )) &&
                (searchQuery.isEmpty() || product.name.contains(searchQuery, ignoreCase = true))
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("cart_screen") }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_cart),
                    contentDescription = "Cart"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // "All" means reset to empty category
                FilterButton("All", selectedCategory) { selectedCategory = "" }
                FilterButton("Fruits", selectedCategory) { selectedCategory = "Fruits" }
                FilterButton("Vegetables", selectedCategory) { selectedCategory = "Vegetables" }
                FilterButton("Dairy", selectedCategory) { selectedCategory = "Dairy" }
                FilterButton("Beverages", selectedCategory) { selectedCategory = "Beverages" }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(filteredProducts) { product ->
                    ProductItem(product, cartViewModel)
                }
            }
        }
    }
}

@Composable
fun FilterButton(
    category: String,
    selectedCategory: String,
    onClick: () -> Unit
) {
    val isSelected = (category == "All" && selectedCategory.isEmpty()) ||
            category.equals(selectedCategory, ignoreCase = true)
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Text(text = category, color = Color.Black)
    }
}
