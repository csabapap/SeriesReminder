package hu.csabapap.seriesreminder.data.db.entities

data class SRNextEpisode(
    val showId: Int,
    val showTitle: String,
    val poster: String,
    val season: Int,
    val number: Int,
    val absNumber: Int,
    val episodeTitle: String,
    val episodeId: Int)