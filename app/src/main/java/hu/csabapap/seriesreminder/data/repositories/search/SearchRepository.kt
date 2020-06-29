package hu.csabapap.seriesreminder.data.repositories.search

import hu.csabapap.seriesreminder.data.network.entities.SearchResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepository @Inject constructor(private val remoteDataSource: RemoteSearchDataSource) {

    suspend fun search(query: String) = remoteDataSource.search(query)

    fun clearCache() {
        remoteDataSource.clearCache()
    }
}