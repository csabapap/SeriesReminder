package hu.csabapap.seriesreminder.data.states

import hu.csabapap.seriesreminder.data.network.entities.NextEpisode

sealed class NextEpisodeState

data class NextEpisodeSuccess(val nextEpisode: NextEpisode) : NextEpisodeState()

data class NextEpisodeError(val message: String) : NextEpisodeState()

object NoNextEpisode: NextEpisodeState()