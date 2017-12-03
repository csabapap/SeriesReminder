package hu.csabapap.seriesreminder.ui.add_show

import android.os.Bundle
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.data.ShowsRepository
import hu.csabapap.seriesreminder.data.db.entities.SRShow
import hu.csabapap.seriesreminder.extensions.loadFromUrl
import hu.csabapap.seriesreminder.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_add_show.*
import org.apache.commons.cli.MissingArgumentException
import timber.log.Timber
import javax.inject.Inject

class AddShowActivity : BaseActivity(), AddShowContract.View {

    var showId: Int = -1
    var show: SRShow? = null

    @Inject lateinit var presenter: AddShowPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_show)

        initParams(intent.extras)

        toolbar.apply {
            setNavigationIcon(R.drawable.ic_arrow_back_24dp)
            setNavigationOnClickListener({finish()})
        }

        presenter.attach(this)
    }

    override fun onStart() {
        super.onStart()

        presenter.loadShow(showId)
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detach()
    }

    private fun initParams(extras: Bundle?) {
        if(extras == null) {
            finish()
            return
        }
        if (!extras.containsKey("show_id")) {
            throw MissingArgumentException("Missing show id")
        }
        showId = extras.getInt("show_id", -1)
    }

    override fun displayShow(show: SRShow) {
        Timber.d("display show: $show")
        poster.loadFromUrl(show.posterThumb)
        if(show.coverThumbUrl != null) {
            cover.loadFromUrl(show.coverThumbUrl!!)
        }
        description.text = show.overview
    }
}
