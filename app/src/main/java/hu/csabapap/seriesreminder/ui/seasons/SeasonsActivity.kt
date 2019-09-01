package hu.csabapap.seriesreminder.ui.seasons

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.android.support.DaggerAppCompatActivity
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.data.db.entities.SREpisode
import hu.csabapap.seriesreminder.extensions.exhaustive
import hu.csabapap.seriesreminder.utils.Activities
import hu.csabapap.seriesreminder.utils.Episode
import kotlinx.android.synthetic.main.activity_episode.*
import kotlinx.android.synthetic.main.activity_episode.toolbar
import kotlinx.android.synthetic.main.activity_seasons.*
import javax.inject.Inject
import javax.inject.Named

class SeasonsActivity : DaggerAppCompatActivity() {

    @Inject
    @Named("SeasonsViewModelFactory")
    lateinit var viewModelProvider: SeasonsViewModelProvider
    private lateinit var viewModel: SeasonsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seasons)

        viewModel = viewModelProvider.create(SeasonsViewModel::class.java)

        viewModel.detailsUiState.observe(this, Observer<SeasonsUiState> { state ->
            if (state != null) {
                updateUiState(state)
            }
        })

        val intent = intent ?: return
        val extras = intent.extras ?: return
        initParams(extras)

        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun initParams(extras: Bundle) {
        val showId = extras.getInt(Activities.Season.SHOW_ID)
        val seasonNumber = extras.getInt(Activities.Season.SEASON_NUMBER)
        viewModel.getShow(showId)
        viewModel.getSeasonWithEpisodes(showId, seasonNumber)
    }

    private fun updateUiState(state: SeasonsUiState) {
        when (state) {
            is SeasonsUiState.DisplayShow -> toolbar.title = state.show.title
            is SeasonsUiState.DisplayEpisodes -> displayEpisodes(state.episodes)
        }.exhaustive
    }

    private fun displayEpisodes(episodes: List<SREpisode>) {
        val adapter = EpisodesAdapter(episodes)
        adapter.listener = object : EpisodesAdapter.EpisodeItemClickListener {
            override fun onItemClick(episode: SREpisode) {
                Episode.start(this@SeasonsActivity, episode.showId, episode.season, episode.number)
            }

        }
        val layoutManager = episodes_list.layoutManager as LinearLayoutManager
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        val dividerItemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        val drawable = getDrawable(R.drawable.separator_vertical)
        if (drawable != null) {
            dividerItemDecoration.setDrawable(drawable)
        }
        episodes_list.addItemDecoration(dividerItemDecoration)
        episodes_list.adapter = adapter
    }
}
