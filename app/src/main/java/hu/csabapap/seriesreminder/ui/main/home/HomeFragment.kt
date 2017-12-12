package hu.csabapap.seriesreminder.ui.main.home


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.support.DaggerFragment
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.ui.adapters.PopularShowsAdapter
import hu.csabapap.seriesreminder.ui.adapters.TrendingShowsAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import javax.inject.Inject


class HomeFragment : DaggerFragment() {

    @Inject
    lateinit var mainViewModelProvider: ViewModelProvider.Factory
    private lateinit var homeViewModel: HomeViewModel

    lateinit var layoutManager: LinearLayoutManager
    private val trendingShowsAdapter = TrendingShowsAdapter()
    private val popularShowsAdapter = PopularShowsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeViewModel = ViewModelProviders.of(this, mainViewModelProvider)
                .get(HomeViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        homeViewModel.viewState.observe(this, Observer {
            it?.apply {
                render(it)
            }
        })

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        LinearSnapHelper().attachToRecyclerView(trending_grid)

        layoutManager = trending_grid.layoutManager as LinearLayoutManager
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL

        var itemDecorator = DividerItemDecoration(this.activity, layoutManager.orientation)
        itemDecorator.setDrawable(ContextCompat.getDrawable(this.activity!!, R.drawable.horizontal_divider)!!)

        trending_grid.apply {
            adapter = trendingShowsAdapter
            addItemDecoration(itemDecorator)
        }

        LinearSnapHelper().attachToRecyclerView(popular_grid)

        val popularShowsLayoutManager = popular_grid.layoutManager as LinearLayoutManager
        popularShowsLayoutManager.orientation = LinearLayoutManager.HORIZONTAL

        itemDecorator = DividerItemDecoration(this.activity, popularShowsLayoutManager.orientation)
        itemDecorator.setDrawable(ContextCompat.getDrawable(this.activity!!, R.drawable.horizontal_divider)!!)

        popular_grid.apply {
            adapter = popularShowsAdapter
            addItemDecoration(itemDecorator)
        }

    }

    override fun onStart() {
        super.onStart()
        homeViewModel.getTrendingShows()
        homeViewModel.getPopularShows()
    }

    private fun render(state: HomeViewState) {
        when (state.displayProgressBar) {
            true -> progress_bar.visibility = View.VISIBLE
            false -> progress_bar.visibility = View.GONE
        }

        when (state.displayPopularCard) {
            true -> popular_card.visibility = View.VISIBLE
            false -> popular_card.visibility = View.GONE
        }

        when (state.displayTrendingCard) {
            true -> trending_card.visibility = View.VISIBLE
            false -> trending_card.visibility = View.GONE
        }
    }

}
