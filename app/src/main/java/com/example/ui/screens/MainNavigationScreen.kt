package com.example.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AdRewardDialog
import com.example.ui.components.CeoWithdrawalDialog
import com.example.ui.components.ChannelTrialAccessDialog
import com.example.ui.components.MatchPreferencesDialog
import com.example.ui.components.PushNotificationCenterDialog
import com.example.ui.components.RegistrationDialog
import com.example.ui.components.SubscriptionPaywallDialog
import com.example.ui.components.UploadPaymentPaywallDialog
import com.example.ui.components.ZubTopBar
import com.example.ui.theme.ZubActionAmber
import com.example.ui.theme.ZubBlack
import com.example.ui.theme.ZubBorder
import com.example.ui.theme.ZubCardBg
import com.example.ui.theme.ZubCyan
import com.example.ui.theme.ZubDarkNavy
import com.example.ui.theme.ZubElectricViolet
import com.example.ui.theme.ZubIceBlue
import com.example.ui.theme.ZubTextMuted
import com.example.ui.theme.ZubTextPrimary
import com.example.ui.theme.ZubTextSecondary
import com.example.ui.theme.ZubTwilightCrimson
import com.example.ui.viewmodel.AppNavTab
import com.example.ui.viewmodel.ZubZeroViewModel

/**
 * Data representation for the 4 core bottom navigation destinations:
 * 1. Video Generator
 * 2. Marketplace
 * 3. Hotel Locator
 * 4. Style Storefront
 */
private data class BottomNavItem(
    val tab: AppNavTab,
    val label: String,
    val icon: ImageVector,
    val testTag: String
)

/**
 * MainNavigationScreen:
 * Implements the main navigation screen using a Material 3 Scaffold and a BottomNavigation pattern,
 * allowing users to switch effortlessly between the video generator, marketplace, hotel locator,
 * and style storefront.
 */
@Composable
fun MainNavigationScreen(
    viewModel: ZubZeroViewModel,
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val isGeneratingScript by viewModel.isGeneratingScriptOnly.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(toastMessage) {
        toastMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearToast()
        }
    }

    // Dialog state collectors
    val showRegDialog by viewModel.showRegistrationDialog.collectAsState()
    val showUploadDialog by viewModel.showUploadPaymentDialog.collectAsState()
    val showSubDialog by viewModel.showSubscriptionDialog.collectAsState()
    val showAdDialog by viewModel.showAdRewardDialog.collectAsState()
    val showTrialDialog by viewModel.showTrialActivationDialog.collectAsState()
    val showWithdrawalDialog by viewModel.showWithdrawalDialog.collectAsState()
    val showPreferencesDialog by viewModel.showPreferencesDialog.collectAsState()
    val showPushDialog by viewModel.showPushNotificationCenter.collectAsState()
    val pushNotifications by viewModel.pushNotifications.collectAsState()
    val fcmState by viewModel.fcmState.collectAsState()
    val unreadNotifications by viewModel.unreadNotificationCount.collectAsState()

    // Dialog Triggers
    if (showRegDialog) {
        RegistrationDialog(
            initialMobile = userProfile.mobileNumber,
            initialEmail = userProfile.email,
            onDismiss = { viewModel.showRegistrationDialog.value = false },
            onRegistered = { name, mobile, email, mediaType, photoUri ->
                viewModel.registerUser(name, mobile, email, mediaType, photoUri)
            }
        )
    }

    if (showTrialDialog) {
        ChannelTrialAccessDialog(
            onDismiss = { viewModel.showTrialActivationDialog.value = false },
            onActivateTrial = { method, ref ->
                viewModel.activateChannelTrialWithInitialAccessFee(method, ref)
            }
        )
    }

    if (showUploadDialog) {
        UploadPaymentPaywallDialog(
            onDismiss = { viewModel.showUploadPaymentDialog.value = false },
            onPaymentConfirmed = { tier, method, ref ->
                viewModel.confirmUploadPayment(
                    title = "Creator Cinema Production / Store Channel",
                    description = "Official creator upload verified in ZUB-ZERO magical app",
                    category = "CINEMA_PRODUCTIONS",
                    price = tier.usdPrice.toDouble(),
                    tier = tier,
                    channelName = "${userProfile.fullName}'s Channel Hub",
                    method = method,
                    reference = ref
                )
            }
        )
    }

    if (showSubDialog) {
        SubscriptionPaywallDialog(
            currentPlan = userProfile.activeSubscription,
            onDismiss = { viewModel.showSubscriptionDialog.value = false },
            onPlanSubscribed = { plan, method, ref, currency ->
                viewModel.subscribePlan(plan, method, ref, currency)
            }
        )
    }

    if (showAdDialog) {
        AdRewardDialog(
            currentAdCount = userProfile.adsWatchedForCycle,
            onDismiss = { viewModel.showAdRewardDialog.value = false },
            onAdCompleted = {
                viewModel.handleAdCompletion()
            }
        )
    }

    if (showWithdrawalDialog) {
        CeoWithdrawalDialog(
            receivingWallets = viewModel.receivingWallets.collectAsState().value,
            ceoBankDetails = viewModel.ceoBankDetails.collectAsState().value,
            onDismiss = { viewModel.showWithdrawalDialog.value = false },
            onConfirmWithdrawal = { currency, amount, bankName, accountNumber, accountName ->
                viewModel.processCeoWithdrawal(currency, amount, bankName, accountNumber, accountName)
            }
        )
    }

    if (showPreferencesDialog) {
        MatchPreferencesDialog(
            userProfile = viewModel.matchUserProfile.collectAsState().value,
            onDismiss = { viewModel.showPreferencesDialog.value = false },
            onApplyPreferences = { goal, genre, hobbies, minAge, maxAge ->
                viewModel.updateUserPreferences(goal, genre, hobbies, minAge, maxAge)
            }
        )
    }

    if (showPushDialog) {
        PushNotificationCenterDialog(
            fcmState = fcmState,
            notifications = pushNotifications,
            onDismiss = { viewModel.showPushNotificationCenter.value = false },
            onNavigateToTab = { tabString ->
                val tab = when (tabString.uppercase()) {
                    "MATCHMAKING" -> AppNavTab.MATCHMAKING
                    "MARKETPLACE" -> AppNavTab.MARKETPLACE
                    "MY_HUB" -> AppNavTab.MY_HUB
                    "AI_STUDIO" -> AppNavTab.AI_STUDIO
                    "CINEMA_THEATER" -> AppNavTab.CINEMA_THEATER
                    "HOTELS" -> AppNavTab.HOTELS
                    "FASHION" -> AppNavTab.FASHION
                    else -> AppNavTab.HOME
                }
                viewModel.setTab(tab)
            },
            onTriggerMatchPush = { viewModel.triggerTestMatchPush() },
            onTriggerOrderPush = { viewModel.triggerTestMarketplaceOrderPush() },
            onTriggerRenewalPush = { viewModel.triggerTestSubscriptionRenewalPush() },
            onToggleTopic = { topic, enabled -> viewModel.toggleFcmTopic(topic, enabled) },
            onMarkAllRead = { viewModel.markAllNotificationsAsRead() },
            onClearAll = { viewModel.clearNotificationInbox() }
        )
    }

    // The 4 Core Navigation Items requested by user
    val bottomNavItems = remember {
        listOf(
            BottomNavItem(
                tab = AppNavTab.AI_STUDIO,
                label = "Video Generator",
                icon = Icons.Default.Videocam,
                testTag = "bottom_nav_video_generator"
            ),
            BottomNavItem(
                tab = AppNavTab.MARKETPLACE,
                label = "Marketplace",
                icon = Icons.Default.Storefront,
                testTag = "bottom_nav_marketplace"
            ),
            BottomNavItem(
                tab = AppNavTab.HOTELS,
                label = "Hotel Locator",
                icon = Icons.Default.Hotel,
                testTag = "bottom_nav_hotel_locator"
            ),
            BottomNavItem(
                tab = AppNavTab.FASHION,
                label = "Style Storefront",
                icon = Icons.Default.Checkroom,
                testTag = "bottom_nav_style_storefront"
            )
        )
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("main_navigation_scaffold"),
        containerColor = ZubBlack,
        topBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                ZubTopBar(
                    title = "ZUB-ZERO",
                    subtitle = when (currentTab) {
                        AppNavTab.AI_STUDIO -> "AI VIDEO GENERATION STUDIO"
                        AppNavTab.MARKETPLACE -> "FILM GEAR & PROPS MARKETPLACE"
                        AppNavTab.HOTELS -> "NEAREST LUXURY & CREW HOTEL LOCATOR"
                        AppNavTab.FASHION -> "WARDROBE & COUTURE STYLE STOREFRONT"
                        AppNavTab.HOME -> "CINEMA & CREATOR PLATFORM HUB"
                        AppNavTab.CINEMA_THEATER -> "4K NEURAL CINEMA THEATER"
                        AppNavTab.MATCHMAKING -> "AI COMPATIBILITY & DATING HUB"
                        AppNavTab.MY_HUB -> "REELS • CART • RESERVATIONS & HUB"
                    },
                    cartCount = cartItems.sumOf { it.quantity },
                    notificationCount = unreadNotifications,
                    onHomeClick = { viewModel.setTab(AppNavTab.HOME) },
                    onCinemaClick = { viewModel.setTab(AppNavTab.CINEMA_THEATER) },
                    onMatchClick = { viewModel.setTab(AppNavTab.MATCHMAKING) },
                    onCartClick = { viewModel.setTab(AppNavTab.MY_HUB) },
                    onCeoClick = { viewModel.showWithdrawalDialog.value = true },
                    onNotificationsClick = { viewModel.showPushNotificationCenter.value = true }
                )

                // Quick Navigation Chip Strip for Fast Access
                QuickNavStrip(
                    currentTab = currentTab,
                    onSelectTab = { viewModel.setTab(it) }
                )
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = ZubDarkNavy.copy(alpha = 0.98f),
                tonalElevation = 10.dp,
                modifier = Modifier
                    .border(BorderStroke(1.dp, ZubBorder.copy(alpha = 0.6f)))
                    .navigationBarsPadding()
                    .testTag("zub_bottom_navigation")
            ) {
                bottomNavItems.forEach { item ->
                    val isSelected = currentTab == item.tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.setTab(item.tab) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (item.tab == AppNavTab.AI_STUDIO && isGeneratingScript) {
                                        Badge(
                                            containerColor = ZubTwilightCrimson,
                                            contentColor = Color.White
                                        ) {
                                            Text("AI", fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                        }
                                    } else if (item.tab == AppNavTab.MARKETPLACE && cartItems.isNotEmpty()) {
                                        Badge(
                                            containerColor = ZubCyan,
                                            contentColor = Color(0xFF03101C)
                                        ) {
                                            Text(cartItems.size.toString(), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        },
                        label = {
                            Text(
                                text = item.label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1
                            )
                        },
                        alwaysShowLabel = true,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF03101C),
                            selectedTextColor = ZubCyan,
                            indicatorColor = ZubCyan,
                            unselectedIconColor = ZubTextMuted,
                            unselectedTextColor = ZubTextMuted
                        ),
                        modifier = Modifier.testTag(item.testTag)
                    )
                }
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = ZubDarkNavy,
                    contentColor = ZubTextPrimary,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    ) { innerPadding ->
        Crossfade(
            targetState = currentTab,
            animationSpec = tween(durationMillis = 200),
            label = "main_nav_crossfade",
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) { tab ->
            when (tab) {
                // 4 Core Destinations requested
                AppNavTab.AI_STUDIO -> AiStudioScreen(viewModel = viewModel)
                AppNavTab.MARKETPLACE -> MarketplaceScreen(viewModel = viewModel)
                AppNavTab.HOTELS -> HotelsScreen(viewModel = viewModel)
                AppNavTab.FASHION -> FashionStyleScreen(viewModel = viewModel)

                // Additional ecosystem destinations
                AppNavTab.HOME -> HomeScreen(viewModel = viewModel)
                AppNavTab.CINEMA_THEATER -> CinemaPlayerScreen(viewModel = viewModel)
                AppNavTab.MATCHMAKING -> MatchmakingScreen(viewModel = viewModel)
                AppNavTab.MY_HUB -> MyHubScreen(viewModel = viewModel)
            }
        }
    }
}

/**
 * Compact Quick Navigation Strip under the TopBar that allows users to quickly
 * jump between the core 4 tabs and ecosystem tabs like Home, Cinema, Singles Dating, and Hub.
 */
@Composable
private fun QuickNavStrip(
    currentTab: AppNavTab,
    onSelectTab: (AppNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = ZubDarkNavy.copy(alpha = 0.85f),
        border = BorderStroke(0.5.dp, ZubBorder.copy(alpha = 0.4f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val chips = listOf(
                Triple(AppNavTab.AI_STUDIO, "🎥 Video Gen", ZubCyan),
                Triple(AppNavTab.MARKETPLACE, "🛍️ Marketplace", ZubCyan),
                Triple(AppNavTab.HOTELS, "🏨 Hotel Locator", ZubCyan),
                Triple(AppNavTab.FASHION, "👗 Style Store", ZubCyan),
                Triple(AppNavTab.HOME, "🏠 Home Hub", ZubIceBlue),
                Triple(AppNavTab.CINEMA_THEATER, "🎬 Cinema 4K", ZubElectricViolet),
                Triple(AppNavTab.MATCHMAKING, "💖 Singles Match", ZubTwilightCrimson),
                Triple(AppNavTab.MY_HUB, "📦 My Bag & Reels", ZubActionAmber)
            )

            chips.forEach { (tab, title, accentColor) ->
                val isActive = currentTab == tab
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isActive) accentColor.copy(alpha = 0.22f) else ZubCardBg)
                        .border(
                            width = 1.dp,
                            color = if (isActive) accentColor else ZubBorder.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { onSelectTab(tab) }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .testTag("quick_nav_${tab.name.lowercase()}")
                ) {
                    Text(
                        text = title,
                        color = if (isActive) accentColor else ZubTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
