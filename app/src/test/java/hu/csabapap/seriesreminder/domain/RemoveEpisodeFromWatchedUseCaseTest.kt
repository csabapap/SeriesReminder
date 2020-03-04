package hu.csabapap.seriesreminder.domain

import com.nhaarman.mockitokotlin2.*
import hu.csabapap.seriesreminder.data.SeasonsRepository
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.data.repositories.WatchedEpisodesRepository
import hu.csabapap.seriesreminder.data.repositories.episodes.EpisodesRepository
import hu.csabapap.seriesreminder.mindhunter
import hu.csabapap.seriesreminder.mindhunterEpisode
import hu.csabapap.seriesreminder.mindhunterSeason
import hu.csabapap.seriesreminder.watchedEpisode
import kotlinx.coroutines.runBlocking
import org.junit.Test

class RemoveEpisodeFromWatchedUseCaseTest {

    private val showsRepository: ShowsRepository = mock()
    private val seasonsRepository: SeasonsRepository = mock()
    private val episodesRepository: EpisodesRepository = mock()
    private val watchedRepository: WatchedEpisodesRepository = mock()

    private val removeWatchedEpisodeUseCase = RemoveEpisodeFromWatchedUseCase(showsRepository,
            seasonsRepository, episodesRepository, watchedRepository)


    @Test
    fun `should do nothing if cannot delete watched episode`() = runBlocking {
        val watchedEpisode = watchedEpisode

        whenever(watchedRepository.getWatchedEpisode(watchedEpisode.showId, watchedEpisode.season, watchedEpisode.number)).thenReturn(watchedEpisode)
        whenever(watchedRepository.removeEpisodeFromWatched(watchedEpisode)).thenReturn(0)

        removeWatchedEpisodeUseCase(watchedEpisode)

        verify(seasonsRepository, never()).updateSeason(any())
        verify(showsRepository, never()).updateNextEpisode(any(), any())
    }

    @Test
    fun `should decrement number of watched episode in season when watched episode is deleted`() = runBlocking {
        val show = mindhunter
        val season = mindhunterSeason.copy(nmbOfWatchedEpisodes = 1)
        val episode = mindhunterEpisode
        val watchedEpisode = watchedEpisode

        whenever(watchedRepository.getWatchedEpisode(watchedEpisode.showId, watchedEpisode.season, watchedEpisode.number)).thenReturn(watchedEpisode)
        whenever(watchedRepository.removeEpisodeFromWatched(watchedEpisode)).thenReturn(1)
        whenever(seasonsRepository.getSeason(watchedEpisode.showId, watchedEpisode.season)).thenReturn(season)

        removeWatchedEpisodeUseCase(watchedEpisode)
        verify(seasonsRepository, times(1)).updateSeason(any())
    }
}