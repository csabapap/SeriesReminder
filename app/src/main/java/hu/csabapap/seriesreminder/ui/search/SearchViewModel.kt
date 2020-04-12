package hu.csabapap.seriesreminder.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import hu.csabapap.seriesreminder.data.db.PopularShowsResult
import hu.csabapap.seriesreminder.data.db.TrendingShowsResult
import hu.csabapap.seriesreminder.data.db.entities.PopularGridItem
import hu.csabapap.seriesreminder.data.db.entities.TrendingGridItem
import hu.csabapap.seriesreminder.data.repositories.popularshows.PopularShowsRepository
import hu.csabapap.seriesreminder.data.repositories.trendingshows.TrendingShowsRepository
import hu.csabapap.seriesreminder.domain.GetSearchResultUseCase
import hu.csabapap.seriesreminder.utils.AppCoroutineDispatchers
import hu.csabapap.seriesreminder.utils.RxSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class SearchViewModel(private val getSearchResultUseCase: GetSearchResultUseCase,
                      private val trendingShowsRepository: TrendingShowsRepository,
                      private val popularShowsRepository: PopularShowsRepository,
                      private val schedulers: RxSchedulers,
                      private val dispatchers: AppCoroutineDispatchers)
    : ViewModel() {

    private val job = Job()
    private val scope = CoroutineScope(dispatchers.main + job)

    val searchState = MutableLiveData<SearchState>()
    private val loadTrendingShows = MutableLiveData<Boolean>()
    private val loadPopularShows = MutableLiveData<Boolean>()
    private val disposables = CompositeDisposable()

    fun getSearchResult() {
        scope.launch(context = dispatchers.io) {
            val searchResult = getSearchResultUseCase.getLastResult()
            withContext(dispatchers.main) {
                if (searchResult.isEmpty().not()) {
                    searchState.value = SearchState.SearchResultLoaded(searchResult)
                }
            }
        }
    }

    fun search(query: String) {
        searchState.value = SearchState.HideDiscoverContent
        searchState.value = SearchState.Loading
        val disposable = getSearchResultUseCase.search(query)
                .subscribeOn(schedulers.io())
                .observeOn(schedulers.ui())
                .subscribe({
                    if (it.isEmpty()) {
                        searchState.value = SearchState.NoResult
                    } else {
                        searchState.value = SearchState.SearchResultLoaded(it)
                    }
                }, {
                    Timber.e(it)
                })
        disposables.add(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        getSearchResultUseCase.clearLastSearchResults()
        disposables.clear()
        if (job.isActive) {
            job.cancel()
        }
    }

    private val trendingShowsResult: LiveData<TrendingShowsResult> = Transformations.map(loadTrendingShows) {
        trendingShowsRepository.getTrendingShows(60)
    }

    private val popularShowsResult: LiveData<PopularShowsResult> = Transformations.map(loadPopularShows) {
        popularShowsRepository.getPopularShows(60)
    }

    val trendingShows: LiveData<PagedList<TrendingGridItem>> = Transformations.switchMap(trendingShowsResult) {
        it.data
    }

    val popularShows: LiveData<PagedList<PopularGridItem>> = Transformations.switchMap(popularShowsResult) {
        it.data
    }

    private fun loadTrendingShows() {
        loadTrendingShows.value = true
    }

    private fun loadPopularShows() {
        loadPopularShows.value = true
    }
}