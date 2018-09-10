package hu.csabapap.seriesreminder.ui.search

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.support.DaggerAppCompatActivity
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.data.models.SrSearchResult
import hu.csabapap.seriesreminder.data.network.entities.BaseShow
import hu.csabapap.seriesreminder.services.SyncService
import hu.csabapap.seriesreminder.utils.hideKeyboard
import kotlinx.android.synthetic.main.activity_search.*
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
        searchViewModel.searchResult.observe(this,
                Observer<List<SrSearchResult>> { results ->
                    adapter.searchResult = results
                })

        setupSearchView()
        searchback.setOnClickListener { finish() }
    }

    private fun setupSearchView() {
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        search_view.apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            queryHint = getString(R.string.search_hint)
            imeOptions = EditorInfo.IME_ACTION_SEARCH
            requestFocus()
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

    override fun onItemClick(showId: Int) {

    }

    override fun onAddClick(showId: Int) {
        searchViewModel.addShowToCollection(showId)
        SyncService.syncShow(this, showId)
    }
}
