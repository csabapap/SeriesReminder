package hu.csabapap.seriesreminder.ui.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import hu.csabapap.seriesreminder.domain.GetSearchResultUseCase
import hu.csabapap.seriesreminder.utils.AppCoroutineDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel(private val getSearchResultUseCase: GetSearchResultUseCase,
                      private val dispatchers: AppCoroutineDispatchers)
    : ViewModel() {

    private val job = Job()
    private val scope = CoroutineScope(dispatchers.main + job)

    val searchState = MutableLiveData<SearchState>()

    fun search(query: String) {
        searchState.value = SearchState.HideDiscoverContent
        searchState.value = SearchState.Loading

        scope.launch(dispatchers.io) {
            val searchResult = getSearchResultUseCase.search(query)
            withContext(dispatchers.main) {
                if (searchResult.isEmpty()) {
                    searchState.value = SearchState.NoResult
                } else {
                    searchState.value = SearchState.SearchResultLoaded(searchResult)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        getSearchResultUseCase.clearLastSearchResults()
        if (job.isActive) {
            job.cancel()
        }
    }
}