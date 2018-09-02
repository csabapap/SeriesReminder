package hu.csabapap.seriesreminder.ui.search

import androidx.lifecycle.ViewModel
import hu.csabapap.seriesreminder.data.network.TraktApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class SearchViewModel(val api: TraktApi): ViewModel() {

    fun search(query: String) {
        val disposable = api.search("show", query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Timber.d("number of results: ${it.size}")
                }, {
                    Timber.e(it)
                })
    }
}