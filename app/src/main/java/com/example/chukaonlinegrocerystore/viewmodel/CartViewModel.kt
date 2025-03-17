package com.example.chukaonlinegrocerystore.viewmodel

import androidx.lifecycle.ViewModel
import com.example.chukaonlinegrocerystore.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CartViewModel : ViewModel() {

    private val _cartItems = MutableStateFlow<List<Product>>(emptyList())
    val cartItems: StateFlow<List<Product>> = _cartItems

    private val _totalPrice = MutableStateFlow(0.0)
    val totalPrice: StateFlow<Double> = _totalPrice

    fun addToCart(product: Product) {
        _cartItems.value = _cartItems.value.toMutableList().apply { add(product) }
        updateTotalPrice()
    }

    fun removeFromCart(product: Product) {
        _cartItems.value = _cartItems.value.filterNot { it == product }
        updateTotalPrice()
    }

    fun clearCart() {
        _cartItems.value = emptyList()
        _totalPrice.value = 0.0
    }

    private fun updateTotalPrice() {
        _totalPrice.value = _cartItems.value.sumOf { it.price }
    }
}
