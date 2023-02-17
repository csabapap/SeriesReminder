package hu.csabapap.seriesreminder.ui.addshow

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.ViewModelProvider
import androidx.palette.graphics.Palette
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import dagger.android.support.DaggerAppCompatActivity
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.SRApplication
import hu.csabapap.seriesreminder.data.db.entities.SRShow
import hu.csabapap.seriesreminder.data.network.getCoverUrl
import hu.csabapap.seriesreminder.data.network.getThumbnailUrl
import hu.csabapap.seriesreminder.extensions.loadFromTmdbUrl
import hu.csabapap.seriesreminder.services.SyncService
import hu.csabapap.seriesreminder.services.workers.SyncShowsWorker
import hu.csabapap.seriesreminder.tasks.DownloadShowTask
import hu.csabapap.seriesreminder.utils.AddShow
import kotlinx.android.synthetic.main.activity_add_show.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named

class AddShowActivity : DaggerAppCompatActivity() {

    @Inject @Named("AddShowViewModelFactory")
    lateinit var viewModelProvider: ViewModelProvider.Factory

    private val addShowViewModel: AddShowViewModel by viewModels { viewModelProvider }

    private var showId: Int = -1
    var show: SRShow? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initParams(intent.extras)

        setContentView(
            ComposeView(this).apply {
                setContent {
                    AddShowScreenUi(
                        viewModel = addShowViewModel,
                        imageColorState = ImageColorState(
                            this@AddShowActivity,
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.onPrimary
                        ),
                        onAddShowClick = { addShow() },
                        onBackPress = { finish() }
                    )
                }
            })
    }

    private fun addShow() {
        val task = DownloadShowTask(showId)
        (application as SRApplication).appComponent.inject(task)
        addShowViewModel.syncShow(task)
        SyncService.syncShow(this)

        startSyncWorkManager()
        finishWithResult()
    }

    private fun startSyncWorkManager() {
        val workRequest = PeriodicWorkRequest.Builder(SyncShowsWorker::class.java,
                8, TimeUnit.HOURS,
                30, TimeUnit.MINUTES)
                .build()

        WorkManager.getInstance(this)
                .enqueueUniquePeriodicWork("SyncShowsWorker",
                        ExistingPeriodicWorkPolicy.KEEP, workRequest)
    }

    override fun onStart() {
        super.onStart()
        addShowViewModel.getShow(showId)
    }

    private fun initParams(extras: Bundle?) {
        if(extras == null) {
            throw IllegalArgumentException("Missing required extras")
        }
        if (!extras.containsKey("show_id")) {
            throw IllegalArgumentException("Missing show id")
        }
        showId = extras.getInt("show_id", -1)
    }

    private fun displayShow(srShow: SRShow) {
        toolbar.title = srShow.title
        show_title.text = srShow.title
        status.text = srShow.status
        status.text = if (srShow.status == "ended") {
            srShow.status
        } else {
            "${srShow.status} - ${String.format(getString(R.string.air_time), srShow.airingTime.day, srShow.airingTime.time)}"
        }
        tv_overview.text = srShow.overview
        ratings.text = String.format(getString(R.string.ratings_value), (srShow.rating * 10).toInt(), srShow.votes)
        genres.text = srShow.genres
        poster.loadFromTmdbUrl(srShow.tvdbId, callback = (object: Callback {
            override fun onSuccess() {
                val drawable = poster.drawable as BitmapDrawable
                val bitmap = drawable.bitmap
                generatePalette(bitmap)
            }

            override fun onError(e: Exception?) {

            }
        }))
        val url = if (srShow.coverThumb.isEmpty()) {
            getCoverUrl(srShow.tvdbId)
        } else {
            getThumbnailUrl(srShow.coverThumb)
        }
        Picasso.get()
                .load(url)
                .into(cover)
    }

    private fun generatePalette(bitmap: Bitmap) {
        Palette.from(bitmap)
                .clearFilters()
                .generate {
                    val vibrant = it?.vibrantSwatch
                    vibrant?.apply {
                        title_background.setBackgroundColor(rgb)
                        toolbar.setBackgroundColor(rgb)
                        cover_overflow.setBackgroundColor(rgb)
                        show_title.setTextColor(titleTextColor)
                    }
                    val darkVibrant = it?.darkVibrantSwatch
                    darkVibrant?.apply {
                        fab_add_show.backgroundTintList = ColorStateList.valueOf(rgb)
                        fab_add_show.imageTintList = ColorStateList.valueOf(titleTextColor)
                    }
                }
    }

    private fun finishWithResult() {
        val intent = Intent()
        intent.putExtra(AddShow.EXTRA_SHOW_ID, showId)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
