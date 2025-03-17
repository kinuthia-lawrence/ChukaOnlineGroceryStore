package com.example.chukaonlinegrocerystore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.chukaonlinegrocerystore.navigation.AppNavigation
import com.example.chukaonlinegrocerystore.ui.theme.ChukaOnlineGroceryStoreTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChukaOnlineGroceryStoreTheme {
                val navController = rememberNavController()
                AppNavigation(navController)
            }
        }
    }
}
