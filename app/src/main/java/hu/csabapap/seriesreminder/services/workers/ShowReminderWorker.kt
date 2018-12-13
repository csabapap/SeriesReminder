package hu.csabapap.seriesreminder.services.workers

import android.content.Context
import android.content.Intent
import androidx.core.os.bundleOf
import androidx.work.Result
import androidx.work.Worker
import androidx.work.WorkerParameters
import hu.csabapap.seriesreminder.services.ReminderService
import hu.csabapap.seriesreminder.utils.Reminder

class ShowReminderWorker(context: Context, workerParameters: WorkerParameters)
    : Worker(context, workerParameters) {

    override fun doWork(): Result {

        val showId = inputData.getInt(Reminder.SHOW_ID, -1)
        if (showId == -1) {
            return Result.failure()
        }
        val title = inputData.getString(Reminder.SHOW_TITLE)

        val serviceIntent = Intent(applicationContext, ReminderService::class.java)
        serviceIntent.putExtras(bundleOf(Reminder.SHOW_ID to showId,
                Reminder.SHOW_TITLE to title))
        applicationContext.startService(serviceIntent)

        return Result.success()
    }
}