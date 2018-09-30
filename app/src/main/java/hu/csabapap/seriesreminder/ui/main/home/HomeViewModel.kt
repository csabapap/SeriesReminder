package hu.csabapap.seriesreminder.ui.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import hu.csabapap.seriesreminder.data.CollectionRepository
import hu.csabapap.seriesreminder.data.EpisodesRepository
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.data.db.entities.NextEpisodeItem
import hu.csabapap.seriesreminder.data.repositories.popularshows.PopularShowsRepository
import hu.csabapap.seriesreminder.data.repositories.trendingshows.TrendingShowsRepository
import hu.csabapap.seriesreminder.ui.adapters.items.ShowItem
import hu.csabapap.seriesreminder.utils.AppRxSchedulers
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import javax.inject.Inject

class HomeViewModel @Inject constructor(private val showsRepository: ShowsRepository,
                                        trendingShowsRepository: TrendingShowsRepository,
                                        popularShowsRepository: PopularShowsRepository,
                                        collectionRepository: CollectionRepository,
                                        private val episodesRepository: EpisodesRepository,
                                        private val rxSchedulers: AppRxSchedulers)
    : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    val upcomingEpisodesLiveData = MutableLiveData<List<NextEpisodeItem>>()

    val trendingShows: LiveData<List<ShowItem>> = Transformations.map(trendingShowsRepository.getTrendingShows().data) { result ->
        result.map {
            ShowItem(it.show!!.traktId,
                    it.show!!.tvdbId,
                    it.show!!.title,
                    it.show!!.posterThumb,
                    it.inCollection)
        }
    }

    val popularShows: LiveData<List<ShowItem>> = Transformations.map(popularShowsRepository.getPopularShows().data) { result ->
        result.map {
            ShowItem(it.show!!.traktId,
                    it.show!!.tvdbId,
                    it.show!!.title,
                    it.show!!.posterThumb,
                    it.inCollection)
        }
    }

    val myShowsLiveData: LiveData<List<ShowItem>> = Transformations.map(collectionRepository.getCollectionGridItems()) {
        result -> result.map {
            ShowItem(it.show!!.traktId,
                    it.show!!.tvdbId,
                    it.show!!.title,
                    it.show!!.posterThumb,
                    it.show!!.inCollection)
        }
    }

    fun getNextEpisodes() {
        val disposable = episodesRepository.getNextEpisodes(3)
                .subscribeOn(rxSchedulers.io)
                .observeOn(rxSchedulers.main)
                .subscribe( { nextEpisodes ->
                    if (nextEpisodes.isEmpty().not()) {
                        upcomingEpisodesLiveData.value = nextEpisodes
                    }
                }, {Timber.e(it)})
        compositeDisposable.add(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}