package hu.csabapap.seriesreminder.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat
import hu.csabapap.seriesreminder.services.ReminderService
import hu.csabapap.seriesreminder.utils.Reminder

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val serviceIntent = Intent(context, ReminderService::class.java)
        val extras = intent.extras ?: return
        serviceIntent.putExtras(extras)
        ContextCompat.startForegroundService(context, serviceIntent)
    }
}
