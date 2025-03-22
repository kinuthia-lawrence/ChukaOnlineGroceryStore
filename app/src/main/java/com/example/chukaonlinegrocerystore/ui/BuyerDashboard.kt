package com.example.chukaonlinegrocerystore.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.chukaonlinegrocerystore.enums.ProductCategory
import com.example.chukaonlinegrocerystore.ui.ProductItem

@Composable
fun BuyerDashboard(
    navController: NavHostController,
    cartViewModel: CartViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<ProductCategory?>(null) }

    // Sample product list
    val products = listOf(
        Product("1", "Apple", 1.99, ProductCategory.FRUITS, 5, R.drawable.apple),
        Product("2", "Banana", 0.99, ProductCategory.FRUITS, 5, R.drawable.banana),
        Product("3", "Carrot", 1.49, ProductCategory.VEGETABLES, 5, R.drawable.carrot),
        Product("4", "Milk", 2.49, ProductCategory.DAIRY, 5, R.drawable.milk),
        Product("5", "Juice", 3.99, ProductCategory.BEVERAGES, 5, R.drawable.juice)
    )

    // Filter logic
    val filteredProducts = products.filter { product ->
        (selectedCategory == null || product.category == selectedCategory) &&
                (searchQuery.isEmpty() || product.name.contains(searchQuery, ignoreCase = true))
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("cart_screen") },
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_cart),
                    contentDescription = "Cart",
                    modifier = Modifier.size(24.dp)
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
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // "All" resets category to null
                FilterButton("All", selectedCategory == null) { selectedCategory = null }
                FilterButton(
                    ProductCategory.FRUITS.name,
                    selectedCategory == ProductCategory.FRUITS
                ) {
                    selectedCategory = ProductCategory.FRUITS
                }
                FilterButton(
                    ProductCategory.VEGETABLES.name,
                    selectedCategory == ProductCategory.VEGETABLES
                ) {
                    selectedCategory = ProductCategory.VEGETABLES
                }
                FilterButton(
                    ProductCategory.DAIRY.name,
                    selectedCategory == ProductCategory.DAIRY
                ) {
                    selectedCategory = ProductCategory.DAIRY
                }
                FilterButton(
                    ProductCategory.BEVERAGES.name,
                    selectedCategory == ProductCategory.BEVERAGES
                ) {
                    selectedCategory = ProductCategory.BEVERAGES
                }
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
    isSelected: Boolean,
    onClick: () -> Unit
) {
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
