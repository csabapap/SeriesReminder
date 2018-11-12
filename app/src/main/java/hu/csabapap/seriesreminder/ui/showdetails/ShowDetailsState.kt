package hu.csabapap.seriesreminder.ui.showdetails

import hu.csabapap.seriesreminder.data.db.entities.SRShow
import org.threeten.bp.OffsetDateTime

sealed class ShowDetailsState {

    data class Show(val show: SRShow) : ShowDetailsState()

    data class Reminder(val show: SRShow, val airDate: OffsetDateTime) : ShowDetailsState()
}
