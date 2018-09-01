package hu.csabapap.seriesreminder.ui.search

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import dagger.android.support.DaggerAppCompatActivity
import hu.csabapap.seriesreminder.R
import javax.inject.Inject
import javax.inject.Named

class SearchActivity : DaggerAppCompatActivity() {

    @Inject @field:Named("SearchViewModelFactory")
    lateinit var viewModelProvider: ViewModelProvider.Factory

    private lateinit var searchViewModel: SearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchViewModel = ViewModelProviders.of(this, viewModelProvider)
                .get(SearchViewModel::class.java)
    }
}
