package hu.csabapap.seriesreminder.data.network.entities

import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class TvdbEpisode(val absoluteNumber: Int, val fileName: String)