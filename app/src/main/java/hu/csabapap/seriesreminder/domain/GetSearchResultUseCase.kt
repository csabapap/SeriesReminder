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
        val ids = searchResult.map { it.show.ids.trakt }
        val collectionIds = collectionRepository.getItemsFromCollection(ids)
        return searchResult.map { searchItem ->
            val inCollection = collectionIds?.contains(searchItem.show.ids.trakt) ?: false
            SrSearchResult(searchItem.show, inCollection)
        }
    }

    fun clearLastSearchResults() {
        searchRepository.clearCache()
    }
}