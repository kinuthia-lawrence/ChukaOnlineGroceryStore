package com.example.chukaonlinegrocerystore.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Authentication") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Login as:", fontSize = 20.sp)

            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = { navController.navigate("buyer_dashboard") }) {
                Text(text = "Buyer")
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(onClick = { navController.navigate("seller_dashboard") }) {
                Text(text = "Seller")
            }
        }
    }
}