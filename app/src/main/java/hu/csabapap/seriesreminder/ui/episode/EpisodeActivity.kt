package hu.csabapap.seriesreminder.ui.episode

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.squareup.picasso.Picasso
import dagger.android.support.DaggerAppCompatActivity
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.data.db.entities.SREpisode
import hu.csabapap.seriesreminder.utils.Episode
import kotlinx.android.synthetic.main.activity_episode.*
import javax.inject.Inject
import javax.inject.Named

class EpisodeActivity : DaggerAppCompatActivity() {

    @Inject
    @field:Named("EpisodeViewModelFactory")
    lateinit var viewModelProvider: ViewModelProvider.Factory

    lateinit var viewModel: EpisodeViewModel

    private var showId = -1
    private var seasonNumber = -1
    private var episodeNumber = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_episode)

        toolbar.apply {
            setNavigationOnClickListener {
                supportFinishAfterTransition()
            }
            title = ""
        }

        setSupportActionBar(toolbar)


        viewModel = ViewModelProviders.of(this, viewModelProvider)
                .get(EpisodeViewModel::class.java)

        val extras = intent.extras
        if (extras != null) {
            initParams(extras)
        }

        viewModel.uiState.observe(this, Observer { state ->
            updateUi(state)
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.getEpisode(showId, seasonNumber, episodeNumber)
    }

    private fun initParams(extras: Bundle) {
        showId = extras.getInt(Episode.SHOW_ID)
        seasonNumber = extras.getInt(Episode.SEASON_NMB)
        episodeNumber = extras.getInt(Episode.EPISODE_NMB)
    }

    private fun updateUi(state: EpisodeUiState) {
        when (state) {
            is EpisodeUiState.DisplayEpisode -> displayEpisode(state.srEpisode)
        }
    }

    private fun displayEpisode(episode: SREpisode) {
        Picasso.with(this)
                .load(episode.image)
                .into(episode_art)
        episode_overview.text = episode.overview
    }
}
