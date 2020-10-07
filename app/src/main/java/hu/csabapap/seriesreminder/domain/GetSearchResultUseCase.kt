package hu.csabapap.seriesreminder.domain

import hu.csabapap.seriesreminder.data.CollectionRepository
import hu.csabapap.seriesreminder.data.models.SrSearchResult
import hu.csabapap.seriesreminder.data.repositories.search.SearchRepository
import javax.inject.Inject

class GetSearchResultUseCase @Inject constructor(private val searchRepository: SearchRepository,
                                                 private val collectionRepository: CollectionRepository) {

    suspend fun search(query: String): List<SrSearchResult> {
        val searchResult = searchRepository.search(query)
        if (searchResult.isEmpty()) {
            return emptyList()
        }
        val ids = searchResult.mapNotNull { it.show?.ids?.trakt }
        val collectionIds = collectionRepository.getItemsFromCollection(ids)
        return searchResult.map { searchItem ->
            val show = searchItem.show ?: return@map null
            val inCollection = collectionIds?.contains(show.ids?.trakt ?: -1) ?: false
            SrSearchResult(show, inCollection)
        }
                .filterNotNull()
    }

    fun clearLastSearchResults() {
        searchRepository.clearCache()
    }
}