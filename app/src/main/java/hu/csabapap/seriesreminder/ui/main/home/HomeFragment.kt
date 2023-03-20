package hu.csabapap.seriesreminder.ui.main.home


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerFragment
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.data.db.relations.EpisodeWithShow
import hu.csabapap.seriesreminder.ui.adapters.DiscoverPreviewAdapter
import hu.csabapap.seriesreminder.ui.adapters.EpisodeCardsAdapter
import hu.csabapap.seriesreminder.ui.adapters.HomeCardsAdapter
import hu.csabapap.seriesreminder.utils.Collectible
import hu.csabapap.seriesreminder.utils.Episode
import kotlinx.android.synthetic.main.fragment_home.*
import javax.inject.Inject
import javax.inject.Named


class HomeFragment: DaggerFragment(),
        DiscoverPreviewAdapter.PreviewShowListener,
        HomeCardsAdapter.CardClickListener,
        EpisodeCardsAdapter.EpisodeClickListener {

    @field:[Inject Named("Main")]
    lateinit var mainViewModelProvider: ViewModelProvider.Factory
    private val homeViewModel: HomeViewModel by viewModels{ mainViewModelProvider }

    lateinit var layoutManager: LinearLayoutManager

    private var listener: HomeFragmentListener? = null

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
        return ComposeView(requireContext()).apply {
            setContent {
                HomeScreenUi(
                    viewModel = homeViewModel,
                    onShowItemClick = {
                        Collectible.start(requireContext(), it.traktId, it.inCollection)
                    },
                    onNextEpisodeClick = {
                        val activity = activity
                        if (activity != null) {
                            Episode.start(activity, it.showId, it.season, it.number)
                        }
                    },
                    setEpisodeAsWatched = { nextEpisode ->
                        homeViewModel.setEpisodeWatched(nextEpisode)
                        val message = String.format(getString(R.string.episode_number), nextEpisode.season, nextEpisode.number) + " set as watched"
                        view?.let {
                            Snackbar.make(it, message, Snackbar.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }
    }

//    @Deprecated("Deprecated in Java")
//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//
//        cardsAdapter.previewShowListener = this
//        cardsAdapter.episodesClickListener = this
//        cardsAdapter.nextEpisodesClickListener = object: NextEpisodesAdapter.NextEpisodeClickListener {
//            override fun onItemClick(nextEpisode: SRNextEpisode) {

//            }
//        }
//
//        homeViewModel.upcomingEpisodesLiveData.observe(viewLifecycleOwner, Observer {
//            it?.apply {
//                cardsAdapter.addCard(UpcomingEpisodeCardItem(it, CardItem.UPCOMING_EPISODE_TYPE))
//            }
//        })
//    }

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
}
