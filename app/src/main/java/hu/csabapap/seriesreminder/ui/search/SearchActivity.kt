package hu.csabapap.seriesreminder.ui.search

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.transition.TransitionInflater
import android.transition.TransitionManager
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.support.DaggerAppCompatActivity
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.data.models.SrSearchResult
import hu.csabapap.seriesreminder.services.SyncService
import hu.csabapap.seriesreminder.ui.addshow.AddShowActivity
import hu.csabapap.seriesreminder.ui.showdetails.ShowDetailsActivity
import hu.csabapap.seriesreminder.utils.AddShow
import hu.csabapap.seriesreminder.utils.ShowDetails
import hu.csabapap.seriesreminder.utils.hideKeyboard
import hu.csabapap.seriesreminder.utils.showKeyboard
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class SearchActivity : DaggerAppCompatActivity(), SearchResultAdapter.SearchItemClickListener {

    @Inject
    @field:Named("SearchViewModelFactory")
    lateinit var viewModelProvider: ViewModelProvider.Factory

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
        searchViewModel.getSearchResult()
    }

    override fun onEnterAnimationComplete() {
        search_view.requestFocus()
        showKeyboard(search_view)
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
            val intent = Intent(this, ShowDetailsActivity::class.java)
            intent.putExtra(ShowDetails.EXTRA_SHOW_ID, showId)
            startActivity(intent)
            return
        }

        val intent = Intent(this, AddShowActivity::class.java)
        intent.putExtra(AddShow.EXTRA_SHOW_ID, showId)
        startActivity(intent)
        return
    }

    override fun onAddClick(showId: Int) {
        searchViewModel.addShowToCollection(showId)
        SyncService.syncShow(this, showId)
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
