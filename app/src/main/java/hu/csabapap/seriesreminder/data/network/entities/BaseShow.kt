package hu.csabapap.seriesreminder.data.network.entities

import se.ansman.kotshi.JsonSerializable

@JsonSerializable
open class BaseShow (var title: String = "",
                     var ids: Ids,
                     var overview: String,
                     val rating: Float,
                     val votes: Int)

