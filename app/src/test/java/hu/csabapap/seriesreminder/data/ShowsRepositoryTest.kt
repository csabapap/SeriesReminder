package hu.csabapap.seriesreminder.data

import com.nhaarman.mockito_kotlin.mock
import hu.csabapap.seriesreminder.data.db.daos.SRShowDao
import hu.csabapap.seriesreminder.data.network.TraktApi
import hu.csabapap.seriesreminder.data.network.TvdbApi
import io.reactivex.Flowable
import io.reactivex.Observable
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class ShowsRepositoryTest {

    @Mock
    var traktApi : TraktApi = mock()

    @Mock
    var tvdbApi : TvdbApi = mock()

    @Mock
    var showDao : SRShowDao = mock()

    var showsRepository : ShowsRepository

    init {
        MockitoAnnotations.initMocks(this)
        showsRepository = ShowsRepository(traktApi, tvdbApi, showDao)
    }

    @Test
    fun getTrendingShowsFromTraktApiWhenCacheIsEmpty() {
        showsRepository.cachedTrendingShows = arrayListOf()

        Mockito.`when`(traktApi.trendingShows()).thenReturn(Flowable.just(arrayListOf()))
    }
}