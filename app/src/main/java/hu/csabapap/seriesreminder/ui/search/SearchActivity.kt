package hu.csabapap.seriesreminder.ui.search

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.transition.TransitionInflater
import android.transition.TransitionManager
import android.view.View
import android.view.View.GONE
import android.view.inputmethod.EditorInfo
import android.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.support.DaggerAppCompatActivity
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.SRApplication
import hu.csabapap.seriesreminder.data.models.SrSearchResult
import hu.csabapap.seriesreminder.services.SyncService
import hu.csabapap.seriesreminder.tasks.DownloadShowTask
import hu.csabapap.seriesreminder.tasks.TaskExecutor
import hu.csabapap.seriesreminder.utils.*
import kotlinx.android.synthetic.main.activity_search.*
import javax.inject.Inject
import javax.inject.Named

class SearchActivity : DaggerAppCompatActivity(), SearchResultAdapter.SearchItemClickListener {

    @Inject
    @field:Named("SearchViewModelFactory")
    lateinit var viewModelProvider: ViewModelProvider.Factory

    @Inject
    lateinit var taskExecutor: TaskExecutor

    private lateinit var searchViewModel: SearchViewModel

    val adapter = SearchResultAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchViewModel = ViewModelProviders.of(this, viewModelProvider)
                .get(SearchViewModel::class.java)

        search_result.let {
            it.layoutManager = LinearLayoutManager(this)
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
        searchback.setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
//        searchViewModel.getSearchResult()
    }

    override fun onEnterAnimationComplete() {
        search_view.requestFocus()
        showKeyboard(search_view)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == Search.RC_ADD) {
            val showId = data?.extras?.getInt(AddShow.EXTRA_SHOW_ID, -1) ?: -1
            if (showId != -1) {
                adapter.itemAddedToCollection(showId)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun setupSearchView() {
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        search_view.apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            queryHint = getString(R.string.search_hint)
            imeOptions = EditorInfo.IME_ACTION_SEARCH
            setOnQueryTextListener(object: SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.apply {
                        adapter.clear()
                        search_result.visibility = GONE
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

    override fun onItemClick(showId: Int, inCollection: Boolean) {
        if (inCollection) {
            ShowDetails.start(this, showId)
            return
        }
        AddShow.startForResult(this, showId, Search.RC_ADD)
    }

    override fun onAddClick(showId: Int) {
        val task = DownloadShowTask(showId)
        (application as SRApplication).appComponent.inject(task)
        taskExecutor.queue.add(task)
        SyncService.syncShow(this)
    }

    private fun displayLoader() {
        val transition = TransitionInflater.from(this)
                .inflateTransition(R.transition.auto)
        TransitionManager.beginDelayedTransition(content, transition)
        progress_bar.visibility = View.VISIBLE
    }

    private fun displaySearchResult(result: List<SrSearchResult>) {
        progress_bar.visibility = View.GONE
        val transition = TransitionInflater.from(this)
                .inflateTransition(R.transition.auto)
        TransitionManager.beginDelayedTransition(content, transition)
        adapter.searchResult = result
        search_result.visibility = View.VISIBLE
    }
}
