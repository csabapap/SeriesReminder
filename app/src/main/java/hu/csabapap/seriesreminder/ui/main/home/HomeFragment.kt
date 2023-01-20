package hu.csabapap.seriesreminder.ui.main.home


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerFragment
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.data.db.entities.SRNextEpisode
import hu.csabapap.seriesreminder.data.db.relations.EpisodeWithShow
import hu.csabapap.seriesreminder.extensions.exhaustive
import hu.csabapap.seriesreminder.ui.adapters.DiscoverPreviewAdapter
import hu.csabapap.seriesreminder.ui.adapters.EpisodeCardsAdapter
import hu.csabapap.seriesreminder.ui.adapters.HomeCardsAdapter
import hu.csabapap.seriesreminder.ui.adapters.NextEpisodesAdapter
import hu.csabapap.seriesreminder.ui.adapters.items.CardItem
import hu.csabapap.seriesreminder.ui.adapters.items.DiscoverCardItem
import hu.csabapap.seriesreminder.ui.adapters.items.NextEpisodesCardItem
import hu.csabapap.seriesreminder.ui.adapters.items.UpcomingEpisodeCardItem
import hu.csabapap.seriesreminder.utils.Collectible
import hu.csabapap.seriesreminder.utils.Episode
import kotlinx.android.synthetic.main.fragment_home.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named


class HomeFragment: DaggerFragment(),
        DiscoverPreviewAdapter.PreviewShowListener,
        HomeCardsAdapter.CardClickListener,
        EpisodeCardsAdapter.EpisodeClickListener {

    @field:[Inject Named("Main")]
    lateinit var mainViewModelProvider: ViewModelProvider.Factory
    private lateinit var homeViewModel: HomeViewModel

    lateinit var layoutManager: LinearLayoutManager
    private val cardsAdapter = HomeCardsAdapter(this)

    private var listener: HomeFragmentListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeViewModel = ViewModelProviders.of(this, mainViewModelProvider)
                .get(HomeViewModel::class.java)
        setHasOptionsMenu(true)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is HomeFragmentListener) {
            listener = context
        } else {
            throw RuntimeException(("$context must implement HomeFragmentListener"))
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        cardsAdapter.previewShowListener = this
        cardsAdapter.episodesClickListener = this
        cardsAdapter.nextEpisodesClickListener = object: NextEpisodesAdapter.NextEpisodeClickListener {
            override fun onItemClick(nextEpisode: SRNextEpisode) {
                val activity = activity
                if (activity != null) {
                    Episode.start(activity, nextEpisode.showId, nextEpisode.season, nextEpisode.number)
                }
            }

            override fun onSetAsWatchedButtonClick(nextEpisode: SRNextEpisode) {
                homeViewModel.setEpisodeWatched(nextEpisode)
                val message = String.format(getString(R.string.episode_number), nextEpisode.season, nextEpisode.number) + " set as watched"
                view?.let {
                    Snackbar.make(it, message, Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        homeViewModel.myShowsLiveData.observe(viewLifecycleOwner, Observer {
            it.apply {
                if (isNotEmpty()) {
                    cardsAdapter.addCard(DiscoverCardItem(getString(R.string.title_my_shows), it,
                        CardItem.MY_SHOWS_TYPE, CardItem.PRIORITY_MEDIUM))
                }
            }
        })

        homeViewModel.upcomingEpisodesLiveData.observe(viewLifecycleOwner, Observer {
            it?.apply {
                cardsAdapter.addCard(UpcomingEpisodeCardItem(it, CardItem.UPCOMING_EPISODE_TYPE))
            }
        })

        homeViewModel.viewStateLiveData.observe(viewLifecycleOwner, Observer {
            updateState(it)
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        home_recycler_view.adapter = cardsAdapter
        home_recycler_view.isNestedScrollingEnabled = false
        layoutManager = home_recycler_view.layoutManager as LinearLayoutManager
        layoutManager.orientation = RecyclerView.VERTICAL
    }

    override fun onStart() {
        super.onStart()
        homeViewModel.getShows()
        homeViewModel.getUpcomingEpisodes()
        homeViewModel.getNextEpisodes()
        homeViewModel.syncWatchedShows()
    }

    interface HomeFragmentListener {
        fun onMoreButtonClick(type: Int)
    }

    override fun onItemClick(traktId: Int, inCollection: Boolean) {
        activity?.let {
            Collectible.start(it, traktId, inCollection)
        }
    }

    override fun onMoreButtonClick(type: Int) {
        listener?.onMoreButtonClick(type)
    }

    override fun onItemClick(nextEpisode: EpisodeWithShow) {
        val entry = nextEpisode.episode
        val activity = activity
        if (activity != null) {
            Episode.start(activity, entry.showId, entry.season, entry.number)
        }
    }

    private fun updateState(state: HomeViewState) {
        Timber.d("view state: $state")
        when (state) {
            DisplayTrendingLoader -> cardsAdapter.addCard(DiscoverCardItem(getString(R.string.title_trending), emptyList(),
                    CardItem.TRENDING_CARD_TYPE, CardItem.PRIORITY_TRENDING))
            DisplayPopularLoader -> cardsAdapter.addCard(DiscoverCardItem(getString(R.string.title_popular), emptyList(),
                    CardItem.POPULAR_CARD_TYPE, CardItem.PRIORITY_POPULAR))
            is TrendingState -> cardsAdapter.addCard(DiscoverCardItem(getString(R.string.title_trending),
                    state.items, CardItem.TRENDING_CARD_TYPE, CardItem.PRIORITY_TRENDING))
            is PopularState -> cardsAdapter.addCard(DiscoverCardItem(getString(R.string.title_popular),
                    state.items, CardItem.POPULAR_CARD_TYPE, CardItem.PRIORITY_POPULAR))
            is MyShowsState -> Timber.d("display shows for popular shows")
            is NextEpisodesState -> cardsAdapter.addCard(NextEpisodesCardItem(state.episodes, CardItem.NEXT_EPISODES_TYPE))
            HideTrendingSection -> cardsAdapter.removeCard(CardItem.TRENDING_CARD_TYPE)
        }.exhaustive
    }
}
