package hu.csabapap.seriesreminder.data.states

import hu.csabapap.seriesreminder.data.db.entities.NextEpisodeEntry

sealed class NextEpisodeState

data class NextEpisodeSuccess(val nextEpisode: NextEpisodeEntry) : NextEpisodeState()

data class NextEpisodeError(val message: String) : NextEpisodeState()

object NoNextEpisode: NextEpisodeState()