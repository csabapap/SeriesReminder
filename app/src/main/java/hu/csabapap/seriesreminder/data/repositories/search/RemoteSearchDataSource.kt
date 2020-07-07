package hu.csabapap.seriesreminder.data.repositories.search

import hu.csabapap.seriesreminder.data.Result
import hu.csabapap.seriesreminder.data.network.entities.SearchResult
import hu.csabapap.seriesreminder.data.network.services.SearchService
import hu.csabapap.seriesreminder.utils.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteSearchDataSource @Inject constructor(private val searchService: SearchService) {

    val cache: MutableList<SearchResult> = mutableListOf()

    suspend fun search(query: String): Result<List<SearchResult>> = safeApiCall({
        val searchResult = searchService.search("show", query)
        return@safeApiCall Result.Success(searchResult)
    }, errorMessage = "error during search")


    fun clearCache() {
        cache.clear()
    }

}