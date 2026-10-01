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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ProductItem
import com.example.ui.components.NeonButton
import com.example.ui.components.RatingBar
import com.example.ui.theme.ZubActionAmber
import com.example.ui.theme.ZubBorder
import com.example.ui.theme.ZubCardBg
import com.example.ui.theme.ZubCyan
import com.example.ui.theme.ZubDarkNavy
import com.example.ui.theme.ZubElectricViolet
import com.example.ui.theme.ZubIceBlue
import com.example.ui.theme.ZubSurface
import com.example.ui.theme.ZubSurfaceVariant
import com.example.ui.theme.ZubTextMuted
import com.example.ui.theme.ZubTextPrimary
import com.example.ui.theme.ZubTextSecondary
import com.example.ui.theme.ZubTwilightCrimson
import com.example.ui.viewmodel.ZubZeroViewModel

@Composable
fun FashionStyleScreen(
    viewModel: ZubZeroViewModel,
    modifier: Modifier = Modifier
) {
    val fashionItems = viewModel.getFilteredFashion()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("fashion_style_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, ZubCyan.copy(alpha = 0.5f))
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_fashion_cyber),
                        contentDescription = "Fashion Showcase Banner",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xE6070B12),
                                        Color(0x99070B12),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(ZubCyan)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "CINEMA WARDROBE & COUTURE",
                                color = Color(0xFF03101C),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "FASHION & STYLE SALES",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )

                        Text(
                            text = "Exclusive movie-grade streetwear, twilight velvet gowns, & high-tech trench coats.",
                            color = ZubTextSecondary,
                            fontSize = 11.sp,
                            modifier = Modifier.width(220.dp)
                        )
                    }
                }
            }
        }

        // Product Cards with Picture Gallery & Interactive Size/Color selection
        items(fashionItems) { product ->
            FashionProductCard(
                product = product,
                onAddToCart = { size, color ->
                    viewModel.addToCart(product, size = size, color = color)
                }
            )
        }
    }
}

@Composable
private fun FashionProductCard(
    product: ProductItem,
    onAddToCart: (String, String) -> Unit
) {
    var selectedSize by remember { mutableStateOf(product.sizes.firstOrNull() ?: "M") }
    var selectedColor by remember { mutableStateOf(product.colors.firstOrNull() ?: "Standard") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .testTag("fashion_item_${product.id}"),
        colors = CardDefaults.cardColors(containerColor = ZubCardBg),
        border = BorderStroke(1.dp, ZubBorder)
    ) {
        Column {
            // Main Product Picture
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) {
                Image(
                    painter = painterResource(id = product.imageRes),
                    contentDescription = product.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Wardrobe Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xCC070B12))
                        .border(0.8.dp, ZubCyan.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Checkroom, contentDescription = null, tint = ZubCyan, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Movie Wardrobe Edition", color = ZubCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Price Tag
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xE6070B12))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "$${product.price.toInt()}",
                            color = ZubCyan,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                        if (product.originalPrice > product.price) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "$${product.originalPrice.toInt()}",
                                color = ZubTextMuted,
                                fontSize = 11.sp,
                                textDecoration = TextDecoration.LineThrough
                            )
                        }
                    }
                }
            }

            // Info & Selections
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = product.title,
                    color = ZubTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RatingBar(rating = product.rating, reviewsCount = product.reviewsCount)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = "Brand: ${product.seller}", color = ZubIceBlue, fontSize = 11.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = product.description,
                    color = ZubTextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Size Picker
                if (product.sizes.isNotEmpty()) {
                    Text(text = "SELECT SIZE", color = ZubTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        product.sizes.forEach { size ->
                            val isSelected = selectedSize == size
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) ZubCyan else ZubSurface)
                                    .border(1.dp, if (isSelected) ZubCyan else ZubBorder, RoundedCornerShape(6.dp))
                                    .clickable { selectedSize = size }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = size,
                                    color = if (isSelected) Color(0xFF03101C) else ZubTextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Color Swatches
                if (product.colors.isNotEmpty()) {
                    Text(text = "COLORWAY: $selectedColor", color = ZubTextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        product.colors.forEach { colorName ->
                            val isSelected = selectedColor == colorName
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) ZubCyan.copy(alpha = 0.2f) else ZubSurface)
                                    .border(1.dp, if (isSelected) ZubCyan else ZubBorder, RoundedCornerShape(6.dp))
                                    .clickable { selectedColor = colorName }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = colorName,
                                    color = if (isSelected) ZubCyan else ZubTextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                NeonButton(
                    text = "BUY NOW • ADD TO BAG ($selectedSize / $selectedColor)",
                    icon = Icons.Default.ShoppingBag,
                    onClick = { onAddToCart(selectedSize, selectedColor) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
