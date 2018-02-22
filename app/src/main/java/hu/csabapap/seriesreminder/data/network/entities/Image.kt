package hu.csabapap.seriesreminder.data.network.entities

import se.ansman.kotshi.JsonDefaultValue
import se.ansman.kotshi.JsonSerializable


@JsonSerializable
data class Image(val id: Int,
                 val keyType: String,
                 val fileName: String,
                 @JsonDefaultValue
                 var ratingsInfo: Rating,
                 val thumbnail: String){
    companion object {
        @JsonDefaultValue
        @JvmField
        var defaultRatings = Rating(0F, 0)
    }
}