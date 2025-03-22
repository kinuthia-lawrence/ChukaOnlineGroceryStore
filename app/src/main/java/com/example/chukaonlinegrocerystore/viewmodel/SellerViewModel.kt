package com.example.chukaonlinegrocerystore.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.chukaonlinegrocerystore.enums.ProductCategory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.chukaonlinegrocerystore.model.Product
import com.example.chukaonlinegrocerystore.ui.ProductItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await

data class SellerUiState(
    val productName: String = "",
    val productPrice: String = "",
    var productCategory: ProductCategory = ProductCategory.FRUITS,
    val productQuantity: String = "",
    val sellerProducts: List<Product> = emptyList()
)

class SellerViewModel : ViewModel() {
       private val _productToDelete = MutableStateFlow<Product?>(null)
    val productToDelete: StateFlow<Product?> = _productToDelete

    private val _productToEdit = MutableStateFlow<Product?>(null)
    val productToEdit: StateFlow<Product?> = _productToEdit

    fun setProductToDelete(product: Product?) {
        _productToDelete.value = product
    }

    fun setProductToEdit(product: Product?) {
        _productToEdit.value = product
    }

    private val _uiState = MutableStateFlow(SellerUiState())
    val uiState: StateFlow<SellerUiState> = _uiState

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val sellerUid: String? = auth.currentUser?.uid

    init {
        observeSellerInventory()
//        Log.d("SellerViewModel", "sellerUid:: $sellerUid")
    }

    private fun observeSellerInventory() {
        sellerUid?.let { uid ->
            firestore.collection("sellers")
                .document(uid)
                .collection("inventory")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        // Log or handle the error appropriately.
                        Log.d("SellerViewModel", "Error encountered $error")
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

    fun onProductCategoryChanged(category: ProductCategory) {
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
                productCategory = ProductCategory.FRUITS,
                productQuantity = ""
            )
        }
    }

    // Adds a new product to the seller's inventory in Firestore.
    // In production, you should add error handling and input validation.
    suspend fun addProduct(imageUrl: String?, context: Context) {
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

        firestore.collection("sellers")
            .document(sellerUid)
            .collection("inventory")
            .add(productData)
            .await()

        clearFields()
        Toast.makeText(context, "Product added successfully", Toast.LENGTH_SHORT).show()
    }

    suspend fun updateProduct(product: Product, context: Context) {
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

        firestore.collection("sellers")
            .document(sellerUid)
            .collection("inventory")
            .document(product.id)
            .update(productData)
            .await()

        clearFields()
        Toast.makeText(context, "Product updated successfully", Toast.LENGTH_SHORT).show()
        setProductToEdit(null)
    }

    // Deletes a product from the seller's inventory.
    suspend fun deleteProduct(product: Product) {
        if (sellerUid == null) return
        firestore.collection("sellers")
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