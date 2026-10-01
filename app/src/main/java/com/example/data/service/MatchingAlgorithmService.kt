package com.example.data.service

import com.example.data.model.MatchCandidate
import com.example.data.model.MatchScoreBreakdown
import com.example.data.model.MatchUserProfile
import com.example.data.model.RelationshipGoal
import com.example.data.model.ScoredMatchCandidate
import kotlin.math.abs

/**
 * Intelligent Matching Algorithm Service for ZUB-ZERO Matchmaking.
 * Evaluates multi-dimensional compatibility based on:
 * 1. Relationship Intentions & Long-term Goals (30%)
 * 2. Cinema & Screen Entertainment Taste (25%)
 * 3. Hobbies & Lifestyle Habits (20%)
 * 4. Geographical Proximity & City Fit (15%)
 * 5. Age Range Compatibility & Mutual Preferences (10%)
 */
class MatchingAlgorithmService {

    /**
     * Scores a single candidate against the current user's profile and preferences.
     */
    fun calculateScore(
        userProfile: MatchUserProfile,
        candidate: MatchCandidate
    ): ScoredMatchCandidate {
        // 1. Goal Alignment (Max 30)
        val goalScore = calculateGoalScore(userProfile.relationshipGoal, candidate.relationshipGoal)

        // 2. Cinema & Entertainment Affinity (Max 25)
        val (cinemaScore, sharedGenres) = calculateCinemaScore(userProfile.favoriteGenre, candidate.favoriteCinemaGenre)

        // 3. Shared Hobbies & Lifestyle (Max 20)
        val (hobbiesScore, sharedHobbies) = calculateHobbiesScore(userProfile.preferredHobbies, candidate.hobbies)

        // 4. Location & Proximity (Max 15)
        val locationScore = calculateLocationScore(userProfile.location, candidate.location, candidate.distanceKm)

        // 5. Age Compatibility (Max 10)
        val ageScore = calculateAgeScore(
            candidateAge = candidate.age,
            preferredMin = userProfile.preferredMinAge,
            preferredMax = userProfile.preferredMaxAge,
            userAge = userProfile.age,
            lookingForRange = candidate.lookingForAgeRange
        )

        // Bonus for verified traits (Dear, Fitter & Top Rated)
        var bonus = 0
        if (candidate.isDearCandidate) bonus += 2
        if (candidate.isFittedMatch) bonus += 2
        if (candidate.rating >= 4.8f) bonus += 1

        val rawTotal = goalScore + cinemaScore + hobbiesScore + locationScore + ageScore + bonus
        val finalScore = rawTotal.coerceIn(50, 100)

        val breakdown = MatchScoreBreakdown(
            relationshipGoalScore = goalScore,
            cinemaGenreScore = cinemaScore,
            hobbiesScore = hobbiesScore,
            locationProximityScore = locationScore,
            ageScore = ageScore
        )

        val grade = when {
            finalScore >= 95 -> "🌟 Soulmate Chemistry (${finalScore}%)"
            finalScore >= 88 -> "💖 High Affinity Match (${finalScore}%)"
            finalScore >= 80 -> "✨ Great Connection (${finalScore}%)"
            finalScore >= 70 -> "💫 Compatible Partner (${finalScore}%)"
            else -> "🤝 Emerging Match (${finalScore}%)"
        }

        val highlights = mutableListOf<String>()
        if (userProfile.relationshipGoal == candidate.relationshipGoal) {
            highlights.add("Aligned Goals: Both seeking ${candidate.relationshipGoal.label}")
        } else {
            highlights.add("Compatible Intentions: ${candidate.relationshipGoal.label}")
        }

        if (sharedGenres.isNotEmpty()) {
            highlights.add("Cinema Synergy: ${sharedGenres.joinToString(" & ")}")
        } else {
            highlights.add("Cinema Partner: ${candidate.favoriteCinemaGenre}")
        }

        if (sharedHobbies.isNotEmpty()) {
            highlights.add("Shared Hobbies: ${sharedHobbies.take(2).joinToString(", ")}")
        }

        if (candidate.distanceKm <= 10) {
            highlights.add("Nearby: Only ${candidate.distanceKm}km away in ${candidate.location.split(",").firstOrNull()?.trim() ?: candidate.location}")
        }

        val recommendationReason = buildRecommendationReason(candidate, finalScore, sharedGenres)

        return ScoredMatchCandidate(
            candidate = candidate,
            matchScore = finalScore,
            compatibilityGrade = grade,
            sharedInterests = highlights,
            scoreBreakdown = breakdown,
            recommendationReason = recommendationReason
        )
    }

    /**
     * Ranks and suggests compatible candidates ordered by highest match score.
     */
    fun suggestCompatibleUsers(
        userProfile: MatchUserProfile,
        candidates: List<MatchCandidate>
    ): List<ScoredMatchCandidate> {
        return candidates
            .filter { it.gender == userProfile.targetGender }
            .map { calculateScore(userProfile, it) }
            .sortedByDescending { it.matchScore }
    }

    // --- Private Calculation Helpers ---

    private fun calculateGoalScore(userGoal: RelationshipGoal, candidateGoal: RelationshipGoal): Int {
        if (userGoal == candidateGoal) return 30
        return when {
            (userGoal == RelationshipGoal.SERIOUS_RELATIONSHIP && candidateGoal == RelationshipGoal.COMPANIONSHIP) ||
            (userGoal == RelationshipGoal.COMPANIONSHIP && candidateGoal == RelationshipGoal.SERIOUS_RELATIONSHIP) -> 25
            (userGoal == RelationshipGoal.DATING && candidateGoal == RelationshipGoal.SERIOUS_RELATIONSHIP) ||
            (userGoal == RelationshipGoal.SERIOUS_RELATIONSHIP && candidateGoal == RelationshipGoal.DATING) -> 22
            (userGoal == RelationshipGoal.DATING && candidateGoal == RelationshipGoal.COMPANIONSHIP) ||
            (userGoal == RelationshipGoal.COMPANIONSHIP && candidateGoal == RelationshipGoal.DATING) -> 22
            else -> 18
        }
    }

    private fun calculateCinemaScore(userGenre: String, candidateGenre: String): Pair<Int, List<String>> {
        val userTokens = userGenre.lowercase().split("&", ",", "/", " ", "and").map { it.trim() }.filter { it.length > 2 }
        val candidateTokens = candidateGenre.lowercase().split("&", ",", "/", " ", "and").map { it.trim() }.filter { it.length > 2 }

        val overlap = userTokens.filter { u -> candidateTokens.any { c -> c.contains(u) || u.contains(c) } }.distinct()
        val formattedOverlap = overlap.map { it.replaceFirstChar { char -> char.uppercase() } }

        val score = when {
            overlap.size >= 2 -> 25
            overlap.size == 1 -> 21
            userGenre.contains("Cinema", ignoreCase = true) || candidateGenre.contains("Cinema", ignoreCase = true) -> 18
            else -> 15
        }
        return Pair(score, formattedOverlap)
    }

    private fun calculateHobbiesScore(userHobbies: List<String>, candidateHobbies: List<String>): Pair<Int, List<String>> {
        val matchedHobbies = mutableListOf<String>()
        for (u in userHobbies) {
            val uLower = u.lowercase()
            for (c in candidateHobbies) {
                val cLower = c.lowercase()
                if (uLower.contains(cLower) || cLower.contains(uLower) ||
                    (uLower.contains("cinema") && cLower.contains("film")) ||
                    (uLower.contains("dining") && cLower.contains("culinary")) ||
                    (uLower.contains("art") && cLower.contains("design"))
                ) {
                    matchedHobbies.add(c)
                }
            }
        }
        val distinctMatches = matchedHobbies.distinct()

        val score = when {
            distinctMatches.size >= 3 -> 20
            distinctMatches.size == 2 -> 18
            distinctMatches.size == 1 -> 15
            else -> 12
        }
        return Pair(score, distinctMatches)
    }

    private fun calculateLocationScore(userLocation: String, candidateLocation: String, distanceKm: Int): Int {
        val sameCity = (userLocation.contains("Lagos", ignoreCase = true) && candidateLocation.contains("Lagos", ignoreCase = true)) ||
                       (userLocation.contains("Abuja", ignoreCase = true) && candidateLocation.contains("Abuja", ignoreCase = true))

        val distanceScore = when {
            distanceKm <= 5 -> 15
            distanceKm <= 10 -> 13
            distanceKm <= 20 -> 11
            else -> 8
        }

        return if (sameCity) distanceScore.coerceAtLeast(12) else distanceScore
    }

    private fun calculateAgeScore(
        candidateAge: Int,
        preferredMin: Int,
        preferredMax: Int,
        userAge: Int,
        lookingForRange: String
    ): Int {
        val inPreferredRange = candidateAge in preferredMin..preferredMax
        if (inPreferredRange) return 10

        val diff = when {
            candidateAge < preferredMin -> preferredMin - candidateAge
            else -> candidateAge - preferredMax
        }

        return when {
            diff <= 2 -> 8
            diff <= 4 -> 6
            else -> 4
        }
    }

    private fun buildRecommendationReason(
        candidate: MatchCandidate,
        score: Int,
        sharedGenres: List<String>
    ): String {
        return when {
            score >= 95 -> "Top Suggested Soulmate: Exceptional alignment in relationship goals, lifestyle values, and shared ${candidate.favoriteCinemaGenre} taste."
            score >= 88 -> "High Affinity Match: Strong mutual connection with aligned cinema passions and close location proximity in ${candidate.location}."
            score >= 80 -> "Great Chemistry: Compatible lifestyle interests, verified relationship focus, and engaging conversational spark."
            else -> "Promising Match: Shared creative perspectives and positive community rating."
        }
    }
}
