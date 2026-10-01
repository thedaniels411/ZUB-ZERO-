package com.example.data.model

import androidx.compose.ui.graphics.Color

/**
 * Upload tier fees for establishing channels, stores, products, or goods in ZUB-ZERO
 */
enum class UploadFeeTier(
    val usdPrice: Int,
    val nairaEquivalent: Long,
    val label: String,
    val description: String,
    val badge: String
) {
    STANDARD_PROD(
        usdPrice = 10,
        nairaEquivalent = 15_000,
        label = "Standard Upload ($10)",
        description = "Single product item, goods, or media listing with basic distribution",
        badge = "STARTER"
    ),
    CHANNEL_STORE(
        usdPrice = 50,
        nairaEquivalent = 75_000,
        label = "Store / Channel Establishment ($50)",
        description = "Full studio store, creator channel establishment & catalog shelf",
        badge = "CREATOR"
    ),
    PRODUCTION_ENTERPRISE(
        usdPrice = 80,
        nairaEquivalent = 120_000,
        label = "Production Enterprise ($80)",
        description = "Complete cinema production house, media pipeline & multi-channel syndication",
        badge = "PRO STUDIO"
    )
}

/**
 * Subscription tiers for ZUB-ZERO
 * Exact price structure:
 * - 1 Month: 6,557 Thousand Naira (₦6,557,000) ~ $4,370 USD / £3,450 GBP
 * - 3 Months: ₦20,500 Naira ~ $14 USD / £11 GBP
 * - 1 Year: ₦78,900 Naira ~ $53 USD / £42 GBP
 */
enum class SubscriptionPlan(
    val planName: String,
    val durationMonths: Int,
    val priceNaira: Long,
    val priceUsd: Int,
    val priceGbp: Int,
    val priceFormatted: String,
    val priceUsdFormatted: String,
    val priceGbpFormatted: String,
    val perks: List<String>,
    val badge: String
) {
    ONE_MONTH(
        planName = "Standard 1-Month",
        durationMonths = 1,
        priceNaira = 6_557_000,
        priceUsd = 4_370,
        priceGbp = 3_450,
        priceFormatted = "6,557 Thousand Naira (₦6,557,000) [$4,370 USD / £3,450 GBP]",
        priceUsdFormatted = "$4,370 USD",
        priceGbpFormatted = "£3,450 GBP",
        perks = listOf(
            "Unlimited 4K AI video rendering",
            "Store and channel management tools",
            "100 free video seconds per day",
            "Priority Gemini script generation"
        ),
        badge = "STANDARD"
    ),
    THREE_MONTHS(
        planName = "Pro 3-Months",
        durationMonths = 3,
        priceNaira = 20_500,
        priceUsd = 14,
        priceGbp = 11,
        priceFormatted = "₦20,500 Naira [$14 USD / £11 GBP]",
        priceUsdFormatted = "$14 USD",
        priceGbpFormatted = "£11 GBP",
        perks = listOf(
            "Everything in Standard",
            "300 bonus coin allowance",
            "Zero upload commission on marketplace products",
            "VIP Hotel & crew booking concierge"
        ),
        badge = "POPULAR"
    ),
    ONE_YEAR(
        planName = "Master 1-Year",
        durationMonths = 12,
        priceNaira = 78_900,
        priceUsd = 53,
        priceGbp = 42,
        priceFormatted = "₦78,900 Naira [$53 USD / £42 GBP]",
        priceUsdFormatted = "$53 USD",
        priceGbpFormatted = "£42 GBP",
        perks = listOf(
            "Full 365-day studio access",
            "All channels & bodies establishment unlocked",
            "Direct syndication to ZUB-ZERO cinema network",
            "Verified Merchant & Director checkmark badge"
        ),
        badge = "BEST VALUE"
    )
}

/**
 * Currency enumeration for multi-currency receiving wallets & balances
 */
enum class AppCurrency(
    val code: String,
    val symbol: String,
    val currencyName: String,
    val flagEmoji: String,
    val exchangeRateToNaira: Double
) {
    USD("USD", "$", "United States Dollar", "🇺🇸", 1500.0),
    GBP("GBP", "£", "British Pound Sterling", "🇬🇧", 1900.0),
    NGN("NGN", "₦", "Nigerian Naira", "🇳🇬", 1.0)
}

/**
 * Receiving Wallet for ZUB-ZERO
 * Each currency has its own dedicated receiving wallet with virtual accounts,
 * receiving IDs, swift codes, and live revenue tracking.
 */
data class ReceivingWallet(
    val currency: AppCurrency,
    val balance: Double,
    val totalRevenueReceived: Double,
    val pendingPayout: Double,
    val walletAddressOrVirtualAccount: String,
    val bankOrProvider: String,
    val accountHolder: String = "ADEKOYA DANIEL EBENEZER (CEO D-DANIEL'S INVENTION BIZ)"
)

/**
 * CEO Local Bank Payout Account Details
 */
data class CeoBankPayoutDetails(
    val bankName: String = "First Bank of Nigeria",
    val accountNumber: String = "3094810828",
    val accountName: String = "ADEKOYA DANIEL EBENEZER",
    val bvnOrTinVerified: Boolean = true,
    val swiftCode: String = "FBNINGLA"
)

/**
 * Withdrawal transaction record
 */
data class WithdrawalTransaction(
    val id: String,
    val currency: AppCurrency,
    val amountRequested: Double,
    val nairaPayoutAmount: Double,
    val destinationBank: String,
    val destinationAccount: String,
    val destinationAccountName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "COMPLETED", // COMPLETED, PROCESSING
    val reference: String,
    val paymentType: String = "CEO_LOCAL_BANK_PAYOUT"
)

/**
 * Payment method channels accepted in ZUB-ZERO
 */
enum class PaymentMethodType(val title: String, val description: String) {
    LOCAL_CARD("Local Debit / Credit Card", "Mastercard, Visa, Verve & Interswitch"),
    BANK_TRANSFER("Direct Bank Payment", "Instant dynamic virtual bank account with automatic webhook confirmation"),
    USSD("USSD Banking Transfer", "*737#, *894#, *901#, *966#, *919# & more")
}

/**
 * Registered User Profile
 */
data class UserProfile(
    val isRegistered: Boolean = false,
    val fullName: String = "",
    val mobileNumber: String = "",
    val email: String = "",
    val facialPictureUri: String? = null,
    val mediaDisplayType: String = "PHOTO", // PHOTO or VIDEO
    val activeSubscription: SubscriptionPlan? = null,
    val coins: Int = 50, // 50 coins free for first-time user
    val lastDailyCheckInEpochMs: Long = 0L,
    val activityCount: Int = 0,
    val adsWatchedForCycle: Int = 0,
    val hasClaimedFirstTimeBonus: Boolean = true,
    val checkInStreak: Int = 1,
    // 3-Day Free Trial Attached to Subscribed Channels (Attracting initial ₦500 Naira trial access)
    val isTrialActive: Boolean = false,
    val trialDaysRemaining: Int = 3,
    val hasPaidInitialTrialFee: Boolean = false,
    val trialExpirationEpochMs: Long = 0L
) {
    // 50 coins can be used for 20 seconds of video generation -> 2.5 coins per second
    val videoSecondsAvailable: Double
        get() = (coins / 50.0) * 20.0

    val coinsValueInNaira: Double
        get() = (coins.toDouble() / 50.0) * 350.0
}

/**
 * Upload item submission data model
 */
data class UploadSubmission(
    val title: String,
    val description: String,
    val price: Double,
    val category: String,
    val tier: UploadFeeTier,
    val mediaUri: String? = null,
    val channelOrStoreName: String,
    val paidAmountUsd: Int,
    val paidAmountNaira: Long,
    val paymentReference: String,
    val timestamp: Long = System.currentTimeMillis()
)
