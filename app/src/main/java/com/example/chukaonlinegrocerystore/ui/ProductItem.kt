package com.example.chukaonlinegrocerystore.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.chukaonlinegrocerystore.model.Product
import com.example.chukaonlinegrocerystore.viewmodel.CartViewModel

@Composable
fun ProductItem(product: Product, cartViewModel: CartViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product Image
            Image(
                painter = painterResource(id = product.image),
                contentDescription = product.name,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            // Product Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "Ksh ${product.price}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            // Add to Cart Button
            Button(onClick = { cartViewModel.addToCart(product) }) {
                Text("Add")
            }
        }
    }
}
