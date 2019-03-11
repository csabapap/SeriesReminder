package hu.csabapap.seriesreminder.services.workers

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.ui.showdetails.ShowDetailsActivity
import hu.csabapap.seriesreminder.utils.Reminder
import hu.csabapap.seriesreminder.utils.ShowDetails
import hu.csabapap.seriesreminder.utils.createChannel
import javax.inject.Inject

class ShowReminderWorker(context: Context, workerParameters: WorkerParameters)
    : Worker(context, workerParameters) {

    override fun doWork(): Result {

        val showId = inputData.getInt(Reminder.SHOW_ID, -1)
        if (showId == -1) {
            return Result.failure()
        }
        val title = inputData.getString(Reminder.SHOW_TITLE) ?: return Result.failure()

        displayNotification(showId, title)

        return Result.success()
    }

    private fun displayNotification(id: Int, title: String) {
        val resultIntent = Intent(applicationContext, ShowDetailsActivity::class.java)
        resultIntent.putExtra(ShowDetails.EXTRA_SHOW_ID, id)
        val resultPendingIntent = PendingIntent.getActivity(applicationContext, 17,
                resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = NotificationCompat.Builder(applicationContext, "episode_reminder")
        builder.setContentTitle(title)
        builder.setSmallIcon(R.drawable.ic_notifications_black_24dp)
        builder.setContentIntent(resultPendingIntent)
        builder.createChannel()

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
        notificationManager.notify(id, builder.build())
    }

    class Factory @Inject constructor(): ChildWorkerFactory {
        override fun create(appContext: Context, params: WorkerParameters): ListenableWorker {
            return ShowReminderWorker(appContext, params)
        }

    }
}