package com.example.chukaonlinegrocerystore.model

import com.google.firebase.Timestamp
import java.util.Date

data class Sale(
    val id: String = "",
    val productId: String = "",
    val productName: String = "",
    val quantity: Int = 0,
    val price: Double = 0.0,
    val totalAmount: Double = 0.0,
    val buyerPhone: String = "",
    val timestamp: Timestamp? = null,
    val sellerId: String = "",
    // Computed properties for easier access
    val date: Date = timestamp?.toDate() ?: Date()
)