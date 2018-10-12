package hu.csabapap.seriesreminder.ui.showdetails

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.squareup.picasso.Picasso
import dagger.android.support.DaggerAppCompatActivity
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.data.network.getThumbnailUrl
import hu.csabapap.seriesreminder.extensions.loadFromTmdbUrl
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

        viewModel.getShow(showId)

        viewModel.showLiveData.observe(this, Observer {
            it?.let {
                show_title.text = it.title
                overview.text = it.overview
//                ratings.text = String.format(getString(R.string.ratings_value), (it.rating * 10).toInt(), it.votes)
//                genres.text = it.genres
                poster.loadFromTmdbUrl(it.tvdbId)
                val url = if (it.coverThumb.isEmpty()) {
                    "tvdb://fanart?tvdbid=${it.tvdbId}"
                } else {
                    getThumbnailUrl(it.coverThumb)
                }
                Picasso.with(this)
                        .load(url)
                        .into(cover)
            }
        })
    }
}
