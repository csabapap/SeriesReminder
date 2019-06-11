package hu.csabapap.seriesreminder.ui.episode

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import dagger.android.support.DaggerFragment
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.data.db.entities.SREpisode
import hu.csabapap.seriesreminder.utils.Episode
import kotlinx.android.synthetic.main.fragment_episode.*
import javax.inject.Inject
import javax.inject.Named

class EpisodeActivityFragment : DaggerFragment() {

    @Inject
    @field:Named("EpisodeViewModelFactory")
    lateinit var viewModelProvider: ViewModelProvider.Factory

    lateinit var viewModel: EpisodeViewModel

    var showId = -1
    var seasonNumber = -1
    var episodeNumber = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this, viewModelProvider)
                .get(EpisodeViewModel::class.java)

        val args = arguments
        if (args != null) {
            initParams(args)
        }

        viewModel.uiState.observe(this, Observer { state ->
            updateUi(state)
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_episode, container, false)
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
        episode_overview.text = episode.overview
    }
}
