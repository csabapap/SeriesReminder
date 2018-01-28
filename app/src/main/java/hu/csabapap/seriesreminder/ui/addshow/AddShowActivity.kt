package hu.csabapap.seriesreminder.ui.addshow

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import dagger.android.support.DaggerAppCompatActivity
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.data.db.entities.SRShow
import hu.csabapap.seriesreminder.extensions.loadFromUrl
import kotlinx.android.synthetic.main.activity_add_show.*
import org.apache.commons.cli.MissingArgumentException
import javax.inject.Inject

class AddShowActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelProvider: ViewModelProvider.Factory
    private lateinit var addShowViewModel: AddShowViewModel

    private var showId: Int = -1
    var show: SRShow? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_show)

        addShowViewModel = ViewModelProviders.of(this, viewModelProvider)
                .get(AddShowViewModel::class.java)

        initParams(intent.extras)

        toolbar.apply {
            setNavigationIcon(R.drawable.ic_arrow_back_24dp)
            setNavigationOnClickListener({finish()})
        }

        addShowViewModel.showLiveData.observe(this, Observer {
            it?.apply {
                displayShow(it.show)
            }
        })
    }

    override fun onStart() {
        super.onStart()
        addShowViewModel.getShow(showId)
    }

    private fun initParams(extras: Bundle?) {
        if(extras == null) {
            throw MissingArgumentException("Missing required extras")
        }
        if (!extras.containsKey("show_id")) {
            throw MissingArgumentException("Missing show id")
        }
        showId = extras.getInt("show_id", -1)
    }

    private fun displayShow(srShow: SRShow) {
        tv_title.text = srShow.title
        tv_overview.text = srShow.overview
        poster.loadFromUrl("https://thetvdb.com/banners/${srShow.posterThumb}")
    }
}
