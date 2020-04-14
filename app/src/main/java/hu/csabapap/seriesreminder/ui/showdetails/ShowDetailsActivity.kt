package hu.csabapap.seriesreminder.ui.showdetails

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import dagger.android.support.DaggerAppCompatActivity
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.data.db.entities.SREpisode
import hu.csabapap.seriesreminder.data.db.entities.SRSeason
import hu.csabapap.seriesreminder.data.db.entities.SRShow
import hu.csabapap.seriesreminder.data.db.entities.SrNotification
import hu.csabapap.seriesreminder.data.network.getCoverUrl
import hu.csabapap.seriesreminder.data.network.getEpisodeUrl
import hu.csabapap.seriesreminder.data.network.getPosterUrl
import hu.csabapap.seriesreminder.data.network.getThumbnailUrl
import hu.csabapap.seriesreminder.ui.adapters.DiscoverPreviewAdapter
import hu.csabapap.seriesreminder.ui.adapters.SeasonsAdapter
import hu.csabapap.seriesreminder.ui.adapters.items.CardItem
import hu.csabapap.seriesreminder.ui.adapters.items.ShowItem
import hu.csabapap.seriesreminder.utils.*
import kotlinx.android.synthetic.main.activity_show_details.*
import kotlinx.android.synthetic.main.content_next_episode.*
import kotlinx.android.synthetic.main.content_seasons.*
import org.threeten.bp.LocalDateTime
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named


class ShowDetailsActivity : DaggerAppCompatActivity() {

    @Inject @Named("ShowDetailsViewModelFactory")
    lateinit var viewModelProvider: ShowDetailsViewModelProvider

    private lateinit var viewModel: ShowDetailsViewModel

    lateinit var adapter: DiscoverPreviewAdapter

    var showId: Int = -1
    private var isReminderCreatable = false

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

        back_button.setOnClickListener { finish() }
        toolbar.setNavigationOnClickListener { finish() }

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

        viewModel.getShow(showId)
        viewModel.getNotifications(showId)
        viewModel.refreshRelatedShows(showId)
        viewModel.observeRelatedShows(showId).observe(this, Observer {
            if (it.isNotEmpty()) {
                displayRelatedShows(it)
            }
        })
        viewModel.observeSeasons(showId).observe(this, Observer {
            if (it.isNotEmpty()) {
                displaySeasons(it)
            }
        })

        viewModel.detailsUiState.observe(this, Observer { state ->
            updateUi(state)
        })

        add_notification_button.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Select a Group Name")
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

    override fun onResume() {
        super.onResume()
        viewModel.loadNextEpisode(showId)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_show_details, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId ?: -1) {
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
            toolbar.setBackgroundColor(rgb)
            show_title.setTextColor(titleTextColor)
            status.setTextColor(titleTextColor)
            air_daytime.setTextColor(titleTextColor)
            cover.setBackgroundColor(rgb)
            cover_overflow.setBackgroundColor(rgb)
        }
    }

    private fun updateUi(state: ShowDetailsState) {
        when(state) {
            is ShowDetailsState.Show -> displayShow(state.show)
            is ShowDetailsState.NextEpisode -> displayNextEpisode(state.episode)
            is ShowDetailsState.NextEpisodeNotFound -> hideNextEpisode()
            is ShowDetailsState.NotificationCreated -> notificationCreated()
            is ShowDetailsState.NotificationDeleted -> notificationDeleted()
            is ShowDetailsState.AddNotificationButton -> displayAddNotificationButton()
            is ShowDetailsState.Notification -> displayNotification(state.notification)
            is ShowDetailsState.RelatedShows -> displayRelatedShows(state.relatedShowItems)
            is ShowDetailsState.Seasons -> displaySeasons(state.seasons)
        }
    }

    @SuppressLint("RestrictedApi")
    private fun displayShow(show: SRShow) {
        show.let {
            show_title.text = it.title
            toolbar.title = it.title
            overview.text = it.overview
            status.text = it.status
            val localDateTime = getAirDateTimeInCurrentTimeZone(LocalDateTime.now(), it.airingTime)
            air_daytime.text = getDayAndTimeString(localDateTime)
            val posterUrl = if (it.posterThumb.isEmpty()) {
                getPosterUrl(it.tvdbId)
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
                getCoverUrl(it.tvdbId)
            } else {
                getThumbnailUrl(it.cover)
            }
            Picasso.with(this)
                    .load(url)
                    .into(cover)
        }
    }

    private fun displayNextEpisode(episode: SREpisode) {
        next_episode_content.visibility = View.VISIBLE
        next_episode_title.text = getString(R.string.episode_title_with_numbers)
                .format(episode.season, episode.number, episode.title)
        Picasso.with(this)
                .load(getEpisodeUrl(episode.tvdbId))
                .into(episode_art)

        next_episode_content.setOnClickListener {
            val options = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(this, episode_art, "episodeArt")
            Episode.startWithOptions(this, episode.showId, episode.season, episode.number, options)
        }

        set_watched.setOnClickListener {
             viewModel.setEpisodeWatched(episode)
        }
    }

    private fun displaySeasons(seasons: List<SRSeason>) {
        val seasonsAdapter = SeasonsAdapter(seasons)
        seasonsAdapter.listener = object : SeasonsAdapter.SeasonClickListener {
            override fun onItemClick(season: SRSeason) {
                Activities.Season.start(this@ShowDetailsActivity, season.showId, season.number)
            }

        }
        seasonsAdapter.menuListener = object : SeasonsAdapter.SeasonMenuListener {
            override fun setAllEpisodeWatched(season: SRSeason) {
                viewModel.setWatchedAllEpisodeInSeason(season)
            }

        }
        seasons_list.isNestedScrollingEnabled = false
        seasons_list.layoutManager = LinearLayoutManager(this)
        seasons_list.adapter = seasonsAdapter
    }

    private fun hideNextEpisode() {
        next_episode_content.visibility = View.GONE
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
