package com.example.chukaonlinegrocerystore.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chukaonlinegrocerystore.model.Product
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CartViewModel : ViewModel() {
    private val _cartItems = MutableStateFlow<Map<String, CartItem>>(emptyMap())
    val cartItems: StateFlow<List<Product>> = _cartItems.map { map ->
        map.values.map { it.product }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _totalPrice = MutableStateFlow(0.0)
    val totalPrice: StateFlow<Double> = _totalPrice

    data class CartItem(val product: Product, val quantity: Int = 1)

    private val _checkoutState = MutableStateFlow<CheckoutState>(CheckoutState.Idle)
    val checkoutState: StateFlow<CheckoutState> = _checkoutState

    sealed class CheckoutState {
        object Idle : CheckoutState()
        object Loading : CheckoutState()
        object Success : CheckoutState()
        data class Error(val message: String) : CheckoutState()
    }

    fun addToCart(product: Product): Boolean {
        val productExists = _cartItems.value.containsKey(product.id)

        if (!productExists) {
            val updatedCart = _cartItems.value.toMutableMap().apply {
                put(product.id, CartItem(product))
            }
            _cartItems.value = updatedCart
            updateTotalPrice()
        }

        return productExists
    }

    fun getQuantity(productId: String): Int {
        return _cartItems.value[productId]?.quantity ?: 0
    }

    fun incrementQuantity(productId: String) {
        val updatedCart = _cartItems.value.toMutableMap()
        updatedCart[productId]?.let { cartItem ->
            updatedCart[productId] = cartItem.copy(quantity = cartItem.quantity + 1)
            _cartItems.value = updatedCart
            updateTotalPrice()
        }
    }

    fun decrementQuantity(productId: String) {
        val updatedCart = _cartItems.value.toMutableMap()
        updatedCart[productId]?.let { cartItem ->
            if (cartItem.quantity > 1) {
                updatedCart[productId] = cartItem.copy(quantity = cartItem.quantity - 1)
                _cartItems.value = updatedCart
                updateTotalPrice()
            }
        }
        updateTotalPrice()
    }

    fun removeFromCart(product: Product) {
        _cartItems.value = _cartItems.value.toMutableMap().apply {
            remove(product.id)
        }
        updateTotalPrice()
    }

    fun clearCart() {
        _cartItems.value = emptyMap()
        _totalPrice.value = 0.0
    }

    private fun updateTotalPrice() {
        _totalPrice.value = _cartItems.value.values.sumOf {
            it.product.price * it.quantity
        }
    }

    fun checkout() {
        viewModelScope.launch {
            _checkoutState.value = CheckoutState.Loading
            try {
                val db = FirebaseFirestore.getInstance()
                val batch = db.batch()

                // Process each item in cart
                _cartItems.value.forEach { (_, cartItem) ->
                    val product = cartItem.product
                    val quantity = cartItem.quantity

                    // Find the seller's product document
                    val productRef = db.collection("sellers")
                        .document(product.SellerId)
                        .collection("inventory")
                        .document(product.id)

                    // Update quantity (ensure it never goes below 0)
                    batch.update(
                        productRef,
                        "quantity",
                        FieldValue.increment(-quantity.toLong())
                    )
                }

                // Execute all updates atomically
                batch.commit().await()

                // Clear cart after successful checkout
                clearCart()
                _checkoutState.value = CheckoutState.Success
            } catch (e: Exception) {
                Log.e("CartViewModel", "Checkout failed", e)
                _checkoutState.value = CheckoutState.Error(e.message ?: "Checkout failed")
            }
        }
    }
}
