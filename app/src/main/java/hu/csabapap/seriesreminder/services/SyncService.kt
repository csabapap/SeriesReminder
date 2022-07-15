package hu.csabapap.seriesreminder.services

import android.content.Context
import android.content.Intent
import dagger.android.DaggerIntentService
import hu.csabapap.seriesreminder.data.repositories.episodes.EpisodesRepository
import hu.csabapap.seriesreminder.data.repositories.shows.ShowsRepository
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

    @Deprecated("Deprecated in Java")
    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val action = intent.action
            when {
                ACTION_SYNC_SHOW == action -> syncShow()
                ACTION_SYNC_MY_SHOWS == action -> syncCollection()
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
    }
}
