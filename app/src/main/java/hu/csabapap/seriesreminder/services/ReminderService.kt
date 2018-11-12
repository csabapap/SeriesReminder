package hu.csabapap.seriesreminder.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import hu.csabapap.seriesreminder.utils.Reminder
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.ui.showdetails.ShowDetailsActivity
import hu.csabapap.seriesreminder.utils.ShowDetails
import timber.log.Timber


class ReminderService : Service() {

    override fun onBind(intent: Intent): IBinder {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("on start command")
        intent?.apply {
            val id = getIntExtra(Reminder.SHOW_ID, -1)
            val title = getStringExtra(Reminder.SHOW_TITLE)

            displayNotification(id, title)
        }

        return START_STICKY
    }

    private fun displayNotification(id: Int, title: String) {
        val resultIntent = Intent(applicationContext, ShowDetailsActivity::class.java)
        resultIntent.putExtra(ShowDetails.EXTRA_SHOW_ID, id)
        val resultPendingIntent = PendingIntent.getActivity(applicationContext, 17,
                resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = NotificationCompat.Builder(this, "reminder")
        builder.setContentTitle(title)
        builder.setSmallIcon(R.drawable.ic_notifications_black_24dp)
        builder.setContentIntent(resultPendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(id, builder.build())
        stopSelf()
    }

}
