package hu.csabapap.seriesreminder.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import hu.csabapap.seriesreminder.data.network.TraktApi
import javax.inject.Inject

@Suppress( "UNCHECKED_CAST")
class SearchViewModelProvider @Inject constructor(private val api: TraktApi)
    : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass != SearchViewModel::class.java) {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
        return SearchViewModel(api) as T
    }
}