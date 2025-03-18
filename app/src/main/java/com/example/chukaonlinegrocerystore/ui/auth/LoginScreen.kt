package com.example.chukaonlinegrocerystore.ui.auth

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavHostController, userType: String) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Layout for login screen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Login as $userType", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            shape = RoundedCornerShape(30),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            shape = RoundedCornerShape(30),
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }
        Button(
            onClick = {
                isLoading = true
                val auth = FirebaseAuth.getInstance()
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        isLoading = false
                        if (task.isSuccessful) {
                            val uid = task.result?.user?.uid
                            if (uid != null) {
                                val firestore = FirebaseFirestore.getInstance()
                                firestore.collection("users").document(uid)
                                    .get()
                                    .addOnSuccessListener { document ->
                                        if (document != null) {
                                            val role = document.getString("role")
                                            if (role != null && role.equals(userType, ignoreCase = true)) {
                                                val destination = if (userType == "BUYER") "buyer_dashboard" else "seller_dashboard"
                                                navController.navigate(destination) {
                                                    popUpTo(0) { inclusive = true }
                                                }
                                            } else {
                                                errorMessage = "Role mismatch. Please check your login credentials."
                                            }
                                        } else {
                                            errorMessage = "User data not found."
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        errorMessage = "Failed to retrieve user data: ${e.message}"
                                    }
                            }
                        } else {
                            errorMessage = task.exception?.message ?: "Login failed"
                            Log.d("LoginError", task.exception.toString())
                        }
                    }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.Green)
            } else {
                Text("Login")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = { navController.navigate("register/$userType") }) {
            Text("Don't have an account? Register")
        }
    }
}