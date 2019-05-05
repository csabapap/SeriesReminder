package hu.csabapap.seriesreminder.ui.showdetails

import hu.csabapap.seriesreminder.data.db.entities.SRShow
import hu.csabapap.seriesreminder.data.db.entities.SrNotification

sealed class ShowDetailsState {

    data class Show(val show: SRShow) : ShowDetailsState()

    object NotificationCreated: ShowDetailsState()

    object NotificationDeleted: ShowDetailsState()

    object AddNotificationButton: ShowDetailsState()

    data class Notification(val notification: SrNotification): ShowDetailsState()
}
