package hu.csabapap.seriesreminder.services

import android.app.IntentService
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
            }
        }
    }

    private fun syncShow(showId: Int) {
        showsRepository.fetchNextEpisode(showId)
                .subscribe({Timber.d("next episode saved")}, {Timber.e(it)})
    }

    companion object {
        private const val ACTION_SYNC_SHOW = "hu.csabapap.seriesreminder.services.action.FOO"

        private const val EXTRA_SHOW_ID = "hu.csabapap.seriesreminder.services.extra.show_id"

        fun syncShow(context: Context, showId: Int) {
            val intent = Intent(context, SyncService::class.java)
            intent.action = ACTION_SYNC_SHOW
            intent.putExtra(EXTRA_SHOW_ID, showId)
            context.startService(intent)
        }
    }
}
