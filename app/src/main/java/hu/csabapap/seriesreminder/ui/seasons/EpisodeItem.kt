package hu.csabapap.seriesreminder.ui.seasons

import hu.csabapap.seriesreminder.data.db.entities.SREpisode

data class EpisodeItem(
        val episode: SREpisode,
        val watched: Boolean)