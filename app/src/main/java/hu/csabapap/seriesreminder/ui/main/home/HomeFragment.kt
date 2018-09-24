package hu.csabapap.seriesreminder.ui.main.home


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.android.support.DaggerFragment
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.services.SyncService
import hu.csabapap.seriesreminder.ui.adapters.DiscoverPreviewAdapter
import hu.csabapap.seriesreminder.ui.adapters.HomeCardsAdapter
import hu.csabapap.seriesreminder.ui.adapters.items.CardItem
import hu.csabapap.seriesreminder.ui.adapters.items.DiscoverCardItem
import hu.csabapap.seriesreminder.ui.adapters.items.UpcomingEpisodeCardItem
import hu.csabapap.seriesreminder.ui.addshow.AddShowActivity
import hu.csabapap.seriesreminder.ui.search.SearchActivity
import kotlinx.android.synthetic.main.fragment_home.*
import javax.inject.Inject
import javax.inject.Named


class HomeFragment : DaggerFragment(), DiscoverPreviewAdapter.PreviewShowListener,
        HomeCardsAdapter.CardClickListener {

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

        homeViewModel.viewState.observe(this, Observer {
            it?.apply {
                render(it)
            }
        })

        homeViewModel.trendingShows.observe(this, Observer {
            it?.apply {
                cardsAdapter.addCard(DiscoverCardItem(getString(R.string.trending_shows), it, CardItem.TRENDING_CARD_TYPE,  CardItem.PRIORITY_MEDIUM))
            }
        })

        homeViewModel.popularShowsLiveData.observe(this, Observer {
            it?.apply {
                cardsAdapter.addCard(DiscoverCardItem(getString(R.string.popular_shows), it, CardItem.POPULAR_CARD_TYPE))
            }
        })

        homeViewModel.upcomingEpisodesLiveData.observe(this, Observer {
            it?.apply {
                cardsAdapter.addCard(UpcomingEpisodeCardItem(it, CardItem.UPCOMING_EPISODE_TYPE))
            }
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
        homeViewModel.getPopularShows()
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
        }
        return super.onOptionsItemSelected(item)
    }

    private fun render(state: HomeViewState) {
        when (state.displayProgressBar) {
            true -> progress_bar.visibility = View.VISIBLE
            false -> progress_bar.visibility = View.GONE
        }
    }

    private fun search() {
        startActivity(Intent(this.activity, SearchActivity::class.java))
    }

    private fun syncShows() {
        activity?.let {
            SyncService.syncMyShows(it.applicationContext)
        }
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
        val intent = Intent(activity, AddShowActivity::class.java)
        intent.putExtras(bundleOf("show_id" to traktId))
        activity?.startActivity(intent)
    }

    override fun onMoreButtonClick(type: Int) {
        listener?.onMoreButtonClick(type)
    }
}
