package hu.csabapap.seriesreminder.ui.addshow

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.content.res.ColorStateList
import androidx.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import androidx.core.graphics.ColorUtils
import androidx.palette.graphics.Palette
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import dagger.android.support.DaggerAppCompatActivity
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.data.db.entities.SRShow
import hu.csabapap.seriesreminder.data.network.getThumbnailUrl
import hu.csabapap.seriesreminder.databinding.ActivityAddShowBinding
import hu.csabapap.seriesreminder.extensions.loadFromTmdbUrl
import hu.csabapap.seriesreminder.services.SyncService
import kotlinx.android.synthetic.main.activity_add_show.*
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
                    setLifecycleOwner(this@AddShowActivity)
                }

        initParams(intent.extras)

        toolbar.apply {
            setNavigationIcon(R.drawable.ic_arrow_back_24dp)
            setNavigationOnClickListener({finish()})
        }

        addShowViewModel.showLiveData.observe(this, Observer {
            it?.apply {
                displayShow(it.show)
            }
        })

        btn_add_show.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val drawable = btn_add_show.drawable as AnimatedVectorDrawable
                drawable.registerAnimationCallback((object: Animatable2.AnimationCallback() {
                    override fun onAnimationEnd(drawable: Drawable?) {
                        finish()
                    }
                }))
                drawable.start()
            } else {
                val drawable = btn_add_show.drawable as AnimatedVectorDrawableCompat
                drawable.registerAnimationCallback((object: Animatable2Compat.AnimationCallback() {
                    override fun onAnimationEnd(drawable: Drawable?) {
                        finish()
                    }
                }))
                drawable.start()
            }

            addShowViewModel.addShowToCollection(showId)
            SyncService.syncShow(this, showId)
        }
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
        tv_title.text = srShow.title
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
            "tvdb://fanart?tvdbid=${srShow.tvdbId}"
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
                        title_container.setBackgroundColor(rgb)
                        cover_overflow.setBackgroundColor(
                                ColorUtils.setAlphaComponent(rgb, /* 30% */ 0x40))
                        tv_title.setTextColor(titleTextColor)
                    }
                    val darkVibrant = it?.darkVibrantSwatch
                    darkVibrant?.apply {
                        btn_add_show.backgroundTintList = ColorStateList.valueOf(rgb)
                        btn_add_show.imageTintList = ColorStateList.valueOf(titleTextColor)
                    }
                }
    }

    fun getShowId(): Int {
        return intent.extras.getInt("show_id", -1)
    }
}
