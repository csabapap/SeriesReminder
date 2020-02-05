package hu.csabapap.seriesreminder.ui.main.home


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.android.support.DaggerFragment
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.data.db.entities.NextEpisodeItem
import hu.csabapap.seriesreminder.extensions.exhaustive
import hu.csabapap.seriesreminder.services.SyncService
import hu.csabapap.seriesreminder.ui.adapters.DiscoverPreviewAdapter
import hu.csabapap.seriesreminder.ui.adapters.EpisodeCardsAdapter
import hu.csabapap.seriesreminder.ui.adapters.HomeCardsAdapter
import hu.csabapap.seriesreminder.ui.adapters.items.CardItem
import hu.csabapap.seriesreminder.ui.adapters.items.DiscoverCardItem
import hu.csabapap.seriesreminder.ui.adapters.items.NextEpisodesCardItem
import hu.csabapap.seriesreminder.ui.adapters.items.UpcomingEpisodeCardItem
import hu.csabapap.seriesreminder.ui.search.SearchActivity
import hu.csabapap.seriesreminder.ui.settings.SettingsActivity
import hu.csabapap.seriesreminder.utils.Collectible
import hu.csabapap.seriesreminder.utils.Episode
import kotlinx.android.synthetic.main.fragment_home.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named


class HomeFragment : DaggerFragment(),
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

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is HomeFragmentListener) {
            listener = context
        } else {
            throw RuntimeException((context!!.toString() + " must implement HomeFragmentListener"))
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        cardsAdapter.previewShowListener = this
        cardsAdapter.episodesClickListener = this

        homeViewModel.myShowsLiveData.observe(this, Observer {
            it.apply {
                if (isEmpty().not()) {
                    cardsAdapter.addCard(DiscoverCardItem(getString(R.string.title_my_shows), it,
                            CardItem.MY_SHOWS_TYPE, CardItem.PRIORITY_MEDIUM))
                }
            }
        })

        homeViewModel.upcomingEpisodesLiveData.observe(this, Observer {
            it?.apply {
                cardsAdapter.addCard(UpcomingEpisodeCardItem(it, CardItem.UPCOMING_EPISODE_TYPE))
            }
        })

        homeViewModel.viewStateLiveData.observe(this, Observer {
            updateState(it)
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        home_toolbar.title = getString(R.string.app_name)
        (activity as AppCompatActivity).setSupportActionBar(home_toolbar)

        home_recycler_view.adapter = cardsAdapter
        layoutManager = home_recycler_view.layoutManager as LinearLayoutManager
        layoutManager.orientation = RecyclerView.VERTICAL
    }

    override fun onStart() {
        super.onStart()
        homeViewModel.getUpcomingEpisodes()
        homeViewModel.getNextEpisodes()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        activity?.menuInflater?.inflate(R.menu.main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.search -> {
                search()
                return true
            }
            R.id.sync -> {
                syncShows()
                return true
            }
            R.id.sync_next_episodes -> {
                syncNextEpisodes()
                return true
            }
            R.id.settings -> {
                startActivity(Intent(this.activity, SettingsActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun search() {
        startActivity(Intent(this.activity, SearchActivity::class.java))
    }

    private fun syncShows() {
        Toast.makeText(activity, "syncing shows...", Toast.LENGTH_SHORT).show()
        homeViewModel.syncShows()
    }

    private fun syncNextEpisodes() {
        activity?.let {
            SyncService.syncNextEpisodes(it.applicationContext)
        }
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

    override fun onItemClick(nextEpisode: NextEpisodeItem) {
        val entry = nextEpisode.entry
        val activity = activity
        if (entry != null && activity != null) {
            Episode.start(activity, entry.showId, entry.season, entry.number)
        }
    }

    private fun updateState(state: HomeViewState) {
        Timber.d("view state: $state")
        when (state) {
            DisplayTrendingLoader -> cardsAdapter.addCard(DiscoverCardItem(getString(R.string.trending_shows), emptyList(),
                    CardItem.TRENDING_CARD_TYPE, CardItem.PRIORITY_TRENDING))
            DisplayPopularLoader -> cardsAdapter.addCard(DiscoverCardItem(getString(R.string.popular_shows), emptyList(),
                    CardItem.POPULAR_CARD_TYPE, CardItem.PRIORITY_POPULAR))
            is TrendingState -> cardsAdapter.addCard(DiscoverCardItem(getString(R.string.trending_shows),
                    state.items, CardItem.TRENDING_CARD_TYPE, CardItem.PRIORITY_TRENDING))
            is PopularState -> cardsAdapter.addCard(DiscoverCardItem(getString(R.string.popular_shows),
                    state.items, CardItem.POPULAR_CARD_TYPE, CardItem.PRIORITY_POPULAR))
            is MyShowsState -> Timber.d("display shows for popular shows")
            is NextEpisodesState -> cardsAdapter.addCard(NextEpisodesCardItem(state.episodes, CardItem.NEXT_EPISODES_TYPE))
        }.exhaustive
    }
}
