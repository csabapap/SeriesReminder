package hu.csabapap.seriesreminder.ui.main.discover


import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.android.support.DaggerFragment
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.data.db.entities.GridItem
import hu.csabapap.seriesreminder.data.db.entities.Item
import hu.csabapap.seriesreminder.ui.adapters.GridAdapter
import hu.csabapap.seriesreminder.ui.addshow.AddShowActivity
import hu.csabapap.seriesreminder.ui.showdetails.ShowDetailsActivity
import hu.csabapap.seriesreminder.utils.ShowDetails
import hu.csabapap.seriesreminder.utils.SpacingItemDecorator
import kotlinx.android.synthetic.main.fragment_grid.*
import javax.inject.Inject
import javax.inject.Named

class GridFragment : DaggerFragment(), GridAdapter.GridItemClickListener {

    @field:[Inject Named("Main")]
    lateinit var mainViewModelProvider: ViewModelProvider.Factory
    private lateinit var discoverViewModel: DiscoverViewModel

    private var listType = TYPE_TRENDING

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        discoverViewModel = ViewModelProviders.of(this, mainViewModelProvider)
                .get(DiscoverViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_grid, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = GridAdapter()
        adapter.listener = this
        val gridLayoutManager = GridLayoutManager(activity, 4) as RecyclerView.LayoutManager
        rv_grid.layoutManager = gridLayoutManager
        rv_grid.addItemDecoration(SpacingItemDecorator(4, 4))
        rv_grid.adapter = adapter
        arguments?.let {
            listType= it.getInt(ARG_DISCOVER_TYPE, TYPE_TRENDING)
        }

        if (listType == TYPE_TRENDING) {
            discoverViewModel.trendingShows.observe(this, Observer {
                it?.apply {
                    adapter.submitList(it as PagedList<GridItem<Item>>)
                }
            })
        } else {
            discoverViewModel.popularShows.observe(this, Observer {
                it?.apply {
                    adapter.submitList(it as PagedList<GridItem<Item>>)
                }
            })
        }
    }

    override fun onStart() {
        super.onStart()

        discoverViewModel.getItems(listType)
    }

    override fun onItemClick(traktId: Int, inCollection: Boolean) {
        if (inCollection) {
            val intent = Intent(activity, ShowDetailsActivity::class.java)
            intent.putExtra(ShowDetails.EXTRA_SHOW_ID, traktId)
            activity?.startActivity(intent)
        } else {
            val intent = Intent(activity, AddShowActivity::class.java)
            intent.putExtra("show_id", traktId)
            activity?.startActivity(intent)
        }
    }

    companion object {

        const val TYPE_TRENDING = 1
        const val TYPE_POPULAR = 2

        private const val ARG_DISCOVER_TYPE = "discover_type"

        fun newInstance(type: Int): GridFragment {
            val fragment = GridFragment()
            fragment.arguments = bundleOf(ARG_DISCOVER_TYPE to type)
            return fragment
        }
    }
}
