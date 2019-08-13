package hu.csabapap.seriesreminder.ui.seasons

import android.os.Bundle
import androidx.lifecycle.Observer
import dagger.android.support.DaggerAppCompatActivity
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.extensions.exhaustive
import hu.csabapap.seriesreminder.utils.Activities
import kotlinx.android.synthetic.main.activity_episode.*
import javax.inject.Inject
import javax.inject.Named

class SeasonsActivity : DaggerAppCompatActivity() {

    @Inject
    @Named("SeasonsViewModelFactory")
    lateinit var viewModelProvider: SeasonsViewModelProvider
    private lateinit var viewModel: SeasonsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seasons)

        viewModel = viewModelProvider.create(SeasonsViewModel::class.java)

        viewModel.detailsUiState.observe(this, Observer<SeasonsUiState> { state ->
            if (state != null) {
                updateUiState(state)
            }
        })

        val intent = intent ?: return
        val extras = intent.extras ?: return
        initParams(extras)

        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun updateUiState(state: SeasonsUiState) {
        when (state) {
            is SeasonsUiState.DisplayShow -> toolbar.title = state.show.title
        }.exhaustive
    }

    private fun initParams(extras: Bundle) {
        val showId = extras.getInt(Activities.Season.SHOW_ID)
        viewModel.getShow(showId)
    }
}
