package hu.csabapap.seriesreminder.ui.addshow

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.graphics.drawable.Animatable2Compat
import android.support.graphics.drawable.AnimatedVectorDrawableCompat
import android.support.v4.graphics.ColorUtils
import android.support.v7.graphics.Palette
import com.squareup.picasso.Callback
import dagger.android.support.DaggerAppCompatActivity
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.data.db.entities.SRShow
import hu.csabapap.seriesreminder.extensions.loadFromTmdbUrl
import hu.csabapap.seriesreminder.services.SyncService
import kotlinx.android.synthetic.main.activity_add_show.*
import org.apache.commons.cli.MissingArgumentException
import javax.inject.Inject

class AddShowActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelProvider: ViewModelProvider.Factory
    private lateinit var addShowViewModel: AddShowViewModel

    private var showId: Int = -1
    var show: SRShow? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_show)

        addShowViewModel = ViewModelProviders.of(this, viewModelProvider)
                .get(AddShowViewModel::class.java)

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

        btn_add_show.setOnClickListener({
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
        })
    }

    override fun onStart() {
        super.onStart()
        addShowViewModel.getShow(showId)
    }

    private fun initParams(extras: Bundle?) {
        if(extras == null) {
            throw MissingArgumentException("Missing required extras")
        }
        if (!extras.containsKey("show_id")) {
            throw MissingArgumentException("Missing show id")
        }
        showId = extras.getInt("show_id", -1)
    }

    private fun displayShow(srShow: SRShow) {
        tv_title.text = srShow.title
        tv_overview.text = srShow.overview
        ratings.text = String.format(getString(R.string.ratings_value), (srShow.rating * 10).toInt(), srShow.votes)
        genres.text = srShow.genres
        poster.loadFromTmdbUrl(srShow.posterThumb, (object: Callback {
            override fun onSuccess() {
                val drawable = poster.drawable as BitmapDrawable
                val bitmap = drawable.bitmap
                generatePalette(bitmap)
            }

            override fun onError() {

            }

        }))
        cover.loadFromTmdbUrl(srShow.cover)
    }

    private fun generatePalette(bitmap: Bitmap) {
        Palette.from(bitmap)
                .clearFilters()
                .generate {
                    val vibrant = it.vibrantSwatch
                    vibrant?.apply {
                        title_container.setBackgroundColor(rgb)
                        cover_overflow.setBackgroundColor(
                                ColorUtils.setAlphaComponent(rgb, /* 30% */ 0x40))
                        tv_title.setTextColor(titleTextColor)
                    }
                    val darkVibrant = it.darkVibrantSwatch
                    darkVibrant?.apply {
                        btn_add_show.backgroundTintList = ColorStateList.valueOf(rgb)
                        btn_add_show.imageTintList = ColorStateList.valueOf(titleTextColor)
                    }
                }
    }
}
