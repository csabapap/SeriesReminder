package hu.csabapap.seriesreminder.data.repositories.nextepisodes

import hu.csabapap.seriesreminder.data.network.TraktApi
import hu.csabapap.seriesreminder.data.states.NextEpisodeError
import hu.csabapap.seriesreminder.data.states.NextEpisodeState
import hu.csabapap.seriesreminder.data.states.NextEpisodeSuccess
import hu.csabapap.seriesreminder.data.states.NoNextEpisode
import javax.inject.Inject

class NextEpisodesRemoteDataSource @Inject constructor(private val traktApi: TraktApi) {

    suspend fun fetchNextEpisode(showId: Int): NextEpisodeState {
        val response = traktApi.nextEpisode(showId).await()
        return when (response.code()) {
            200 -> NextEpisodeSuccess(response.body()!!)
            204 -> NoNextEpisode
            else -> NextEpisodeError("error during next episodeSingle fetching")
        }
    }

}