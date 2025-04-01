package com.example.chukaonlinegrocerystore.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
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
            .background(Color.Green),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = if (showSplash && !showAuthOptions) Arrangement.Center else Arrangement.Top
    ) {
        // Splash content that stays
        Spacer(
            modifier = Modifier.height(
                when {
                    showAuthOptions -> (0.20f * LocalConfiguration.current.screenHeightDp).dp
                    else -> 0.dp
                }
            )
        )
        Image(
            painter = painterResource(id = R.drawable.groceries),
            contentDescription = "Grocery Store Logo",
            modifier = Modifier
                .size((150 * logoScale).dp)
                .alpha(logoAlpha.value)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Chuka Online Grocery Store",
            fontSize = if (showAuthOptions) 30.sp else 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
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
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { navController.navigate("login/BUYER") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Green
                        )
                    ) {
                        Text(
                            text = "Buyer",
                            color = Color.Green,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 28.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { navController.navigate("login/SELLER") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Green
                        )
                    ) {
                        Text(
                            text = "Seller",
                            color = Color.Green,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 24.sp
                        )
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