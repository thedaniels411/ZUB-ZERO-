package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: String,
    val title: String,
    val category: String,
    val price: Double,
    val quantity: Int,
    val selectedSize: String,
    val selectedColor: String,
    val imageRes: Int
)

@Entity(tableName = "hotel_bookings")
data class HotelBookingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val hotelId: String,
    val hotelName: String,
    val location: String,
    val checkInDate: String,
    val checkOutDate: String,
    val guests: Int,
    val roomType: String,
    val totalPrice: Double,
    val status: String = "Confirmed",
    val bookingTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "cached_marketplace_listings")
data class MarketListingEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: String,
    val price: Double,
    val originalPrice: Double,
    val rating: Float,
    val reviewsCount: Int,
    val imageRes: Int,
    val description: String,
    val specsJson: String = "[]",
    val inStock: Boolean = true,
    val sizesJson: String = "[]",
    val colorsJson: String = "[]",
    val isFashion: Boolean = false,
    val seller: String = "ZUB-ZERO Official",
    val lastCachedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "cached_hotels")
data class CachedHotelEntity(
    @PrimaryKey val id: String,
    val name: String,
    val location: String,
    val distanceKm: Double,
    val pricePerNight: Double,
    val rating: Float,
    val reviewsCount: Int,
    val imageRes: Int,
    val description: String,
    val amenitiesJson: String = "[]",
    val roomTypesJson: String = "[]",
    val badge: String = "Production Partner",
    val lastCachedTimestamp: Long = System.currentTimeMillis()
)
