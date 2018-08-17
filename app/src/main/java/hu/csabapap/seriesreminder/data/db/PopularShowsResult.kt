package hu.csabapap.seriesreminder.data.db

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import hu.csabapap.seriesreminder.data.db.entities.PopularGridItem

data class PopularShowsResult(val data: LiveData<PagedList<PopularGridItem>>)