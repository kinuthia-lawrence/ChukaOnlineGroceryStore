package com.example.chukaonlinegrocerystore.model

import androidx.annotation.Keep
import com.example.chukaonlinegrocerystore.enums.ProductCategory

@Keep
data class Product(
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val category: ProductCategory = ProductCategory.FRUITS,
    val quantity: Int = 0,
    val imageResId: Int = 0
) {
    constructor() : this("", "", 0.0, ProductCategory.FRUITS, 0, 0)
}
