package hu.csabapap.seriesreminder.ui.showdetails

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.palette.graphics.Palette
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import dagger.android.support.DaggerAppCompatActivity
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.data.network.getThumbnailUrl
import hu.csabapap.seriesreminder.utils.ShowDetails
import kotlinx.android.synthetic.main.activity_show_details.*
import javax.inject.Inject
import javax.inject.Named

class ShowDetailsActivity : DaggerAppCompatActivity() {
    @Inject @Named("ShowDetailsViewModelFactory")
    lateinit var viewModelProvider: ShowDetailsViewModelProvider
    private lateinit var viewModel: ShowDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_details)

        val showId = intent.getIntExtra(ShowDetails.EXTRA_SHOW_ID, -1)

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

        viewModel.showLiveData.observe(this, Observer { srShow ->
            srShow?.let {
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
        })
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
}
