package hu.csabapap.seriesreminder

import com.uwetrottmann.trakt5.entities.BaseShow
import com.uwetrottmann.trakt5.entities.Show
import com.uwetrottmann.trakt5.entities.ShowIds
import hu.csabapap.seriesreminder.data.db.entities.SREpisode
import hu.csabapap.seriesreminder.data.db.entities.SRSeason
import hu.csabapap.seriesreminder.data.db.entities.WatchedEpisode
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneOffset



fun getShow() = BaseShow().apply{
    show = Show().apply {
        title = "Humans"
        ids = ShowIds().apply {
            trakt = 1
            tvdb = 1
        }
        overview = "Lorem ipsum"
        rating = 3.14
        votes = 42
    }
}

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
