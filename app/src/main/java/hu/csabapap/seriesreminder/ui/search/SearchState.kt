package hu.csabapap.seriesreminder.ui.search

import hu.csabapap.seriesreminder.data.models.SrSearchResult

sealed class SearchState {
    object Loading: SearchState()
    object NoResult: SearchState()
    class SearchResultLoaded(val result: List<SrSearchResult>): SearchState()

}