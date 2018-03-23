package hu.csabapap.seriesreminder.data.network.entities

import com.squareup.moshi.Json
import se.ansman.kotshi.JsonDefaultValue
import se.ansman.kotshi.JsonSerializable

@Target(AnnotationTarget.VALUE_PARAMETER,
        AnnotationTarget.FUNCTION,
        AnnotationTarget.CONSTRUCTOR,
        AnnotationTarget.FIELD,
        AnnotationTarget.PROPERTY_GETTER)
@MustBeDocumented
@Retention(AnnotationRetention.SOURCE)
@JsonDefaultValue
annotation class DefaultEpisodes

@JsonSerializable
data class Season(
        val number: Int,
        val ids: Ids,
        @field:Json(name = "episode_count") val episodeCount: Int,
        @field:Json(name = "aired_episodes") val airedEpisodes: Int,
        @DefaultEpisodes
        val episodes: List<Episode>) {
    companion object {
        @DefaultEpisodes
        @JvmField
        val defaultEpisodes = emptyList<Episode>()
    }
}