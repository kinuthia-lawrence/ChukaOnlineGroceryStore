package com.example.chukaonlinegrocerystore.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.chukaonlinegrocerystore.enums.ProductCategory
import com.example.chukaonlinegrocerystore.model.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

data class SellerUiState(
    val productName: String = "",
    val productPrice: String = "",
    var productCategory: ProductCategory = ProductCategory.FRUITS,
    val productQuantity: String = "",
    val sellerProducts: List<Product> = emptyList(),
    val productImageUri: Uri? = null
)

class SellerViewModel : ViewModel() {
    private val _productToDelete = MutableStateFlow<Product?>(null)
    val productToDelete: StateFlow<Product?> = _productToDelete

    private val _productToEdit = MutableStateFlow<Product?>(null)
    val productToEdit: StateFlow<Product?> = _productToEdit

    private val currentSellerId: String
        get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""

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

    //? Updates the UI state when the product name changes.
    fun onProductNameChanged(name: String) {
        _uiState.update { it.copy(productName = name) }
    }

    fun onProductPriceChanged(price: Double) {
        _uiState.update { it.copy(productPrice = price.toString()) }
    }

    fun onProductCategoryChanged(category: ProductCategory) {
        _uiState.update { it.copy(productCategory = category) }
    }

    fun onProductQuantityChanged(quantity: Int) {
        _uiState.update { it.copy(productQuantity = quantity.toString()) }
    }

    fun onProductImageSelected(uri: Uri?) {
        _uiState.update { it.copy(productImageUri = uri) }
    }

    fun clearFields() {
        _uiState.update {
            it.copy(
                productName = "",
                productPrice = "",
                productCategory = ProductCategory.FRUITS,
                productQuantity = "",
                productImageUri = null
            )
        }
    }

    // Adds a new product to the seller's inventory in Firestore.
    // In production, you should add error handling and input validation.
    suspend fun addProduct(context: Context) {
        val name = _uiState.value.productName
        val price = _uiState.value.productPrice.toDoubleOrNull() ?: 0.0
        val category = _uiState.value.productCategory
        val quantity = _uiState.value.productQuantity.toIntOrNull() ?: 0
        val imageUri = _uiState.value.productImageUri

        if (sellerUid == null) return

        val imageUrl = imageUri?.let { convertImageToBase64(context, it) } ?: ""
        val productData = mapOf(
            "name" to name,
            "price" to price,
            "category" to category,
            "quantity" to quantity,
            "SellerId" to currentSellerId,
            "imageUrl" to imageUrl
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
        val sellerId = if (product.SellerId.isEmpty()) currentSellerId else product.SellerId
        val imageUri = _uiState.value.productImageUri

        if (sellerUid == null) return

        val imageUrl = imageUri?.let { convertImageToBase64(context, it) } ?: product.imageUrl ?: ""


        val productData = mapOf(
            "name" to name,
            "price" to price,
            "category" to category,
            "quantity" to quantity,
            "SellerId" to sellerId,
            "imageUrl" to imageUrl
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

    private suspend fun convertImageToBase64(context: Context, imageUri: Uri): String {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(imageUri)
                val bytes = inputStream?.readBytes() ?: ByteArray(0)
                inputStream?.close()
                Base64.encodeToString(bytes, Base64.DEFAULT)
            } catch (e: Exception) {
                Log.e("SellerViewModel", "Base64 conversion failed", e)
                ""
            }
        }
    }
    /*// Add image upload function
    private suspend fun uploadProductImage(imageUri: Uri): String {
        Log.d("SellerViewModel", "Starting image upload process with URI: $imageUri")

        return try {
            val storage = FirebaseStorage.getInstance()
            val fileName = UUID.randomUUID().toString()
            val storageRef = storage.reference
            val imageRef = storageRef.child("product_images/$sellerUid/$fileName")

            // Upload file and wait for completion
            val uploadTask = imageRef.putFile(imageUri)
            val taskSnapshot = uploadTask.await()
            Log.d("SellerViewModel", "Upload completed, bytes: ${taskSnapshot.bytesTransferred}")

            // Get download URL through task - this is the key part
            val downloadUrlTask = imageRef.downloadUrl
            val url = downloadUrlTask.await().toString()

            Log.d("SellerViewModel", "Download URL retrieved: $url")
            url
        } catch (e: Exception) {
            Log.e("SellerViewModel", "Upload failed with exception:", e)
            ""
        }
    }*/

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