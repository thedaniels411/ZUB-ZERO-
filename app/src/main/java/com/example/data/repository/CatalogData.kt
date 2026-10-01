package com.example.data.repository

import com.example.R
import com.example.data.model.HotelItem
import com.example.data.model.MarketCategory
import com.example.data.model.MovieGenre
import com.example.data.model.ProductItem
import com.example.data.model.VideoScene

object CatalogData {

    val sampleHotels = listOf(
        HotelItem(
            id = "hotel_1",
            name = "Grand Sub-Zero Luxury Towers",
            location = "0.4 km away • Downtown Studio District",
            distanceKm = 0.4,
            pricePerNight = 280.0,
            rating = 4.9f,
            reviewsCount = 342,
            imageRes = R.drawable.img_hotel_luxury,
            description = "Premier cinematic luxury hotel equipped with soundproof celebrity penthouses, infinity skyline pool, 24/7 catering for film crews, and private rooftop helipad.",
            amenities = listOf("Rooftop Helipad", "Infinity Pool", "Soundproof Suites", "Film Crew Lounge", "24/7 Room Service", "Valet Parking"),
            roomTypes = listOf("Director's Penthouse Suite", "Executive Cinema Loft", "Deluxe Twin Studio"),
            badge = "Official Film Partner"
        ),
        HotelItem(
            id = "hotel_2",
            name = "The Twilight Noir Boutique Stays",
            location = "1.2 km away • Old Gothic Quarter",
            distanceKm = 1.2,
            pricePerNight = 195.0,
            rating = 4.8f,
            reviewsCount = 215,
            imageRes = R.drawable.img_hotel_luxury,
            description = "Atmospheric boutique hotel with velvet-lined corridors, moody ambient lighting, speakeasy jazz club, and antique stone courtyards favoured for drama & mystery shoots.",
            amenities = listOf("Moody Velvet Lounge", "Antique Courtyard", "High-Speed Wi-Fi", "Spa & Sauna", "Screening Room"),
            roomTypes = listOf("Gothic Balcony Suite", "Midnight Velvet Studio", "Classic King Room"),
            badge = "Aesthetic Pick"
        ),
        HotelItem(
            id = "hotel_3",
            name = "Apex Horizon Waterfront Resort",
            location = "2.1 km away • Marina Pier",
            distanceKm = 2.1,
            pricePerNight = 340.0,
            rating = 4.95f,
            reviewsCount = 512,
            imageRes = R.drawable.img_hotel_luxury,
            description = "Five-star waterfront resort featuring glass underwater dining, private yacht charters, sun deck cabanas, and panoramic twilight ocean vistas.",
            amenities = listOf("Private Yacht Slip", "Underwater Dining", "Oceanfront Terrace", "Concierge Butler", "Chauffeured Limo"),
            roomTypes = listOf("Oceanic Presidential Suite", "Panoramic Marina Loft", "Azure Master Bedroom"),
            badge = "Ultra Luxury"
        ),
        HotelItem(
            id = "hotel_4",
            name = "Cyberpunk Capsule & Production Hub",
            location = "0.8 km away • Neo-Tech Boulevard",
            distanceKm = 0.8,
            pricePerNight = 110.0,
            rating = 4.7f,
            reviewsCount = 189,
            imageRes = R.drawable.img_hotel_luxury,
            description = "Futuristic smart stay built for content creators, animators, and digital artists. Equipped with dual-monitor editing workstations and cryo-pod showers.",
            amenities = listOf("Dual-Screen Workstation", "Fibre 10Gbps Internet", "RGB Pod Lighting", "24/7 Espresso Bar"),
            roomTypes = listOf("Solo Cyber Pod", "Creator Studio Double", "Team Hack Suite"),
            badge = "Creator Friendly"
        )
    )

    val sampleFashionItems = listOf(
        ProductItem(
            id = "fash_1",
            title = "Sub-Zero Cryo-Tactical Trench Coat",
            category = MarketCategory.FASHION,
            price = 389.0,
            originalPrice = 450.0,
            rating = 4.9f,
            reviewsCount = 128,
            imageRes = R.drawable.img_fashion_cyber,
            description = "Signature futuristic weather-resistant trench coat with thermal-reactive lining, magnetic closure latches, and iridescent cyber-cyan piping. As seen in blockbuster sci-fi action films.",
            specs = listOf("GORE-TEX Pro 3-Layer", "Thermal Cryo-Lining", "Fidlock Magnetic Snaps", "Internal Holster Pocket"),
            sizes = listOf("S", "M", "L", "XL", "XXL"),
            colors = listOf("Obsidian Black", "Frost Titanium", "Midnight Navy"),
            isFashion = true,
            seller = "ZUB-ZERO Atelier"
        ),
        ProductItem(
            id = "fash_2",
            title = "Twilight Velvet Red Carpet Gown",
            category = MarketCategory.FASHION,
            price = 520.0,
            originalPrice = 640.0,
            rating = 5.0f,
            reviewsCount = 94,
            imageRes = R.drawable.img_fashion_cyber,
            description = "Deep nocturnal burgundy silk-velvet evening gown with asymmetrical shoulder drape, thigh-high slit, and crystal-embedded trim designed for movie premiere galas.",
            specs = listOf("100% French Silk Velvet", "Swarovski Star Dust Trim", "Built-in Corsetry", "Dry Clean Only"),
            sizes = listOf("XS", "S", "M", "L"),
            colors = listOf("Twilight Crimson", "Deep Onyx", "Emerald Shadow"),
            isFashion = true,
            seller = "Maison Nocturne"
        ),
        ProductItem(
            id = "fash_3",
            title = "Stealth Operative Carbon Leather Jacket",
            category = MarketCategory.FASHION,
            price = 445.0,
            originalPrice = 499.0,
            rating = 4.85f,
            reviewsCount = 210,
            imageRes = R.drawable.img_fashion_cyber,
            description = "Heavyweight calfskin leather motorcycle jacket tailored for action heroes. Integrated carbon-fiber shoulder plates, reinforced elbow patches, and heavy industrial zippers.",
            specs = listOf("1.4mm Full-Grain Leather", "Removable CE-Armor", "YKK Heavy Duty Zippers", "Quilted Satin Lining"),
            sizes = listOf("M", "L", "XL"),
            colors = listOf("Matte Carbon", "Distressed Espresso"),
            isFashion = true,
            seller = "Apex Stunt Wardrobe"
        ),
        ProductItem(
            id = "fash_4",
            title = "Cyber-Visor Polarized Holographic Shades",
            category = MarketCategory.ACCESSORIES,
            price = 165.0,
            originalPrice = 195.0,
            rating = 4.75f,
            reviewsCount = 145,
            imageRes = R.drawable.img_prop_artifact,
            description = "Aerospace grade titanium frameless shield sunglasses with mirrored UV400 lens and neon cyan edge illumination for sci-fi shoots and street fashion.",
            specs = listOf("Grade 5 Titanium Frame", "UV400 Polarized Lens", "Micro-USB Edge Glow", "Anti-Scratch Coating"),
            sizes = listOf("Universal Fit"),
            colors = listOf("Ice Blue Mirror", "Solar Amber", "Phantom Dark"),
            isFashion = true,
            seller = "ZUB-ZERO Optics"
        )
    )

    val sampleMarketGear = listOf(
        ProductItem(
            id = "gear_1",
            title = "Sub-Zero CineDrone 8K Cinema FPV",
            category = MarketCategory.FILM_GEAR,
            price = 1850.0,
            originalPrice = 2100.0,
            rating = 4.95f,
            reviewsCount = 86,
            imageRes = R.drawable.img_prop_artifact,
            description = "Cinematic aerial camera drone capable of 8K 60fps ProRes RAW capture with tri-axial brushless gimbal, obstacle radar, and sub-zero blizzard battery heating.",
            specs = listOf("8K 60fps / 4K 120fps ProRes", "45-min Flight Time", "Cryo-Battery Pre-Heater", "15km Digital HD Range"),
            seller = "Zub Aero Dynamics"
        ),
        ProductItem(
            id = "gear_2",
            title = "Holographic Neural Prop Core",
            category = MarketCategory.PROPS,
            price = 620.0,
            originalPrice = 750.0,
            rating = 4.8f,
            reviewsCount = 42,
            imageRes = R.drawable.img_prop_artifact,
            description = "Screen-accurate high-tech hero prop artifact featuring pulsing RGB LED reactor core, tactile aluminum chassis, and programmable light sequences for sci-fi scenes.",
            specs = listOf("Anodized Aircraft Aluminum", "Programmable DMX Light FX", "Internal Li-Po Battery", "Weight: 1.8 kg"),
            seller = "Pinewood Prop Foundry"
        ),
        ProductItem(
            id = "gear_3",
            title = "35mm Anamorphic Cinema Lens T1.8",
            category = MarketCategory.FILM_GEAR,
            price = 1420.0,
            originalPrice = 1600.0,
            rating = 5.0f,
            reviewsCount = 67,
            imageRes = R.drawable.img_prop_artifact,
            description = "Ultra-fast anamorphic prime lens delivering signature cinematic horizontal blue flares, oval bokeh, and razor-sharp focus falloff.",
            specs = listOf("PL / EF Mount Compatible", "2.0x Anamorphic Squeeze", "Stepless Focus Ring", "Multi-Coated Schott Glass"),
            seller = "Zeiss Cine Labs"
        ),
        ProductItem(
            id = "gear_4",
            title = "Pro Wireless Boom & Lav Audio Kit",
            category = MarketCategory.FILM_GEAR,
            price = 480.0,
            originalPrice = 550.0,
            rating = 4.7f,
            reviewsCount = 112,
            imageRes = R.drawable.img_prop_artifact,
            description = "Dual-channel 32-bit float wireless microphone transmitter system with noise-rejection shotgun capsule and windjammer kit for dialogue capture on set.",
            specs = listOf("32-bit Float Recording", "Zero Latency Lossless RF", "200m Line-of-Sight", "Built-in Internal Backup"),
            seller = "Acoustic Cinema"
        )
    )

    fun getSampleScenesForGenre(genre: MovieGenre, title: String): List<VideoScene> {
        return when (genre) {
            MovieGenre.ACTION -> listOf(
                VideoScene(
                    sceneNumber = 1,
                    title = "Opening Chase: The Cryo Viaduct",
                    visualPrompt = "Wide low-angle tracking shot of a matte-black supercar drifting across an icy bridge amid neon sparks and police sirens",
                    cameraMovement = "Low-Angle FPV Chase (High Velocity)",
                    dialogue = "\"If we cross that tollgate, there is no turning back. Hold on tight.\"",
                    durationSeconds = 6,
                    ambientAudio = "Roaring V10 Engine & Cyber Synths"
                ),
                VideoScene(
                    sceneNumber = 2,
                    title = "Combat in the Server Vault",
                    visualPrompt = "Dynamic handheld orbit shot of martial artists clashing inside an ice-chilled quantum server facility, shattering glass and blue smoke",
                    cameraMovement = "Circular Handheld Orbit 360°",
                    dialogue = "\"You came all this way for encrypted code? You won't leave with it.\"",
                    durationSeconds = 8,
                    ambientAudio = "Impact Sub-Bass & Shattering Crystals"
                ),
                VideoScene(
                    sceneNumber = 3,
                    title = "The Rooftop Extraction",
                    visualPrompt = "Epic cinematic pull-back crane shot revealing a dual-rotor stealth chopper hovering above a foggy skyscraper at midnight",
                    cameraMovement = "Crane Ascend & Slow Dolly Out",
                    dialogue = "\"Package secured. Extracting immediately. Mission accomplished.\"",
                    durationSeconds = 7,
                    ambientAudio = "Thunderous Blades & Orchestral Brass"
                )
            )
            MovieGenre.DRAMA -> listOf(
                VideoScene(
                    sceneNumber = 1,
                    title = "The Estranged Banquet",
                    visualPrompt = "Intimate slow push-in shot across an opulent candlelit mahogany dinner table, reflections shimmering in vintage wine glasses",
                    cameraMovement = "Slow Dolly Push-In (Prestige Cinema)",
                    dialogue = "\"Ten years of silence, and you summon us back as if father's empire still had a pulse.\"",
                    durationSeconds = 8,
                    ambientAudio = "Melancholic Solo Cello & Flickering Flames"
                ),
                VideoScene(
                    sceneNumber = 2,
                    title = "The Glass Penthouse Confrontation",
                    visualPrompt = "Over-the-shoulder medium shot through rain-streaked penthouse glass overlooking a melancholic city skyline at dusk",
                    cameraMovement = "Subtle Handheld Drift with Rack Focus",
                    dialogue = "\"I built every cornerstone of this legacy with my bare hands. You merely inherited the shadows.\"",
                    durationSeconds = 9,
                    ambientAudio = "Gentle Rain on Glass & Distant City Drone"
                ),
                VideoScene(
                    sceneNumber = 3,
                    title = "A Lonely Revelation",
                    visualPrompt = "Extreme wide composition with subject standing solitary on an empty pier as the grey ocean fog rolls across the horizon",
                    cameraMovement = "Locked Static 35mm Master Shot",
                    dialogue = "\"Forgiveness was never on the table. Only peace.\"",
                    durationSeconds = 7,
                    ambientAudio = "Low Ocean Swells & Distant Foghorn"
                )
            )
            MovieGenre.TWILIGHT -> listOf(
                VideoScene(
                    sceneNumber = 1,
                    title = "Descent into the Mist",
                    visualPrompt = "Gliding drone shot skimming inches above ancient pine tree canopies engulfed in heavy silver mist beneath a full crimson moon",
                    cameraMovement = "Gliding Drone Skim (Dreamlike Float)",
                    dialogue = "\"The forest remembers what mortals choose to forget. Stay close to the path.\"",
                    durationSeconds = 7,
                    ambientAudio = "Ethereal Choral Whispers & Night Wind"
                ),
                VideoScene(
                    sceneNumber = 2,
                    title = "The Nocturnal Gaze",
                    visualPrompt = "Extreme close-up on pale silver eyes glowing in the twilight darkness, soft wind whispering through raven hair and crimson velvet",
                    cameraMovement = "Slow Cinematic Zoom & Shimmer Glow",
                    dialogue = "\"I have walked this earth for three centuries, yet my heart stopped only when you entered.\"",
                    durationSeconds = 8,
                    ambientAudio = "Heartbeat Thump & Haunting Violin"
                ),
                VideoScene(
                    sceneNumber = 3,
                    title = "Covenant at Midnight",
                    visualPrompt = "High-angle god's-eye view looking down on an ancient stone courtyard where two immortal factions stand in moonlit confrontation",
                    cameraMovement = "Top-Down God's Eye Descent",
                    dialogue = "\"If blood must seal our treaty, let it be spilled beneath the twin stars.\"",
                    durationSeconds = 8,
                    ambientAudio = "Thunderclap & Gothic Organ Swell"
                )
            )
            MovieGenre.SCI_FI -> listOf(
                VideoScene(
                    sceneNumber = 1,
                    title = "Neon Cryo-Stasis Chamber",
                    visualPrompt = "Slow horizontal tracking shot across frosted cryo-pods venting pressurized icy vapor in an obsidian deep-space station",
                    cameraMovement = "Linear Lateral Track with Lens Flare",
                    dialogue = "\"De-frost sequence 94% complete. Subject vitals stabilizing in sector sub-zero.\"",
                    durationSeconds = 7,
                    ambientAudio = "Pressurized Steam Vent & Deep Sub Drone"
                ),
                VideoScene(
                    sceneNumber = 2,
                    title = "Holographic War Room",
                    visualPrompt = "Medium shot of commander interacting with floating three-dimensional cyan tactical maps and orbital trajectory grids",
                    cameraMovement = "Floating Parallax Orbit",
                    dialogue = "\"The planetary shield is collapsing. Reroute power to the primary magnetic cannon!\"",
                    durationSeconds = 8,
                    ambientAudio = "Synthesizer Glitch & Quantum Resonance"
                ),
                VideoScene(
                    sceneNumber = 3,
                    title = "Hyper-Drive Horizon",
                    visualPrompt = "Spectacular exterior shot of a battlecruiser activating its warp drive, space folding into streaks of electric blue and violet",
                    cameraMovement = "Warp Accel Snap & Spatial Distortion",
                    dialogue = "\"Engaging faster-than-light transit. See you on the other side of eternity.\"",
                    durationSeconds = 6,
                    ambientAudio = "Cosmic Sub Bass Implosion"
                )
            )
        }
    }
}
