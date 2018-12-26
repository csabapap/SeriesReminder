package hu.csabapap.seriesreminder.ui.showdetails

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.palette.graphics.Palette
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import dagger.android.support.DaggerAppCompatActivity
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.data.db.entities.SRShow
import hu.csabapap.seriesreminder.data.network.getThumbnailUrl
import hu.csabapap.seriesreminder.services.workers.ShowReminderWorker
import hu.csabapap.seriesreminder.utils.Reminder
import hu.csabapap.seriesreminder.utils.ShowDetails
import kotlinx.android.synthetic.main.activity_show_details.*
import org.threeten.bp.OffsetDateTime
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named


class ShowDetailsActivity : DaggerAppCompatActivity() {
    @Inject @Named("ShowDetailsViewModelFactory")
    lateinit var viewModelProvider: ShowDetailsViewModelProvider

    private lateinit var viewModel: ShowDetailsViewModel
    private lateinit var workManager: WorkManager

    var showId: Int = -1
    var isReminderCreatable = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_details)

        workManager = WorkManager.getInstance()

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

        motion_layout.setTransitionListener(object : MotionLayout.TransitionListener {

            @SuppressLint("RestrictedApi")
            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
                Timber.d("p3: $p3")
                if (poster.y <= toolbar.height + toolbar.y) {
                    poster.visibility = View.GONE
                    fab_reminder.hide()
                 } else {
                    poster.visibility = View.VISIBLE
                    if (isReminderCreatable) {
                        fab_reminder.show()
                    } else {
                        fab_reminder.visibility = View.GONE
                    }
                }
            }

            @SuppressLint("RestrictedApi")
            override fun onTransitionCompleted(p0: MotionLayout?, id: Int) {
                when (id) {
                    R.id.collapsed -> {
                        poster.visibility = View.GONE
                        fab_reminder.visibility = View.GONE
                    }
                    R.id.expanded -> {
                        poster.visibility = View.VISIBLE
                        if (isReminderCreatable) {
                            fab_reminder.visibility = View.VISIBLE
                        } else {
                            fab_reminder.visibility = View.GONE
                        }
                    }
                }
            }

        })

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
                        return@generate
                    }

                    val mute = it?.mutedSwatch
                    if (mute != null) {
                        updateUiColors(mute)
                    }
                }
    }

    private fun updateUiColors(swatch: Palette.Swatch) {
        swatch.apply {
            title_background.setBackgroundColor(rgb)
            show_title.setTextColor(titleTextColor)
            toolbar.setBackgroundColor(rgb)
            toolbar.backgroundColorAlpha = 0
            toolbar.navigationIcon?.setTint(titleTextColor)
            cover.setBackgroundColor(rgb)
            fab_reminder.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
            fab_reminder.imageTintList = ColorStateList.valueOf(rgb)
        }
    }

    @SuppressLint("RestrictedApi")
    private fun displayShow(show: SRShow) {
        show.let {
            show_title.text = it.title
            overview.text = it.overview
            status.text = it.status
            air_daytime.text = "${show.airingTime.day} ${show.airingTime.time}"
            val posterUrl = if (it.posterThumb.isEmpty()) {
                "tvdb://${it.tvdbId}"
            } else {
                getThumbnailUrl(it.posterThumb)
            }

            if (it.status == "returning series") {
                isReminderCreatable = true
                fab_reminder.visibility = View.VISIBLE
            } else {
                isReminderCreatable = false
                fab_reminder.visibility = View.GONE
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

    @SuppressLint("RestrictedApi")
    private fun setAlarm(show: SRShow, airDateTime: OffsetDateTime) {
        Timber.d("airing datetime: $airDateTime")
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, airDateTime.dayOfMonth)
        calendar.set(Calendar.HOUR_OF_DAY, airDateTime.hour)
        calendar.set(Calendar.MINUTE, airDateTime.minute)
        calendar.set(Calendar.SECOND, 0)
        val request = OneTimeWorkRequest.Builder(ShowReminderWorker::class.java)
//                .setInitialDelay(calendar.timeInMillis - System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .setInitialDelay(60_000L, TimeUnit.MILLISECONDS)
                .setInputData(
                        Data.Builder()
                                .put(Reminder.SHOW_ID, show.traktId)
                                .put(Reminder.SHOW_TITLE, show.title)
                                .build())
                .build()
        workManager.enqueue(request)
    }
}
