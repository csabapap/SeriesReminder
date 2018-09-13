package hu.csabapap.seriesreminder.ui.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import hu.csabapap.seriesreminder.data.CollectionRepository
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.data.db.entities.CollectionEntry
import hu.csabapap.seriesreminder.data.models.SrSearchResult
import hu.csabapap.seriesreminder.data.network.TraktApi
import hu.csabapap.seriesreminder.utils.AppRxSchedulers
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class SearchViewModel(private val api: TraktApi,
                      private val showsRepository: ShowsRepository,
                      private val collectionRepository: CollectionRepository,
                      private val schedulers: AppRxSchedulers)
    : ViewModel() {

    val searchState = MutableLiveData<SearchState>()
    private val disposables = CompositeDisposable()

    fun search(query: String) {
        searchState.value = SearchState.Loading
        val disposable = api.search("show", query)
                .flatMap { searchResult ->
                    val ids = mutableListOf<Int>()
                    searchResult.forEach {
                        ids.add(it.show.ids.trakt)
                    }
                    collectionRepository.getItemsFromCollection(ids)
                            .flatMap {
                                val srSearchResult = mutableListOf<SrSearchResult>()
                                searchResult.forEach { item ->
                                    srSearchResult.add(SrSearchResult(item.show, it.contains(item.show.ids.trakt)))
                                }
                                Single.just(srSearchResult)
                            }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    searchState.value = SearchState.SearchResultLoaded(it)
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
                .subscribeOn(schedulers.database)
                .observeOn(schedulers.main)
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