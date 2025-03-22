package com.example.chukaonlinegrocerystore.ui.auth

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.chukaonlinegrocerystore.enums.Role
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(navController: NavHostController, userType: String) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var storeName by remember { mutableStateOf("") }
    var storeAddress by remember { mutableStateOf("") }
    var storePhoneNumber by remember { mutableStateOf("") }
    var paymentMethods by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val role = if (userType == "BUYER") Role.BUYER else Role.SELLER
    val context = LocalContext.current

    fun clearFieldsAndNavigate() {
        email = ""
        password = ""
        confirmPassword = ""
        name = ""
        address = ""
        phone = ""
        storeName = ""
        storeAddress = ""
        storePhoneNumber = ""
        paymentMethods = ""
        errorMessage = ""
        Toast.makeText(context, "Registration successful", Toast.LENGTH_SHORT).show()
        navController.navigate("login/$userType") {
            popUpTo("register") { inclusive = true }
        }
    }
    fun saveBuyerData(firestore: FirebaseFirestore, uid: String) {
        val buyerData = mapOf(
            "address" to address,
            "phone" to phone,
            "paymentMethods" to paymentMethods.split(",").map { it.trim() }
        )

        Log.d("REGISTRATION", "Saving buyer data")
        firestore.collection("buyers").document(uid)
            .set(buyerData)
            .addOnSuccessListener {
                Log.d("REGISTRATION", "Buyer data saved successfully")
                clearFieldsAndNavigate()
            }
            .addOnFailureListener { e ->
                Log.e("REGISTRATION", "Failed to save buyer data", e)
                errorMessage = "Failed to save buyer data: ${e.message}"
            }
    }

    fun saveSellerData(firestore: FirebaseFirestore, uid: String) {
        val sellerData = mapOf(
            "storeName" to storeName,
            "storeAddress" to storeAddress,
            "storePhoneNumber" to storePhoneNumber
        )

        Log.d("REGISTRATION", "Saving seller data")
        firestore.collection("sellers").document(uid)
            .set(sellerData)
            .addOnSuccessListener {
                Log.d("REGISTRATION", "Seller data saved successfully")
                clearFieldsAndNavigate()
            }
            .addOnFailureListener { e ->
                Log.e("REGISTRATION", "Failed to save seller data", e)
                errorMessage = "Failed to save seller data: ${e.message}"
            }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                "Register as ${role.name}",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                shape = RoundedCornerShape(30),
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
        item {
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = run {
                        it.trim().lowercase(Locale.ROOT)
                    }
                },
                label = { Text("Email") },
                shape = RoundedCornerShape(30),
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
        item {
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                shape = RoundedCornerShape(30),
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )
        }
        item {
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                shape = RoundedCornerShape(30),
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
        if (role == Role.BUYER) {
            item {
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address") },
                    shape = RoundedCornerShape(30),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    shape = RoundedCornerShape(30),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                OutlinedTextField(
                    value = paymentMethods,
                    onValueChange = { paymentMethods = it },
                    shape = RoundedCornerShape(30),
                    label = { Text("Payment Methods (comma separated)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            item {
                OutlinedTextField(
                    value = storeName,
                    onValueChange = { storeName = it },
                    label = { Text("Store Name") },
                    shape = RoundedCornerShape(30),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                OutlinedTextField(
                    value = storeAddress,
                    onValueChange = { storeAddress = it },
                    label = { Text("Store Address") },
                    shape = RoundedCornerShape(30),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                OutlinedTextField(
                    value = storePhoneNumber,
                    onValueChange = { storePhoneNumber = it },
                    label = { Text("Store Phone Number") },
                    shape = RoundedCornerShape(30),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        item {
            Button(
                onClick = {
                    //check if password and confirm password match
                    if (password.length < 6) {
                        errorMessage = "Password must be at least 6 characters"
                        return@Button
                    }
                    if (password != confirmPassword) {
                        errorMessage = "Passwords do not match"
                        return@Button
                    }
                    //check if any field is empty
                    if (email.isEmpty() || password.isEmpty() || name.isEmpty() ||
                        (role == Role.BUYER && (address.isEmpty() || phone.isEmpty() || paymentMethods.isEmpty())) ||
                        (role == Role.SELLER && (storeName.isEmpty() || storeAddress.isEmpty() || storePhoneNumber.isEmpty()))
                    ) {
                        errorMessage = "All fields are required"
                        return@Button
                    }

                    isLoading = true
                    Log.d("REGISTRATION", "Starting registration process for $email")

                    val auth = FirebaseAuth.getInstance()
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            isLoading = false
                            if (task.isSuccessful) {
                                Log.d("REGISTRATION", "Auth created successfully")
                                val uid = task.result?.user?.uid
                                if (uid != null) {
                                    val firestore = FirebaseFirestore.getInstance()
                                    val userData = mapOf(
                                        "name" to name,
                                        "email" to email,
                                        "role" to role.name
                                    )

                                    Log.d("REGISTRATION", "Saving user data for $uid")
                                    firestore.collection("users").document(uid)
                                        .set(userData)
                                        .addOnSuccessListener {
                                            Log.d("REGISTRATION", "User data saved successfully")

                                            try {
                                                // Run on UI thread to ensure UI operations work
                                                (context as? android.app.Activity)?.runOnUiThread {
                                                    if (role == Role.BUYER) {
                                                        saveBuyerData(firestore, uid)
                                                    } else {
                                                        saveSellerData(firestore, uid)
                                                    }
                                                }
                                            } catch (e: Exception) {
                                                Log.e("REGISTRATION", "UI thread error: ${e.message}", e)
                                                errorMessage = "UI error: ${e.message}"
                                            }
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e("REGISTRATION", "Failed to save user data", e)
                                            errorMessage = "Failed to save user data: ${e.message}"
                                        }
                                }
                            } else {
                                Log.e("REGISTRATION", "Auth failed: ${task.exception?.message}")
                                errorMessage = task.exception?.message ?: "Registration failed"
                            }
                        }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.Green)
                } else {
                    Text("Register as a ${role.name}")
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = { navController.navigate("login/$userType") }) {
                Text("Already have an account? Login")
            }
        }
    }

}