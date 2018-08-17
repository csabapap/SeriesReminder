package hu.csabapap.seriesreminder.data.db

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import hu.csabapap.seriesreminder.data.db.entities.TrendingGridItem

data class TrendingShowsResult(
        val data: LiveData<PagedList<TrendingGridItem>>
)