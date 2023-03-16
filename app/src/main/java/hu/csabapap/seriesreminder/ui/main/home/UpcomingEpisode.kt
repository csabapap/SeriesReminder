package hu.csabapap.seriesreminder.ui.main.home

data class UpcomingEpisode(
    val showId: Int,
    val episodeId: Int,
    val showTitle: String,
    val episodeTitle: String,
    val airsIn: String
)