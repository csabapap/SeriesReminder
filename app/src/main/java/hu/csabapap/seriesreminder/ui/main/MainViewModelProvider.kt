package hu.csabapap.seriesreminder.ui.main

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import hu.csabapap.seriesreminder.ui.main.home.HomeViewModel

class MainViewModelProvider(val homeViewModel: HomeViewModel) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return homeViewModel as T
        }

        throw IllegalArgumentException("unknown model class " + modelClass)
    }
}