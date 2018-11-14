package hu.csabapap.seriesreminder.ui.showdetails

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.palette.graphics.Palette
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import dagger.android.support.DaggerAppCompatActivity
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.data.db.entities.SRShow
import hu.csabapap.seriesreminder.data.network.getThumbnailUrl
import hu.csabapap.seriesreminder.receivers.NotificationReceiver
import hu.csabapap.seriesreminder.utils.Reminder
import hu.csabapap.seriesreminder.utils.ShowDetails
import kotlinx.android.synthetic.main.activity_show_details.*
import org.threeten.bp.OffsetDateTime
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import javax.inject.Named


class ShowDetailsActivity : DaggerAppCompatActivity() {
    @Inject @Named("ShowDetailsViewModelFactory")
    lateinit var viewModelProvider: ShowDetailsViewModelProvider

    @Inject
    lateinit var alarmManager: AlarmManager

    private lateinit var viewModel: ShowDetailsViewModel

    var showId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_details)

        showId = intent.getIntExtra(ShowDetails.EXTRA_SHOW_ID, -1)

        if (showId == -1) {
            finish()
            return
        }

        viewModel = ViewModelProviders.of(this, viewModelProvider)
                .get(ShowDetailsViewModel::class.java)

        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        supportActionBar?.title = ""

        viewModel.getShow(showId)

        viewModel.detailsUiState.observe(this, Observer { state ->
            updateUi(state)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_show_details, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val itemId = item?.itemId ?: -1
        when(itemId) {
            R.id.add_reminder -> viewModel.createReminder(showId)
            R.id.remove_from_collection -> {
                viewModel.removeFromCollection(showId)
                finish()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun generatePalette(bitmap: Bitmap) {
        Palette.from(bitmap)
                .clearFilters()
                .generate {
                    val vibrant = it?.vibrantSwatch
                    if (vibrant != null) {
                        updateUiColors(vibrant)
                        return@generate
                    }

                    val darkVibrant = it?.darkVibrantSwatch
                    if (darkVibrant != null) {
                        updateUiColors(darkVibrant)
                    }
                }
    }

    private fun updateUiColors(swatch: Palette.Swatch) {
        swatch.apply {
            title_background.setBackgroundColor(rgb)
            show_title.setTextColor(titleTextColor)
            toolbar.navigationIcon?.setTint(titleTextColor)
        }
    }

    private fun displayShow(show: SRShow) {
        show.let {
            show_title.text = it.title
            overview.text = it.overview
            val posterUrl = if (it.poster.isEmpty()) {
                "tvdb://${it.tvdbId}"
            } else {
                getThumbnailUrl(it.poster)
            }
            Picasso.with(this)
                    .load(posterUrl)
                    .into(poster, object: Callback {
                        override fun onSuccess() {
                            val drawable = poster.drawable as BitmapDrawable
                            val bitmap = drawable.bitmap
                            generatePalette(bitmap)
                        }

                        override fun onError() {

                        }

                    })

            val url = if (it.coverThumb.isEmpty()) {
                "tvdb://fanart?tvdbid=${it.tvdbId}"
            } else {
                getThumbnailUrl(it.cover)
            }
            Picasso.with(this)
                    .load(url)
                    .into(cover)
        }
    }

    private fun updateUi(state: ShowDetailsState) {
        when(state) {
            is ShowDetailsState.Show -> displayShow(state.show)
            is ShowDetailsState.Reminder -> setAlarm(state.show, state.airDate)
        }
    }

    private fun setAlarm(show: SRShow, airDateTime: OffsetDateTime) {
        Timber.d("airing datetime: $airDateTime")
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, airDateTime.dayOfMonth)
        calendar.set(Calendar.HOUR_OF_DAY, airDateTime.hour)
        calendar.set(Calendar.MINUTE, airDateTime.minute)
        calendar.set(Calendar.SECOND, 0)
        val alarmIntent = Intent(this, NotificationReceiver::class.java)
        alarmIntent.putExtra(Reminder.SHOW_ID, show.traktId)
        alarmIntent.putExtra(Reminder.SHOW_TITLE, show.title)
        val pendingIntent = PendingIntent.getBroadcast(this, show.traktId, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.set(AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent)
    }
}
