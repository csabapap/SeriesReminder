package hu.csabapap.seriesreminder.ui.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import hu.csabapap.seriesreminder.data.network.TraktApi
import hu.csabapap.seriesreminder.data.network.entities.BaseShow
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class SearchViewModel(val api: TraktApi): ViewModel() {

    val searchResult = MutableLiveData<List<BaseShow>>()
    private val disposables = CompositeDisposable()

    fun search(query: String) {
        val disposable = api.search("show", query)
                .toFlowable()
                .flatMapIterable { it }
                .map { it.show }
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Timber.d("number of results: ${it.size}")
                    searchResult.value = it
                }, {
                    Timber.e(it)
                })
        disposables.add(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}