package hu.csabapap.seriesreminder

import hu.csabapap.seriesreminder.data.network.entities.BaseShow
import hu.csabapap.seriesreminder.data.network.entities.Ids
import hu.csabapap.seriesreminder.data.network.entities.SearchResult
import hu.csabapap.seriesreminder.data.network.entities.TrendingShow


fun getSearchResult() = SearchResult(getShow())

fun getShow() = BaseShow(
        title = "Humans",
        ids = Ids(1, 1),
        overview = "Lorem ipsum",
        rating = 3.14f,
        votes = 42
)

val theBoys = BaseShow(
        title = "The Boys",
        ids = Ids(139960, 355567),
        overview = "In a world where superheroes embrace the darker side of their massive celebrity and fame, The Boys centres on a group of vigilantes known informally as \\\"The Boys,\\\" who set out to take down corrupt superheroes with no more than blue collar grit and a willingness to fight dirty.",
        rating = 8.61099f,
        votes = 1820)

val bigBangTheory = BaseShow(
        title = "The Big Bang Theory",
        ids = Ids(1234, 4321),
        overview = "A woman who moves into an apartment across the hall from two brilliant but socially awkward physicists shows them how little they know about life outside of the laboratory.",
        rating = 8.21645f,
        votes = 53481)

val trendingShow1 = TrendingShow(123, theBoys)
val trendingShow2 = TrendingShow(2, bigBangTheory)

val trendingShows = listOf(trendingShow1, trendingShow2)
