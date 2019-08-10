package hu.csabapap.seriesreminder.ui.episode

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.squareup.picasso.Picasso
import dagger.android.support.DaggerAppCompatActivity
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.data.db.relations.EpisodeWithShow
import hu.csabapap.seriesreminder.data.network.getFullSizeUrl
import hu.csabapap.seriesreminder.extensions.exhaustive
import hu.csabapap.seriesreminder.utils.Episode
import kotlinx.android.synthetic.main.activity_episode.*
import java.util.*
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
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            title = ""
            setDisplayHomeAsUpEnabled(true)
        }

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

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId ?: return super.onOptionsItemSelected(item)
        when (id) {
            android.R.id.home -> {
                finish()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun initParams(extras: Bundle) {
        showId = extras.getInt(Episode.SHOW_ID)
        seasonNumber = extras.getInt(Episode.SEASON_NMB)
        episodeNumber = extras.getInt(Episode.EPISODE_NMB)
    }

    private fun updateUi(state: EpisodeUiState) {
        when (state) {
            is EpisodeUiState.DisplayEpisode -> displayEpisode(state.episodeWithShow)
        }.exhaustive
    }

    private fun displayEpisode(episodeWithShow: EpisodeWithShow) {
        val show = episodeWithShow.show
        val episode = episodeWithShow.episode
        val image = if (episode.image.isNotEmpty()) {
            episode.image
        } else {
            getFullSizeUrl(show?.cover)
        }
        Picasso.with(this)
                .load(image)
                .placeholder(R.color.dark_grey)
                .into(episode_art)
        if (show != null) {
            show_details.text = show.title
            show_details.visibility = View.VISIBLE
        }
        val unformattedEpisodeTitle = getString(R.string.episode_title_with_numbers)
        episode_title.text = String.format(Locale.ENGLISH, unformattedEpisodeTitle,
                episode.season, episode.number, episode.title)
        episode_overview.text = episode.overview
    }
}
