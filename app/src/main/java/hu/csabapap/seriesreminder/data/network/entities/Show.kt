package hu.csabapap.seriesreminder.data.network.entities

import se.ansman.kotshi.JsonDefaultValue
import se.ansman.kotshi.JsonDefaultValueString
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
class Show(val title: String = "",
           val ids: Ids,
           @JsonDefaultValueString(value = "")
           val overview: String,
           val runtime: Int,
           val rating: Float,
           val votes: Int,
           val genres: Array<String>,
           var aired_episodes: Int,
           var status: String,
           @JsonDefaultValueString(value = "")
           var network: String,
           var trailer: String?,
           var homepage: String?,
           var updated_at: String,
           @JsonDefaultValue
           var airs: Airs?) {
    companion object {
        @JsonDefaultValue
        @JvmField
        var defaultAirs = Airs()
    }
}