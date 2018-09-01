package hu.csabapap.seriesreminder.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

@Suppress( "UNCHECKED_CAST")
class SearchViewModelProvider @Inject constructor(): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass != SearchViewModel::class.java) {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
        return SearchViewModel() as T
    }
}