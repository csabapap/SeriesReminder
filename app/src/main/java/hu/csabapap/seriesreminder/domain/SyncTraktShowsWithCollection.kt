package hu.csabapap.seriesreminder.domain

import javax.inject.Inject

class SyncTraktShowsWithCollection @Inject constructor(
        val getWatchedShowUseCase: GetWatchedShowUseCase
) {



}