package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import com.example.data.model.CoinTransaction
import com.example.data.model.DailyStreakDayInfo
import com.example.data.service.CoinManager
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.entity.CartItemEntity
import com.example.data.local.entity.HotelBookingEntity
import com.example.data.model.GeneratedMovie
import com.example.ui.components.GenreBadge
import com.example.ui.components.NeonButton
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
import com.example.ui.theme.ZubTwilightCrimson
import com.example.ui.viewmodel.AppNavTab
import com.example.ui.viewmodel.ZubZeroViewModel

@Composable
fun MyHubScreen(
    viewModel: ZubZeroViewModel,
    modifier: Modifier = Modifier
) {
    val savedMovies by viewModel.savedMovies.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val hotelBookings by viewModel.hotelBookings.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val uploadSubmissions by viewModel.uploadSubmissions.collectAsState()
    val coinTransactions by viewModel.coinTransactions.collectAsState()
    val streakWeek = viewModel.getDailyStreakWeekInfo()
    val canClaimCheckIn = viewModel.coinManager.canClaimDailyCheckIn(userProfile.lastDailyCheckInEpochMs)
    val hoursUntilCheckIn = viewModel.coinManager.getHoursUntilNextCheckIn(userProfile.lastDailyCheckInEpochMs)

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        "Wallet & Channel (${userProfile.coins}c)",
        "CEO Payouts 👑",
        "Bag (${cartItems.size})",
        "Movies (${savedMovies.size})",
        "Stays (${hotelBookings.size})"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("my_hub_screen")
    ) {
        // Hub Header with Ownership Attribution
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Surface(
                color = ZubDarkNavy,
                border = BorderStroke(1.dp, ZubCyan.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "OWNERSHIP NOTICE: ZUB-ZERO MAGICAL AI APP is owned and censored by THE CEO OF D-DANIEL'S INVENTION BIZ a PERSON OF ADEKOYA DANIEL EBENEZER.",
                    color = ZubCyan,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "MY ZUB-ZERO HUB",
                        color = ZubTextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Wallet, channel establishment, reels & orders",
                        color = ZubTextSecondary,
                        fontSize = 12.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(ZubActionAmber)
                        .clickable { viewModel.showSubscriptionDialog.value = true }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Subscribe Plan",
                        color = Color(0xFF03101C),
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = ZubCardBg,
                contentColor = ZubCyan,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, ZubBorder, RoundedCornerShape(12.dp)),
                indicator = { tabPositions ->
                    SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = ZubCyan,
                        height = 3.dp
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                color = if (selectedTab == index) ZubCyan else ZubTextSecondary,
                                fontSize = 10.5.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    )
                }
            }
        }

        when (selectedTab) {
            0 -> WalletAndFinancialTab(
                userProfile = userProfile,
                submissions = uploadSubmissions,
                coinTransactions = coinTransactions,
                streakWeek = streakWeek,
                canClaimCheckIn = canClaimCheckIn,
                hoursUntilCheckIn = hoursUntilCheckIn,
                onRegisterClick = { viewModel.showRegistrationDialog.value = true },
                onDailyClaim = { viewModel.claimDailyCheckIn() },
                onAdBoost = {
                    if (userProfile.activityCount >= 6) {
                        viewModel.showAdRewardDialog.value = true
                    } else {
                        viewModel.showToast("Perform ${6 - userProfile.activityCount} more activities to unlock 2-ad 60 coin boost!")
                    }
                },
                onSubscribe = { viewModel.showSubscriptionDialog.value = true },
                onUploadPayment = { viewModel.showUploadPaymentDialog.value = true },
                onOpenCeoPortal = { selectedTab = 1 },
                onOpenPushCenter = { viewModel.showPushNotificationCenter.value = true },
                onTriggerMatchPush = { viewModel.triggerTestMatchPush() },
                onTriggerOrderPush = { viewModel.triggerTestMarketplaceOrderPush() },
                onTriggerRenewalPush = { viewModel.triggerTestSubscriptionRenewalPush() }
            )
            1 -> CeoManagementScreen(
                viewModel = viewModel
            )
            2 -> ShoppingBagTab(
                items = cartItems,
                onRemove = { viewModel.removeFromCart(it) },
                onCheckout = { viewModel.checkoutCart() },
                onExplore = { viewModel.setTab(AppNavTab.MARKETPLACE) }
            )
            3 -> SavedMoviesTab(
                movies = savedMovies,
                onPlay = { viewModel.playMovie(it) },
                onDelete = { viewModel.deleteMovie(it) },
                onNewMovie = { viewModel.setTab(AppNavTab.AI_STUDIO) }
            )
            4 -> HotelBookingsTab(
                bookings = hotelBookings,
                onCancel = { viewModel.cancelHotelBooking(it) },
                onFindHotels = { viewModel.setTab(AppNavTab.HOTELS) }
            )
        }
    }
}

@Composable
private fun ShoppingBagTab(
    items: List<CartItemEntity>,
    onRemove: (Long) -> Unit,
    onCheckout: () -> Unit,
    onExplore: () -> Unit
) {
    if (items.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.LocalMall, contentDescription = null, tint = ZubTextMuted, modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(12.dp))
                Text("Your Shopping Bag is Empty", color = ZubTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Browse cinema gear, props, or designer fashion.", color = ZubTextMuted, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(16.dp))
                NeonButton(text = "Explore Marketplace", onClick = onExplore)
            }
        }
    } else {
        val total = items.sumOf { it.price * it.quantity }
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(items) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = ZubCardBg),
                        border = BorderStroke(1.dp, ZubBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            ) {
                                Image(
                                    painter = painterResource(id = if (item.imageRes != 0) item.imageRes else R.drawable.img_prop_artifact),
                                    contentDescription = item.title,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.title,
                                    color = ZubTextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${item.category} • Size: ${item.selectedSize}",
                                    color = ZubTextMuted,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "$${item.price.toInt()} x ${item.quantity}",
                                    color = ZubCyan,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }

                            IconButton(onClick = { onRemove(item.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = ZubTwilightCrimson, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }

            // Checkout Footer
            Surface(
                color = ZubDarkNavy,
                border = BorderStroke(1.dp, ZubBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Subtotal:", color = ZubTextSecondary, fontSize = 13.sp)
                        Text("$${total.toInt()}", color = ZubCyan, fontSize = 18.sp, fontWeight = FontWeight.Black)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    NeonButton(
                        text = "COMPLETE PURCHASE • $${total.toInt()}",
                        icon = Icons.Default.ShoppingBag,
                        onClick = onCheckout,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun SavedMoviesTab(
    movies: List<GeneratedMovie>,
    onPlay: (GeneratedMovie) -> Unit,
    onDelete: (Long) -> Unit,
    onNewMovie: () -> Unit
) {
    if (movies.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Movie, contentDescription = null, tint = ZubTextMuted, modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(12.dp))
                Text("No Saved Movies", color = ZubTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Render your first movie using AI in the studio.", color = ZubTextMuted, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(16.dp))
                NeonButton(text = "Go to AI Studio", onClick = onNewMovie)
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(movies) { movie ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp)),
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
                                painter = painterResource(id = R.drawable.img_cinema_hero),
                                contentDescription = movie.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(8.dp)
                            ) {
                                GenreBadge(genre = movie.genre)
                            }
                        }

                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = movie.title,
                                color = ZubTextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = movie.logline,
                                color = ZubTextSecondary,
                                fontSize = 11.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("${movie.scenes.size} Scenes • 4K", color = ZubIceBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)

                                Row {
                                    IconButton(onClick = { onDelete(movie.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ZubTwilightCrimson, modifier = Modifier.size(20.dp))
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(ZubCyan)
                                            .clickable { onPlay(movie) }
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFF03101C), modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Play Reel", color = Color(0xFF03101C), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
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
private fun HotelBookingsTab(
    bookings: List<HotelBookingEntity>,
    onCancel: (Long) -> Unit,
    onFindHotels: () -> Unit
) {
    if (bookings.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Hotel, contentDescription = null, tint = ZubTextMuted, modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(12.dp))
                Text("No Hotel Stays Reserved", color = ZubTextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Book a production suite or luxury stay near the studios.", color = ZubTextMuted, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(16.dp))
                NeonButton(text = "Find Nearest Hotels", onClick = onFindHotels)
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(bookings) { booking ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = ZubCardBg),
                    border = BorderStroke(1.dp, ZubBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = booking.hotelName,
                                color = ZubTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(ZubCyan.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(booking.status, color = ZubCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "${booking.location} • Room: ${booking.roomType}",
                            color = ZubTextMuted,
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${booking.checkInDate} - ${booking.checkOutDate}",
                                color = ZubIceBlue,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "$${booking.totalPrice.toInt()}",
                                color = ZubCyan,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            Text(
                                text = "Cancel Reservation",
                                color = ZubTwilightCrimson,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .clickable { onCancel(booking.id) }
                                    .padding(4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WalletAndFinancialTab(
    userProfile: com.example.data.model.UserProfile,
    submissions: List<com.example.data.model.UploadSubmission>,
    coinTransactions: List<CoinTransaction>,
    streakWeek: List<DailyStreakDayInfo>,
    canClaimCheckIn: Boolean,
    hoursUntilCheckIn: Int,
    onRegisterClick: () -> Unit,
    onDailyClaim: () -> Unit,
    onAdBoost: () -> Unit,
    onSubscribe: () -> Unit,
    onUploadPayment: () -> Unit,
    onOpenCeoPortal: () -> Unit,
    onOpenPushCenter: () -> Unit,
    onTriggerMatchPush: () -> Unit,
    onTriggerOrderPush: () -> Unit,
    onTriggerRenewalPush: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("wallet_financial_tab"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Firebase Cloud Messaging Push Notification Hub Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("fcm_push_hub_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ZubDarkNavy),
                border = BorderStroke(1.5.dp, ZubCyan.copy(alpha = 0.8f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(ZubCyan.copy(alpha = 0.2f))
                                    .border(1.dp, ZubCyan, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    tint = ZubCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "FIREBASE CLOUD MESSAGING",
                                        color = ZubTextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.8.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(androidx.compose.ui.graphics.Color(0xFF00E676).copy(alpha = 0.2f))
                                            .border(0.5.dp, androidx.compose.ui.graphics.Color(0xFF00E676), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 5.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = "ACTIVE",
                                            color = androidx.compose.ui.graphics.Color(0xFF00E676),
                                            fontSize = 8.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Text(
                                    text = "Match Alerts • Order Updates • Subscription Renewals",
                                    color = ZubCyan,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        androidx.compose.material3.OutlinedButton(
                            onClick = onOpenPushCenter,
                            colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(contentColor = ZubCyan),
                            border = BorderStroke(1.dp, ZubCyan),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp).testTag("btn_manage_fcm_hub")
                        ) {
                            Text("Manage", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Real-time push notifications across 3 core topics with system alerts & deep links:",
                        color = ZubTextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        androidx.compose.material3.OutlinedButton(
                            onClick = onTriggerMatchPush,
                            modifier = Modifier.weight(1f).height(38.dp),
                            colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                                containerColor = ZubTwilightCrimson.copy(alpha = 0.15f),
                                contentColor = ZubTwilightCrimson
                            ),
                            border = BorderStroke(1.dp, ZubTwilightCrimson.copy(alpha = 0.6f)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Match", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        androidx.compose.material3.OutlinedButton(
                            onClick = onTriggerOrderPush,
                            modifier = Modifier.weight(1f).height(38.dp),
                            colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                                containerColor = ZubCyan.copy(alpha = 0.15f),
                                contentColor = ZubCyan
                            ),
                            border = BorderStroke(1.dp, ZubCyan.copy(alpha = 0.6f)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.LocalMall, contentDescription = null, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Order", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        androidx.compose.material3.OutlinedButton(
                            onClick = onTriggerRenewalPush,
                            modifier = Modifier.weight(1f).height(38.dp),
                            colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                                containerColor = ZubActionAmber.copy(alpha = 0.15f),
                                contentColor = ZubActionAmber
                            ),
                            border = BorderStroke(1.dp, ZubActionAmber.copy(alpha = 0.6f)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.MonetizationOn, contentDescription = null, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Renewal", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // CEO Multi-Currency Revenue Vault Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenCeoPortal() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ZubDarkNavy),
                border = BorderStroke(1.5.dp, ZubCyan)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(ZubCyan.copy(alpha = 0.2f))
                                    .border(1.dp, ZubCyan, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.MonetizationOn,
                                    contentDescription = null,
                                    tint = ZubCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "CEO RECEIVING WALLETS & PAYOUTS",
                                    color = ZubCyan,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "USD ($) • GBP (£) • NGN (₦) Vaults",
                                    color = ZubTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(ZubCyan)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "WITHDRAW",
                                color = Color(0xFF03101C),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Tap to open executive portal to view live multi-currency balances and initiate instant payout to local Nigerian bank account.",
                        color = ZubTextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }
        }
        // User Profile & Registration Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ZubCardBg),
                border = BorderStroke(1.2.dp, ZubCyan)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(ZubSurface)
                                    .border(2.dp, ZubCyan, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Face, contentDescription = "Facial Picture", tint = ZubCyan, modifier = Modifier.size(32.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (userProfile.isRegistered) userProfile.fullName else "Unregistered User",
                                        color = ZubTextPrimary,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 15.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (userProfile.isRegistered) ZubCyan.copy(alpha = 0.2f) else ZubTwilightCrimson.copy(alpha = 0.2f))
                                            .padding(horizontal = 5.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (userProfile.isRegistered) "VERIFIED" else "REGISTRATION NEEDED",
                                            color = if (userProfile.isRegistered) ZubCyan else ZubTwilightCrimson,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                }
                                Text(
                                    text = if (userProfile.isRegistered) "${userProfile.mobileNumber} • ${userProfile.email}" else "Facial picture, mobile & email needed",
                                    color = ZubTextSecondary,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "Media Display: ${userProfile.mediaDisplayType} MODE",
                                    color = ZubIceBlue,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        if (!userProfile.isRegistered) {
                            NeonButton(
                                text = "Register",
                                onClick = onRegisterClick,
                                modifier = Modifier.height(34.dp)
                            )
                        }
                    }
                }
            }
        }

        // Coin Economics Card (50 coins = 350 NGN = 20s video)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ZubDarkNavy),
                border = BorderStroke(1.dp, ZubBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ZUB-ZERO COIN BALANCE",
                            color = ZubTextPrimary,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            letterSpacing = 1.sp
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(ZubActionAmber.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text("50 COINS = ₦350 NAIRA", color = ZubActionAmber, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = "${userProfile.coins} Coins",
                                color = ZubCyan,
                                fontWeight = FontWeight.Black,
                                fontSize = 28.sp
                            )
                            Text(
                                text = "Equivalent to ₦${userProfile.coinsValueInNaira.toInt()} Naira",
                                color = ZubTextSecondary,
                                fontSize = 12.sp
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${userProfile.videoSecondsAvailable.toInt()} Seconds",
                                color = ZubIceBlue,
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp
                            )
                            Text(
                                text = "Video Rendering Time",
                                color = ZubTextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 7-Day Streak Calendar Progress Ladder
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "7-DAY STREAK PROGRESS",
                            color = ZubTextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Streak: ${userProfile.checkInStreak} Days",
                            color = ZubCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        streakWeek.forEach { dayInfo ->
                            val isCompleted = dayInfo.isCompleted
                            val isToday = dayInfo.isCurrentDay
                            val isJackpot = dayInfo.dayNumber == 7

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        when {
                                            isToday -> ZubCyan.copy(alpha = 0.2f)
                                            isCompleted -> ZubSurfaceVariant
                                            else -> ZubSurface
                                        }
                                    )
                                    .border(
                                        width = if (isToday) 1.5.dp else 1.dp,
                                        color = when {
                                            isToday -> ZubCyan
                                            isCompleted -> ZubIceBlue.copy(alpha = 0.5f)
                                            else -> ZubBorder
                                        },
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(vertical = 6.dp, horizontal = 4.dp)
                                    .width(40.dp)
                            ) {
                                Text(
                                    text = if (isJackpot) "🎁 7" else "D${dayInfo.dayNumber}",
                                    color = if (isToday) ZubCyan else ZubTextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                if (isCompleted) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = "Completed",
                                        tint = ZubCyan,
                                        modifier = Modifier.size(14.dp)
                                    )
                                } else {
                                    Text(
                                        text = "+${dayInfo.rewardCoins}c",
                                        color = if (isJackpot) ZubActionAmber else ZubTextPrimary,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Daily Check-in & Ad Reward Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Daily Check-in Card (Powered by CoinManager)
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onDailyClaim() },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (canClaimCheckIn) ZubSurface else ZubCardBg
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (canClaimCheckIn) ZubCyan else ZubBorder
                            )
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.CardGiftcard,
                                        contentDescription = null,
                                        tint = if (canClaimCheckIn) ZubCyan else ZubTextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        "Daily Check-in",
                                        color = if (canClaimCheckIn) ZubCyan else ZubTextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                val nextReward = CoinManager.STREAK_REWARDS[(userProfile.checkInStreak - 1).coerceIn(0, 6)]
                                Text(
                                    text = if (canClaimCheckIn) "+$nextReward Free Coins" else "Claimed for Today",
                                    color = ZubTextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = if (canClaimCheckIn) "Tap to claim reward" else "Next in ~$hoursUntilCheckIn hour(s)",
                                    color = if (canClaimCheckIn) ZubActionAmber else ZubTextMuted,
                                    fontSize = 9.5.sp
                                )
                            }
                        }

                        // Watch 2 Ads (+60 Coins, unlocked after 6 activities)
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onAdBoost() },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = ZubSurface),
                            border = BorderStroke(1.dp, ZubActionAmber.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Campaign, contentDescription = null, tint = ZubActionAmber, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Watch 2 Ads", color = ZubActionAmber, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("+60 Coins Boost", color = ZubTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Black)
                                Text("${userProfile.adsWatchedForCycle}/2 ads • Act: ${userProfile.activityCount}/6", color = ZubTextMuted, fontSize = 9.5.sp)
                            }
                        }
                    }
                }
            }
        }

        // Coin Transaction History Card (Room SQLite Persisted)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ZubCardBg),
                border = BorderStroke(1.dp, ZubBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.AutoMirrored.Filled.ReceiptLong,
                                contentDescription = null,
                                tint = ZubCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "COIN TRANSACTION LEDGER",
                                color = ZubTextPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(ZubCyan.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "ROOM PERSISTED",
                                color = ZubCyan,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (coinTransactions.isEmpty()) {
                        Text(
                            text = "No coin transactions recorded yet. Complete check-ins, watch ads, or generate videos to view your ledger.",
                            color = ZubTextMuted,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            coinTransactions.take(6).forEach { tx ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(ZubDarkNavy)
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (tx.amount >= 0) ZubCyan.copy(alpha = 0.18f) else ZubTwilightCrimson.copy(alpha = 0.18f)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = if (tx.amount >= 0) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                                                contentDescription = null,
                                                tint = if (tx.amount >= 0) ZubCyan else ZubTwilightCrimson,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Column {
                                            Text(
                                                text = tx.description,
                                                color = ZubTextPrimary,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "${tx.type.label} • Balance: ${tx.balanceAfter}c",
                                                color = ZubTextMuted,
                                                fontSize = 9.5.sp
                                            )
                                        }
                                    }

                                    Text(
                                        text = if (tx.amount >= 0) "+${tx.amount}c" else "${tx.amount}c",
                                        color = if (tx.amount >= 0) ZubCyan else ZubTwilightCrimson,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Active Subscription Breakdown Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ZubCardBg),
                border = BorderStroke(1.dp, ZubBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "APP SUBSCRIPTION STATUS",
                            color = ZubTextPrimary,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            letterSpacing = 1.sp
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(ZubCyan.copy(alpha = 0.15f))
                                .clickable { onSubscribe() }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (userProfile.activeSubscription != null) "Manage Plan" else "Choose Plan",
                                color = ZubCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (userProfile.activeSubscription != null) {
                        val sub = userProfile.activeSubscription!!
                        Text(
                            text = "Current Plan: ${sub.planName} (${sub.priceFormatted})",
                            color = ZubCyan,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Standard subscribers enjoy priority rendering and zero-friction marketplace listing across all channels.",
                            color = ZubTextSecondary,
                            fontSize = 11.sp
                        )
                    } else {
                        Text(
                            text = "No active subscription plan. Financial breakdown:\n• 1 Month: 6,557 Thousand Naira (₦6,557,000) [$4,370 USD / £3,450 GBP]\n• 3 Months: ₦20,500 Naira [$14 USD / £11 GBP]\n• 1 Year: ₦78,900 Naira [$53 USD / £42 GBP]\nPayments are credited directly to ZUB-ZERO multi-currency receiving wallets (USD, GBP, NGN).",
                            color = ZubTextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // Upload Clearance & Channel Establishment Section ($10, $50, $80)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ZubDarkNavy),
                border = BorderStroke(1.2.dp, ZubActionAmber.copy(alpha = 0.7f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = ZubActionAmber, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "UPLOAD & CHANNEL ESTABLISHMENT",
                                color = ZubActionAmber,
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                letterSpacing = 0.8.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Mandatory payment is strictly enforced before uploading products, stores, goods, or productions in ZUB-ZERO magical app ($10, $50, or $80 depending on tier).",
                        color = ZubTextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    NeonButton(
                        text = "Clear Upload / Establish Channel",
                        onClick = onUploadPayment,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (submissions.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Active Submissions & Paid Channels (${submissions.size}):",
                            color = ZubTextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        submissions.forEach { sub ->
                            Surface(
                                color = ZubSurface,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(sub.title, color = ZubTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text("${sub.channelOrStoreName} • Paid $${sub.paidAmountUsd} (~₦${sub.paidAmountNaira})", color = ZubTextMuted, fontSize = 10.sp)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(ZubCyan.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("CLEARED", color = ZubCyan, fontSize = 9.sp, fontWeight = FontWeight.Black)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
