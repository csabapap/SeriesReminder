package hu.csabapap.seriesreminder.data.network.entities

import com.squareup.moshi.Json

data class Episode(val season: Int, val number: Int, val title: String, val ids: Ids,
                   @field:Json(name = "number_abs") val absNumber: Int,
                   val overview: String,
                   @field:Json(name = "first_aired") val firstAired: String,
                   @field:Json(name = "updated_at") val updatedAt: String,
                   val rating: Float,
                   val votes: Int)