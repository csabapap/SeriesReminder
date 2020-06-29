package hu.csabapap.seriesreminder.data.network.services

import hu.csabapap.seriesreminder.data.network.entities.SearchResult
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SearchService {
    @GET("search/{type}")
    suspend fun search(@Path("type") type: String,
               @Query("query") query: String,
               @Query("extended") extended: String = "full")
            : List<SearchResult>
}