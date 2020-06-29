package hu.csabapap.seriesreminder.data.repositories.search

import hu.csabapap.seriesreminder.data.network.entities.SearchResult
import hu.csabapap.seriesreminder.data.network.services.SearchService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteSearchDataSource @Inject constructor(private val searchService: SearchService) {

    val cache: MutableList<SearchResult> = mutableListOf()

    suspend fun search(query: String): List<SearchResult> {
        return searchService.search(query, "show").also {
            cache.clear()
            cache.addAll(it)
        }
    }

    fun clearCache() {
        cache.clear()
    }

}