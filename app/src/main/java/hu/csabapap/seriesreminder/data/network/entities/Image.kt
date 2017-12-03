package hu.csabapap.seriesreminder.data.network.entities

import com.squareup.moshi.Json


data class Image(var fileName: String,
                 var thumbnail: String,
                 @Json(name = "ratingsInfo") var ratings: Rating)