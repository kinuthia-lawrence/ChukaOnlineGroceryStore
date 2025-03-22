package com.example.chukaonlinegrocerystore.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.chukaonlinegrocerystore.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BuyerViewModel : ViewModel() {
    private val _buyerProducts = MutableStateFlow<List<Product>>(emptyList())
    val buyerProducts: StateFlow<List<Product>> = _buyerProducts

    private val firestore = FirebaseFirestore.getInstance();

    fun fetchProducts() {
        firestore.collectionGroup("inventory")
            .get()
            .addOnSuccessListener { snapshot ->
                val products = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Product::class.java)?.copy(id = doc.id)
                }
                _buyerProducts.value = products
            }
            .addOnFailureListener { exception ->
                Log.e("BuyerViewModel", "Error fetching products: ", exception)
            }
    }
}