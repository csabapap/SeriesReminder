package hu.csabapap.seriesreminder.data.network.entities

import se.ansman.kotshi.JsonDefaultValueInt
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class Ids (
        var trakt: Int,
        @JsonDefaultValueInt(value = 0)
        var tvdb: Int)