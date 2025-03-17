package com.example.chukaonlinegrocerystore.model

import androidx.annotation.Keep

@Keep
data class Product(
    val id: String = "",           // Firestore document ID (or unique identifier)
    val name: String = "",
    val price: Double = 0.0,
    val category: String = "",
    val quantity: Int = 0,         // Inventory quantity
    val imageResId: Int = 0        // Drawable resource ID (if needed)
)
