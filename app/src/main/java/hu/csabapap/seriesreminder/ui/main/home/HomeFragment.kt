package hu.csabapap.seriesreminder.ui.main.home


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.support.DaggerFragment
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.ui.adapters.TrendingShowsAdapter
import kotlinx.android.synthetic.main.fragment_home.*
import javax.inject.Inject


class HomeFragment : DaggerFragment() {

    @Inject
    lateinit var mainViewModelProvider: ViewModelProvider.Factory
    private lateinit var homeViewModel: HomeViewModel

    lateinit var layoutManager: LinearLayoutManager
    private val trendingShowsAdapter = TrendingShowsAdapter()
    private val popularShowsAdapter = TrendingShowsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeViewModel = ViewModelProviders.of(this, mainViewModelProvider)
                .get(HomeViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        homeViewModel.trendingShowsLiveData.observe(this, Observer {
            it?.apply {
                trendingShowsAdapter.shows = it
            }
        })

        homeViewModel.popularShowsLiveData.observe(this, Observer {
            it?.apply {
                popularShowsAdapter.shows = it
            }
        })
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutManager = shows_grid.layoutManager as LinearLayoutManager
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL

        shows_grid.apply {
            adapter = trendingShowsAdapter
        }

        val popularShowsLayoutManager = popular_shows_grid.layoutManager as LinearLayoutManager
        popularShowsLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        popular_shows_grid.apply {
            adapter = popularShowsAdapter
        }

    }

    override fun onStart() {
        super.onStart()
        homeViewModel.getTrendingShows()
        homeViewModel.getPopularShows()
    }

}
