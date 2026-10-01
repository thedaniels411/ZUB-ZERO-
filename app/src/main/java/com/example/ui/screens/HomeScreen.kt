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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.GeneratorType
import com.example.data.model.HotelItem
import com.example.data.model.MovieGenre
import com.example.data.model.ProductItem
import com.example.ui.components.GenreBadge
import com.example.ui.components.NeonButton
import com.example.ui.components.RatingBar
import com.example.ui.components.SectionHeader
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
import com.example.ui.viewmodel.AppNavTab
import com.example.ui.viewmodel.ZubZeroViewModel

@Composable
fun HomeScreen(
    viewModel: ZubZeroViewModel,
    modifier: Modifier = Modifier
) {
    val activeMovie by viewModel.activeMovie.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_content"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Ownership & Censorship Legal Ribbon
        item {
            Surface(
                color = ZubDarkNavy,
                border = BorderStroke(1.dp, ZubCyan.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Verified,
                        contentDescription = null,
                        tint = ZubCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "OWNERSHIP NOTICE: ZUB-ZERO MAGICAL AI APP is owned and censored by THE CEO OF D-DANIEL'S INVENTION BIZ a PERSON OF ADEKOYA DANIEL EBENEZER.",
                        color = ZubIceBlue,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 13.sp
                    )
                }
            }
        }

        // User Account, Coin & Subscription Status Bar
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = ZubCardBg),
                border = BorderStroke(1.dp, ZubBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { viewModel.showRegistrationDialog.value = true }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(ZubSurface)
                                .border(1.5.dp, ZubCyan, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Face,
                                contentDescription = "Facial Profile",
                                tint = ZubCyan,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (userProfile.isRegistered) userProfile.fullName else "Tap to Register",
                                    color = ZubTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(ZubCyan.copy(alpha = 0.2f))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = if (userProfile.isRegistered) "VERIFIED" else "UNREGISTERED",
                                        color = ZubCyan,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                            Text(
                                text = "${userProfile.coins} Coins (₦${userProfile.coinsValueInNaira.toInt()} NGN) • ${userProfile.videoSecondsAvailable.toInt()}s Video",
                                color = ZubActionAmber,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        // Daily 30 Coin Check-in
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(ZubCyan.copy(alpha = 0.15f))
                                .border(1.dp, ZubCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .clickable { viewModel.claimDailyCheckIn() }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Daily +30",
                                color = ZubCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Watch 2 Ads (+60 Coins)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(ZubActionAmber.copy(alpha = 0.15f))
                                .border(1.dp, ZubActionAmber.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .clickable {
                                    if (userProfile.activityCount >= 6) {
                                        viewModel.showAdRewardDialog.value = true
                                    } else {
                                        viewModel.showToast("Perform ${6 - userProfile.activityCount} more activities to unlock 2-ad 60 coins boost!")
                                    }
                                }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Ad +60",
                                color = ZubActionAmber,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
        // Hero Cinematic Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_cinema_hero),
                    contentDescription = "Cinematic Studio Hero",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Dramatic Dark & Cyan Vignette Gradient
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Transparent,
                                    Color(0x99070B12),
                                    Color(0xFF070B12)
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(ZubTwilightCrimson)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "AI VIDEO GENERATOR v4.0",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "4K 60FPS SYNTHESIS",
                            color = ZubCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "ZUB-ZERO CINEMA",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.2.sp
                    )

                    Text(
                        text = "Generate Action, Drama, Twilight & Sci-Fi movies from ideas, scripts, and images in seconds.",
                        color = ZubTextSecondary,
                        fontSize = 12.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row {
                        NeonButton(
                            text = "Create Video Now",
                            icon = Icons.Default.AutoAwesome,
                            onClick = { viewModel.setTab(AppNavTab.AI_STUDIO) },
                            modifier = Modifier.height(42.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        OutlinedButton(
                            onClick = { viewModel.setTab(AppNavTab.CINEMA_THEATER) },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, ZubCyan.copy(alpha = 0.6f)),
                            modifier = Modifier.height(42.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = ZubCyan)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Watch Reel", color = ZubCyan, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // Quick AI Generator Modalities
        item {
            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader(
                title = "AI Video Generation Modes",
                actionLabel = "All Studios",
                onActionClick = { viewModel.setTab(AppNavTab.AI_STUDIO) }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GeneratorModeCard(
                    title = "Idea to Video",
                    subtitle = "Pitch concept to 4K reel",
                    icon = Icons.Default.FlashOn,
                    color = ZubCyan,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.setGeneratorType(GeneratorType.IDEA_TO_VIDEO)
                        viewModel.setTab(AppNavTab.AI_STUDIO)
                    }
                )
                GeneratorModeCard(
                    title = "Script to Video",
                    subtitle = "Screenplay to shots",
                    icon = Icons.Default.Description,
                    color = ZubElectricViolet,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.setGeneratorType(GeneratorType.SCRIPT_TO_VIDEO)
                        viewModel.setTab(AppNavTab.AI_STUDIO)
                    }
                )
                GeneratorModeCard(
                    title = "Image to Video",
                    subtitle = "Motion trajectory fx",
                    icon = Icons.Default.Image,
                    color = ZubTwilightCrimson,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.setGeneratorType(GeneratorType.IMAGE_TO_VIDEO)
                        viewModel.setTab(AppNavTab.AI_STUDIO)
                    }
                )
            }
        }

        // Cinematic Genres Section
        item {
            Spacer(modifier = Modifier.height(20.dp))
            SectionHeader(
                title = "Cinematic Movie Generators",
                actionLabel = "Explore",
                onActionClick = { viewModel.setTab(AppNavTab.AI_STUDIO) }
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(MovieGenre.values()) { genre ->
                    GenreCard(
                        genre = genre,
                        onClick = {
                            viewModel.setSelectedGenre(genre)
                            viewModel.setTab(AppNavTab.AI_STUDIO)
                        }
                    )
                }
            }
        }

        // Fashion & Style Sales Section
        item {
            Spacer(modifier = Modifier.height(24.dp))
            SectionHeader(
                title = "Fashion & Style Sales",
                actionLabel = "View Boutique",
                onActionClick = { viewModel.setTab(AppNavTab.FASHION) }
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(viewModel.getFilteredFashion()) { product ->
                    FashionPreviewCard(
                        product = product,
                        onSelect = { viewModel.selectProduct(product) },
                        onAddToCart = { viewModel.addToCart(product) }
                    )
                }
            }
        }

        // Nearest Hotels Search Section
        item {
            Spacer(modifier = Modifier.height(24.dp))
            SectionHeader(
                title = "Nearest Production & Luxury Hotels",
                actionLabel = "Book Rooms",
                onActionClick = { viewModel.setTab(AppNavTab.HOTELS) }
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(viewModel.getFilteredHotels()) { hotel ->
                    HotelPreviewCard(
                        hotel = hotel,
                        onSelect = { viewModel.selectHotel(hotel) }
                    )
                }
            }
        }

        // Single & Searching Matchmaking Platform Section
        item {
            Spacer(modifier = Modifier.height(24.dp))
            SectionHeader(
                title = "Single & Searching Matchmaking",
                actionLabel = "Find Matches",
                onActionClick = { viewModel.setTab(AppNavTab.MATCHMAKING) }
            )

            // Romantic banner card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clickable { viewModel.setTab(AppNavTab.MATCHMAKING) },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = ZubDarkNavy),
                border = BorderStroke(1.dp, ZubElectricViolet.copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(ZubElectricViolet.copy(alpha = 0.2f))
                            .border(1.5.dp, ZubElectricViolet, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Favorite, contentDescription = null, tint = ZubElectricViolet, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Single Men & Women Hub",
                                color = ZubTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(ZubCyan.copy(alpha = 0.2f))
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text("FREE TO REGISTER", color = ZubCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Text(
                            text = "Connect with Dear & Fitter matches. Star rating system, cinema dates & 3-day free trial (₦500 initial access)!",
                            color = ZubIceBlue,
                            fontSize = 11.sp,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }

        // Marketplace Film Gear Section
        item {
            Spacer(modifier = Modifier.height(24.dp))
            SectionHeader(
                title = "Marketplace Products & Film Props",
                actionLabel = "Shop All",
                onActionClick = { viewModel.setTab(AppNavTab.MARKETPLACE) }
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(viewModel.getFilteredProducts().take(4)) { product ->
                    GearPreviewCard(
                        product = product,
                        onSelect = { viewModel.selectProduct(product) },
                        onAddToCart = { viewModel.addToCart(product) }
                    )
                }
            }
        }
    }
}

@Composable
private fun GeneratorModeCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = ZubCardBg),
        border = BorderStroke(1.dp, ZubBorder)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color.copy(alpha = 0.15f))
                    .border(0.8.dp, color.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                color = ZubTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                maxLines = 1
            )
            Text(
                text = subtitle,
                color = ZubTextMuted,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun GenreCard(
    genre: MovieGenre,
    onClick: () -> Unit
) {
    val (accent, bgGradient) = when (genre) {
        MovieGenre.ACTION -> ZubActionAmber to listOf(Color(0xFF3B1503), ZubCardBg)
        MovieGenre.DRAMA -> ZubElectricViolet to listOf(Color(0xFF280B3B), ZubCardBg)
        MovieGenre.TWILIGHT -> ZubTwilightCrimson to listOf(Color(0xFF3B0B18), ZubCardBg)
        MovieGenre.SCI_FI -> ZubCyan to listOf(Color(0xFF03263B), ZubCardBg)
    }

    Card(
        modifier = Modifier
            .width(160.dp)
            .height(115.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = ZubCardBg),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.4f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(bgGradient))
                .padding(12.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GenreBadge(genre = genre)
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = genre.tagline,
                    color = ZubTextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun FashionPreviewCard(
    product: ProductItem,
    onSelect: () -> Unit,
    onAddToCart: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(170.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable { onSelect() },
        colors = CardDefaults.cardColors(containerColor = ZubCardBg),
        border = BorderStroke(1.dp, ZubBorder)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                Image(
                    painter = painterResource(id = product.imageRes),
                    contentDescription = product.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xCC070B12))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "$${product.price.toInt()}",
                        color = ZubCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
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
                Spacer(modifier = Modifier.height(4.dp))
                RatingBar(rating = product.rating, reviewsCount = product.reviewsCount)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product.seller,
                        color = ZubTextMuted,
                        fontSize = 10.sp,
                        maxLines = 1
                    )
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(ZubCyan)
                            .clickable { onAddToCart() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = "Add to Bag",
                            tint = Color(0xFF03101C),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HotelPreviewCard(
    hotel: HotelItem,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable { onSelect() },
        colors = CardDefaults.cardColors(containerColor = ZubCardBg),
        border = BorderStroke(1.dp, ZubBorder)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                Image(
                    painter = painterResource(id = hotel.imageRes),
                    contentDescription = hotel.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xCC070B12))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${hotel.distanceKm} km away",
                        color = ZubIceBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = hotel.name,
                    color = ZubTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = hotel.location,
                    color = ZubTextMuted,
                    fontSize = 10.sp,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RatingBar(rating = hotel.rating, reviewsCount = hotel.reviewsCount)
                    Text(
                        text = "$${hotel.pricePerNight.toInt()}/nt",
                        color = ZubCyan,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun GearPreviewCard(
    product: ProductItem,
    onSelect: () -> Unit,
    onAddToCart: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable { onSelect() },
        colors = CardDefaults.cardColors(containerColor = ZubCardBg),
        border = BorderStroke(1.dp, ZubBorder)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
            ) {
                Image(
                    painter = painterResource(id = product.imageRes),
                    contentDescription = product.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
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
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$${product.price.toInt()}",
                        color = ZubCyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(ZubBorder)
                            .clickable { onAddToCart() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.LocalMall, contentDescription = null, tint = ZubCyan, modifier = Modifier.size(13.dp))
                    }
                }
            }
        }
    }
}
