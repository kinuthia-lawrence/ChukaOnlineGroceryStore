package com.example.chukaonlinegrocerystore.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontVariation.width
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.chukaonlinegrocerystore.R
import com.example.chukaonlinegrocerystore.enums.ProductCategory
import com.example.chukaonlinegrocerystore.model.Product
import com.example.chukaonlinegrocerystore.model.Sale
import com.example.chukaonlinegrocerystore.ui.theme.lightBrown
import com.example.chukaonlinegrocerystore.ui.theme.lightGray
import com.example.chukaonlinegrocerystore.viewmodel.CartViewModel
import com.example.chukaonlinegrocerystore.viewmodel.DailySales
import com.example.chukaonlinegrocerystore.viewmodel.MonthlySales
import com.example.chukaonlinegrocerystore.viewmodel.SalesReport
import com.example.chukaonlinegrocerystore.viewmodel.SellerViewModel
import com.example.chukaonlinegrocerystore.viewmodel.WeeklySales
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerDashboard(
    navController: NavHostController,
    sellerViewModel: SellerViewModel = viewModel()
) {

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Inventory", "Add Product", "Sales", "Reports")

    val uiState by sellerViewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val productToDelete by sellerViewModel.productToDelete.collectAsState()
    val productToEdit by sellerViewModel.productToEdit.collectAsState()
    val salesHistory by sellerViewModel.salesHistory.collectAsState()
    val salesReport by sellerViewModel.salesReport.collectAsState()
    var showEditingDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        sellerViewModel.fetchSalesData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Seller Dashboard",
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(text = title) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            //Display Products
            if (selectedTabIndex == 0) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Your Inventory", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn {
                    items(uiState.sellerProducts) { product ->
                        SellerProductItem(
                            product = product,
                            onDeleteClick = {
                                sellerViewModel.setProductToDelete(product)
                            },
                            onEditClick = {
                                sellerViewModel.setProductToEdit(product)
                                showEditingDialog = true
                            }
                        )
                    }
                }
            }
            //Add Product
            if (selectedTabIndex == 1) {
                Text(
                    text = "Add Product",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = uiState.productName,
                    onValueChange = { sellerViewModel.onProductNameChanged(it) },
                    label = { Text("Product Name") },
                    shape = RoundedCornerShape(30),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.productPrice,
                    onValueChange = { newValue ->
                        // Only accept valid decimal inputs
                        if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                            // Convert to Double (or 0.0 if invalid/empty)
                            val doubleValue = newValue.toDoubleOrNull() ?: 0.0
                            sellerViewModel.onProductPriceChanged(doubleValue)
                        }
                    },
                    label = { Text("Price") },
                    shape = RoundedCornerShape(30),
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                Spacer(modifier = Modifier.height(8.dp))
                CategoryDropdown(
                    selectedCategory = uiState.productCategory,
                    onCategorySelected = { sellerViewModel.onProductCategoryChanged(it) }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.productQuantity,
                    onValueChange = { newValue ->
                        if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                            val intValue = newValue.toIntOrNull() ?: 0
                            sellerViewModel.onProductQuantityChanged(intValue)
                        }
                    },
                    label = { Text("Quantity") },
                    shape = RoundedCornerShape(30),
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(8.dp))
                val context = LocalContext.current
                var bitmap by remember { mutableStateOf<Bitmap?>(null) }

                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.GetContent()
                ) { uri: Uri? ->
                    uri?.let {
                        sellerViewModel.onProductImageSelected(it)
                        // Display preview
                        bitmap = if (Build.VERSION.SDK_INT < 28) {
                            MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                        } else {
                            val source = ImageDecoder.createSource(context.contentResolver, it)
                            ImageDecoder.decodeBitmap(source)
                        }
                    }
                }

                // Image preview
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .padding(vertical = 8.dp)
                        .align(Alignment.CenterHorizontally),
                    contentAlignment = Alignment.Center
                ) {
                    val productBeingEdited = productToEdit

                    val localBitmap = bitmap
                    if (localBitmap != null) {
                        // Show newly selected image if available
                        Image(
                            bitmap = localBitmap.asImageBitmap(),
                            contentDescription = "Product Image",
                            modifier = Modifier.fillMaxHeight()
                        )
                    } else if (productBeingEdited != null && productBeingEdited.imageUrl.isNotEmpty()) {
                        // Show existing Base64 image when editing
                        val existingBitmap = remember(productBeingEdited.imageUrl) {
                            try {
                                val imageBytes =
                                    Base64.decode(productBeingEdited.imageUrl, Base64.DEFAULT)
                                BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                            } catch (e: Exception) {
                                null
                            }
                        }

                        if (existingBitmap != null) {
                            Image(
                                bitmap = existingBitmap.asImageBitmap(),
                                contentDescription = "Product Image",
                                modifier = Modifier.fillMaxHeight()
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.groceries),
                                contentDescription = "Default Image",
                                modifier = Modifier.size(100.dp)
                            )
                        }
                    } else {
                        // Default image when no image available
                        Image(
                            painter = painterResource(id = R.drawable.groceries),
                            contentDescription = "Default Image",
                            modifier = Modifier.size(100.dp)
                        )
                    }
                }

                Button(
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Pick Product Image")
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Button(onClick = {
                        coroutineScope.launch {
                            productToEdit?.let { product ->
                                sellerViewModel.updateProduct(product, context)
                            } ?: sellerViewModel.addProduct(
                                context
                            )
                        }
                    }, modifier = Modifier.weight(1f)) {
                        Text(text = if (productToEdit == null) "Add" else "Update")
                    }
                    Button(
                        onClick = { sellerViewModel.clearFields() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Clear")
                    }
                }
            }
            // Sales Tab
            if (selectedTabIndex == 2) {
                SalesTab(salesHistory)
            }
            // Reports Tab
            if (selectedTabIndex == 3) {
                ReportsTab(salesReport, uiState.sellerProducts)
            }
        }
    }
    //Delete Confirm Dialog
    productToDelete?.let { product ->
        BasicAlertDialog(
            onDismissRequest = { sellerViewModel.setProductToDelete(null) },
            modifier = Modifier.padding(16.dp)
        ) {
            Card(
                modifier = Modifier.padding(16.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Confirm Delete", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Are your sure you want to delete ${product.name}?")
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                sellerViewModel.setProductToDelete(null)
                                coroutineScope.launch {
                                    sellerViewModel.deleteProduct(product)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) { Text("Delete") }
                        OutlinedButton(onClick = { sellerViewModel.setProductToDelete(null) }) {
                            Text(
                                "Cancel"
                            )
                        }
                    }

                }
            }
        }

    }
    //Edit Confirm Dialog
    productToEdit?.let { product ->
        if (showEditingDialog) {
            BasicAlertDialog(
                onDismissRequest = { showEditingDialog = false },
                modifier = Modifier.padding(16.dp)
            ) {
                Card(
                    modifier = Modifier.padding(16.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Confirm Update", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Are your sure you want to Update ${product.name}?")
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    sellerViewModel.loadProductForEditing(product)
                                    selectedTabIndex = 1
                                    showEditingDialog = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                            ) { Text("Update") }
                            OutlinedButton(onClick = { sellerViewModel.setProductToEdit(null) }) {
                                Text(
                                    "Cancel"
                                )
                            }
                        }

                    }
                }
            }
        }

    }

}

@Composable
fun SellerProductItem(
    product: Product,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 3.dp)
            .border(
                width = 1.dp,
                color = Color.Black.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = lightGray
        )
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Product Image - with Base64 support
            if (product.imageUrl.isNotEmpty()) {
                // If we have a Base64 image string, decode and display it
                val bitmap = remember(product.imageUrl) {
                    try {
                        val imageBytes = Base64.decode(product.imageUrl, Base64.DEFAULT)
                        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    } catch (e: Exception) {
                        null
                    }
                }

                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = product.name,
                        modifier = Modifier.size(64.dp)
                    )
                } else {
                    // Fallback if decoding fails
                    Image(
                        painter = painterResource(id = R.drawable.groceries),
                        contentDescription = product.name,
                        modifier = Modifier.size(64.dp)
                    )
                }
            } else {
                // Fallback for no image
                Image(
                    painter = painterResource(id = product.imageResId.takeIf { it != 0 }
                        ?: R.drawable.groceries),
                    contentDescription = product.name,
                    modifier = Modifier.size(64.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Price: Ksh ${product.price} | Qty: ${product.quantity}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 12.sp
                )
                Text(
                    text = "Category: ${product.category}",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onEditClick,
                    modifier = Modifier.height(28.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            "Edit",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.height(28.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            "Delete",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(
    selectedCategory: ProductCategory, // Enum type
    onCategorySelected: (ProductCategory) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        OutlinedTextField(
            value = selectedCategory.name, // Show selected category
            onValueChange = {}, // Read-only field
            readOnly = true,
            label = { Text("Category") },
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(), // Anchor for dropdown
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown Icon",
                    modifier = Modifier.clickable { expanded = true }
                )
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            ProductCategory.values().forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.name) },
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    }
                )
            }
        }
    }
}

// Add to SellerDashboard.kt

@Composable
fun SalesTab(salesHistory: List<Sale>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Sales History",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (salesHistory.isEmpty()) {
            EmptyStateCard(
                title = "No Sales Yet",
                message = "Your sales history will appear here once you make your first sale"
            )
        } else {
            // Sales statistics summary
            SalesSummaryCard(salesHistory)

            Spacer(modifier = Modifier.height(16.dp))

            // Recent sales list
            LazyColumn {
                items(salesHistory) { sale ->
                    SaleItem(sale)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun SalesSummaryCard(sales: List<Sale>) {
    val totalSales = sales.sumOf { it.totalAmount }
    val totalItems = sales.sumOf { it.quantity }
    val averageSale = if (sales.isNotEmpty()) totalSales / sales.size else 0.0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Sales Overview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricItem(
                    icon = Icons.Filled.ShoppingCart,
                    value = "Ksh ${totalSales.format(2)}",
                    label = "Total Revenue"
                )

                MetricItem(
                    icon = Icons.Filled.DateRange,
                    value = totalItems.toString(),
                    label = "Items Sold"
                )

                MetricItem(
                    icon = Icons.Default.Create,
                    value = "Ksh ${averageSale.format(2)}",
                    label = "Avg Order"
                )
            }
        }
    }
}

@Composable
fun MetricItem(icon: ImageVector, value: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(24.dp)
        )

        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun SaleItem(sale: Sale) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }
    val formattedDate = sale.timestamp?.toDate()?.let { dateFormat.format(it) } ?: "Unknown date"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sale icon with circular background
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.ShoppingCart,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = sale.productName,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall
                )

                Text(
                    text = "${sale.quantity} items • Ksh ${sale.totalAmount.format(2)}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )

                if (sale.buyerPhone.isNotEmpty()) {
                    Text(
                        text = "Customer: ${sale.buyerPhone}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyStateCard(title: String, message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.DateRange,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsTab(salesReport: SalesReport, inventory: List<Product>) {
    var selectedPeriod by remember { mutableStateOf(0) } // 0: Daily, 1: Weekly, 2: Monthly
    val periods = listOf("Daily", "Weekly", "Monthly")
    var showGenerateDataDialog by remember { mutableStateOf(false) }
    val currentUser = FirebaseAuth.getInstance().currentUser
    val cartViewModel: CartViewModel = viewModel()
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text(
                "Sales Reports",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
        // Period selector pills
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                periods.forEachIndexed { index, period ->
                    PeriodSelectorPill(
                        text = period,
                        selected = selectedPeriod == index,
                        onClick = { selectedPeriod = index }
                    )
                    if (index < periods.size - 1) {
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }
        }
        // Summary cards
        item {
            ReportSummaryCards(salesReport)
        }
        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
        // Sales chart
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Sales Trend",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(modifier = Modifier.fillMaxSize()) {
                        when (selectedPeriod) {
                            0 -> SalesChart(
                                data = salesReport.dailySales.take(10).reversed(),
                                xValueSelector = { it.day.substringAfterLast("-") }, // Just the day
                                yValueSelector = { it.amount }
                            )

                            1 -> SalesChart(
                                data = salesReport.weeklySales.take(8).reversed(),
                                xValueSelector = {
                                    it.week.substring(
                                        5,
                                        7
                                    )
                                }, // Just the week number
                                yValueSelector = { it.amount }
                            )

                            2 -> SalesChart(
                                data = salesReport.monthlySales.take(6).reversed(),
                                xValueSelector = {
                                    it.month.substring(
                                        0,
                                        3
                                    )
                                }, // Just month abbreviation
                                yValueSelector = { it.amount }
                            )
                        }
                    }
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Inventory summary
        item {
            Text(
                "Inventory Summary",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        item {
            LazyRow {
                items(inventory) { product ->
                    InventorySummaryItem(product)
                }
            }
        }
        /*item {
            if (currentUser?.uid == "M9D26AAUz9fcWDX6XYUxFGNebX52") {
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedButton(
                    onClick = { showGenerateDataDialog = true },
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    Text("Generate Test Sales Data", color = MaterialTheme.colorScheme.primary)
                }
            }
        }*/
        // Confirmation dialog
        item {
            if (showGenerateDataDialog) {
                BasicAlertDialog(
                    onDismissRequest = { showGenerateDataDialog = false }
                ) {
                    Card(
                        modifier = Modifier.padding(16.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Generate Test Data?",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "This will create 100 sample sales records for testing purposes. Continue?",
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Button(
                                    onClick = {
                                        cartViewModel.insertDummySalesData()
                                        showGenerateDataDialog = false
                                        Toast.makeText(
                                            context,
                                            "Generating test data...",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                ) {
                                    Text("Generate")
                                }
                                OutlinedButton(
                                    onClick = { showGenerateDataDialog = false }
                                ) {
                                    Text("Cancel")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun PeriodSelectorPill(text: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(50),
        color = if (selected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.outline
        ),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = if (selected) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun ReportSummaryCards(report: SalesReport) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SummaryCard(
            title = "Total Revenue",
            value = "Ksh ${report.totalSales.format(2)}",
            icon = Icons.Filled.Star,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )

        SummaryCard(
            title = "Items Sold",
            value = report.totalItems.toString(),
            icon = Icons.Filled.DateRange,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun SummaryCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.15f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color
            )

            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun <T> SalesChart(
    data: List<T>,
    xValueSelector: (T) -> String,
    yValueSelector: (T) -> Double
) {
    if (data.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No data available for this period")
        }
        return
    }

    // Simple bar chart implementation
    val maxValue = data.maxOfOrNull { yValueSelector(it) }?.times(1.1) ?: 1.0

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Chart area
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            data.forEach { item ->
                val value = yValueSelector(item)
                val heightPercentage = (value / maxValue).toFloat()

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .fillMaxHeight(heightPercentage)
                            .background(
                                MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                            )
                    )
                }
            }
        }

        // X-axis labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            data.forEach { item ->
                Text(
                    text = xValueSelector(item),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun InventorySummaryItem(product: Product) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(100.dp)
            .padding(end = 8.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Ksh ${product.price}",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Stock:",
                    style = MaterialTheme.typography.bodySmall
                )

                // Stock indicator
                val stockColor = when {
                    product.quantity > 20 -> Color.Green
                    product.quantity > 10 -> Color.Yellow
                    else -> Color.Red
                }

                Box(
                    modifier = Modifier
                        .background(stockColor, CircleShape)
                        .size(8.dp)
                )

                Text(
                    text = "${product.quantity}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SalesTabPreview() {
    val sampleSales = listOf(
        Sale(
            id = "1",
            productName = "Apple",
            quantity = 5,
            price = 20.0,
            totalAmount = 100.0,
            timestamp = Timestamp(Date())
        ),
        Sale(
            id = "2",
            productName = "Banana",
            quantity = 3,
            price = 15.0,
            totalAmount = 45.0,
            timestamp = Timestamp(Date(System.currentTimeMillis() - 86400000))
        )
    )

    MaterialTheme {
        SalesTab(salesHistory = sampleSales)
    }
}

@Preview(showBackground = true)
@Composable
fun ReportsTabPreview() {
    val sampleReport = SalesReport(
        dailySales = listOf(
            DailySales("2023-06-01", 120.0, 10),
            DailySales("2023-06-02", 80.0, 5),
            DailySales("2023-06-03", 150.0, 12)
        ),
        weeklySales = listOf(
            WeeklySales("Week 22, 2023", 700.0, 50),
            WeeklySales("Week 23, 2023", 850.0, 65)
        ),
        monthlySales = listOf(
            MonthlySales("May 2023", 3200.0, 210),
            MonthlySales("Jun 2023", 2800.0, 180)
        ),
        totalSales = 6000.0,
        totalItems = 390
    )

    val sampleProducts = listOf(
        Product(id = "1", name = "Apples", price = 20.0, quantity = 25),
        Product(id = "2", name = "Bananas", price = 15.0, quantity = 12),
        Product(id = "3", name = "Oranges", price = 18.0, quantity = 8)
    )

    MaterialTheme {
        ReportsTab(salesReport = sampleReport, inventory = sampleProducts)
    }
}

@Preview
@Composable
fun EmptyStateCardPreview() {
    MaterialTheme {
        EmptyStateCard(
            title = "No Sales Yet",
            message = "Your sales history will appear here once you make your first sale"
        )
    }
}

// Add this helper function
fun Double.format(digits: Int) = "%.${digits}f".format(this)


@Preview(showBackground = true)
@Composable
fun SellerDashboardPreview() {
    MaterialTheme {
        SellerDashboard(navController = rememberNavController())
    }
}