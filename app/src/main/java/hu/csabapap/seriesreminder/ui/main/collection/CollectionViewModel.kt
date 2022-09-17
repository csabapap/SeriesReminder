package hu.csabapap.seriesreminder.ui.main.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import hu.csabapap.seriesreminder.data.CollectionRepository
import hu.csabapap.seriesreminder.data.db.entities.CollectionItem
import hu.csabapap.seriesreminder.utils.AppCoroutineDispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CollectionViewModel @Inject constructor(
    private val collectionRepository: CollectionRepository,
    private val dispatchers: AppCoroutineDispatchers
)
    : ViewModel() {


    init {
        loadCollection()
    }

    private val _uiState = MutableStateFlow<CollectionUiState>(CollectionUiState.Collection(emptyList()))
    val uiState: StateFlow<CollectionUiState>
        get() = _uiState

    fun loadCollection() {
        viewModelScope.launch {
            val collection = collectionRepository.getCollectionsSuspendable()
            withContext(dispatchers.main) {
                _uiState.value = CollectionUiState.Collection(collection)
            }
        }
    }
}