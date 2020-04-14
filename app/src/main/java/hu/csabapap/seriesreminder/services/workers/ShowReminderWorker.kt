package hu.csabapap.seriesreminder.services.workers

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.data.db.entities.SREpisode
import hu.csabapap.seriesreminder.data.repositories.episodes.EpisodesRepository
import hu.csabapap.seriesreminder.ui.showdetails.ShowDetailsActivity
import hu.csabapap.seriesreminder.utils.Reminder
import hu.csabapap.seriesreminder.utils.ShowDetails
import hu.csabapap.seriesreminder.utils.createChannel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class ShowReminderWorker(context: Context, workerParameters: WorkerParameters, val episodesRepository: EpisodesRepository)
    : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {

        val showId = inputData.getInt(Reminder.SHOW_ID, -1)
        val title = inputData.getString(Reminder.SHOW_TITLE) ?: return Result.failure()
        val episodeAbsNumber = inputData.getInt(Reminder.EPISODE_ABS_NUMBER, -1)
        if (showId == -1) {
            return Result.failure()
        }
        if (episodeAbsNumber == -1) {
            return Result.failure()
        }
        return coroutineScope {
            val job = async {
                val episode = episodesRepository.getNextEpisode(showId, episodeAbsNumber)
                displayNotification(showId, title, episode)
            }
            job.await()
            Result.success()
        }
    }

    private fun displayNotification(id: Int, title: String, episode: SREpisode?) {
        if (episode == null) return
        val resultIntent = Intent(applicationContext, ShowDetailsActivity::class.java)
        resultIntent.putExtra(ShowDetails.EXTRA_SHOW_ID, id)
        val resultPendingIntent = PendingIntent.getActivity(applicationContext, 17,
                resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = NotificationCompat.Builder(applicationContext, "episode_reminder")
        val desc = applicationContext.getString(R.string.notification_episode_title_with_numbers)
                .format(episode.season, episode.number, episode.title)
        builder.setContentTitle(title)
        builder.setContentText(desc)
        builder.setSmallIcon(R.drawable.ic_notifications_black_24dp)
        builder.setContentIntent(resultPendingIntent)
        builder.createChannel()

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
        notificationManager.notify(id, builder.build())
    }

    class Factory @Inject constructor(private val episodesRepository: EpisodesRepository): ChildWorkerFactory {
        override fun create(appContext: Context, params: WorkerParameters): ListenableWorker {
            return ShowReminderWorker(appContext, params, episodesRepository)
        }
    }
}