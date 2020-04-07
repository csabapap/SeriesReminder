package hu.csabapap.seriesreminder.ui.addshow

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.graphics.ColorUtils
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.palette.graphics.Palette
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
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
import hu.csabapap.seriesreminder.databinding.ActivityAddShowBinding
import hu.csabapap.seriesreminder.extensions.loadFromTmdbUrl
import hu.csabapap.seriesreminder.services.SyncService
import hu.csabapap.seriesreminder.services.workers.SyncShowsWorker
import hu.csabapap.seriesreminder.tasks.DownloadShowTask
import hu.csabapap.seriesreminder.utils.AddShow
import kotlinx.android.synthetic.main.activity_add_show.*
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named

class AddShowActivity : DaggerAppCompatActivity() {

    @Inject @field:Named("AddShowViewModelFactory")
    lateinit var viewModelProvider: ViewModelProvider.Factory

    private lateinit var addShowViewModel: AddShowViewModel

    private var showId: Int = -1
    var show: SRShow? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addShowViewModel = ViewModelProviders.of(this, viewModelProvider)
                .get(AddShowViewModel::class.java)

        DataBindingUtil.setContentView<ActivityAddShowBinding>(this, R.layout.activity_add_show)
                .apply {
                    viewModel = addShowViewModel
                    lifecycleOwner = this@AddShowActivity
                }

        initParams(intent.extras)

        toolbar.apply {
            setNavigationIcon(R.drawable.ic_arrow_back_24dp)
            setNavigationOnClickListener {finish()}
        }

        addShowViewModel.showLiveData.observe(this, Observer {
            it?.apply {
                displayShow(it.show)
            }
        })

        fab_add_show.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val drawable = fab_add_show.drawable as AnimatedVectorDrawable
                drawable.registerAnimationCallback((object: Animatable2.AnimationCallback() {
                    override fun onAnimationEnd(drawable: Drawable?) {
                        finishWithResult()
                    }
                }))
                drawable.start()
            } else {
                val drawable = fab_add_show.drawable as AnimatedVectorDrawableCompat
                drawable.registerAnimationCallback((object: Animatable2Compat.AnimationCallback() {
                    override fun onAnimationEnd(drawable: Drawable?) {
                        finishWithResult()
                    }
                }))
                drawable.start()
            }

            val task = DownloadShowTask(showId)
            (application as SRApplication).appComponent.inject(task)
            addShowViewModel.syncShow(task)
            SyncService.syncShow(this)

            startSyncWorkManager()
        }

        motion_layout.setTransitionListener(object : MotionLayout.TransitionListener {

            @SuppressLint("RestrictedApi")
            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
                Timber.d("p3: $p3")
                if (poster.y <= toolbar.height + toolbar.y) {
                    poster.visibility = View.GONE
                    fab_add_show.hide()
                } else {
                    poster.visibility = View.VISIBLE
                    fab_add_show.show()
                }
            }

            @SuppressLint("RestrictedApi")
            override fun onTransitionCompleted(p0: MotionLayout?, id: Int) {
                when (id) {
                    R.id.collapsed -> {
                        poster.visibility = View.GONE
                        fab_add_show.visibility = View.GONE
                    }
                    R.id.expanded -> {
                        poster.visibility = View.VISIBLE
                        fab_add_show.visibility = View.VISIBLE
                    }
                }
            }

        })
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
        show_title.text = srShow.title
        status.text = srShow.status
        air_daytime.text = String.format(getString(R.string.air_time), srShow.airingTime.day, srShow.airingTime.time)
        tv_overview.text = srShow.overview
        ratings.text = String.format(getString(R.string.ratings_value), (srShow.rating * 10).toInt(), srShow.votes)
        genres.text = srShow.genres
        poster.loadFromTmdbUrl(srShow.tvdbId, callback = (object: Callback {
            override fun onSuccess() {
                val drawable = poster.drawable as BitmapDrawable
                val bitmap = drawable.bitmap
                generatePalette(bitmap)
            }

            override fun onError() {

            }

        }))
        val url = if (srShow.coverThumb.isEmpty()) {
            getCoverUrl(srShow.tvdbId)
        } else {
            getThumbnailUrl(srShow.coverThumb)
        }
        Picasso.with(this)
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
                        cover_overflow.setBackgroundColor(
                                ColorUtils.setAlphaComponent(rgb, /* 30% */ 0x40))
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
