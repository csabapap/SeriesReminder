package hu.csabapap.seriesreminder.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import hu.csabapap.seriesreminder.services.ReminderService
import hu.csabapap.seriesreminder.utils.Reminder

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val serviceIntent = Intent(context, ReminderService::class.java)
        serviceIntent.putExtras(intent.extras)
        context.startService(serviceIntent)
    }
}
