package com.example.data.model

enum class MarketCategory(val label: String) {
    ALL("All Items"),
    FILM_GEAR("Film & Studio Gear"),
    PROPS("Movie Props"),
    FASHION("Fashion & Style"),
    ACCESSORIES("Wearables & Tech")
}

data class ProductItem(
    val id: String,
    val title: String,
    val category: MarketCategory,
    val price: Double,
    val originalPrice: Double,
    val rating: Float,
    val reviewsCount: Int,
    val imageRes: Int,
    val description: String,
    val specs: List<String>,
    val inStock: Boolean = true,
    val sizes: List<String> = emptyList(),
    val colors: List<String> = emptyList(),
    val isFashion: Boolean = false,
    val seller: String = "ZUB-ZERO Official"
)

data class HotelItem(
    val id: String,
    val name: String,
    val location: String,
    val distanceKm: Double,
    val pricePerNight: Double,
    val rating: Float,
    val reviewsCount: Int,
    val imageRes: Int,
    val description: String,
    val amenities: List<String>,
    val roomTypes: List<String>,
    val badge: String = "Production Partner"
)
