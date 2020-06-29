package hu.csabapap.seriesreminder.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import hu.csabapap.seriesreminder.data.repositories.popularshows.PopularShowsRepository
import hu.csabapap.seriesreminder.data.repositories.trendingshows.TrendingShowsRepository
import hu.csabapap.seriesreminder.domain.GetSearchResultUseCase
import hu.csabapap.seriesreminder.utils.AppCoroutineDispatchers
import hu.csabapap.seriesreminder.utils.RxSchedulers
import javax.inject.Inject

class SearchViewModelProvider @Inject constructor(
        private val getSearchResultUseCase: GetSearchResultUseCase,
        private val dispatchers: AppCoroutineDispatchers)
    : ViewModelProvider.NewInstanceFactory() {

    @Suppress( "UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass != SearchViewModel::class.java) {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
        return SearchViewModel(getSearchResultUseCase, dispatchers) as T
    }
}