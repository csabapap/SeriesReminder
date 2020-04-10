package hu.csabapap.seriesreminder.ui.search

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.transition.TransitionInflater
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.support.DaggerFragment
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.SRApplication
import hu.csabapap.seriesreminder.data.models.SrSearchResult
import hu.csabapap.seriesreminder.services.SyncService
import hu.csabapap.seriesreminder.tasks.DownloadShowTask
import hu.csabapap.seriesreminder.tasks.TaskExecutor
import hu.csabapap.seriesreminder.utils.AddShow
import hu.csabapap.seriesreminder.utils.Search
import hu.csabapap.seriesreminder.utils.ShowDetails
import hu.csabapap.seriesreminder.utils.hideKeyboard
import kotlinx.android.synthetic.main.fragment_search.*
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
                Observer<SearchState> { state ->
                    when(state) {
                        is SearchState.Loading -> displayLoader()
                        is SearchState.SearchResultLoaded -> displaySearchResult(state.result)
                    }
                })

        setupSearchView()
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
        val transition = TransitionInflater.from(activity)
                .inflateTransition(R.transition.auto)
        TransitionManager.beginDelayedTransition(content, transition)
        progress_bar.visibility = View.VISIBLE
    }

    private fun displaySearchResult(result: List<SrSearchResult>) {
        progress_bar.visibility = View.GONE
        val transition = TransitionInflater.from(activity)
                .inflateTransition(R.transition.auto)
        TransitionManager.beginDelayedTransition(content, transition)
        adapter.searchResult = result
        search_result.visibility = View.VISIBLE
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
}
