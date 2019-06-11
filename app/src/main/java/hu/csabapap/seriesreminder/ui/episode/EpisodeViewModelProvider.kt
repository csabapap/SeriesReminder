package hu.csabapap.seriesreminder.ui.episode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import hu.csabapap.seriesreminder.data.repositories.episodes.EpisodesRepository
import hu.csabapap.seriesreminder.utils.AppCoroutineDispatchers
import javax.inject.Inject

class EpisodeViewModelProvider @Inject constructor(
        private val episodesRepository: EpisodesRepository,
        private val dispatchers: AppCoroutineDispatchers
): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EpisodeViewModel(episodesRepository, dispatchers) as T
    }
}