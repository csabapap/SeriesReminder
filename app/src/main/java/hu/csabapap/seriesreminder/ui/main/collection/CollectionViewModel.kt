package hu.csabapap.seriesreminder.ui.main.collection

import android.arch.lifecycle.ViewModel
import hu.csabapap.seriesreminder.data.CollectionRepository
import javax.inject.Inject

class CollectionViewModel @Inject constructor(val collectionRepository: CollectionRepository)
    : ViewModel()