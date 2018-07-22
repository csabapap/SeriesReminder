package hu.csabapap.seriesreminder.ui.showdetails

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider

class ShowDetailsViewModelProvider(): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ShowDetailsViewModel() as T
    }
}