package hu.csabapap.seriesreminder.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider

class MainViewModelProvider @Inject constructor(
        private val viewModels: @JvmSuppressWildcards Map<Class<out ViewModel>, Provider<ViewModel>>
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        var viewModel: Provider<out ViewModel>? = viewModels[modelClass]
        if (viewModel == null) {
            for ((key, value) in viewModels) {
                if (modelClass.isAssignableFrom(key)) {
                    viewModel = value
                    break
                }
            }
        }
        if (viewModel == null) {
            throw IllegalArgumentException("unknown model class " + modelClass)
        }
        try {
            @Suppress("UNCHECKED_CAST")
            return viewModel.get() as T
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}