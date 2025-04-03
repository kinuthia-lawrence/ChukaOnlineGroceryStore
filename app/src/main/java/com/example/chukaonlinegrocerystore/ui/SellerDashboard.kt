package com.example.chukaonlinegrocerystore.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontVariation.width
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.chukaonlinegrocerystore.R
import com.example.chukaonlinegrocerystore.enums.ProductCategory
import com.example.chukaonlinegrocerystore.model.Product
import com.example.chukaonlinegrocerystore.ui.theme.lightBrown
import com.example.chukaonlinegrocerystore.ui.theme.lightGray
import com.example.chukaonlinegrocerystore.viewmodel.SellerViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerDashboard(
    navController: NavHostController,
    sellerViewModel: SellerViewModel = viewModel()
) {

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Inventory", "Add Product")

    val uiState by sellerViewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val productToDelete by sellerViewModel.productToDelete.collectAsState()
    val productToEdit by sellerViewModel.productToEdit.collectAsState()
    var showEditingDialog by remember { mutableStateOf(false) }

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
                    bitmap?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "Product Image",
                            modifier = Modifier.fillMaxHeight()
                        )
                    } ?: Image(
                        painter = painterResource(id = R.drawable.groceries),
                        contentDescription = "Default Image",
                        modifier = Modifier.size(100.dp)
                    )
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


@Preview(showBackground = true)
@Composable
fun SellerDashboardPreview() {
    MaterialTheme {
        SellerDashboard(navController = rememberNavController())
    }
}