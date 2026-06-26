package com.example.studymateandroidapp.data.model

import com.example.studymateandroidapp.R

/**
 * Represents an ambient sound that can be played during study sessions.
 *
 * @property displayName Human-readable label shown in the bottom sheet.
 * @property rawResId     Resource ID of the audio file in res/raw/.
 * @property icon         Emoji used as a visual identifier.
 */
enum class AmbientSound(
    val displayName: String,
    val rawResId: Int,
    val icon: String
) {
    BONFIRE("Bonfire", R.raw.bonfire, "🔥"),
    FOREST("Forest", R.raw.forest, "🌲"),
    BLUE_BIRD("Blue Bird", R.raw.blue_bird, "🐦"),
    WAVES("Waves", R.raw.waves, "🌊"),
    BOAT("Boat", R.raw.boat, "⛵"),
    RAIN("Rain", R.raw.rain, "🌧️"),
    WATER_DROP("Water Drop", R.raw.water_drop, "💧"),
    CLOCK("Clock", R.raw.clock, "🕐"),
    WRITING("Writing", R.raw.writing, "✍️")
}
