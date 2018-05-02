package hu.csabapap.seriesreminder.data.network.entities

import com.squareup.moshi.Json
import se.ansman.kotshi.JsonDefaultValueInt
import se.ansman.kotshi.JsonDefaultValueString
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class Episode(val season: Int,
                   val number: Int,
                   @JsonDefaultValueString(value = "")
                   val title: String,
                   val ids: Ids,
                   @JsonDefaultValueInt(value = 0)
                   @field:Json(name = "number_abs") val absNumber: Int,
                   @JsonDefaultValueString(value = "")
                   val overview: String,
                   @JsonDefaultValueString(value = "-999999999-01-01T00:00:00+18:00") // OffsetDateTime.MIN
                   @field:Json(name = "first_aired") val firstAired: String,
                   @field:Json(name = "updated_at") val updatedAt: String,
                   val rating: Float,
                   val votes: Int)