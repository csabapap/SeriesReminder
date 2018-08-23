package hu.csabapap.seriesreminder.ui.showdetails

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import com.google.android.material.appbar.AppBarLayout
import dagger.android.support.DaggerAppCompatActivity
import hu.csabapap.seriesreminder.R
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



//        app_bar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, offset ->
//            val toolbarHeight = toolbar.height
//            val scrollRange = appBarLayout.totalScrollRange
//            val i = scrollRange - toolbarHeight + offset
//            if (i <= 0) {
//                poster.visibility = View.GONE
//                poster_placeholder.visibility = View.VISIBLE
//            } else {
//                poster.visibility = View.VISIBLE
//                poster_placeholder.visibility = View.INVISIBLE
//                val posterHeightDiff = poster.height - poster_placeholder.height
//                var scale = Math.abs(scrollRange + offset.toFloat() + posterHeightDiff) / scrollRange
//                if (scale > 1f) scale = 1f
//                if (scale > poster_placeholder.height.toFloat() / poster.height.toFloat()) {
//                    poster.scaleX = scale
//                    poster.scaleY = scale
//                    poster.translationY = (poster.height - poster.height.toFloat() * scale) / 2f
//                }
//            }
//        })

        viewModel.getShow(showId)

        viewModel.showLiveData.observe(this, Observer {
            it?.let {
                show_title.text = it.title
                overview.text = it.overview
//                ratings.text = String.format(getString(R.string.ratings_value), (it.rating * 10).toInt(), it.votes)
//                genres.text = it.genres
                poster.loadFromTmdbUrl(it.posterThumb)
                cover.loadFromTmdbUrl(it.cover)
            }
        })
    }
}