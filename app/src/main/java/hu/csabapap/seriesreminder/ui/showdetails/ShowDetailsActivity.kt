package hu.csabapap.seriesreminder.ui.showdetails

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.WorkManager
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import dagger.android.support.DaggerAppCompatActivity
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.data.db.entities.SRShow
import hu.csabapap.seriesreminder.data.db.entities.SrNotification
import hu.csabapap.seriesreminder.data.network.getThumbnailUrl
import hu.csabapap.seriesreminder.ui.adapters.DiscoverPreviewAdapter
import hu.csabapap.seriesreminder.ui.adapters.items.CardItem
import hu.csabapap.seriesreminder.ui.adapters.items.ShowItem
import hu.csabapap.seriesreminder.utils.Collectible
import hu.csabapap.seriesreminder.utils.ShowDetails
import hu.csabapap.seriesreminder.utils.readableDate
import kotlinx.android.synthetic.main.activity_show_details.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named


class ShowDetailsActivity : DaggerAppCompatActivity() {

    @Inject @Named("ShowDetailsViewModelFactory")
    lateinit var viewModelProvider: ShowDetailsViewModelProvider

    private lateinit var viewModel: ShowDetailsViewModel
    private lateinit var workManager: WorkManager

    lateinit var adapter: DiscoverPreviewAdapter

    var showId: Int = -1
    private var isReminderCreatable = false

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

        adapter = DiscoverPreviewAdapter(CardItem.TRENDING_CARD_TYPE)
        adapter.listener = object:DiscoverPreviewAdapter.PreviewShowListener{
            override fun onItemClick(traktId: Int, inCollection: Boolean) {
                Collectible.start(this@ShowDetailsActivity, traktId, inCollection)
            }
        }
        related_shows.adapter = adapter
        val layoutManager = related_shows.layoutManager as LinearLayoutManager
        layoutManager.orientation = RecyclerView.HORIZONTAL
        val dividerItemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        dividerItemDecoration.setDrawable(this.getDrawable(R.drawable.vertical_separator))
        related_shows.addItemDecoration(dividerItemDecoration)

        motion_layout.setTransitionListener(object : MotionLayout.TransitionListener {

            @SuppressLint("RestrictedApi")
            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {

            }

            @SuppressLint("RestrictedApi")
            override fun onTransitionCompleted(p0: MotionLayout?, id: Int) {
                when (id) {
                    R.id.collapsed -> {
                        poster.visibility = View.GONE
                    }
                    R.id.expanded -> {
                        poster.visibility = View.VISIBLE
                    }
                }
            }

        })

        viewModel.getShow(showId)
        viewModel.getNotifications(showId)
        viewModel.refreshRelatedShows(showId)
        viewModel.observeRelatedShows(showId).observe(this, Observer {
            if (it.isNotEmpty()) {
                displayRelatedShows(it)
            }
        })

        viewModel.detailsUiState.observe(this, Observer { state ->
            updateUi(state)
        })

        add_notification_button.setOnClickListener {
//            viewModel.createReminder(showId)
            val builder = AlertDialog.Builder(this)
            //alt_bld.setIcon(R.drawable.icon);
            builder.setTitle("Select a Group Name");
            var notificationTime = 0
            builder.setSingleChoiceItems(arrayOf("30 minutes", "1 hour"), -1) { _, which ->
                Timber.d("positions: $which")
                notificationTime = when (which) {
                    0 -> 30 * 60 * 1000
                    1 -> 60 * 60 * 1000
                    else -> 0
                }
            }
            builder.setPositiveButton("Create Reminder") { _, _ -> viewModel.createNotification(showId, notificationTime) }
            builder.setNegativeButton("Cancel", null)
            builder.create().show()
        }

        delete_notification.setOnClickListener {
            viewModel.removeNotification(showId)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_show_details, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val itemId = item?.itemId ?: -1
        when(itemId) {
            R.id.remove_from_collection -> {
                viewModel.removeFromCollection(showId)
                finish()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun generatePalette(bitmap: Bitmap, callback: (palette: Palette?) -> Unit) {
        Palette.from(bitmap)
                .clearFilters()
                .generate {
                    callback(it)
                }
    }

    private fun updateUiColors(swatch: Palette.Swatch) {
        swatch.apply {
            title_background.setBackgroundColor(rgb)
            show_title.setTextColor(titleTextColor)
            status.setTextColor(titleTextColor)
            air_daytime.setTextColor(titleTextColor)
            toolbar.setBackgroundColor(rgb)
            toolbar.backgroundColorAlpha = 0
            toolbar.navigationIcon?.setTint(titleTextColor)
            cover.setBackgroundColor(rgb)
//            fab_reminder.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
//            fab_reminder.imageTintList = ColorStateList.valueOf(rgb)
            cover_overflow.setBackgroundColor(rgb)
        }
    }

    @SuppressLint("RestrictedApi")
    private fun displayShow(show: SRShow) {
        show.let {
            show_title.text = it.title
            overview.text = it.overview
            status.text = it.status
            air_daytime.text = String.format(getString(R.string.air_time), it.airingTime.day, it.airingTime.time)
            val posterUrl = if (it.posterThumb.isEmpty()) {
                "tvdb://${it.tvdbId}"
            } else {
                getThumbnailUrl(it.posterThumb)
            }

            isReminderCreatable = it.status == "returning series"

            Picasso.with(this)
                    .load(posterUrl)
                    .into(poster, object: Callback {
                        override fun onSuccess() {
                            val drawable = poster.drawable as BitmapDrawable
                            val bitmap = drawable.bitmap
                            generatePalette(bitmap) { palette ->
                                val vibrant = palette?.vibrantSwatch
                                if (vibrant != null) {
                                    updateUiColors(vibrant)
                                    return@generatePalette
                                }

                                val darkVibrant = palette?.darkVibrantSwatch
                                if (darkVibrant != null) {
                                    updateUiColors(darkVibrant)
                                    return@generatePalette
                                }

                                val mute = palette?.mutedSwatch
                                if (mute != null) {
                                    updateUiColors(mute)
                                }
                            }
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
            is ShowDetailsState.NotificationCreated -> notificationCreated()
            is ShowDetailsState.NotificationDeleted -> notificationDeleted()
            is ShowDetailsState.AddNotificationButton -> displayAddNotificationButton()
            is ShowDetailsState.Notification -> displayNotification(state.notification)
            is ShowDetailsState.RelatedShows -> displayRelatedShows(state.relatedShowItems)
        }
    }

    private fun displayNotification(notification: SrNotification) {
        add_notification_button.visibility = View.GONE
        active_notification_group.visibility = View.VISIBLE
        active_notification_group.updatePreLayout(show_content)
        notification_text.text = "${readableDate(notification.delay)} before"
    }

    private fun displayAddNotificationButton() {
        add_notification_button.visibility = View.VISIBLE
        active_notification_group.visibility = View.GONE
        active_notification_group.updatePreLayout(show_content)
    }

    private fun notificationCreated() {
        viewModel.getNotifications(showId)
        displaySnack(getString(R.string.notification_created))
    }

    private fun notificationDeleted() {
        displayAddNotificationButton()
        displaySnack(getString(R.string.notification_deleted))
    }

    private fun displaySnack(message: String) {
        Snackbar.make(motion_layout, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun displayRelatedShows(relatedShows: List<ShowItem>) {
        Timber.d("display related shows, nmb of related shows: ${relatedShows.size}")
        adapter.updateItems(relatedShows)
        related_shows_label.visibility = View.VISIBLE
        related_shows.visibility = View.VISIBLE
    }
}
