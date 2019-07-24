package hu.csabapap.seriesreminder.domain

import hu.csabapap.seriesreminder.data.CollectionRepository
import hu.csabapap.seriesreminder.data.models.SrSearchResult
import hu.csabapap.seriesreminder.data.network.TraktApi
import hu.csabapap.seriesreminder.data.repositories.search.SearchRepository
import io.reactivex.Single
import kotlinx.coroutines.rx2.await
import javax.inject.Inject

class GetSearchResultUseCase @Inject constructor(private val searchRepository: SearchRepository,
                                                 private val collectionRepository: CollectionRepository) {

    fun search(query: String): Single<List<SrSearchResult>> {
        return searchRepository.search(query)
                .flatMap { searchResult ->
                    if (searchResult.isEmpty()) {
                        Single.just(emptyList<SrSearchResult>())
                    } else {
                        val ids = mutableListOf<Int>()
                        searchResult.forEach {
                            ids.add(it.show.ids.trakt)
                        }
                        collectionRepository.getItemsFromCollection(ids)
                                .flatMap {
                                    val srSearchResult = mutableListOf<SrSearchResult>()
                                    searchResult.forEach { item ->
                                        System.out.println("adding item")
                                        srSearchResult.add(SrSearchResult(item.show, it.contains(item.show.ids.trakt)))
                                    }
                                    Single.just(srSearchResult)
                                }
                    }
                }
    }

    suspend fun getLastResult(): List<SrSearchResult> {
        val lastSearchResult = searchRepository.getLastResults()
        if (lastSearchResult.isEmpty()) {
            return emptyList()
        }

        val resultIds = lastSearchResult.map { it.show.ids.trakt }
        val collectionIds = collectionRepository.getItemsFromCollection(resultIds).await()

        val srSearchResult = mutableListOf<SrSearchResult>()
        lastSearchResult.forEach { item ->
            System.out.println("adding item_season")
            srSearchResult.add(SrSearchResult(item.show, collectionIds.contains(item.show.ids.trakt)))
        }

        return srSearchResult
    }

    fun clearLastSearchResults() {
        searchRepository.clearCache()
    }
}