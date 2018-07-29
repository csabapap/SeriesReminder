package hu.csabapap.seriesreminder.data.db

import android.arch.lifecycle.LiveData
import android.arch.paging.PagedList
import hu.csabapap.seriesreminder.data.db.entities.TrendingGridItem

data class TrendingShowsResult(
        val data: LiveData<PagedList<TrendingGridItem>>
)