package hu.csabapap.seriesreminder.data.network.entities

import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class NextEpisode(val season: Int, val number: Int, val title: String, val ids: Ids)