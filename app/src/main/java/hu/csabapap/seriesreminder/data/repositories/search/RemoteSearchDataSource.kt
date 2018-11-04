package hu.csabapap.seriesreminder.data.repositories.search

import hu.csabapap.seriesreminder.data.network.TraktApi
import hu.csabapap.seriesreminder.data.network.entities.SearchResult
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteSearchDataSource @Inject constructor(val traktApi: TraktApi) {

    val cache: MutableList<SearchResult> = mutableListOf()

    fun search(query: String): Single<List<SearchResult>> {
        return traktApi.search(query, "show")
                .doOnSuccess {
                    cache.clear()
                    cache.addAll(it)
                }
    }

    fun clearCache() {
        cache.clear()
    }

}