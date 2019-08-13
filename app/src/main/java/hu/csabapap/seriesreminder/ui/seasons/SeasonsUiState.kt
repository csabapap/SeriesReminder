package hu.csabapap.seriesreminder.ui.seasons

import hu.csabapap.seriesreminder.data.db.entities.SRShow

sealed class SeasonsUiState {
    data class DisplayShow(val show: SRShow): SeasonsUiState()
}