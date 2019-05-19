package hu.csabapap.seriesreminder.data.repositories.relatedshows

import hu.csabapap.seriesreminder.data.network.services.RelatedShowsService
import javax.inject.Inject

class RemoteRelatedShowsDataSource @Inject constructor(
        private val relatedShowsService: RelatedShowsService){

    fun relatedShows(id: Int) = relatedShowsService.getRelatedShows(id)
}