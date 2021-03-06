package hu.csabapap.seriesreminder.ui.showdetails

import hu.csabapap.seriesreminder.data.db.entities.SREpisode
import hu.csabapap.seriesreminder.data.db.entities.SRSeason
import hu.csabapap.seriesreminder.data.db.entities.SRShow
import hu.csabapap.seriesreminder.data.db.entities.SrNotification
import hu.csabapap.seriesreminder.ui.adapters.items.ShowItem

sealed class ShowDetailsState {

    data class Show(val show: SRShow) : ShowDetailsState()

    data class Seasons(val seasons: List<SRSeason>): ShowDetailsState()

    data class NextEpisode(val episode: SREpisode): ShowDetailsState()

    object NextEpisodeNotFound: ShowDetailsState()

    object NotificationCreated: ShowDetailsState()

    object NotificationDeleted: ShowDetailsState()

    object AddNotificationButton: ShowDetailsState()

    data class Notification(val notification: SrNotification): ShowDetailsState()

    data class RelatedShows(val relatedShowItems: List<ShowItem>): ShowDetailsState()
}
