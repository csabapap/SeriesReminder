package hu.csabapap.seriesreminder.ui.main.home

import hu.csabapap.seriesreminder.data.db.entities.SRNextEpisode
import hu.csabapap.seriesreminder.ui.adapters.items.ShowItem

sealed class HomeViewState
object DisplayTrendingLoader: HomeViewState()
object DisplayPopularLoader: HomeViewState()
data class TrendingState(val items: List<ShowItem>): HomeViewState()
data class PopularState(val items: List<ShowItem>): HomeViewState()
data class MyShowsState(val items: List<ShowItem>): HomeViewState()
data class NextEpisodesState(val episodes: List<SRNextEpisode>): HomeViewState()
object HideTrendingSection: HomeViewState()