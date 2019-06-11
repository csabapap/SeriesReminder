package hu.csabapap.seriesreminder.services

import android.content.Context
import android.content.Intent
import dagger.android.DaggerIntentService
import hu.csabapap.seriesreminder.data.repositories.episodes.EpisodesRepository
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.data.network.TvdbApi
import hu.csabapap.seriesreminder.tasks.TaskExecutor
import timber.log.Timber
import javax.inject.Inject

class SyncService : DaggerIntentService("SyncService") {

    @Inject
    lateinit var showsRepository: ShowsRepository

    @Inject
    lateinit var tvdbApi: TvdbApi

    @Inject
    lateinit var taskExecutor: TaskExecutor

    @Inject
    lateinit var episodesRepository: EpisodesRepository

    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val action = intent.action
            when {
                ACTION_SYNC_SHOW == action -> syncShow()
                ACTION_SYNC_MY_SHOWS == action -> syncCollection()
                ACTION_SYNC_NEXT_EPISODES == action -> syncUpcomingEpisodes()
            }
        }
    }

    private fun syncShow() {
        taskExecutor.executeTasks {
            stopSelf()
        }
    }

    private fun syncCollection() {
        Timber.d("sync collection")
        showsRepository.syncShows()
                .subscribe({
                    Timber.d("show updated: %d", it.size )
                }, {
                    Timber.e(it)
                })
    }

    private fun syncUpcomingEpisodes() {
//        episodesRepository.getNextEpisodes()
//                .flattenAsFlowable { it }
//                .flatMap {
//                    it.episodeSingle?.let {episodeSingle ->
//                        showsRepository.fetchNextEpisode(episodeSingle.showId)
//                                .toFlowable()
//                                .flatMap {
//                                    if (it is NextEpisodeSuccess) {
//                                        Timber.d("update episodeSingle, episodeSingle id: %d", episodeSingle.traktId)
//                                        episodesRepository.getEpisode(it.nextEpisode.showId,
//                                                it.nextEpisode.season, it.nextEpisode.number)
//                                                .toFlowable()
//                                    } else {
//                                        Flowable.just(EpisodeError)
//                                    }
//                                }
//                    }
//                }
//                .toList()
//                .subscribe({
//                    Timber.d("nmb of episodes updated: %d", it.size)
//                }, {
//                    Timber.e(it)
//                })

    }

    companion object {
        private const val ACTION_SYNC_SHOW = "hu.csabapap.seriesreminder.services.action.SyncEpisode"
        private const val ACTION_SYNC_MY_SHOWS = "hu.csabapap.seriesreminder.services.action.SyncMyShows"
        private const val ACTION_SYNC_NEXT_EPISODES = "hu.csabapap.seriesreminder.services.action.SyncNextEpisodes"

        fun syncShow(context: Context) {
            val intent = Intent(context, SyncService::class.java)
            intent.action = ACTION_SYNC_SHOW
            context.startService(intent)
        }

        fun syncMyShows(context: Context) {
            val intent = Intent(context, SyncService::class.java)
            intent.action = ACTION_SYNC_MY_SHOWS
            context.startService(intent)
        }

        fun syncNextEpisodes(context: Context) {
            val intent = Intent(context, SyncService::class.java)
            intent.action = ACTION_SYNC_NEXT_EPISODES
            context.startService(intent)
        }
    }
}
