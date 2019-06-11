package hu.csabapap.seriesreminder.ui.episode

import hu.csabapap.seriesreminder.data.db.entities.SREpisode

sealed class EpisodeUiState {

    data class DisplayEpisode(val srEpisode: SREpisode): EpisodeUiState()
}