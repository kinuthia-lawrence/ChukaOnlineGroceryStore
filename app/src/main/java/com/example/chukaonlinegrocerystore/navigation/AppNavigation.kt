package com.example.chukaonlinegrocerystore.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chukaonlinegrocerystore.ui.auth.LoginScreen
import com.example.chukaonlinegrocerystore.ui.auth.RegistrationScreen
import com.example.chukaonlinegrocerystore.ui.BuyerDashboard
import com.example.chukaonlinegrocerystore.ui.SellerDashboard
import com.example.chukaonlinegrocerystore.ui.CartScreen
import com.example.chukaonlinegrocerystore.viewmodel.CartViewModel

@Composable
fun AppNavigation(navController: NavHostController) {
    // Create a NavController instance
    val navController = rememberNavController()

    // Create/remember a single CartViewModel instance to share between buyer-related screens
    val cartViewModel: CartViewModel = viewModel()

    NavHost(navController = navController, startDestination = "login") {
        // Authentication Screens
        composable("login") {
            LoginScreen(navController)
        }
        composable("register") {
            RegistrationScreen(navController)
        }
        // Buyer Dashboard: Navigated to after login as buyer
        composable("buyer_dashboard") {
            BuyerDashboard(navController, cartViewModel)
        }
        // Seller Dashboard: Navigated to after login as seller
        composable("seller_dashboard") {
            SellerDashboard(navController)
        }
        // Cart Screen: For buyers to review and manage cart items
        composable("cart_screen") {
            CartScreen(navController, cartViewModel)
        }
    }
}
