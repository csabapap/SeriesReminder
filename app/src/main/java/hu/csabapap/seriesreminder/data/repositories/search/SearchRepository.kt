package hu.csabapap.seriesreminder.data.repositories.search

import hu.csabapap.seriesreminder.data.Result
import hu.csabapap.seriesreminder.data.network.entities.SearchResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepository @Inject constructor(private val remoteDataSource: RemoteSearchDataSource) {

    suspend fun search(query: String): List<SearchResult> {
        val result = remoteDataSource.search(query)
        if (result is Result.Success) {
            return result.data
        }
        return emptyList()
    }

    fun clearCache() {
        remoteDataSource.clearCache()
    }
}