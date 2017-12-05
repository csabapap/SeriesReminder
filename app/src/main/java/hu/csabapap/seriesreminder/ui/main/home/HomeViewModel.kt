package hu.csabapap.seriesreminder.ui.main.home

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import android.view.View
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.data.db.entities.SRShow
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class HomeViewModel @Inject constructor(private val showsRepository: ShowsRepository) : ViewModel() {

    val compositeDisposable = CompositeDisposable()

    val trendingShowsLiveData = MutableLiveData<List<SRShow>>()

    fun getTrendingShows() {
         val disposable = showsRepository.getTrendingShows()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    trendingShowsLiveData.value = it
                },{})

        compositeDisposable.add(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}