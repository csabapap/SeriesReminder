package hu.csabapap.seriesreminder.ui.seasons

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import hu.csabapap.seriesreminder.data.SeasonsRepository
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.utils.AppCoroutineDispatchers
import javax.inject.Inject

class SeasonsViewModelProvider @Inject constructor(private val showsRepository: ShowsRepository,
                                                   private val seasonsRepository: SeasonsRepository,
                                                   private val dispatchers: AppCoroutineDispatchers)
    : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SeasonsViewModel(showsRepository, seasonsRepository, dispatchers) as T
    }
}