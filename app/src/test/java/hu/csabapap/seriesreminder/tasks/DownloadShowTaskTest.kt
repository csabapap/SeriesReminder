package hu.csabapap.seriesreminder.tasks

import hu.csabapap.seriesreminder.mindhunterEpisode
import hu.csabapap.seriesreminder.mindhunterSeason
import org.junit.Assert.*
import org.junit.Test

class DownloadShowTaskTest {

    private val task = DownloadShowTask(1)

    @Test
    fun `should set abs number for single seasons if it is not set` () {
        val season1 = mindhunterSeason
        val episode1 = mindhunterEpisode.copy(absNumber = 0)
        val episode2 = mindhunterEpisode.copy(absNumber = 0, number = 2)
        val episode3 = mindhunterEpisode.copy(absNumber = 0, number = 3)
        val episode4 = mindhunterEpisode.copy(absNumber = 0, number = 4)
        season1.episodes = listOf(episode1, episode2, episode3, episode4)

        val seasons = listOf(season1)
        val fixedSeasons = task.setEpisodeAbsNumberIfNotExists(seasons)
        assertEquals(1, fixedSeasons[0].episodes[0].absNumber)
        assertEquals(2, fixedSeasons[0].episodes[1].absNumber)
        assertEquals(3, fixedSeasons[0].episodes[2].absNumber)
        assertEquals(4, fixedSeasons[0].episodes[3].absNumber)
    }

    @Test
    fun `should set abs number for multiple seasons if it is not set` () {
        val season1 = mindhunterSeason
        val episode1 = mindhunterEpisode.copy(absNumber = 0)
        val episode2 = mindhunterEpisode.copy(absNumber = 0, number = 2)
        val episode3 = mindhunterEpisode.copy(absNumber = 0, number = 3)
        val episode4 = mindhunterEpisode.copy(absNumber = 0, number = 4)
        season1.episodes = listOf(episode1, episode2, episode3, episode4)

        val season2 = mindhunterSeason.copy(number = 2)
        val episodeS2E1 = mindhunterEpisode.copy(absNumber = 0, season = 2)
        season2.episodes = listOf(episodeS2E1)

        val seasons = listOf(season1, season2)
        val fixedSeasons = task.setEpisodeAbsNumberIfNotExists(seasons)
        assertEquals(1, fixedSeasons[0].episodes[0].absNumber)
        assertEquals(2, fixedSeasons[0].episodes[1].absNumber)
        assertEquals(3, fixedSeasons[0].episodes[2].absNumber)
        assertEquals(4, fixedSeasons[0].episodes[3].absNumber)
        assertEquals(5, fixedSeasons[1].episodes[0].absNumber)
    }

    @Test
    fun `should not change abs number if it is set` () {
        val season1 = mindhunterSeason
        val episode1 = mindhunterEpisode.copy(absNumber = 4)
        season1.episodes = listOf(episode1)

        val seasons = listOf(season1)
        val fixedSeasons = task.setEpisodeAbsNumberIfNotExists(seasons)
        assertEquals(4, fixedSeasons[0].episodes[0].absNumber)
    }

    @Test
    fun `should not change abs number for special episodes` () {
        val specials = mindhunterSeason.copy(number = 0)
        val episode1 = mindhunterEpisode.copy(absNumber = 0)
        specials.episodes = listOf(episode1)

        val seasons = listOf(specials)
        val fixedSeasons = task.setEpisodeAbsNumberIfNotExists(seasons)
        assertEquals(0, fixedSeasons[0].episodes[0].absNumber)
    }

    @Test
    fun `should not change abs number for special episodes for multiple seasons` () {
        val specials = mindhunterSeason.copy(number = 0)
        val specialEpisode = mindhunterEpisode.copy(absNumber = 0)
        specials.episodes = listOf(specialEpisode)
        val season1 = mindhunterSeason
        val episode1 = mindhunterEpisode.copy(absNumber = 0)
        season1.episodes = listOf(episode1)

        val seasons = listOf(specials, season1)
        val fixedSeasons = task.setEpisodeAbsNumberIfNotExists(seasons)
        assertEquals(0, fixedSeasons[0].episodes[0].absNumber)
        assertEquals(1, fixedSeasons[1].episodes[0].absNumber)
    }
}