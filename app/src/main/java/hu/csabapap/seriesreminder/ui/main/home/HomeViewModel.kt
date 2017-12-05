package hu.csabapap.seriesreminder.ui.main.home

import android.arch.lifecycle.ViewModel
import hu.csabapap.seriesreminder.data.ShowsRepository
import javax.inject.Inject

class HomeViewModel @Inject constructor(val showsRepository: ShowsRepository) : ViewModel() {


}