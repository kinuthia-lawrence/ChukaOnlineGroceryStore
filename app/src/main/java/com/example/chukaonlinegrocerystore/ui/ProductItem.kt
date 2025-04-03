package com.example.chukaonlinegrocerystore.ui

import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chukaonlinegrocerystore.R
import com.example.chukaonlinegrocerystore.model.Product
import com.example.chukaonlinegrocerystore.ui.theme.lightGray
import com.example.chukaonlinegrocerystore.viewmodel.CartViewModel

@Composable
fun ProductItem(product: Product, cartViewModel: CartViewModel) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(
                width = 1.dp,
                color = Color.Black.copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = lightGray)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product Image
            if (product.imageUrl.isNotEmpty()) {
                // If we have a Base64 image string, decode and display it
                val bitmap = remember(product.imageUrl) {
                    try {
                        val imageBytes = Base64.decode(product.imageUrl, Base64.DEFAULT)
                        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    } catch (e: Exception) {
                        null
                    }
                }

                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = product.name,
                        modifier = Modifier.size(64.dp)
                    )
                } else {
                    // Fallback if decoding fails
                    Image(
                        painter = painterResource(id = R.drawable.groceries),
                        contentDescription = product.name,
                        modifier = Modifier.size(64.dp)
                    )
                }
            } else {
                // Fallback for no image
                Image(
                    painter = painterResource(id = product.imageResId.takeIf { it != 0 }
                        ?: R.drawable.groceries),
                    contentDescription = product.name,
                    modifier = Modifier.size(64.dp)
                )
            }
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
            OutlinedButton(
                onClick = {
                    val alreadyInCart = cartViewModel.addToCart(product)
                    if (alreadyInCart) {
                        Toast.makeText(
                            context,
                            "${product.name} is already in yout cart",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(context, "${product.name} added to cart", Toast.LENGTH_SHORT)
                            .show()
                    }
                },
                modifier = Modifier.height(28.dp),
                contentPadding = PaddingValues(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        modifier = Modifier.size(12.dp)
                    )
                    Text("Add", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}


@Preview
@Composable
fun ProductItemPreview() {
    val product = Product(
        id = 1.toString(),
        name = "Sample Product",
        price = 100.0,
        imageResId = R.drawable.groceries
    )
    val cartViewModel = CartViewModel()
    ProductItem(product, cartViewModel)
}