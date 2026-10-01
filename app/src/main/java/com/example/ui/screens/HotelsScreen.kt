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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.runtime.mutableStateOf
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
import com.example.data.model.HotelItem
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
fun HotelsScreen(
    viewModel: ZubZeroViewModel,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.hotelSearchQuery.collectAsState()
    val selectedHotel by viewModel.selectedHotel.collectAsState()
    val hotels = viewModel.getFilteredHotels()

    var distanceFilter by remember { mutableStateOf("All Distances") }

    val filteredList = hotels.filter {
        when (distanceFilter) {
            "< 1.0 km" -> it.distanceKm <= 1.0
            "Luxury" -> it.pricePerNight >= 250.0
            else -> true
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("hotels_screen")
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "NEAREST PRODUCTION & LUXURY HOTELS",
                color = ZubTextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
            )
            Text(
                text = "Find cast & crew accommodations, suites, & studio shoot partner hotels",
                color = ZubTextSecondary,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Search input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.hotelSearchQuery.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hotel_search_input"),
                placeholder = { Text("Search location, district, hotel name...", color = ZubTextMuted, fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = ZubCyan) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.hotelSearchQuery.value = "" }) {
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

            // Distance & Type Filters
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("All Distances", "< 1.0 km", "Luxury").forEach { filter ->
                    val isSelected = distanceFilter == filter
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) ZubCyan else ZubCardBg)
                            .border(1.dp, if (isSelected) ZubCyan else ZubBorder, RoundedCornerShape(8.dp))
                            .clickable { distanceFilter = filter }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = filter,
                            color = if (isSelected) Color(0xFF03101C) else ZubTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
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
                    .testTag("room_cache_status_hotels"),
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
                        text = "Room SQLite Cache: ${filteredList.size} Accommodations Cached Offline",
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
                        .testTag("refresh_room_cache_hotels")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Refresh, contentDescription = "Sync", tint = ZubCyan, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Sync Cache", color = ZubCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Hotels List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(filteredList) { hotel ->
                HotelCard(
                    hotel = hotel,
                    onBookClick = { viewModel.selectHotel(hotel) }
                )
            }
        }
    }

    // Booking BottomSheet
    val hotelToBook = selectedHotel
    if (hotelToBook != null) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { viewModel.selectHotel(null) },
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
            HotelBookingSheet(
                hotel = hotelToBook,
                onConfirmBooking = { checkIn, checkOut, guests, roomType ->
                    viewModel.bookHotel(hotelToBook, checkIn, checkOut, guests, roomType)
                },
                onDismiss = { viewModel.selectHotel(null) }
            )
        }
    }
}

@Composable
private fun HotelCard(
    hotel: HotelItem,
    onBookClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .testTag("hotel_card_${hotel.id}"),
        colors = CardDefaults.cardColors(containerColor = ZubCardBg),
        border = BorderStroke(1.dp, ZubBorder)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
            ) {
                Image(
                    painter = painterResource(id = hotel.imageRes),
                    contentDescription = hotel.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Distance Badge
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
                        Icon(Icons.Default.NearMe, contentDescription = null, tint = ZubCyan, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${hotel.distanceKm} km away", color = ZubCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Partner Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xE6070B12))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(hotel.badge, color = ZubIceBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = hotel.name,
                        color = ZubTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "$${hotel.pricePerNight.toInt()}",
                        color = ZubCyan,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Text(
                    text = "${hotel.location} • per night",
                    color = ZubTextMuted,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                RatingBar(rating = hotel.rating, reviewsCount = hotel.reviewsCount)

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = hotel.description,
                    color = ZubTextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Amenities chips
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(hotel.amenities) { amenity ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(ZubSurface)
                                .border(0.8.dp, ZubBorder, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(amenity, color = ZubTextSecondary, fontSize = 10.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                NeonButton(
                    text = "BOOK NOW • CHECK AVAILABILITY",
                    icon = Icons.Default.Hotel,
                    onClick = onBookClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun HotelBookingSheet(
    hotel: HotelItem,
    onConfirmBooking: (String, String, Int, String) -> Unit,
    onDismiss: () -> Unit
) {
    var checkIn by remember { mutableStateOf("Oct 12, 2026") }
    var checkOut by remember { mutableStateOf("Oct 14, 2026") }
    var guests by remember { mutableIntStateOf(2) }
    var selectedRoomType by remember { mutableStateOf(hotel.roomTypes.firstOrNull() ?: "Deluxe Studio") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Text(
            text = "RESERVE SUITE AT ${hotel.name.uppercase()}",
            color = ZubTextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 0.5.sp
        )
        Text(text = hotel.location, color = ZubTextSecondary, fontSize = 12.sp)

        Spacer(modifier = Modifier.height(14.dp))

        // Room Type Selection
        Text(text = "SELECT ROOM / SUITE", color = ZubTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        hotel.roomTypes.forEach { room ->
            val isSelected = selectedRoomType == room
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) ZubCyan.copy(alpha = 0.15f) else ZubSurface)
                    .border(1.dp, if (isSelected) ZubCyan else ZubBorder, RoundedCornerShape(8.dp))
                    .clickable { selectedRoomType = room }
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(room, color = if (isSelected) ZubCyan else ZubTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("$${hotel.pricePerNight.toInt()}/night", color = ZubIceBlue, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Check in / Check out row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(ZubSurface)
                    .border(1.dp, ZubBorder, RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DateRange, contentDescription = null, tint = ZubCyan, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Check-In", color = ZubTextMuted, fontSize = 10.sp)
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(checkIn, color = ZubTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(ZubSurface)
                    .border(1.dp, ZubBorder, RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DateRange, contentDescription = null, tint = ZubCyan, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Check-Out", color = ZubTextMuted, fontSize = 10.sp)
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(checkOut, color = ZubTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Total Price & Confirmation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Est. Total (2 Nights + Studio Pass):", color = ZubTextSecondary, fontSize = 12.sp)
            Text("$${(hotel.pricePerNight * 2).toInt()}", color = ZubCyan, fontSize = 18.sp, fontWeight = FontWeight.Black)
        }

        Spacer(modifier = Modifier.height(16.dp))

        NeonButton(
            text = "CONFIRM RESERVATION",
            icon = Icons.Default.Hotel,
            onClick = { onConfirmBooking(checkIn, checkOut, guests, selectedRoomType) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
