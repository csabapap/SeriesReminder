package hu.csabapap.seriesreminder.data.network.entities

import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class WatchedShow(var plays: Int, var show: BaseShow)