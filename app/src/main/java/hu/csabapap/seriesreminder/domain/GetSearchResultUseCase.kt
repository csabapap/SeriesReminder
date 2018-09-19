package hu.csabapap.seriesreminder.domain

import hu.csabapap.seriesreminder.data.CollectionRepository
import hu.csabapap.seriesreminder.data.models.SrSearchResult
import hu.csabapap.seriesreminder.data.network.TraktApi
import io.reactivex.Single
import javax.inject.Inject

class GetSearchResultUseCase @Inject constructor(private val traktApi: TraktApi,
                                                 private val collectionRepository: CollectionRepository) {

    fun search(query: String): Single<List<SrSearchResult>> {
        return traktApi.search("show", query)
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

}