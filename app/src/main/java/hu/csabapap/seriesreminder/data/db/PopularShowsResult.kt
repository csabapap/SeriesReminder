package hu.csabapap.seriesreminder.data.db

import android.arch.lifecycle.LiveData
import android.arch.paging.PagedList
import hu.csabapap.seriesreminder.data.db.entities.PopularGridItem

data class PopularShowsResult(val data: LiveData<PagedList<PopularGridItem>>)