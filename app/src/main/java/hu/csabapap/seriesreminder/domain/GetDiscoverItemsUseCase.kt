package hu.csabapap.seriesreminder.domain

import hu.csabapap.seriesreminder.data.CollectionRepository
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.data.models.SrSearchResult
import hu.csabapap.seriesreminder.ui.adapters.items.ShowItem
import io.reactivex.Single

class GetDiscoverItemsUseCase(
        private val showsRepository: ShowsRepository,
        private val collectionRepository: CollectionRepository) {

    fun getTrendingShowItems(): Single<List<ShowItem>> {
        TODO("return trending show items")
    }

    fun getPopularShowItems(): Single<List<ShowItem>> {
        TODO("return popular show items")
    }
}