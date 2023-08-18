package hu.csabapap.seriesreminder.ui.main.home

import hu.csabapap.seriesreminder.data.db.entities.AiringTime

data class UpcomingEpisode(
    val showId: Int,
    val episodeId: Int,
    val showTitle: String,
    val episodeTitle: String,
    val airsIn: AiringTime,
    val image: String,
    val showCover: String,
) {
    fun getEpisodeImage(): String {
        return image.ifEmpty {
            showCover
        }
    }
}