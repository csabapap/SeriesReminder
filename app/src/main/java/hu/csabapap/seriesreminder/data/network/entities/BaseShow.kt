package hu.csabapap.seriesreminder.data.network.entities

import se.ansman.kotshi.JsonDefaultValueFloat
import se.ansman.kotshi.JsonDefaultValueInt
import se.ansman.kotshi.JsonDefaultValueString
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
open class BaseShow (var title: String = "",
                     var ids: Ids,
                     @JsonDefaultValueString(value = "")
                     var overview: String = "",
                     @JsonDefaultValueFloat(value = 0f)
                     val rating: Float = 0f,
                     @JsonDefaultValueInt(value = 0)
                     val votes: Int = 0)

