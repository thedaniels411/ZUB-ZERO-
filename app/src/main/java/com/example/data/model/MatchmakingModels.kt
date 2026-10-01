package com.example.data.model

import androidx.annotation.DrawableRes
import com.example.R

/**
 * Gender orientation for Matchmaking
 */
enum class MatchGender(val label: String, val emoji: String) {
    MEN("Single Men", "👨"),
    WOMEN("Single Women", "👩")
}

/**
 * Looking for / Relationship Goal
 */
enum class RelationshipGoal(val label: String) {
    SERIOUS_RELATIONSHIP("Marriage & Long-term"),
    DATING("Romantic Dating"),
    COMPANIONSHIP("Deep Companionship & Cinema Partner"),
    NETWORKING_CREATIVE("Creative Arts & Film Partnership")
}

/**
 * Profile of single and searching men and women in ZUB-ZERO Matchmaking
 */
data class MatchCandidate(
    val id: String,
    val fullName: String,
    val age: Int,
    val gender: MatchGender,
    val location: String,
    val profession: String,
    val bio: String,
    val relationshipGoal: RelationshipGoal,
    val hobbies: List<String>,
    val lookingForAgeRange: String,
    val isVerified: Boolean = true,
    @DrawableRes val avatarRes: Int,
    val distanceKm: Int,
    val favoriteCinemaGenre: String = "Romantic Twilight & Drama",
    val channelOrBrand: String = "ZUB-ZERO Verified Singles Hub",
    val rating: Float = 4.9f,
    val reviewsCount: Int = 28,
    val isDearCandidate: Boolean = true,
    val isFittedMatch: Boolean = true
)

/**
 * User's own Matchmaking Registration State
 */
data class MatchUserProfile(
    val isRegistered: Boolean = false,
    val fullName: String = "",
    val age: Int = 26,
    val gender: MatchGender = MatchGender.MEN,
    val targetGender: MatchGender = MatchGender.WOMEN,
    val location: String = "Lagos, Nigeria",
    val profession: String = "Film Producer / Tech Executive",
    val bio: String = "Creative visionary looking for a genuine, ambitious soul to build together.",
    val relationshipGoal: RelationshipGoal = RelationshipGoal.SERIOUS_RELATIONSHIP,
    val contactPhoneOrWhatsapp: String = "",
    val instagramOrSocial: String = "",
    val favoriteGenre: String = "Action & Drama",
    val preferredHobbies: List<String> = listOf("Cinema Viewing", "Film Premiere", "Fine Dining", "Travel", "Art Galleries"),
    val preferredMinAge: Int = 22,
    val preferredMaxAge: Int = 38,
    val registrationDateEpochMs: Long = System.currentTimeMillis(),
    val isTrialActive: Boolean = true,
    val trialDaysRemaining: Int = 3
)

/**
 * Breakdown of compatibility scoring computed by MatchingAlgorithmService
 */
data class MatchScoreBreakdown(
    val relationshipGoalScore: Int, // Max 30
    val cinemaGenreScore: Int,      // Max 25
    val hobbiesScore: Int,          // Max 20
    val locationProximityScore: Int,// Max 15
    val ageScore: Int               // Max 10
) {
    val totalScore: Int = relationshipGoalScore + cinemaGenreScore + hobbiesScore + locationProximityScore + ageScore
}

/**
 * Scored Match Candidate output by the Matching Algorithm Service
 */
data class ScoredMatchCandidate(
    val candidate: MatchCandidate,
    val matchScore: Int, // 0 to 100 percentage
    val compatibilityGrade: String, // e.g. "Soulmate Chemistry (98%)"
    val sharedInterests: List<String>,
    val scoreBreakdown: MatchScoreBreakdown,
    val recommendationReason: String
)
