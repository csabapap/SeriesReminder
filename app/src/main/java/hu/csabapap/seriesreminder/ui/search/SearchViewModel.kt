package hu.csabapap.seriesreminder.ui.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import hu.csabapap.seriesreminder.domain.GetSearchResultUseCase
import hu.csabapap.seriesreminder.utils.AppCoroutineDispatchers
import hu.csabapap.seriesreminder.utils.RxSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import timber.log.Timber

class SearchViewModel(private val getSearchResultUseCase: GetSearchResultUseCase,
                      private val schedulers: RxSchedulers,
                      private val dispatchers: AppCoroutineDispatchers)
    : ViewModel() {

    private val job = Job()
    private val scope = CoroutineScope(dispatchers.main + job)

    val searchState = MutableLiveData<SearchState>()
    private val disposables = CompositeDisposable()

    fun search(query: String) {
        searchState.value = SearchState.HideDiscoverContent
        searchState.value = SearchState.Loading
        val disposable = getSearchResultUseCase.search(query)
                .subscribeOn(schedulers.io())
                .observeOn(schedulers.ui())
                .subscribe({
                    try {
                        if (it.isEmpty()) {
                            searchState.value = SearchState.NoResult
                        } else {
                            searchState.value = SearchState.SearchResultLoaded(it)
                        }
                    } catch (e: Exception) {
                        Timber.e(e)
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
}