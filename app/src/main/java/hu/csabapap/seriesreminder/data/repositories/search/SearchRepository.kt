package hu.csabapap.seriesreminder.data.repositories.search

import hu.csabapap.seriesreminder.data.network.entities.SearchResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepository @Inject constructor(private val remoteDataSource: RemoteSearchDataSource) {

    fun search(query: String) = remoteDataSource.search(query)

    fun getLastResults(): List<SearchResult> {
        return remoteDataSource.cache
    }

    fun clearCache() {
        remoteDataSource.clearCache()
    }
}