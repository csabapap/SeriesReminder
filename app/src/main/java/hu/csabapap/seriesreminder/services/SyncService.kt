package hu.csabapap.seriesreminder.services

import android.content.Context
import android.content.Intent
import dagger.android.DaggerIntentService
import hu.csabapap.seriesreminder.data.ShowsRepository
import timber.log.Timber
import javax.inject.Inject

class SyncService : DaggerIntentService("SyncService") {

    @Inject
    lateinit var showsRepository: ShowsRepository

    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val action = intent.action
            if (ACTION_SYNC_SHOW == action) {
                val showId = intent.getIntExtra(EXTRA_SHOW_ID, -1)
                if (showId != -1) {
                    syncShow(showId)
                }
            } else if (ACTION_SYNC_MY_SHOWS == action) {
                syncCollection()
            } else if (ACTION_SYNC_NEXT_EPISODES == action) {
                syncUpcomingEpisodes()
            }
        }
    }

    private fun syncShow(showId: Int) {
        showsRepository.getSeasons(showId)
                .toCompletable()
                .andThen(showsRepository.fetchNextEpisode(showId))
                .subscribe({

                }, {Timber.e(it)})
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
        Timber.d("sync upcoming episodes")
    }

    companion object {
        private const val ACTION_SYNC_SHOW = "hu.csabapap.seriesreminder.services.action.SyncEpisode"
        private const val ACTION_SYNC_MY_SHOWS = "hu.csabapap.seriesreminder.services.action.SyncMyShows"
        private const val ACTION_SYNC_NEXT_EPISODES = "hu.csabapap.seriesreminder.services.action.SyncNextEpisodes"

        private const val EXTRA_SHOW_ID = "hu.csabapap.seriesreminder.services.extra.show_id"

        fun syncShow(context: Context, showId: Int) {
            val intent = Intent(context, SyncService::class.java)
            intent.action = ACTION_SYNC_SHOW
            intent.putExtra(EXTRA_SHOW_ID, showId)
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
