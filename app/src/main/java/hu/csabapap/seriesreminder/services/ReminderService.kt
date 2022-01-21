package hu.csabapap.seriesreminder.services

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.work.WorkManager
import dagger.android.DaggerService
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.SRApplication
import hu.csabapap.seriesreminder.tasks.FetchNextEpisodeTask
import hu.csabapap.seriesreminder.tasks.TaskExecutor
import hu.csabapap.seriesreminder.ui.showdetails.ShowDetailsActivity
import hu.csabapap.seriesreminder.utils.Reminder
import hu.csabapap.seriesreminder.utils.ShowDetails
import hu.csabapap.seriesreminder.utils.createChannel
import javax.inject.Inject


class ReminderService : DaggerService() {

    @Inject
    lateinit var taskExecutor: TaskExecutor

    override fun onBind(intent: Intent): IBinder {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.apply {
            val id = getIntExtra(Reminder.SHOW_ID, -1)
            val title = getStringExtra(Reminder.SHOW_TITLE) ?: ""

            displayNotification(id, title)
            getNextEpisode(id)
        }

        return START_STICKY
    }

    private fun displayNotification(id: Int, title: String) {
        val resultIntent = Intent(applicationContext, ShowDetailsActivity::class.java)
        resultIntent.putExtra(ShowDetails.EXTRA_SHOW_ID, id)
        val resultPendingIntent = PendingIntent.getActivity(applicationContext, 17,
                resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = NotificationCompat.Builder(this, "episode_reminder")
        builder.setContentTitle(title)
        builder.setSmallIcon(R.drawable.ic_notifications_black_24dp)
        builder.setContentIntent(resultPendingIntent)
        builder.createChannel()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(id, builder.build())
        startForeground(1, builder.build())
    }

    private fun getNextEpisode(showId: Int) {
        val task = FetchNextEpisodeTask(showId)
        (application as SRApplication).appComponent.inject(task)
        taskExecutor.queue.add(task)
        taskExecutor.executeTasks {
            stopSelf()
        }
    }
}
