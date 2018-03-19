package hu.csabapap.seriesreminder.data

import com.nhaarman.mockito_kotlin.mock
import hu.csabapap.seriesreminder.data.db.daos.PopularDao
import hu.csabapap.seriesreminder.data.db.daos.SRShowDao
import hu.csabapap.seriesreminder.data.db.daos.TrendingDao
import hu.csabapap.seriesreminder.data.network.TraktApi
import hu.csabapap.seriesreminder.data.network.TvdbApi
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
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

    @Mock
    var trendingDao : TrendingDao = mock()

    @Mock
    var popularDao : PopularDao = mock()

    @Mock
    var episodesRepository : EpisodesRepository = mock()

    var showsRepository : ShowsRepository

    init {
        MockitoAnnotations.initMocks(this)
        showsRepository = ShowsRepository(traktApi, tvdbApi, showDao, trendingDao, popularDao,
                episodesRepository)
    }

    @Test
    fun getTrendingShowsFromTraktApiWhenCacheIsEmpty() {
        showsRepository.cachedTrendingShows = arrayListOf()

        Mockito.`when`(traktApi.trendingShows()).thenReturn(Single.just(arrayListOf()))
    }
}