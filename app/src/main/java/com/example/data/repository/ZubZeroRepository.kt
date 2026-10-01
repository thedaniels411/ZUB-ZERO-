package com.example.data.repository

import com.example.R
import com.example.data.local.dao.MarketDao
import com.example.data.local.dao.MovieDao
import com.example.data.local.entity.CachedHotelEntity
import com.example.data.local.entity.CartItemEntity
import com.example.data.local.entity.HotelBookingEntity
import com.example.data.local.entity.MarketListingEntity
import com.example.data.local.entity.MovieProjectEntity
import com.example.data.model.GeneratedMovie
import com.example.data.model.HotelItem
import com.example.data.model.MarketCategory
import com.example.data.model.MovieGenre
import com.example.data.model.ProductItem
import com.example.data.model.VideoScene
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

class ZubZeroRepository(
    private val movieDao: MovieDao,
    private val marketDao: MarketDao
) {
    // Flow of saved movies
    val savedMovies: Flow<List<GeneratedMovie>> = movieDao.getAllMovies().map { entities ->
        entities.map { entity ->
            val scenes = parseScenesJson(entity.scenesJson)
            val genreEnum = try {
                MovieGenre.valueOf(entity.genre)
            } catch (e: Exception) {
                MovieGenre.ACTION
            }
            val generatorTypeEnum = try {
                com.example.data.model.GeneratorType.valueOf(entity.generatorType)
            } catch (e: Exception) {
                com.example.data.model.GeneratorType.IDEA_TO_VIDEO
            }
            val defaultImg = when (genreEnum) {
                MovieGenre.ACTION -> R.drawable.img_cinema_hero
                MovieGenre.DRAMA -> R.drawable.img_cinema_hero
                MovieGenre.TWILIGHT -> R.drawable.img_cinema_hero
                MovieGenre.SCI_FI -> R.drawable.img_cinema_hero
            }
            GeneratedMovie(
                id = entity.id,
                title = entity.title,
                genre = genreEnum,
                generatorType = generatorTypeEnum,
                logline = entity.logline,
                synopsis = entity.synopsis,
                scenes = scenes,
                aspectRatio = entity.aspectRatio,
                dateCreated = entity.dateCreated,
                imageRes = defaultImg
            )
        }
    }

    suspend fun saveMovie(movie: GeneratedMovie): Long {
        val scenesJson = serializeScenesToJson(movie.scenes)
        val entity = MovieProjectEntity(
            title = movie.title,
            genre = movie.genre.name,
            generatorType = movie.generatorType.name,
            logline = movie.logline,
            synopsis = movie.synopsis,
            scenesJson = scenesJson,
            aspectRatio = movie.aspectRatio,
            dateCreated = movie.dateCreated
        )
        return movieDao.insertMovie(entity)
    }

    suspend fun deleteMovie(id: Long) {
        movieDao.deleteMovieById(id)
    }

    // Cart operations
    val cartItems: Flow<List<CartItemEntity>> = marketDao.getCartItems()

    suspend fun addToCart(
        product: ProductItem,
        selectedSize: String = "M",
        selectedColor: String = "Default",
        quantity: Int = 1
    ) {
        val entity = CartItemEntity(
            productId = product.id,
            title = product.title,
            category = product.category.label,
            price = product.price,
            quantity = quantity,
            selectedSize = selectedSize,
            selectedColor = selectedColor,
            imageRes = product.imageRes
        )
        marketDao.insertCartItem(entity)
    }

    suspend fun removeFromCart(cartItemId: Long) {
        marketDao.deleteCartItem(cartItemId)
    }

    suspend fun clearCart() {
        marketDao.clearCart()
    }

    // Hotel bookings
    val hotelBookings: Flow<List<HotelBookingEntity>> = marketDao.getAllBookings()

    suspend fun bookHotel(
        hotel: HotelItem,
        checkIn: String,
        checkOut: String,
        guests: Int,
        roomType: String,
        totalPrice: Double
    ): Long {
        val entity = HotelBookingEntity(
            hotelId = hotel.id,
            hotelName = hotel.name,
            location = hotel.location,
            checkInDate = checkIn,
            checkOutDate = checkOut,
            guests = guests,
            roomType = roomType,
            totalPrice = totalPrice,
            status = "Confirmed",
            bookingTimestamp = System.currentTimeMillis()
        )
        return marketDao.insertBooking(entity)
    }

    suspend fun cancelBooking(bookingId: Long) {
        marketDao.cancelBooking(bookingId)
    }

    // Room Flows for Cached Marketplace Listings and Hotel Data (Offline Viewing)
    val cachedProductsFlow: Flow<List<ProductItem>> = marketDao.getAllCachedMarketListings().map { entities ->
        entities.map { it.toDomainModel() }
    }

    val cachedHotelsFlow: Flow<List<HotelItem>> = marketDao.getAllCachedHotels().map { entities ->
        entities.map { it.toDomainModel() }
    }

    /**
     * Seeds Room local database cache on initial app launch if empty so data is immediately cached offline.
     */
    suspend fun ensureDefaultCachePopulated() {
        val productCount = marketDao.getMarketListingsCount()
        if (productCount == 0) {
            val initialProducts = CatalogData.sampleMarketGear + CatalogData.sampleFashionItems
            marketDao.insertMarketListings(initialProducts.map { it.toEntity() })
        }
        val hotelCount = marketDao.getHotelsCount()
        if (hotelCount == 0) {
            val initialHotels = CatalogData.sampleHotels
            marketDao.insertHotels(initialHotels.map { it.toEntity() })
        }
    }

    /**
     * Refreshes / syncs all marketplace products and hotel entries into Room cache.
     */
    suspend fun refreshAllMarketplaceAndHotelCache() {
        val initialProducts = CatalogData.sampleMarketGear + CatalogData.sampleFashionItems
        marketDao.insertMarketListings(initialProducts.map { it.toEntity() })
        val initialHotels = CatalogData.sampleHotels
        marketDao.insertHotels(initialHotels.map { it.toEntity() })
    }

    suspend fun cacheProduct(product: ProductItem) {
        marketDao.insertMarketListing(product.toEntity())
    }

    suspend fun cacheHotel(hotel: HotelItem) {
        marketDao.insertHotel(hotel.toEntity())
    }

    // Catalog queries (with fallback to memory if database isn't ready)
    fun getHotels(): List<HotelItem> = CatalogData.sampleHotels

    fun getFashionItems(): List<ProductItem> = CatalogData.sampleFashionItems

    fun getAllMarketProducts(): List<ProductItem> = CatalogData.sampleMarketGear + CatalogData.sampleFashionItems

    private fun MarketListingEntity.toDomainModel(): ProductItem {
        val cat = try {
            MarketCategory.valueOf(this.category)
        } catch (e: Exception) {
            MarketCategory.entries.find { it.label == this.category } ?: MarketCategory.ALL
        }
        return ProductItem(
            id = this.id,
            title = this.title,
            category = cat,
            price = this.price,
            originalPrice = this.originalPrice,
            rating = this.rating,
            reviewsCount = this.reviewsCount,
            imageRes = this.imageRes,
            description = this.description,
            specs = parseStringList(this.specsJson),
            inStock = this.inStock,
            sizes = parseStringList(this.sizesJson),
            colors = parseStringList(this.colorsJson),
            isFashion = this.isFashion,
            seller = this.seller
        )
    }

    private fun ProductItem.toEntity(): MarketListingEntity {
        return MarketListingEntity(
            id = this.id,
            title = this.title,
            category = this.category.name,
            price = this.price,
            originalPrice = this.originalPrice,
            rating = this.rating,
            reviewsCount = this.reviewsCount,
            imageRes = this.imageRes,
            description = this.description,
            specsJson = serializeStringList(this.specs),
            inStock = this.inStock,
            sizesJson = serializeStringList(this.sizes),
            colorsJson = serializeStringList(this.colors),
            isFashion = this.isFashion,
            seller = this.seller,
            lastCachedTimestamp = System.currentTimeMillis()
        )
    }

    private fun CachedHotelEntity.toDomainModel(): HotelItem {
        return HotelItem(
            id = this.id,
            name = this.name,
            location = this.location,
            distanceKm = this.distanceKm,
            pricePerNight = this.pricePerNight,
            rating = this.rating,
            reviewsCount = this.reviewsCount,
            imageRes = this.imageRes,
            description = this.description,
            amenities = parseStringList(this.amenitiesJson),
            roomTypes = parseStringList(this.roomTypesJson),
            badge = this.badge
        )
    }

    private fun HotelItem.toEntity(): CachedHotelEntity {
        return CachedHotelEntity(
            id = this.id,
            name = this.name,
            location = this.location,
            distanceKm = this.distanceKm,
            pricePerNight = this.pricePerNight,
            rating = this.rating,
            reviewsCount = this.reviewsCount,
            imageRes = this.imageRes,
            description = this.description,
            amenitiesJson = serializeStringList(this.amenities),
            roomTypesJson = serializeStringList(this.roomTypes),
            badge = this.badge,
            lastCachedTimestamp = System.currentTimeMillis()
        )
    }

    private fun serializeStringList(list: List<String>): String {
        val array = JSONArray()
        list.forEach { array.put(it) }
        return array.toString()
    }

    private fun parseStringList(json: String?): List<String> {
        if (json.isNullOrBlank()) return emptyList()
        val result = mutableListOf<String>()
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                result.add(array.getString(i))
            }
        } catch (e: Exception) {
            // fallback
        }
        return result
    }

    private fun serializeScenesToJson(scenes: List<VideoScene>): String {
        val array = JSONArray()
        for (scene in scenes) {
            val obj = JSONObject()
            obj.put("sceneNumber", scene.sceneNumber)
            obj.put("title", scene.title)
            obj.put("visualPrompt", scene.visualPrompt)
            obj.put("cameraMovement", scene.cameraMovement)
            obj.put("dialogue", scene.dialogue)
            obj.put("durationSeconds", scene.durationSeconds)
            obj.put("ambientAudio", scene.ambientAudio)
            array.put(obj)
        }
        return array.toString()
    }

    private fun parseScenesJson(json: String): List<VideoScene> {
        val list = mutableListOf<VideoScene>()
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    VideoScene(
                        sceneNumber = obj.optInt("sceneNumber", i + 1),
                        title = obj.optString("title", "Scene ${i + 1}"),
                        visualPrompt = obj.optString("visualPrompt", ""),
                        cameraMovement = obj.optString("cameraMovement", "Cinematic Pan"),
                        dialogue = obj.optString("dialogue", ""),
                        durationSeconds = obj.optInt("durationSeconds", 6),
                        ambientAudio = obj.optString("ambientAudio", "Cinematic Score")
                    )
                )
            }
        } catch (e: Exception) {
            // fallback
        }
        return list
    }
}
