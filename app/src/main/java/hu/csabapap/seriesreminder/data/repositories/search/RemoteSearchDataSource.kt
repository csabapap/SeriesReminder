package hu.csabapap.seriesreminder.data.repositories.search

import com.uwetrottmann.trakt5.entities.SearchResult
import com.uwetrottmann.trakt5.enums.Extended
import com.uwetrottmann.trakt5.services.Search
import hu.csabapap.seriesreminder.data.Result
import hu.csabapap.seriesreminder.utils.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteSearchDataSource @Inject constructor(
        private val search: Search
) {

    val cache: MutableList<SearchResult> = mutableListOf()

    suspend fun search(query: String): Result<List<SearchResult>> = safeApiCall({
        val searchResult = search.textQueryShow(query,
        "", "", "", "", "", "", "", "",
        "", Extended.FULL, 1, 25).execute()
        return@safeApiCall Result.Success(searchResult.body() ?: emptyList())
    }, errorMessage = "error during search")


    fun clearCache() {
        cache.clear()
    }

}