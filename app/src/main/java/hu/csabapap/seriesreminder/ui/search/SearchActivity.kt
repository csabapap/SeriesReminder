package hu.csabapap.seriesreminder.ui.search

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import dagger.android.support.DaggerAppCompatActivity
import hu.csabapap.seriesreminder.R
import kotlinx.android.synthetic.main.activity_search.*
import javax.inject.Inject
import javax.inject.Named

class SearchActivity : DaggerAppCompatActivity() {

    @Inject
    @field:Named("SearchViewModelFactory")
    lateinit var viewModelProvider: ViewModelProvider.Factory

    private lateinit var searchViewModel: SearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchViewModel = ViewModelProviders.of(this, viewModelProvider)
                .get(SearchViewModel::class.java)

        setupSearchView()
        searchback.setOnClickListener { finish() }
    }

    override fun onStart() {
        super.onStart()
        searchViewModel.search("shield")
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
                    }
                    return true
                }

                override fun onQueryTextChange(query: String?): Boolean {
                    return true
                }

            })
        }
    }
}
