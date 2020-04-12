package hu.csabapap.seriesreminder.data.repositories.trendingshows

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import hu.csabapap.seriesreminder.data.Result
import hu.csabapap.seriesreminder.data.network.services.TrendingShowsService
import hu.csabapap.seriesreminder.trendingShows
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import retrofit2.Response

class RemoteTrendingDataSourceTest {

    private val trendingShowsService: TrendingShowsService = mock()
    private val dataSource = RemoteTrendingDataSource(trendingShowsService)

    @Test
    fun `get trending shows with successful request`() = runBlocking {
        val response = Response.success(trendingShows)
        whenever(trendingShowsService.paginatedTrendingShows("full", 1, 20)).thenReturn(response)

        val result =  dataSource.getDeferredPaginatedShows()

        assertNotNull(result)
        assertEquals(Result.Success(trendingShows), result)
    }
}