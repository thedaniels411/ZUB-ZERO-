package com.example.data.repository

import com.example.R
import com.example.data.model.MatchCandidate
import com.example.data.model.MatchGender
import com.example.data.model.RelationshipGoal

object MatchmakingData {
    val initialCandidates: List<MatchCandidate> = listOf(
        // Single and Searching Women
        MatchCandidate(
            id = "w1",
            fullName = "Amina Adeleke",
            age = 25,
            gender = MatchGender.WOMEN,
            location = "Victoria Island, Lagos",
            profession = "Fashion Stylist & Creative Director",
            bio = "Warm, elegant, and spiritually grounded. Passionate about art, cinema nights, and traveling. Searching for a respectful, ambitious gentleman for marriage and building a beautiful home.",
            relationshipGoal = RelationshipGoal.SERIOUS_RELATIONSHIP,
            hobbies = listOf("Film Premiere", "Culinary Arts", "Afro-Beats", "Beach Sunsets"),
            lookingForAgeRange = "26 - 38 years",
            isVerified = true,
            avatarRes = R.drawable.img_match_female,
            distanceKm = 4,
            favoriteCinemaGenre = "Drama & Romance",
            rating = 4.9f,
            reviewsCount = 34,
            isDearCandidate = true,
            isFittedMatch = true
        ),
        MatchCandidate(
            id = "w2",
            fullName = "Zainab Balogun",
            age = 27,
            gender = MatchGender.WOMEN,
            location = "Ikoyi, Lagos",
            profession = "Architect & Interior Designer",
            bio = "Poised, witty, and deeply intentional. Love serene dates, classical jazz, and architecture discussions. Seeking a genuine, prayerful partner with visionary mindset.",
            relationshipGoal = RelationshipGoal.SERIOUS_RELATIONSHIP,
            hobbies = listOf("Art Galleries", "Tennis", "Cinema Studio", "Fine Dining"),
            lookingForAgeRange = "28 - 40 years",
            isVerified = true,
            avatarRes = R.drawable.img_match_female,
            distanceKm = 7,
            favoriteCinemaGenre = "Action & Sci-Fi",
            rating = 4.8f,
            reviewsCount = 29,
            isDearCandidate = true,
            isFittedMatch = true
        ),
        MatchCandidate(
            id = "w3",
            fullName = "Chidinma Okafor",
            age = 24,
            gender = MatchGender.WOMEN,
            location = "Maitama, Abuja",
            profession = "Media Producer & Content Strategist",
            bio = "Joyful energy with an entrepreneurial drive. Single, searching, and ready for true romance. Looking for a partner who communicates with depth and laughs easily.",
            relationshipGoal = RelationshipGoal.DATING,
            hobbies = listOf("Movie Scripting", "Photography", "Travel", "Poetry"),
            lookingForAgeRange = "25 - 35 years",
            isVerified = true,
            avatarRes = R.drawable.img_match_female,
            distanceKm = 12,
            favoriteCinemaGenre = "Twilight & Romantic Thrillers",
            rating = 4.7f,
            reviewsCount = 22,
            isDearCandidate = true,
            isFittedMatch = false
        ),

        // Single and Searching Men
        MatchCandidate(
            id = "m1",
            fullName = "Femi Adebayo",
            age = 29,
            gender = MatchGender.MEN,
            location = "Lekki Phase 1, Lagos",
            profession = "Software Tech Executive & Cinephile",
            bio = "God-fearing, goal-driven, and romantic at heart. I value deep loyalty, good conversations over mocktails, and weekend cinema marathons. Ready to settle down.",
            relationshipGoal = RelationshipGoal.SERIOUS_RELATIONSHIP,
            hobbies = listOf("Cinema Viewing", "Sailing", "Tech Innovations", "Gym"),
            lookingForAgeRange = "22 - 28 years",
            isVerified = true,
            avatarRes = R.drawable.img_match_male,
            distanceKm = 5,
            favoriteCinemaGenre = "Sci-Fi & Action",
            rating = 5.0f,
            reviewsCount = 41,
            isDearCandidate = true,
            isFittedMatch = true
        ),
        MatchCandidate(
            id = "m2",
            fullName = "Kelechi Eze",
            age = 31,
            gender = MatchGender.MEN,
            location = "Ikeja GRA, Lagos",
            profession = "Chartered Architect & Real Estate Investor",
            bio = "Thoughtful, established gentleman who appreciates simplicity, honesty, and family values. Searching for an intelligent lady with a beautiful heart.",
            relationshipGoal = RelationshipGoal.SERIOUS_RELATIONSHIP,
            hobbies = listOf("Design", "Piano", "Cinema Production", "Road Trips"),
            lookingForAgeRange = "24 - 30 years",
            isVerified = true,
            avatarRes = R.drawable.img_match_male,
            distanceKm = 8,
            favoriteCinemaGenre = "Drama & Mystery",
            rating = 4.8f,
            reviewsCount = 19,
            isDearCandidate = true,
            isFittedMatch = true
        ),
        MatchCandidate(
            id = "m3",
            fullName = "Tariq Danjuma",
            age = 28,
            gender = MatchGender.MEN,
            location = "Wuse II, Abuja",
            profession = "Cinematographer & Commercial Director",
            bio = "Life is a movie, let's write our love story. Passionate creative seeking a classy, supportive partner who enjoys laughter, luxury stays, and authentic love.",
            relationshipGoal = RelationshipGoal.DATING,
            hobbies = listOf("Film Cameras", "Rooftop Lounges", "Jazz", "Scuba"),
            lookingForAgeRange = "23 - 29 years",
            isVerified = true,
            avatarRes = R.drawable.img_match_male,
            distanceKm = 15,
            favoriteCinemaGenre = "Action & Romance",
            rating = 4.6f,
            reviewsCount = 17,
            isDearCandidate = false,
            isFittedMatch = true
        )
    )
}
