package hu.csabapap.seriesreminder.ui.main.collection

import hu.csabapap.seriesreminder.data.db.entities.CollectionItem

sealed class CollectionUiState {
    data class Collection(val items: List<CollectionItem>): CollectionUiState()
}
