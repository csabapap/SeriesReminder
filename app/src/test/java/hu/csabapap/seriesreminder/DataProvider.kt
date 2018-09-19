package hu.csabapap.seriesreminder

import hu.csabapap.seriesreminder.data.network.entities.BaseShow
import hu.csabapap.seriesreminder.data.network.entities.Ids
import hu.csabapap.seriesreminder.data.network.entities.SearchResult


fun getSearchResult() = SearchResult(getShow())

fun getShow() = BaseShow(
        title = "Humans",
        ids = Ids(1, 1),
        overview = "Lorem ipsum",
        rating = 3.14f,
        votes = 42
)
