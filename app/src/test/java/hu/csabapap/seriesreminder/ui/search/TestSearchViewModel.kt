package hu.csabapap.seriesreminder.ui.search

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import hu.csabapap.seriesreminder.data.models.SrSearchResult
import hu.csabapap.seriesreminder.domain.GetSearchResultUseCase
import hu.csabapap.seriesreminder.getShow
import hu.csabapap.seriesreminder.utils.AppCoroutineDispatchers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
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
    private val dispatchers = AppCoroutineDispatchers(
            TestCoroutineDispatcher(),
            TestCoroutineDispatcher(),
            TestCoroutineDispatcher())


    @Before
    fun setUp() {
        searchViewModel = SearchViewModel(getSearchResultUseCase, dispatchers)
        searchViewModel.searchState.observeForever(stateObserver)
    }

    @Test
    fun `when the list of search results is empty display no result`() = runBlocking {
        // given
        val response = emptyList<SrSearchResult>()
        whenever(getSearchResultUseCase.search("humans")).thenReturn(response)

        // when
        searchViewModel.search("humans")

        // then
        inOrder(stateObserver) {
            verify(stateObserver).onChanged(SearchState.Loading)
//            verify(stateObserver).onChanged(SearchState.NoResult)
        }
    }

    @Test
    fun `when the list of search results display search loaded`() = runBlocking {
        // given
        val srResponse = listOf(SrSearchResult(getShow(), false), SrSearchResult(getShow(), false))
        whenever(getSearchResultUseCase.search("humans")).thenReturn(srResponse)

        // when
        searchViewModel.search("humans")

        // then
        inOrder(stateObserver) {
            verify(stateObserver).onChanged(SearchState.Loading)
            verify(stateObserver).onChanged(any<SearchState.SearchResultLoaded>())
        }
    }
}