package hu.csabapap.seriesreminder.ui.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import hu.csabapap.seriesreminder.data.CollectionRepository
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.data.db.entities.CollectionEntry
import hu.csabapap.seriesreminder.data.models.SrSearchResult
import hu.csabapap.seriesreminder.data.network.TraktApi
import hu.csabapap.seriesreminder.domain.GetSearchResultUseCase
import hu.csabapap.seriesreminder.utils.RxSchedulers
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class SearchViewModel(private val getSearchResultUseCase: GetSearchResultUseCase,
                      private val showsRepository: ShowsRepository,
                      private val collectionRepository: CollectionRepository,
                      private val schedulers: RxSchedulers)
    : ViewModel() {

    val searchState = MutableLiveData<SearchState>()
    private val disposables = CompositeDisposable()

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
                    collectionRepository.addToCollection(CollectionEntry(showId = showId))
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
        disposables.clear()
    }
}