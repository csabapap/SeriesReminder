package hu.csabapap.seriesreminder.ui.seasons

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.data.repositories.WatchedEpisodesRepository
import hu.csabapap.seriesreminder.domain.SetEpisodeWatchedUseCase
import hu.csabapap.seriesreminder.utils.AppCoroutineDispatchers
import javax.inject.Inject

class SeasonsViewModelProvider @Inject constructor(private val showsRepository: ShowsRepository,
                                                   private val watchedEpisodesRepository: WatchedEpisodesRepository,
                                                   private val setEpisodeWatchedUseCase: SetEpisodeWatchedUseCase,
                                                   private val dispatchers: AppCoroutineDispatchers)
    : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SeasonsViewModel(showsRepository, watchedEpisodesRepository, setEpisodeWatchedUseCase,
                dispatchers) as T
    }
}