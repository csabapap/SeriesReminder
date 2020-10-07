package hu.csabapap.seriesreminder.data.repositories.relatedshows

import com.uwetrottmann.trakt5.enums.Extended
import com.uwetrottmann.trakt5.services.Shows
import javax.inject.Inject

class RemoteRelatedShowsDataSource @Inject constructor(
        private val showsService: Shows){

    fun relatedShows(id: Int) = showsService.related(id.toString(), 1, 20, Extended.FULL)
}