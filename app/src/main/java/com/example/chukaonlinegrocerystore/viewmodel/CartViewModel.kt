package com.example.chukaonlinegrocerystore.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chukaonlinegrocerystore.model.Product
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import kotlin.random.Random

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

    // In CartViewModel.kt - Update the checkout() method
    fun checkout(buyerPhone: String = "") {
        viewModelScope.launch {
            _checkoutState.value = CheckoutState.Loading
            try {
                val db = FirebaseFirestore.getInstance()
                val batch = db.batch()

                // Process each item in cart
                _cartItems.value.forEach { (_, cartItem) ->
                    val product = cartItem.product
                    val quantity = cartItem.quantity
                    val totalAmount = product.price * quantity

                    // Update inventory quantity
                    val productRef = db.collection("sellers")
                        .document(product.SellerId)
                        .collection("inventory")
                        .document(product.id)

                    batch.update(productRef, "quantity", FieldValue.increment(-quantity.toLong()))

                    // Record the sale in seller's sales collection
                    val saleData = hashMapOf(
                        "productId" to product.id,
                        "productName" to product.name,
                        "quantity" to quantity,
                        "price" to product.price,
                        "totalAmount" to totalAmount,
                        "buyerPhone" to buyerPhone,
                        "timestamp" to FieldValue.serverTimestamp(),
                        "sellerId" to product.SellerId
                    )

                    val saleRef = db.collection("sellers")
                        .document(product.SellerId)
                        .collection("sales")
                        .document()

                    batch.set(saleRef, saleData)
                }

                // Execute all updates atomically
                batch.commit().await()
                clearCart()
                _checkoutState.value = CheckoutState.Success
            } catch (e: Exception) {
                Log.e("CartViewModel", "Checkout failed", e)
                _checkoutState.value = CheckoutState.Error(e.message ?: "Checkout failed")
            }
        }
    }


    fun insertDummySalesData() {
        // Launch a coroutine to perform Firestore operations.
        viewModelScope.launch {
            val firestore = FirebaseFirestore.getInstance()
            val sellerID = "M9D26AAUz9fcWDX6XYUxFGNebX52"
            val salesCollection =
                firestore.collection("sellers").document(sellerID).collection("sales")

            // Sample product data
            val products = listOf(
                Triple("Apples", 85.0, "Product1"),
                Triple("Bananas", 65.0, "Product2"),
                Triple("Oranges", 120.0, "Product3"),
                Triple("Milk", 150.0, "Product4"),
                Triple("Bread", 95.0, "Product5"),
                Triple("Tomatoes", 75.0, "Product6"),
                Triple("Potatoes", 150.0, "Product7"),
                Triple("Spinach", 45.0, "Product8")
            )

            // Phone numbers for buyers
            val phoneNumbers = listOf("0712345678", "0723456789", "0734567890", "0745678901")

            // Generate sales over the past 60 days
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -60) // Start 60 days ago

            var batch = firestore.batch()
            var count = 0

            try {
                // Create 100 random sales spread over 60 days
                repeat(100) {
                    // Random product
                    val (productName, price, productId) = products.random()

                    // Random date within the past 60 days
                    calendar.add(Calendar.DAY_OF_YEAR, Random.nextInt(0, 3))
                    calendar.add(Calendar.HOUR_OF_DAY, Random.nextInt(8, 20))
                    calendar.add(Calendar.MINUTE, Random.nextInt(0, 60))

                    val timestamp = Timestamp(calendar.time)
                    val quantity = Random.nextInt(1, 6)
                    val totalAmount = price * quantity
                    val buyerPhone = phoneNumbers.random()

                    val sale = hashMapOf(
                        "productId" to productId,
                        "productName" to productName,
                        "quantity" to quantity,
                        "price" to price,
                        "totalAmount" to totalAmount,
                        "buyerPhone" to buyerPhone,
                        "timestamp" to timestamp,
                        "sellerId" to sellerID
                    )

                    val document = salesCollection.document()
                    batch.set(document, sale)

                    count++

                    // Commit in batches of 20
                    if (count % 20 == 0) {
                        batch.commit().await()
                        batch = firestore.batch() // Create a new batch after committing
                    }
                }

                // Commit any remaining documents
                batch.commit().await()

                Log.d("CartViewModel", "Successfully inserted dummy sales data")
            } catch (e: Exception) {
                Log.e("CartViewModel", "Error inserting dummy sales data", e)
            }
        }
    }
}
