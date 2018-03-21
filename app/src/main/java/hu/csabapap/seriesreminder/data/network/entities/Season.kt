package hu.csabapap.seriesreminder.data.network.entities

import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class Season(
        val number: Int,
        val ids: Ids,
        @field:Json(name = "episode_count") val episodeCount: Int,
        @field:Json(name = "aired_episodes") val airedEpisodes: Int
        )