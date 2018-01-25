package hu.csabapap.seriesreminder.ui.addshow

import android.os.Bundle
import hu.csabapap.seriesreminder.R
import hu.csabapap.seriesreminder.data.db.entities.SRShow
import hu.csabapap.seriesreminder.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_add_show.*
import org.apache.commons.cli.MissingArgumentException

class AddShowActivity : BaseActivity() {

    var showId: Int = -1
    var show: SRShow? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_show)

        initParams(intent.extras)

        toolbar.apply {
            setNavigationIcon(R.drawable.ic_arrow_back_24dp)
            setNavigationOnClickListener({finish()})
        }
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
}
