package hu.csabapap.seriesreminder.ui.addshow

import hu.csabapap.seriesreminder.data.db.entities.SRShow

sealed class AddShowState

data class DisplayShow(val show: SRShow): AddShowState()

object Close: AddShowState()
