@file:JvmName("ActivityHelper")

package hu.csabapap.seriesreminder.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import hu.csabapap.seriesreminder.ui.addshow.AddShowActivity
import hu.csabapap.seriesreminder.ui.showdetails.ShowDetailsActivity

object ShowDetails {
    const val EXTRA_SHOW_ID = "show_id"

    fun start(context: Context, showId: Int) {
        val intent = Intent(context, ShowDetailsActivity::class.java)
        intent.putExtra(EXTRA_SHOW_ID, showId)
        context.startActivity(intent)
    }
}

object Search {
    const val RC_ADD = 101
}

object AddShow {
    const val EXTRA_SHOW_ID = "show_id"

    fun start(context: Context, showId: Int) {
        val intent = Intent(context, AddShowActivity::class.java)
        intent.putExtra(EXTRA_SHOW_ID, showId)
        context.startActivity(intent)
    }

    fun startForResult(activity: Activity, showId: Int, requestCode: Int) {
        val intent = Intent(activity, AddShowActivity::class.java)
        intent.putExtra(EXTRA_SHOW_ID, showId)
        activity.startActivityForResult(intent, requestCode)
    }
}

object Reminder {
    const val SHOW_ID = "show_id"
    const val SHOW_TITLE = "show_title"
}

object Collectible {
    fun start(context: Context, showId: Int, inCollection: Boolean) {
        if (inCollection) {
            ShowDetails.start(context, showId)
        } else {
            AddShow.start(context, showId)
        }
    }
}