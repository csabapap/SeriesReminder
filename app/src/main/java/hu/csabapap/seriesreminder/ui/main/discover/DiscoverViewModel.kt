package hu.csabapap.seriesreminder.ui.main.discover

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
import hu.csabapap.seriesreminder.ui.search.SearchFragment
import javax.inject.Inject

class DiscoverViewModel @Inject constructor(
        private val trendingShowsRepository: TrendingShowsRepository,
        private val popularShowsRepository: PopularShowsRepository)
    : ViewModel() {
    private val loadTrendingShows = MutableLiveData<Boolean>()
    private val loadPopularShows = MutableLiveData<Boolean>()

    private val trendingShowsResult: LiveData<TrendingShowsResult> = Transformations.map(loadTrendingShows) {
        trendingShowsRepository.getPaginatedTrendingShows(60)
    }

    private val popularShowsResult: LiveData<PopularShowsResult> = Transformations.map(loadPopularShows) {
        popularShowsRepository.getPopularShows(60)
    }

    val trendingShows: LiveData<PagedList<TrendingGridItem>> = Transformations.switchMap(trendingShowsResult) {
        it.data.value
        it.data
    }

    val popularShows: LiveData<PagedList<PopularGridItem>> = Transformations.switchMap(popularShowsResult) {
        it.data
    }

    fun getItems(type: Int) {
        when (type) {
            SearchFragment.TYPE_TRENDING -> loadTrendingShows()
            SearchFragment.TYPE_POPULAR -> loadPopularShows()
        }
    }

    private fun loadTrendingShows() {
        loadTrendingShows.value = true
    }

    private fun loadPopularShows() {
        loadPopularShows.value = true
    }
}