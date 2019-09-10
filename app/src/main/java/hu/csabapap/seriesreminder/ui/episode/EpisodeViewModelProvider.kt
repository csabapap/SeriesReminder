package hu.csabapap.seriesreminder.ui.episode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import hu.csabapap.seriesreminder.data.repositories.episodes.EpisodesRepository
import hu.csabapap.seriesreminder.domain.SetEpisodeWatchedUseCase
import hu.csabapap.seriesreminder.utils.AppCoroutineDispatchers
import javax.inject.Inject

class EpisodeViewModelProvider @Inject constructor(
        private val episodesRepository: EpisodesRepository,
        private val setEpisodeWatchedUseCase: SetEpisodeWatchedUseCase,
        private val dispatchers: AppCoroutineDispatchers
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EpisodeViewModel(episodesRepository, setEpisodeWatchedUseCase, dispatchers) as T
    }
}