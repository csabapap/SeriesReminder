package hu.csabapap.seriesreminder.ui.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import hu.csabapap.seriesreminder.data.CollectionRepository
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.data.db.entities.CollectionEntry
import hu.csabapap.seriesreminder.domain.GetSearchResultUseCase
import hu.csabapap.seriesreminder.utils.AppCoroutineDispatchers
import hu.csabapap.seriesreminder.utils.RxSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.OffsetDateTime
import timber.log.Timber

class SearchViewModel(private val getSearchResultUseCase: GetSearchResultUseCase,
                      private val showsRepository: ShowsRepository,
                      private val collectionRepository: CollectionRepository,
                      private val schedulers: RxSchedulers,
                      private val dispatchers: AppCoroutineDispatchers)
    : ViewModel() {

    private val job = Job()
    private val scope = CoroutineScope(dispatchers.main + job)

    val searchState = MutableLiveData<SearchState>()
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

    fun addShowToCollection(showId: Int) {
        val disposable = showsRepository.getShow(showId)
                .doOnSuccess{showsRepository.insertShow(it)}
                .flatMap {
                    collectionRepository.addToCollection(CollectionEntry(showId = showId, added = OffsetDateTime.now()))
                            .toMaybe<Boolean>()
                }
                .subscribeOn(schedulers.io())
                .observeOn(schedulers.ui())
                .subscribe({
                    Timber.d("show added to collection")
                }, Timber::e)
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