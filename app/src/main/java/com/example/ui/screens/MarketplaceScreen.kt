package com.example.ui.screens

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MarketCategory
import com.example.data.model.ProductItem
import com.example.ui.components.NeonButton
import com.example.ui.components.RatingBar
import com.example.ui.theme.ZubActionAmber
import com.example.ui.theme.ZubBorder
import com.example.ui.theme.ZubCardBg
import com.example.ui.theme.ZubCyan
import com.example.ui.theme.ZubDarkNavy
import com.example.ui.theme.ZubIceBlue
import com.example.ui.theme.ZubSurface
import com.example.ui.theme.ZubSurfaceVariant
import com.example.ui.theme.ZubTextMuted
import com.example.ui.theme.ZubTextPrimary
import com.example.ui.theme.ZubTextSecondary
import com.example.ui.viewmodel.ZubZeroViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketplaceScreen(
    viewModel: ZubZeroViewModel,
    modifier: Modifier = Modifier
) {
    val selectedCategory by viewModel.marketCategory.collectAsState()
    val searchQuery by viewModel.marketSearchQuery.collectAsState()
    val selectedProduct by viewModel.selectedProduct.collectAsState()

    val products = viewModel.getFilteredProducts()

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("marketplace_screen")
    ) {
        // Search & Category Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ZUB-ZERO MARKETPLACE",
                        color = ZubTextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Cinema cameras, high-tech movie props, stores & goods",
                        color = ZubTextSecondary,
                        fontSize = 12.sp
                    )
                }

                // Upload Product / Establish Store button (triggers $10, $50, $80 payment paywall)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(ZubCyan)
                        .clickable { viewModel.showUploadPaymentDialog.value = true }
                        .padding(horizontal = 10.dp, vertical = 7.dp)
                        .testTag("upload_product_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color(0xFF03101C), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Upload / Store ($10-$80)",
                            color = Color(0xFF03101C),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.marketSearchQuery.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("market_search_input"),
                placeholder = { Text("Search products, gear, props...", color = ZubTextMuted, fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = ZubCyan) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.marketSearchQuery.value = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = ZubTextMuted)
                        }
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ZubCyan,
                    unfocusedBorderColor = ZubBorder,
                    focusedTextColor = ZubTextPrimary,
                    unfocusedTextColor = ZubTextPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Subscribed Channel 3-Day Free Trial Notice Banner (₦500 Initial Trial Access)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.showTrialActivationDialog.value = true },
                colors = CardDefaults.cardColors(containerColor = ZubDarkNavy),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, ZubActionAmber.copy(alpha = 0.7f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(ZubActionAmber.copy(alpha = 0.2f))
                                .border(1.dp, ZubActionAmber, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("3D", color = ZubActionAmber, fontSize = 11.sp, fontWeight = FontWeight.Black)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "3-DAY FREE TRIAL FOR SUBSCRIBED CHANNELS",
                                color = ZubActionAmber,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.6.sp
                            )
                            Text(
                                text = "Attracting initial ₦500 (Five Hundred Naira) trial access fee for full creator channels & store catalog",
                                color = ZubIceBlue,
                                fontSize = 10.sp
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(ZubActionAmber)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("₦500 TRIAL", color = Color(0xFF03101C), fontSize = 10.sp, fontWeight = FontWeight.Black)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Room Database Offline Cache Status Indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(ZubSurfaceVariant.copy(alpha = 0.7f))
                    .border(1.dp, ZubCyan.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("room_cache_status_marketplace"),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CloudDone,
                        contentDescription = "Room Offline Cache",
                        tint = ZubCyan,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Room SQLite Cache: ${products.size} Listings Saved Offline",
                        color = ZubIceBlue,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(ZubCyan.copy(alpha = 0.18f))
                        .clickable { viewModel.refreshOfflineRoomCache() }
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                        .testTag("refresh_room_cache_marketplace")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Refresh, contentDescription = "Sync", tint = ZubCyan, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Sync Cache", color = ZubCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Categories LazyRow
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(MarketCategory.values()) { cat ->
                    val isSelected = selectedCategory == cat
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) ZubCyan else ZubCardBg)
                            .border(1.dp, if (isSelected) ZubCyan else ZubBorder, RoundedCornerShape(8.dp))
                            .clickable { viewModel.setMarketCategory(cat) }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = cat.label,
                            color = if (isSelected) Color(0xFF03101C) else ZubTextSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Product Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 100.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(products) { product ->
                ProductCard(
                    product = product,
                    onSelect = { viewModel.selectProduct(product) },
                    onAddToCart = { viewModel.addToCart(product) }
                )
            }
        }
    }

    // Detail Bottom Sheet
    val productToInspect = selectedProduct
    if (productToInspect != null) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { viewModel.selectProduct(null) },
            sheetState = sheetState,
            containerColor = ZubDarkNavy,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(ZubBorder)
                )
            }
        ) {
            ProductDetailSheet(
                product = productToInspect,
                onAddToCart = { qty ->
                    viewModel.addToCart(productToInspect)
                    viewModel.selectProduct(null)
                },
                onDismiss = { viewModel.selectProduct(null) }
            )
        }
    }
}

@Composable
private fun ProductCard(
    product: ProductItem,
    onSelect: () -> Unit,
    onAddToCart: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onSelect() }
            .testTag("product_card_${product.id}"),
        colors = CardDefaults.cardColors(containerColor = ZubCardBg),
        border = BorderStroke(1.dp, ZubBorder)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                Image(
                    painter = painterResource(id = product.imageRes),
                    contentDescription = product.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                if (product.originalPrice > product.price) {
                    val savings = ((product.originalPrice - product.price) / product.originalPrice * 100).toInt()
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(ZubActionAmber)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("-$savings%", color = Color(0xFF03101C), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = product.title,
                    color = ZubTextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                RatingBar(rating = product.rating, reviewsCount = product.reviewsCount)

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "$${product.price.toInt()}",
                            color = ZubCyan,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                        if (product.originalPrice > product.price) {
                            Text(
                                text = "$${product.originalPrice.toInt()}",
                                color = ZubTextMuted,
                                fontSize = 10.sp,
                                textDecoration = TextDecoration.LineThrough
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(ZubCyan)
                            .clickable { onAddToCart() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = "Add to Cart",
                            tint = Color(0xFF03101C),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductDetailSheet(
    product: ProductItem,
    onAddToCart: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var quantity by remember { mutableIntStateOf(1) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
        ) {
            Image(
                painter = painterResource(id = product.imageRes),
                contentDescription = product.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = product.title,
                color = ZubTextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "$${product.price.toInt()}",
                color = ZubCyan,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            RatingBar(rating = product.rating, reviewsCount = product.reviewsCount)
            Spacer(modifier = Modifier.width(12.dp))
            Icon(Icons.Default.Verified, contentDescription = null, tint = ZubIceBlue, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(product.seller, color = ZubIceBlue, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = product.description,
            color = ZubTextSecondary,
            fontSize = 12.sp,
            lineHeight = 17.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(text = "SPECIFICATIONS", color = ZubTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))

        product.specs.forEach { spec ->
            Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(ZubCyan)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = spec, color = ZubTextPrimary, fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        NeonButton(
            text = "ADD TO SHOPPING BAG • $${(product.price * quantity).toInt()}",
            icon = Icons.Default.LocalMall,
            onClick = { onAddToCart(quantity) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
