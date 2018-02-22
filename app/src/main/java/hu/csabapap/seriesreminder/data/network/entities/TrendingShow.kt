package hu.csabapap.seriesreminder.data.network.entities

import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class TrendingShow(var watchers: Int, var show: BaseShow)