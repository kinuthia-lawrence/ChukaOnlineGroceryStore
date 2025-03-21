package com.example.chukaonlinegrocerystore.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.chukaonlinegrocerystore.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await

data class SellerUiState(
    val productName: String = "",
    val productPrice: String = "",
    val productCategory: String = "",
    val productQuantity: String = "",
    val sellerProducts: List<Product> = emptyList()
)

class SellerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SellerUiState())
    val uiState: StateFlow<SellerUiState> = _uiState

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    // For development/testing, if no user is logged in, we use a test UID.
    // In production, the current user's UID will be non-null.
    private val sellerUid: String? = auth.currentUser?.uid ?: "testSeller"

    init {
        observeSellerInventory()
    }

    private fun observeSellerInventory() {
        sellerUid?.let { uid ->
            firestore.collection("users")
                .document(uid)
                .collection("inventory")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        // Log or handle the error appropriately.
                        return@addSnapshotListener
                    }
                    snapshot?.let {
                        val products = it.documents.mapNotNull { doc ->
                            doc.toObject(Product::class.java)?.copy(id = doc.id)
                        }
                        _uiState.update { currentState ->
                            currentState.copy(sellerProducts = products)
                        }
                    }
                }
        }
    }

    fun onProductNameChanged(name: String) {
        _uiState.update { it.copy(productName = name) }
    }

    fun onProductPriceChanged(price: String) {
        _uiState.update { it.copy(productPrice = price) }
    }

    fun onProductCategoryChanged(category: String) {
        _uiState.update { it.copy(productCategory = category) }
    }

    fun onProductQuantityChanged(quantity: String) {
        _uiState.update { it.copy(productQuantity = quantity) }
    }

    fun clearFields() {
        _uiState.update {
            it.copy(
                productName = "",
                productPrice = "",
                productCategory = "",
                productQuantity = ""
            )
        }
    }

    // Adds a new product to the seller's inventory in Firestore.
    // In production, you should add error handling and input validation.
    suspend fun addOrUpdateProduct(imageUrl: imageUrl) {
        val name = _uiState.value.productName
        val price = _uiState.value.productPrice.toDoubleOrNull() ?: 0.0
        val category = _uiState.value.productCategory
        val quantity = _uiState.value.productQuantity.toIntOrNull() ?: 0

        if (sellerUid == null) return

        val productData = mapOf(
            "name" to name,
            "price" to price,
            "category" to category,
            "quantity" to quantity
        )

        firestore.collection("users")
            .document(sellerUid)
            .collection("inventory")
            .add(productData)
            .await()

        clearFields()
    }

    // Deletes a product from the seller's inventory.
    suspend fun deleteProduct(product: Product) {
        if (sellerUid == null) return
        firestore.collection("users")
            .document(sellerUid)
            .collection("inventory")
            .document(product.id)
            .delete()
            .await()
    }

    // Loads a product's details into the UI state for editing.
    fun loadProductForEditing(product: Product) {
        _uiState.update {
            it.copy(
                productName = product.name,
                productPrice = product.price.toString(),
                productCategory = product.category,
                productQuantity = product.quantity.toString()
            )
        }
    }
}