package hu.csabapap.seriesreminder.data.states

import hu.csabapap.seriesreminder.data.db.entities.SREpisode

sealed class EpisodeState

data class EpisodeSuccess(val episode: SREpisode, val success: Boolean = true): EpisodeState()

object EpisodeError: EpisodeState()

