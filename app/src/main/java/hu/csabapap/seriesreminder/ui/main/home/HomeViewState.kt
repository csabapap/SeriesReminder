package hu.csabapap.seriesreminder.ui.main.home

import hu.csabapap.seriesreminder.data.db.entities.SRNextEpisode
import hu.csabapap.seriesreminder.data.db.relations.EpisodeWithShow
import hu.csabapap.seriesreminder.ui.adapters.items.ShowItem

sealed class HomeViewState
object InitialState: HomeViewState()
object DisplayTrendingLoader: HomeViewState()
object DisplayPopularLoader: HomeViewState()
object HideTrendingSection: HomeViewState()

data class ContentLoaded(
    val myShows: List<ShowItem> = emptyList(),
    val trendingShows: List<ShowItem> = emptyList(),
    val popularShows: List<ShowItem> = emptyList(),
    val nextEpisodes: List<SRNextEpisode> = emptyList(),
    val upcomingEpisodes: List<UpcomingEpisode> = emptyList(),
): HomeViewState()