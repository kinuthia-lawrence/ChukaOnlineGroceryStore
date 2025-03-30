package com.example.chukaonlinegrocerystore.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.chukaonlinegrocerystore.R
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(navController: NavController) {
    var showSplash by remember { mutableStateOf(true) }
    var showAuthOptions by remember { mutableStateOf(false) }

    val logoScale by animateFloatAsState(
        targetValue = if (showAuthOptions) 0.7f else 1f,
        animationSpec = tween(durationMillis = 1500)
    )

    val logoAlpha = animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 1500)
    )

    LaunchedEffect(key1 = true) {
        delay(1800)
        showAuthOptions = true
        delay(1500)
        showSplash = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = if (showSplash && !showAuthOptions) Arrangement.Center else Arrangement.Top
    ) {
        // Splash content that stays
        Spacer(modifier = Modifier.height(if (showAuthOptions) 60.dp else 0.dp))

        Image(
            painter = painterResource(id = R.drawable.groceries),
            contentDescription = "Grocery Store Logo",
            modifier = Modifier
                .size((120 * logoScale).dp)
                .alpha(logoAlpha.value)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Chuka Online Grocery Store",
            fontSize = if (showAuthOptions) 22.sp else 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            lineHeight = 32.sp,
            modifier = Modifier.alpha(logoAlpha.value)
        )

        if (!showSplash || showAuthOptions) {
            AnimatedVisibility(
                visible = showAuthOptions,
                enter = fadeIn(animationSpec = tween(2000)) +
                        slideInVertically(animationSpec = tween(2000)) { it / 2 }
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(40.dp))

                    Text(
                        text = "Continue as",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { navController.navigate("login/BUYER") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(text = "Buyer", color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { navController.navigate("login/SELLER") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text(text = "Seller", color = Color.White)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun AuthScreenPreview() {
    AuthScreen(navController = rememberNavController())
}