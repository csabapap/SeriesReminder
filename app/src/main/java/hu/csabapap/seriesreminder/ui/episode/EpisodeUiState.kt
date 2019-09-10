package hu.csabapap.seriesreminder.ui.episode

import hu.csabapap.seriesreminder.data.db.relations.EpisodeWithShow

sealed class EpisodeUiState {

    data class DisplayEpisode(val episodeWithShow: EpisodeWithShow): EpisodeUiState()
    object SetEpisodeWatched: EpisodeUiState()
    object RemoveEpisodeFromWatched: EpisodeUiState()
}