package hu.csabapap.seriesreminder.ui.search

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.support.DaggerFragment
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.SRApplication
import hu.csabapap.seriesreminder.data.models.SrSearchResult
import hu.csabapap.seriesreminder.extensions.exhaustive
import hu.csabapap.seriesreminder.services.SyncService
import hu.csabapap.seriesreminder.tasks.DownloadShowTask
import hu.csabapap.seriesreminder.tasks.TaskExecutor
import hu.csabapap.seriesreminder.ui.main.discover.GridFragment
import hu.csabapap.seriesreminder.utils.AddShow
import hu.csabapap.seriesreminder.utils.Search
import hu.csabapap.seriesreminder.utils.ShowDetails
import hu.csabapap.seriesreminder.utils.hideKeyboard
import kotlinx.android.synthetic.main.fragment_search.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class SearchFragment : DaggerFragment(), SearchResultAdapter.SearchItemClickListener {

    @Inject
    @field:Named("SearchViewModelFactory")
    lateinit var viewModelProvider: ViewModelProvider.Factory

    @Inject
    lateinit var taskExecutor: TaskExecutor

    private lateinit var searchViewModel: SearchViewModel

    val adapter = SearchResultAdapter()

    var type: Int = TYPE_TRENDING

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            type = getInt(ARG_DISCOVER_TYPE, TYPE_TRENDING)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchViewModel = ViewModelProviders.of(this, viewModelProvider)
                .get(SearchViewModel::class.java)

        search_result.let {
            it.layoutManager = LinearLayoutManager(context)
            adapter.listener = this
            it.adapter = adapter
        }
        searchViewModel.searchState.observe(this,
                Observer { state ->
                    when(state) {
                        is SearchState.Loading -> displayLoader()
                        is SearchState.SearchResultLoaded -> displaySearchResult(state.result)
                        SearchState.NoResult -> Timber.d("no result")// do nothing
                        SearchState.HideDiscoverContent -> hideDiscoverContent()
                    }.exhaustive
                })

        setupSearchView()

        tab_layout.setupWithViewPager(view_pager)
        view_pager.adapter = DiscoverPagerAdapter(childFragmentManager)

        if (type == TYPE_POPULAR) {
            view_pager.currentItem = 1
        }
    }

    private fun setupSearchView() {
        val searchManager = activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        search_view.apply {
            setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))
            queryHint = getString(R.string.search_hint)
            imeOptions = EditorInfo.IME_ACTION_SEARCH
            setOnQueryTextListener(object: SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.apply {
                        adapter.clear()
                        search_result.visibility = View.GONE
                        searchViewModel.search(query)
                        hideKeyboard(search_view)
                    }
                    return true
                }

                override fun onQueryTextChange(query: String?): Boolean {
                    return true
                }
            })
        }
    }

    private fun displayLoader() {
        search_result_content.visibility = View.VISIBLE
        progress_bar.visibility = View.VISIBLE
    }

    private fun displaySearchResult(result: List<SrSearchResult>) {
        progress_bar.visibility = View.GONE
        adapter.searchResult = result
        search_result.visibility = View.VISIBLE
    }

    private fun hideDiscoverContent() {
        tab_layout.visibility = View.GONE
        view_pager.visibility = View.GONE
    }

    override fun onItemClick(showId: Int, inCollection: Boolean) {
        activity?.apply {
            if (inCollection) {
                ShowDetails.start(this, showId)
                return
            }
            AddShow.startForResult(this, showId, Search.RC_ADD)
        }
    }

    override fun onAddClick(showId: Int) {
        activity?.apply {
            val task = DownloadShowTask(showId)
            (application as SRApplication).appComponent.inject(task)
            taskExecutor.queue.add(task)
            SyncService.syncShow(this)
        }
    }

    class DiscoverPagerAdapter(fragmentManager: FragmentManager):
            FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> GridFragment.newInstance(GridFragment.TYPE_TRENDING)
                1 -> GridFragment.newInstance(GridFragment.TYPE_POPULAR)
                else -> throw IllegalArgumentException("invalid view pager position")
            }
        }

        override fun getCount() = 2

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> "Trending"
                1 -> "Popular"
                else -> ""
            }
        }
    }

    companion object {
        const val TYPE_TRENDING = 1
        const val TYPE_POPULAR = 2

        const val ARG_DISCOVER_TYPE = "discover_type"
    }
}
