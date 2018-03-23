package hu.csabapap.seriesreminder.data.network.entities

import se.ansman.kotshi.JsonDefaultValueString
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class Airs(
        @JsonDefaultValueString(value = "")
        val day: String = "",
        @JsonDefaultValueString(value = "")
        val time: String = "",
        @JsonDefaultValueString(value = "")
        val timezone: String = "")