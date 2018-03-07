package hu.csabapap.seriesreminder.data.network.entities

import se.ansman.kotshi.JsonDefaultValueString
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class EpisodeData(val data: TvdbEpisode)

@JsonSerializable
data class TvdbEpisode(
        @JsonDefaultValueString(value = "")
        val filename: String)