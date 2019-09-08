package hu.csabapap.seriesreminder.domain

import com.nhaarman.mockitokotlin2.*
import hu.csabapap.seriesreminder.data.SeasonsRepository
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.data.db.entities.WatchedEpisode
import hu.csabapap.seriesreminder.data.repositories.WatchedEpisodesRepository
import hu.csabapap.seriesreminder.mindhunterEpisode
import hu.csabapap.seriesreminder.mindhunterSeason
import kotlinx.coroutines.runBlocking
import org.junit.Test

class SetEpisodeWatchedUseCaseTest {

    private val showsRepository: ShowsRepository = mock()
    private val seasonsRepository: SeasonsRepository = mock()
    private val watchedRepository: WatchedEpisodesRepository = mock()

    private val watchedEpisodeUseCase = SetEpisodeWatchedUseCase(showsRepository,
            seasonsRepository, watchedRepository)

    @Test
    fun `should not increase watched episode number when insert fails`() = runBlocking {
        val season = mindhunterSeason
        val episode = mindhunterEpisode
        val watchedEpisode = WatchedEpisode(null, episode.showId, episode.season, episode.number, episode.id!!)
        whenever(watchedRepository.setEpisodeWatched(watchedEpisode)).thenReturn(-1L)
        whenever(seasonsRepository.getSeason(episode.showId, episode.season)).thenReturn(season)

        watchedEpisodeUseCase(mindhunterEpisode)

        verify(showsRepository, never()).updateNextEpisode(episode.showId, episode.absNumber + 1)
        verify(seasonsRepository, never()).updateSeason(any())
    }
}