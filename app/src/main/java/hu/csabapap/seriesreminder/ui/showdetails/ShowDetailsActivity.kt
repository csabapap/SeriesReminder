package hu.csabapap.seriesreminder.ui.showdetails

import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.util.Log
import android.view.View
import hu.csabapap.seriesreminder.R
import kotlinx.android.synthetic.main.activity_show_details.*
import javax.inject.Inject
import javax.inject.Named

class ShowDetailsActivity : AppCompatActivity() {
    @Inject @Named("ShowDetailsViewModelFactory")
    lateinit var viewModelProvider: ShowDetailsViewModelProvider
    private lateinit var viewModel: ShowDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_details)

        viewModel = ViewModelProviders.of(this, viewModelProvider)
                .get(ShowDetailsViewModel::class.java)

        toolbar.navigationIcon = getDrawable(R.drawable.ic_arrow_back_24dp)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }


        app_bar.addOnOffsetChangedListener { appBarLayout, offset ->
            run {
                val toolbarHeight = toolbar.height
                val scrollRange = appBarLayout.totalScrollRange
                val i = scrollRange - toolbarHeight + offset
                if (i <= 0) {
                    poster.visibility = View.GONE
                    poster_placeholder.visibility = View.VISIBLE
                } else {
                    poster.visibility = View.VISIBLE
                    poster_placeholder.visibility = View.INVISIBLE
                    val posterHeightDiff = poster.height - poster_placeholder.height
                    var scale = Math.abs(scrollRange + offset.toFloat() + posterHeightDiff) / scrollRange
                    if (scale > 1f) scale = 1f
                    if (scale > poster_placeholder.height.toFloat() / poster.height.toFloat()) {
                        poster.scaleX = scale
                        poster.scaleY = scale
                        poster.translationY = (poster.height - poster.height.toFloat() * scale) / 2f
                    }
                    Log.d("CustomBehavior", "poster's y: " + poster.y)
                    Log.d("CustomBehavior", "poster's y: " + poster.y)
                }
            }
        }
    }


}
