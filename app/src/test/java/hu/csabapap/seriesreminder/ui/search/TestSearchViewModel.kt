package hu.csabapap.seriesreminder.ui.search

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockito_kotlin.*
import hu.csabapap.seriesreminder.data.CollectionRepository
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.data.models.SrSearchResult
import hu.csabapap.seriesreminder.data.network.entities.SearchResult
import hu.csabapap.seriesreminder.domain.GetSearchResultUseCase
import hu.csabapap.seriesreminder.getSearchResult
import hu.csabapap.seriesreminder.getShow
import hu.csabapap.seriesreminder.utils.RxSchedulers
import hu.csabapap.seriesreminder.utils.TestAppRxSchedulers
import io.reactivex.Single
import org.junit.After
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
    private val collectionRepository = mock<CollectionRepository>()
    private val schedulers: RxSchedulers = TestAppRxSchedulers()


    @Before
    fun setUp() {
        searchViewModel = SearchViewModel(getSearchResultUseCase, showsRepository, collectionRepository,
                schedulers)
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
        verifyNoMoreInteractions(stateObserver)
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
        verifyNoMoreInteractions(stateObserver)
    }
}