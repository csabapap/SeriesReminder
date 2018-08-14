package hu.csabapap.seriesreminder.ui.showdetails

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import hu.csabapap.seriesreminder.data.ShowsRepository
import javax.inject.Inject

class ShowDetailsViewModelProvider @Inject constructor(private val showsRepository: ShowsRepository)
    : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ShowDetailsViewModel(showsRepository) as T
    }
}