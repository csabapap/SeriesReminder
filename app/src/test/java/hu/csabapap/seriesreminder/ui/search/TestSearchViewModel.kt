package hu.csabapap.seriesreminder.ui.search

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.*
import hu.csabapap.seriesreminder.data.CollectionRepository
import hu.csabapap.seriesreminder.data.repositories.shows.ShowsRepository
import hu.csabapap.seriesreminder.data.models.SrSearchResult
import hu.csabapap.seriesreminder.data.repositories.popularshows.PopularShowsRepository
import hu.csabapap.seriesreminder.data.repositories.trendingshows.TrendingShowsRepository
import hu.csabapap.seriesreminder.domain.GetSearchResultUseCase
import hu.csabapap.seriesreminder.getShow
import hu.csabapap.seriesreminder.utils.AppCoroutineDispatchers
import hu.csabapap.seriesreminder.utils.RxSchedulers
import hu.csabapap.seriesreminder.utils.TestAppRxSchedulers
import io.reactivex.Single
import kotlinx.coroutines.rx2.asCoroutineDispatcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class TestSearchViewModel {

    @get:Rule
    var testRule: TestRule = InstantTaskExecutorRule()

    private lateinit var searchViewModel: SearchViewModel
    private val stateObserver = mock<Observer<SearchState>>()

    private var getSearchResultUseCase = mock<GetSearchResultUseCase>()
    private val showsRepository = mock<ShowsRepository>()
    private val trendingShowsRepository: TrendingShowsRepository = mock()
    private val popularShowsRepository: PopularShowsRepository = mock()
    private val collectionRepository = mock<CollectionRepository>()
    private val schedulers: RxSchedulers = TestAppRxSchedulers()
    private val dispatchers = AppCoroutineDispatchers(
            schedulers.io().asCoroutineDispatcher(),
            schedulers.compoutation().asCoroutineDispatcher(),
            schedulers.ui().asCoroutineDispatcher())


    @Before
    fun setUp() {
        searchViewModel = SearchViewModel(getSearchResultUseCase, trendingShowsRepository,
                popularShowsRepository, schedulers, dispatchers)
        searchViewModel.searchState.observeForever(stateObserver)
    }

    @Test
    fun `when the list of search results is empty display no result`() {
        // given
        val response = emptyList<SrSearchResult>()
        whenever(getSearchResultUseCase.search("humans")).thenReturn(Single.just(response))

        // when
        searchViewModel.search("humans")

        // then
        inOrder(stateObserver) {
            verify(stateObserver).onChanged(SearchState.Loading)
            verify(stateObserver).onChanged(SearchState.NoResult)
        }
    }

    @Test
    fun `when the list of search results display search loaded`() {
        // given
        val srResponse = listOf(SrSearchResult(getShow(), false), SrSearchResult(getShow(), false))
        whenever(getSearchResultUseCase.search("humans")).thenReturn(Single.just(srResponse))

        // when
        searchViewModel.search("humans")

        // then
        inOrder(stateObserver) {
            verify(stateObserver).onChanged(SearchState.Loading)
            verify(stateObserver).onChanged(any<SearchState.SearchResultLoaded>())
        }
    }
}