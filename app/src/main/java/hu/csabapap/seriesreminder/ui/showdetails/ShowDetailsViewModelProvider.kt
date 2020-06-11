package hu.csabapap.seriesreminder.ui.showdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import hu.csabapap.seriesreminder.data.CollectionRepository
import hu.csabapap.seriesreminder.data.SeasonsRepository
import hu.csabapap.seriesreminder.data.repositories.shows.ShowsRepository
import hu.csabapap.seriesreminder.data.repositories.episodes.EpisodesRepository
import hu.csabapap.seriesreminder.data.repositories.notifications.NotificationsRepository
import hu.csabapap.seriesreminder.data.repositories.relatedshows.RelatedShowsRepository
import hu.csabapap.seriesreminder.domain.CreateNotificationAlarmUseCase
import hu.csabapap.seriesreminder.domain.SetEpisodeWatchedUseCase
import hu.csabapap.seriesreminder.utils.AppCoroutineDispatchers
import javax.inject.Inject

class ShowDetailsViewModelProvider @Inject constructor(
        private val showsRepository: ShowsRepository,
        private val seasonsRepository: SeasonsRepository,
        private val episodesRepository: EpisodesRepository,
        private val collectionRepository: CollectionRepository,
        private val notificationsRepository: NotificationsRepository,
        private val relatedShowsRepository: RelatedShowsRepository,
        private val setEpisodeWatchedUseCase: SetEpisodeWatchedUseCase,
        private val createNotificationAlarmUseCase: CreateNotificationAlarmUseCase,
        private val dispatchers: AppCoroutineDispatchers)
    : ViewModelProvider.NewInstanceFactory() {

    @Suppress( "UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ShowDetailsViewModel(showsRepository, seasonsRepository, episodesRepository,
                collectionRepository, notificationsRepository, relatedShowsRepository,
                setEpisodeWatchedUseCase, createNotificationAlarmUseCase, dispatchers) as T
    }
}