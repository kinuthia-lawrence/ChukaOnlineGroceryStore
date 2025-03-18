package com.example.chukaonlinegrocerystore.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val password: String = ""
)

data class Buyer(
    val user: User,
    val address: String = "",
    val phone: String = "",
    val paymentMethods: List<String> = emptyList()
)

data class Seller(
    val user: User,
    val storeName: String = "",
    val storeAddress: String = "",
    val storePhoneNumber: String = "",
    val products: List<Product> = emptyList()
)