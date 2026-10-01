package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.CartItemEntity
import com.example.data.local.entity.CachedHotelEntity
import com.example.data.local.entity.HotelBookingEntity
import com.example.data.local.entity.MarketListingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MarketDao {
    @Query("SELECT * FROM cart_items ORDER BY id DESC")
    fun getCartItems(): Flow<List<CartItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartItemEntity): Long

    @Update
    suspend fun updateCartItem(item: CartItemEntity)

    @Query("DELETE FROM cart_items WHERE id = :id")
    suspend fun deleteCartItem(id: Long)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()

    @Query("SELECT * FROM hotel_bookings ORDER BY bookingTimestamp DESC")
    fun getAllBookings(): Flow<List<HotelBookingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: HotelBookingEntity): Long

    @Query("DELETE FROM hotel_bookings WHERE id = :id")
    suspend fun cancelBooking(id: Long)

    // Cached Marketplace Listings for Offline Viewing
    @Query("SELECT * FROM cached_marketplace_listings ORDER BY lastCachedTimestamp DESC")
    fun getAllCachedMarketListings(): Flow<List<MarketListingEntity>>

    @Query("SELECT * FROM cached_marketplace_listings WHERE category = :category ORDER BY lastCachedTimestamp DESC")
    fun getCachedMarketListingsByCategory(category: String): Flow<List<MarketListingEntity>>

    @Query("SELECT * FROM cached_marketplace_listings WHERE isFashion = 1 ORDER BY lastCachedTimestamp DESC")
    fun getCachedFashionListings(): Flow<List<MarketListingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMarketListings(listings: List<MarketListingEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMarketListing(listing: MarketListingEntity)

    @Query("DELETE FROM cached_marketplace_listings WHERE id = :id")
    suspend fun deleteMarketListing(id: String)

    @Query("DELETE FROM cached_marketplace_listings")
    suspend fun clearMarketListings()

    @Query("SELECT COUNT(*) FROM cached_marketplace_listings")
    suspend fun getMarketListingsCount(): Int

    // Cached Hotels for Offline Viewing
    @Query("SELECT * FROM cached_hotels ORDER BY rating DESC")
    fun getAllCachedHotels(): Flow<List<CachedHotelEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHotels(hotels: List<CachedHotelEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHotel(hotel: CachedHotelEntity)

    @Query("DELETE FROM cached_hotels WHERE id = :id")
    suspend fun deleteHotel(id: String)

    @Query("DELETE FROM cached_hotels")
    suspend fun clearHotels()

    @Query("SELECT COUNT(*) FROM cached_hotels")
    suspend fun getHotelsCount(): Int
}
