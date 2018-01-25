package hu.csabapap.seriesreminder.ui.addshow

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import javax.inject.Inject

class AddShowViewModelProvider @Inject constructor(
        private val viewModel: AddShowViewModel
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(AddShowActivity::class.java)) {
            return viewModel as T
        }

        throw IllegalArgumentException("unknown model class " + modelClass)
    }
}