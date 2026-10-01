package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MovieGenre
import com.example.ui.theme.ZubActionAmber
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

@Composable
fun ZubTopBar(
    title: String = "ZUB-ZERO",
    subtitle: String = "AI CINEMA & LIFESTYLE",
    cartCount: Int = 0,
    notificationCount: Int = 0,
    onHomeClick: () -> Unit = {},
    onCinemaClick: () -> Unit = {},
    onMatchClick: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onCeoClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        color = ZubDarkNavy.copy(alpha = 0.95f),
        border = BorderStroke(1.dp, ZubBorder.copy(alpha = 0.5f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onHomeClick() }
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(ZubCyan, Color(0xFF0284C7))
                            )
                        )
                        .border(1.dp, ZubIceBlue.copy(alpha = 0.6f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AcUnit,
                        contentDescription = "Zub-Zero Icon",
                        tint = Color(0xFF03101C),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = title,
                            color = ZubTextPrimary,
                            fontWeight = FontWeight.Black,
                            fontSize = 17.sp,
                            letterSpacing = 1.2.sp
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(ZubCyan.copy(alpha = 0.15f))
                                .border(0.5.dp, ZubCyan.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "PRO",
                                color = ZubCyan,
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                        }
                    }
                    Text(
                        text = subtitle,
                        color = ZubTextSecondary,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.6.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Home shortcut
                IconButton(
                    onClick = onHomeClick,
                    modifier = Modifier
                        .testTag("top_bar_home_btn")
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(ZubCardBg)
                        .border(1.dp, ZubBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Home Hub",
                        tint = ZubIceBlue,
                        modifier = Modifier.size(17.dp)
                    )
                }

                // Cinema Theater shortcut
                IconButton(
                    onClick = onCinemaClick,
                    modifier = Modifier
                        .testTag("top_bar_cinema_btn")
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(ZubCardBg)
                        .border(1.dp, ZubBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Movie,
                        contentDescription = "Cinema Theater",
                        tint = ZubCyan,
                        modifier = Modifier.size(17.dp)
                    )
                }

                // Matchmaking shortcut
                IconButton(
                    onClick = onMatchClick,
                    modifier = Modifier
                        .testTag("top_bar_dating_btn")
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(ZubTwilightCrimson.copy(alpha = 0.15f))
                        .border(1.dp, ZubTwilightCrimson.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Singles Matchmaking",
                        tint = ZubTwilightCrimson,
                        modifier = Modifier.size(17.dp)
                    )
                }

                // CEO Vault & Payouts Shortcut Button
                IconButton(
                    onClick = onCeoClick,
                    modifier = Modifier
                        .testTag("ceo_vault_button")
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(ZubActionAmber.copy(alpha = 0.15f))
                        .border(1.dp, ZubActionAmber.copy(alpha = 0.6f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = "CEO Vaults",
                        tint = ZubActionAmber,
                        modifier = Modifier.size(17.dp)
                    )
                }

                // Push Notifications Hub Shortcut Button with Unread Badge
                BadgedBox(
                    badge = {
                        if (notificationCount > 0) {
                            Badge(
                                containerColor = ZubTwilightCrimson,
                                contentColor = Color.White
                            ) {
                                Text(
                                    text = if (notificationCount > 9) "9+" else notificationCount.toString(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 8.5.sp
                                )
                            }
                        }
                    }
                ) {
                    IconButton(
                        onClick = onNotificationsClick,
                        modifier = Modifier
                            .testTag("top_bar_notifications_btn")
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(ZubCyan.copy(alpha = 0.12f))
                            .border(1.dp, ZubCyan.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Push Notifications Hub",
                            tint = ZubCyan,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }

                BadgedBox(
                    badge = {
                        if (cartCount > 0) {
                            Badge(
                                containerColor = ZubCyan,
                                contentColor = Color(0xFF03101C)
                            ) {
                                Text(cartCount.toString(), fontWeight = FontWeight.Bold, fontSize = 9.sp)
                            }
                        }
                    }
                ) {
                    IconButton(
                        onClick = onCartClick,
                        modifier = Modifier
                            .testTag("cart_icon_button")
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(ZubCardBg)
                            .border(1.dp, ZubBorder, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalMall,
                            contentDescription = "Shopping Bag",
                            tint = ZubCyan,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    actionLabel: String? = null,
    onActionClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(16.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(ZubCyan)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                color = ZubTextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
        if (actionLabel != null) {
            Text(
                text = actionLabel,
                color = ZubCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clickable { onActionClick() }
                    .padding(4.dp)
            )
        }
    }
}

@Composable
fun GenreBadge(
    genre: MovieGenre,
    modifier: Modifier = Modifier
) {
    val (color, bgColor) = when (genre) {
        MovieGenre.ACTION -> ZubActionAmber to ZubActionAmber.copy(alpha = 0.15f)
        MovieGenre.DRAMA -> ZubElectricViolet to ZubElectricViolet.copy(alpha = 0.15f)
        MovieGenre.TWILIGHT -> ZubTwilightCrimson to ZubTwilightCrimson.copy(alpha = 0.15f)
        MovieGenre.SCI_FI -> ZubCyan to ZubCyan.copy(alpha = 0.15f)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .border(0.8.dp, color.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = genre.displayName.uppercase(),
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
        )
    }
}

@Composable
fun RatingBar(
    rating: Float,
    reviewsCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = ZubActionAmber,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = String.format("%.1f", rating),
            color = ZubTextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = "($reviewsCount)",
            color = ZubTextMuted,
            fontSize = 11.sp
        )
    }
}

/**
 * Interactive 5-Star Rating System for users to rate movies, candidates, and services
 */
@Composable
fun StarRatingPicker(
    rating: Int,
    onRatingSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    maxStars: Int = 5,
    starSize: androidx.compose.ui.unit.Dp = 24.dp
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        for (i in 1..maxStars) {
            val isFilled = i <= rating
            Icon(
                imageVector = if (isFilled) Icons.Default.Star else Icons.Default.StarBorder,
                contentDescription = "Rate $i star",
                tint = if (isFilled) ZubActionAmber else ZubTextMuted.copy(alpha = 0.6f),
                modifier = Modifier
                    .size(starSize)
                    .clip(CircleShape)
                    .clickable { onRatingSelected(i) }
                    .padding(2.dp)
            )
        }
    }
}

@Composable
fun NeonButton(
    text: String,
    onClick: () -> Unit,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    accentColor: Color = ZubCyan
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = accentColor,
            contentColor = Color(0xFF03101C)
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .height(48.dp)
            .testTag("neon_button_${text.lowercase().replace(" ", "_")}")
    ) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            letterSpacing = 0.5.sp
        )
    }
}
