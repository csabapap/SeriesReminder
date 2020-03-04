package hu.csabapap.seriesreminder

import hu.csabapap.seriesreminder.data.db.entities.*
import hu.csabapap.seriesreminder.data.network.entities.BaseShow
import hu.csabapap.seriesreminder.data.network.entities.Ids
import hu.csabapap.seriesreminder.data.network.entities.SearchResult
import hu.csabapap.seriesreminder.data.network.entities.TrendingShow
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneOffset


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

val mindhunter = SRShow(
        id = 1L,
        traktId = 1,
        tvdbId = 1,
        title = "Mindhunter",
        overview = "Lorem ipsum...",
        poster = "",
        posterThumb = "",
        cover = "",
        coverThumb = "",
        rating = 99.8f,
        votes = 1234,
        genres = "drama",
        runtime = 60,
        airedEpisodes = 19,
        status = "continuing",
        network = "Netflix",
        trailer = "",
        updatedAt = OffsetDateTime.now(),
        homepage = "",
        airingTime = AiringTime("Monday", "20:00", "US"),
        nextEpisode = 2
)

val mindhunterEpisode = SREpisode(
        1,
        1,
        1,
        "Episode 1",
        2509584,
        6124070,
        1,
        "In 1977, frustrated FBI hostage negotiator Holden Ford finds an unlikely ally in veteran agent Bill Tench and begins studying a new class of murderer.",
        OffsetDateTime.parse("2017-10-13T07:00:00Z"),
        OffsetDateTime.now().toString(),
        7.385469913482666f,
        4130,
        "https://www.thetvdb.com/banners/episodes/328708/6124070.jpg",
        116965,
        3
)

val mindhunterSeason = SRSeason(
        id = 3,
        number = 1,
        traktId = 1234,
        episodeCount = 10,
        airedEpisodeCount = 10,
        showId = 116965,
        fileName = "",
        thumbnail = "",
        nmbOfWatchedEpisodes = 0
)

val watchedEpisode = WatchedEpisode(
        id = 1,
        showId = 1,
        season = 1,
        number = 1,
        episodeId = 1,
        watchedAt = OffsetDateTime.of(2020, 3, 3, 12, 0, 0, 0, ZoneOffset.UTC)
)

val trendingShow1 = TrendingShow(123, theBoys)
val trendingShow2 = TrendingShow(2, bigBangTheory)

val trendingShows = listOf(trendingShow1, trendingShow2)
