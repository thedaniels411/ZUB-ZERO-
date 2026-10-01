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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.MatchCandidate
import com.example.data.model.MatchGender
import com.example.ui.components.MatchRegistrationDialog
import com.example.ui.components.NeonButton
import com.example.ui.components.RatingBar
import com.example.ui.components.StarRatingPicker
import com.example.ui.theme.ZubActionAmber
import com.example.ui.theme.ZubBlack
import com.example.ui.theme.ZubBorder
import com.example.ui.theme.ZubCardBg
import com.example.ui.theme.ZubCyan
import com.example.ui.theme.ZubDarkNavy
import com.example.ui.theme.ZubElectricViolet
import com.example.ui.theme.ZubIceBlue
import com.example.ui.theme.ZubSurface
import com.example.ui.theme.ZubTextMuted
import com.example.ui.theme.ZubTextPrimary
import com.example.ui.theme.ZubTextSecondary
import com.example.ui.theme.ZubTwilightCrimson
import com.example.ui.viewmodel.ZubZeroViewModel

@Composable
fun MatchmakingScreen(
    viewModel: ZubZeroViewModel,
    modifier: Modifier = Modifier
) {
    val candidates by viewModel.matchCandidates.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val matchUserProfile by viewModel.matchUserProfile.collectAsState()
    val selectedGenderFilter by viewModel.selectedGenderFilter.collectAsState()
    val selectedMatchFilter by viewModel.selectedMatchFilter.collectAsState()
    val matchSearchQuery by viewModel.matchSearchQuery.collectAsState()
    val candidateRatings by viewModel.candidateRatings.collectAsState()
    val showRegistrationDialog by viewModel.showMatchRegistrationDialog.collectAsState()

    if (showRegistrationDialog) {
        MatchRegistrationDialog(
            currentProfile = matchUserProfile,
            onDismiss = { viewModel.showMatchRegistrationDialog.value = false },
            onRegister = { name, age, g, tg, loc, prof, bio, goal, phone, ig ->
                viewModel.registerMatchUserProfile(
                    fullName = name,
                    age = age,
                    gender = g,
                    targetGender = tg,
                    location = loc,
                    profession = prof,
                    bio = bio,
                    relationshipGoal = goal,
                    phoneOrWhatsapp = phone,
                    instagramOrSocial = ig
                )
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ZubBlack)
            .testTag("matchmaking_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Hero Romantic Matchmaking Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_match_hero),
                    contentDescription = "Romantic Matchmaking Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Cinematic Dark Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Transparent,
                                    ZubBlack.copy(alpha = 0.65f),
                                    ZubBlack
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
                                .clip(RoundedCornerShape(6.dp))
                                .background(ZubElectricViolet.copy(alpha = 0.85f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "SINGLE & SEARCHING HUB",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(ZubCyan.copy(alpha = 0.25f))
                                .border(1.dp, ZubCyan, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "100% FREE REGISTRATION",
                                color = ZubCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Connect With Eligible Men & Women",
                        color = ZubTextPrimary,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Real singles seeking marriage, romantic dating, cinema companions & lifelong love",
                        color = ZubIceBlue,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Free Registration & 3-Day Trial Status Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = ZubDarkNavy),
                border = BorderStroke(1.dp, ZubBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
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
                                    .background(ZubCyan.copy(alpha = 0.15f))
                                    .border(1.dp, ZubCyan, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = null,
                                    tint = ZubCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (matchUserProfile.isRegistered) "My Matchmaking Profile (Verified)" else "Join Singles & Searching Hub",
                                    color = ZubTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = if (matchUserProfile.isRegistered)
                                        "${matchUserProfile.fullName}, ${matchUserProfile.age} • ${matchUserProfile.location}"
                                    else
                                        "Registration is completely FREE for everyone!",
                                    color = ZubTextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Register Button or Status
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (matchUserProfile.isRegistered) ZubCyan.copy(alpha = 0.2f) else ZubActionAmber.copy(alpha = 0.2f))
                                .border(1.dp, if (matchUserProfile.isRegistered) ZubCyan else ZubActionAmber, RoundedCornerShape(8.dp))
                                .clickable { viewModel.showMatchRegistrationDialog.value = true }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("open_match_registration_btn")
                        ) {
                            Text(
                                text = if (matchUserProfile.isRegistered) "Edit Profile" else "Register Free",
                                color = if (matchUserProfile.isRegistered) ZubCyan else ZubActionAmber,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 3-Day Free Trial Notice Banner
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = ZubCardBg,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(0.8.dp, ZubCyan.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = ZubActionAmber, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = if (userProfile.isTrialActive)
                                            "3-Day Free Trial Active (${userProfile.trialDaysRemaining} days remaining)"
                                        else
                                            "3-Day Free Trial Attached to Subscribed Channels",
                                        color = ZubActionAmber,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Initial ₦500 Naira trial access fee gives 3 days VIP channels & singles connect",
                                        color = ZubTextMuted,
                                        fontSize = 9.5.sp
                                    )
                                }
                            }

                            if (!userProfile.isTrialActive) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(ZubActionAmber.copy(alpha = 0.15f))
                                        .border(1.dp, ZubActionAmber, RoundedCornerShape(6.dp))
                                        .clickable { viewModel.showTrialActivationDialog.value = true }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                        .testTag("activate_channel_trial_btn")
                                ) {
                                    Text(
                                        text = "Get 3-Day Trial (₦500)",
                                        color = ZubActionAmber,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Tab Selector: Single Women / Single Men
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = ZubCardBg),
                border = BorderStroke(1.dp, ZubBorder)
            ) {
                TabRow(
                    selectedTabIndex = selectedGenderFilter.ordinal,
                    containerColor = Color.Transparent,
                    contentColor = ZubCyan,
                    indicator = { tabPositions ->
                        SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedGenderFilter.ordinal]),
                            color = ZubCyan,
                            height = 3.dp
                        )
                    }
                ) {
                    MatchGender.values().forEach { gender ->
                        Tab(
                            selected = selectedGenderFilter == gender,
                            onClick = { viewModel.setSelectedGenderFilter(gender) },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(gender.emoji, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = gender.label,
                                        color = if (selectedGenderFilter == gender) ZubCyan else ZubTextSecondary,
                                        fontSize = 12.sp,
                                        fontWeight = if (selectedGenderFilter == gender) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }

        // Search & Dear / Fitter Filter Controls
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                // Search Input Field
                OutlinedTextField(
                    value = matchSearchQuery,
                    onValueChange = { viewModel.setMatchSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("match_search_input"),
                    placeholder = {
                        Text(
                            "Search singles by city, job, cinema taste...",
                            color = ZubTextMuted,
                            fontSize = 12.sp
                        )
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = ZubCyan, modifier = Modifier.size(18.dp))
                    },
                    trailingIcon = {
                        if (matchSearchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setMatchSearchQuery("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = ZubTextMuted, modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ZubCyan,
                        unfocusedBorderColor = ZubBorder,
                        focusedContainerColor = ZubCardBg,
                        unfocusedContainerColor = ZubCardBg,
                        focusedTextColor = ZubTextPrimary,
                        unfocusedTextColor = ZubTextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Fitter & Dear Filter Chips Row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val filterOptions = listOf(
                        "ALL" to "All Singles",
                        "DEAR" to "❤️ Dear (Cherished)",
                        "FITTER" to "✨ Fitter (Best Match)",
                        "HIGH_RATED" to "⭐ Top Rated (4.8+)"
                    )

                    items(filterOptions) { (key, label) ->
                        val isSelected = selectedMatchFilter == key
                        val activeColor = when (key) {
                            "DEAR" -> ZubTwilightCrimson
                            "FITTER" -> ZubCyan
                            "HIGH_RATED" -> ZubActionAmber
                            else -> ZubIceBlue
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) activeColor.copy(alpha = 0.22f) else ZubCardBg)
                                .border(
                                    1.dp,
                                    if (isSelected) activeColor else ZubBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { viewModel.setSelectedMatchFilter(key) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) activeColor else ZubTextSecondary,
                                fontSize = 11.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Candidates List Header
        item {
            val filterName = when (selectedMatchFilter) {
                "DEAR" -> "DEAR CHERISHED"
                "FITTER" -> "FITTER MATCHES"
                "HIGH_RATED" -> "TOP RATED"
                else -> "ALL"
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$filterName ${selectedGenderFilter.label.uppercase()}",
                    color = ZubTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
                Text(
                    text = "Rating & Filter Active",
                    color = ZubCyan,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Candidate Cards with Rating System and Filtering
        val filteredCandidates = viewModel.getFilteredMatchCandidates()
        if (filteredCandidates.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = ZubDarkNavy),
                    border = BorderStroke(1.dp, ZubBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.FilterList, contentDescription = null, tint = ZubCyan, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No Candidates Found", color = ZubTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Try clearing the search query or selecting 'All Singles' filter.", color = ZubTextMuted, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = {
                                viewModel.setMatchSearchQuery("")
                                viewModel.setSelectedMatchFilter("ALL")
                            },
                            border = BorderStroke(1.dp, ZubCyan),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Reset Filters", color = ZubCyan, fontSize = 11.sp)
                        }
                    }
                }
            }
        } else {
            items(filteredCandidates, key = { it.id }) { candidate ->
                MatchCandidateCard(
                    candidate = candidate,
                    userRating = candidateRatings[candidate.id] ?: 0,
                    onRate = { stars ->
                        viewModel.rateCandidate(candidate.id, stars, candidate.fullName)
                    },
                    onConnect = { viewModel.sendMatchInterest(candidate) }
                )
            }
        }
    }
}

@Composable
fun MatchCandidateCard(
    candidate: MatchCandidate,
    userRating: Int,
    onRate: (Int) -> Unit,
    onConnect: () -> Unit
) {
    var hasLiked by remember { mutableStateOf(false) }
    var showRatingRow by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("candidate_card_${candidate.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ZubCardBg),
        border = BorderStroke(1.dp, ZubBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Profile Avatar Photo
                Box(
                    modifier = Modifier
                        .size(85.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.5.dp, ZubCyan, RoundedCornerShape(12.dp))
                ) {
                    Image(
                        painter = painterResource(id = candidate.avatarRes),
                        contentDescription = candidate.fullName,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Profile Info
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${candidate.fullName}, ${candidate.age}",
                                color = ZubTextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (candidate.isVerified) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    Icons.Default.Verified,
                                    contentDescription = "Verified Single",
                                    tint = ZubCyan,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }

                        // Distance badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(ZubSurface)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("${candidate.distanceKm} km away", color = ZubTextMuted, fontSize = 9.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // Rating Bar & Review Count
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RatingBar(rating = candidate.rating, reviewsCount = candidate.reviewsCount)

                        // Dear / Fitter Trait Badges
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            if (candidate.isDearCandidate) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(ZubTwilightCrimson.copy(alpha = 0.2f))
                                        .border(0.6.dp, ZubTwilightCrimson, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 5.dp, vertical = 2.dp)
                                ) {
                                    Text("DEAR ❤️", color = ZubTwilightCrimson, fontSize = 8.5.sp, fontWeight = FontWeight.Black)
                                }
                            }
                            if (candidate.isFittedMatch) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(ZubCyan.copy(alpha = 0.2f))
                                        .border(0.6.dp, ZubCyan, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 5.dp, vertical = 2.dp)
                                ) {
                                    Text("FITTER ✨", color = ZubCyan, fontSize = 8.5.sp, fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    // Location
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Place, contentDescription = null, tint = ZubCyan, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(candidate.location, color = ZubTextSecondary, fontSize = 11.sp)
                    }

                    // Profession
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Work, contentDescription = null, tint = ZubActionAmber, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(candidate.profession, color = ZubTextSecondary, fontSize = 11.sp, maxLines = 1)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Goal Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(ZubElectricViolet.copy(alpha = 0.2f))
                            .border(0.5.dp, ZubElectricViolet.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Seeking: ${candidate.relationshipGoal.label}",
                            color = ZubElectricViolet,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bio
            Text(
                text = "\"${candidate.bio}\"",
                color = ZubIceBlue,
                fontSize = 11.5.sp,
                lineHeight = 15.sp,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Hobbies & Cinema Tags
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                item {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(ZubCyan.copy(alpha = 0.12f))
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Movie, contentDescription = null, tint = ZubCyan, modifier = Modifier.size(11.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(candidate.favoriteCinemaGenre, color = ZubCyan, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
                items(candidate.hobbies) { hobby ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(ZubSurface)
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(hobby, color = ZubTextSecondary, fontSize = 10.sp)
                    }
                }
            }

            // Interactive Rating Drawer toggle
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(ZubSurface.copy(alpha = 0.6f))
                    .clickable { showRatingRow = !showRatingRow }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = ZubActionAmber,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (userRating > 0) "Your Rating: $userRating ★" else "Rate this profile candidate",
                        color = if (userRating > 0) ZubActionAmber else ZubTextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (showRatingRow) {
                    StarRatingPicker(
                        rating = userRating,
                        onRatingSelected = { stars ->
                            onRate(stars)
                        },
                        starSize = 18.dp
                    )
                } else {
                    Text(
                        text = if (userRating > 0) "Change ★" else "Tap to Rate",
                        color = ZubCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons: Send Match & Connect
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (hasLiked) ZubTwilightCrimson.copy(alpha = 0.25f) else ZubSurface)
                        .border(1.dp, if (hasLiked) ZubTwilightCrimson else ZubBorder, RoundedCornerShape(10.dp))
                        .clickable { hasLiked = !hasLiked },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (hasLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like Candidate",
                        tint = if (hasLiked) ZubTwilightCrimson else ZubTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                NeonButton(
                    text = "CONNECT & CHAT (FREE)",
                    icon = Icons.Default.Chat,
                    onClick = {
                        hasLiked = true
                        onConnect()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                )
            }
        }
    }
}
